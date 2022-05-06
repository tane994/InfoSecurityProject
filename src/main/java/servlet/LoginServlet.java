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

/**
 * Servlet implementation class HelloWorldServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = DbParams.init();

	/**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String email = request.getParameter("email");
		String pwd = request.getParameter("password");

		try (Statement st = conn.createStatement()) {
			String test = "SELECT * "
					+ "FROM users "
					+ "WHERE email='" + email + "' "
					+ "AND password='" + pwd + "'";
			System.out.println(test);
			ResultSet sqlRes = st.executeQuery(
				"SELECT * "
				+ "FROM users "
				+ "WHERE email='" + email + "' "
					+ "AND password='" + pwd + "'"
			);

			if (sqlRes.next()) {
				request.setAttribute("email", sqlRes.getString(3));
				request.setAttribute("password", sqlRes.getString(4));

				System.out.println("Login succeeded!");
				request.setAttribute("content", "");
				request.getRequestDispatcher("home.jsp").forward(request, response);


			} else {
				System.out.println("Login failed!");
				request.getRequestDispatcher("login.html").forward(request, response);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			request.getRequestDispatcher("login.html").forward(request, response);
		}
	}
}
