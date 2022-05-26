package main.java.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* Servlet implementation class HelloWorldServlet
 */
@WebServlet("/AlertServlet")
public class AlertServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Connection conn = DbParams.init();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlertServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String search = request.getParameter("search").replace("'", "''");
        ;
        request.setAttribute("content", fetchEmails(search));

    }
    private String fetchEmails(String search){
        try (Statement st = conn.createStatement()) {
            ResultSet sqlRes = st.executeQuery(
                    "SELECT * FROM mail where subject LIKE '%" + search + "ORDER BY [time] DESC"
            );

            StringBuilder output = new StringBuilder();

            while (sqlRes.next()) {
                output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
                output.append("FROM:&emsp;" + sqlRes.getString(1) + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
                output.append("</span>");
                output.append("<br><b>" + sqlRes.getString(3) + "</b>\r\n");
                output.append("<br>" + sqlRes.getString(4));
                output.append("</div>\r\n");

                output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
            }

            output.append("</div>");
            return output.toString();



        } catch (SQLException e) {
            e.printStackTrace();
            return("ERROR IN FETCHING SEARCHED EMAILS");

        }
    }
    }
