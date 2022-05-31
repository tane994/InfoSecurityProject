package it.unibz.emails.server.persistence;


import it.unibz.emails.server.persistence.exceptions.DbException;
import it.unibz.emails.server.persistence.exceptions.MalformedParameterException;
import it.unibz.emails.server.persistence.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Db {
    private static Db singleton;
    private final Connection connection;

    private Db(Connection connection) {
        this.connection = connection;
    }

    private static void connect(ConnectionParams connParams) {
        Connection conn = null;
        DriverManager.setLoginTimeout(10);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Postgres driver cannot be loaded",e);
        }
        String url = "jdbc:" + connParams.dbms() + "://" + connParams.host() + ":" + connParams.port() + "/" + connParams.name();

        try {
            conn = DriverManager.getConnection(url, connParams.username(), connParams.password());
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            DbException.handleException(singleton.connection, e, "connecting");
        }

        singleton = new Db(conn);
    }

    public static boolean isConnected() {
        try {
            return singleton != null && singleton.connection != null && !singleton.connection.isClosed();
        } catch (SQLException e) {
            DbException.handleException(singleton.connection, e, "isConnected");
            return false;
        }
    }

    public static Db getInstance() {
        if (!isConnected()) {
            connect(ConnectionParams.fromConfig());
            singleton.init();
        }

        return singleton;
    }

    private void init() {
        try {
            select("SELECT * FROM users");
        } catch (NotFoundException e) {
            executeScript("tables.sql");
        }
    }

    private static void disconnect() {
        if (isConnected()) {
            try {
                singleton.connection.close();
            } catch (SQLException e) {
                //not relevant
            }
        }
        singleton = null;
    }


    public int insert(String query, Object... params) {
        PreparedStatement statement = prepareStatement(query, params);
        int affectedRows = 0;

        try {
            affectedRows = statement.executeUpdate();
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            DbException.handleException(connection, e, query);
        }
        return affectedRows;
    }

    public void executeScript(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream sqlScript = classLoader.getResourceAsStream(path);

        try {
            if (sqlScript==null)
                throw new IOException("SQL script at path " + path + " not found");

            String scriptContent = new String(sqlScript.readAllBytes());
            Statement st = connection.createStatement();
            st.executeUpdate(scriptContent);

            st.close();
            sqlScript.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (SQLException e) {
            DbException.handleException(connection, e, "SQL Script");
        }
    }

    public List<Map<String,Object>> select(String query, Object... params) {
        PreparedStatement statement = prepareStatement(query, params);
        ResultSet resultSet;
        List<Map<String,Object>> ret;

        try {
            resultSet = statement.executeQuery();
            ret = getResults(resultSet);
            statement.close();
            return ret;
        } catch (SQLException e) {
            DbException.handleException(connection, e, query);
        }
        return new ArrayList<>();
    }

    private PreparedStatement prepareStatement(String query, Object... params) {
        long actualParamCount = query.chars().filter(ch -> ch == '?').count();

        if (actualParamCount != params.length)
            throw new MalformedParameterException("select", "Wrong number of parameters. Expected " + params.length + ", actual " + actualParamCount);

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            bindParamToStatement(statement, params);
        } catch (SQLException e) {
            DbException.handleException(connection, e, query);
        }

        return statement;
    }

    private void bindParamToStatement(PreparedStatement statement, Object... params) {
        try {
            int i=1;
            for(Object param: params) {
                if (param==null || param=="null")
                    statement.setNull(i, getSqlDataType(param));
                else {
                    switch (getSqlDataType(param)) {
                        case Types.VARCHAR, Types.CHAR -> statement.setString(i, param.toString());
                        case Types.INTEGER -> statement.setInt(i, (Integer) param);
                        case Types.BOOLEAN, Types.BIT -> statement.setBoolean(i, (Boolean) param);
                        case Types.DATE -> statement.setDate(i, (Date) param);
                        case Types.TIME -> statement.setTime(i, (Time) param);
                        case Types.TIMESTAMP -> statement.setTimestamp(i, (Timestamp) param);
                        case Types.FLOAT, Types.DOUBLE -> statement.setDouble(i, (Double) param);
                        case Types.OTHER, Types.JAVA_OBJECT -> statement.setObject(i, param, Types.OTHER);
                        default -> throw new SQLException("Unknown type for input value " + param);
                    }
                }
                i++;
            }
        } catch (SQLException e) {
            DbException.handleException(connection, e, "parameters casting");
        }
    }

    public <T> int getSqlDataType(T content) {
        if (content instanceof String)
            return Types.VARCHAR;
        else if (content instanceof Integer)
            return Types.INTEGER;
        else if (content instanceof Timestamp)
            return Types.TIMESTAMP;
        else
            return Types.VARCHAR;
    }

    private List<Map<String,Object>> getResults(ResultSet resultSet) {
        List<Map<String,Object>> ret = new ArrayList<>();
        List<String> colNames = new ArrayList<>();
        List<Integer> colTypes = new ArrayList<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            for (int i=1; i<=numberOfColumns; i++) {
                colTypes.add(metaData.getColumnType(i));
                colNames.add(metaData.getColumnLabel(i));
            }

            while (resultSet.next()) {
                Map<String,Object> tuple = new LinkedHashMap<>(numberOfColumns);

                for (int i=1; i<=numberOfColumns; i++) {
                    Object param = castParamFromResult(resultSet, i, colTypes);
                    tuple.put(colNames.get(i-1), param);
                }

                ret.add(tuple);
            }
        } catch (SQLException e) {
            DbException.handleException(connection, e, "results conversion");
        }

        return ret;
    }

    private Object castParamFromResult(ResultSet resultSet, int pos, List<Integer> colTypes) {
        int colType = colTypes.get(pos-1);

        try {
            switch (colType) {
                case Types.INTEGER -> { return resultSet.getInt(pos); }
                case Types.BOOLEAN, Types.BIT -> { return resultSet.getBoolean(pos); }
                case Types.TIMESTAMP -> { return resultSet.getTimestamp(pos); }
                case Types.FLOAT, Types.DOUBLE -> { return resultSet.getDouble(pos); }
                case Types.CHAR, Types.VARCHAR -> { return resultSet.getString(pos); }
                case Types.NULL -> { return "null"; }
                default -> throw new SQLException("Unknown data type " + colType +  " in result for column " + pos, "22030");
            }
        } catch (SQLException e) {
            DbException.handleException(connection, e, "type casting");
            return "null";
        }
    }
}
