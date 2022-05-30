package it.unibz.emails.server.persistence.exceptions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public class DbException extends RuntimeException {
    private final String sqlState;
    private final String operation;
    private final String details;

    public DbException(String sqlState, String message, String operation, String details, Throwable cause) {
        super(message + " while " + operation + ". " + details, cause);
        this.sqlState = sqlState;
        this.operation = operation;
        this.details = details;
    }

    public DbException(String sqlState, String message, String operation, String details) {
        this(sqlState, message, operation, details, null);
    }

    public DbException(String sqlState, String operation, String details) {
        this(sqlState, "Database error " + sqlState, operation, details);
    }

    public DbException(String sqlState, String operation, String details, Throwable cause) {
        this(sqlState, details, operation, "", cause);
    }

    public String getSqlState() {
        return sqlState;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetails() {
        return details;
    }

    public static void handleException(Connection connection, SQLException e, String operation) {
        try {
            if (connection!=null) connection.rollback();
        } catch (SQLException ex) {
            //
        }

        String sqlState = e.getSQLState();
        String details = e.getMessage();

        System.out.println(e.getSQLState() + " " + e.getMessage());

        if (sqlState == null)
            throw new DbException("", operation, details, e);
        if (sqlState.startsWith("23"))  //Integrity Constraint Violation
            throw new ConstraintViolatedException(sqlState, operation, details, e);
        else if (sqlState.startsWith("3D") || sqlState.startsWith("3F") || sqlState.startsWith("P0002") || sqlState.startsWith("42703") || sqlState.startsWith("42P01")) //Invalid catalog/schema name / no_data_found
            throw new NotFoundException(sqlState, operation, details, e);
        else if (sqlState.startsWith("42")) //Syntax error or access rule violation
            throw new SyntaxException(sqlState, operation, details, e);
        if (e instanceof SQLTimeoutException || sqlState.startsWith("08"))  //Connection exceptions
            throw new ConnectionException(sqlState, operation, details, e);
        else if (sqlState.startsWith("28") || sqlState.startsWith("42501")) //Invalid authorization specification
            throw new PrivilegeException(sqlState, operation, details, e);
        else if (sqlState.startsWith("999"))
            throw new TypeCastException(sqlState, operation, details, e);
        else if (sqlState.startsWith("22")) //Data exception
            throw new MalformedParameterException(sqlState, operation, details, e);
        else if (sqlState.startsWith("P0001"))  //raise_exception
            throw new ConstraintViolatedException(sqlState, operation, details, e);
        else
            throw new DbException(sqlState, operation, details, e);
    }
}
