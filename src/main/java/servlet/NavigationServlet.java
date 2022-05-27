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
 * Servlet implementation class NavigationServlet
 */
@WebServlet("/NavigationServlet")
public class NavigationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = DbParams.init();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NavigationServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String email = request.getParameter("email").replace("'", "''");;
		String pwd = request.getParameter("password").replace("'", "''");;
		String search = request.getParameter("search").replace("'", "''");;

		if (request.getParameter("newMail") != null)
			request.setAttribute("content", getHtmlForNewMail(email, pwd));
		else if (request.getParameter("inbox") != null)
			request.setAttribute("content", getHtmlForInbox(email, search));
		else if (request.getParameter("sent") != null)
			request.setAttribute("content", getHtmlForSent(email));

		request.setAttribute("email", email);
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}

	private String getHtmlForInbox(String email, String search) {
		try (Statement st = conn.createStatement()) {
			ResultSet sqlRes = st.executeQuery(
					"SELECT * FROM mail "
							+ "WHERE receiver='" + email + "'"
							+ "ORDER BY time DESC"
			);

			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");

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
			return "ERROR IN FETCHING INBOX MAILS!";
		}
	}

	private String getHtmlForNewMail(String email, String pwd) {
		return
				"<form id=\"submitForm\" class=\"form-resize\" action=\"SendMailServlet\" method=\"post\">\r\n"
						+ "		<input type=\"hidden\" name=\"email\" value=\""+email+"\">\r\n"
						+ "		<input type=\"hidden\" name=\"password\" value=\""+pwd+"\">\r\n"
						+ "		<input class=\"single-row-input\" type=\"email\" name=\"receiver\" placeholder=\"Receiver\" required>\r\n"
						+ "		<input class=\"single-row-input\" type=\"text\"  name=\"subject\" placeholder=\"Subject\" required>\r\n"
						+ "		<textarea class=\"textarea-input\" name=\"body\" placeholder=\"Body\" wrap=\"hard\" required></textarea>\r\n"
						+ "		<input type=\"submit\" name=\"sent\" value=\"Send\">\r\n"
						+ "	</form>";
	}

	private String getHtmlForSent(String email) {
		try (Statement st = conn.createStatement()) {
			ResultSet sqlRes = st.executeQuery(
					"SELECT * FROM mail "
							+ "WHERE sender='" + email + "'"
							+ "ORDER BY time DESC"
			);

			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");

			while (sqlRes.next()) {
				output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				output.append("TO:&emsp;" + sqlRes.getString(2) + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
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
			return "ERROR IN FETCHING INBOX MAILS!";
		}
	}
}