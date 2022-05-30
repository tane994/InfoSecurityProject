package it.unibz.emails.server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/navigation")
public class NavigationServlet extends CSRFServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		response.setContentType("text/html");
		
		String email = request.getParameter("email");
		String pwd = request.getParameter("password");

		if (request.getParameter("newMail") != null)
			request.setAttribute("content", getHtmlForNewMail(email, pwd));
		else if (request.getParameter("inbox") != null)
			request.setAttribute("content", getHtmlForInbox(email));
		else if (request.getParameter("sent") != null)
			request.setAttribute("content", getHtmlForSent(email));
		
		request.setAttribute("email", email);
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}

	private String getHtmlForInbox(String email) {
		List<Map<String,Object>> inbox = db.select(
				"SELECT * FROM mail "
				+ "WHERE receiver=?"
				+ "ORDER BY time DESC", email);
			
		StringBuilder output = new StringBuilder();
		output.append("<div>\r\n");

		inbox.forEach((mail) -> {
			output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
			output.append("FROM:&emsp;" + mail.get("sender") + "&emsp;&emsp;AT:&emsp;" + mail.get("time"));
			output.append("</span>");
			output.append("<br><b>" + mail.get("subject") + "</b>\r\n");
			output.append("<br>" + mail.get("body"));
			output.append("</div>\r\n");

			output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
		});

		output.append("</div>");
		return output.toString();
	}
	
	private String getHtmlForNewMail(String email, String pwd) {
		return
			"<form id=\"submitForm\" class=\"form-resize\" action=\"send\" method=\"post\">\r\n"
			+ "		<input type=\"hidden\" name=\"email\" value=\""+email+"\">\r\n"
			+ "		<input type=\"hidden\" name=\"password\" value=\""+pwd+"\">\r\n"
			+ "		<input class=\"single-row-input\" type=\"email\" name=\"receiver\" placeholder=\"Receiver\" required>\r\n"
			+ "		<input class=\"single-row-input\" type=\"text\"  name=\"subject\" placeholder=\"Subject\" required>\r\n"
			+ "		<textarea class=\"textarea-input\" name=\"body\" placeholder=\"Body\" wrap=\"hard\" required></textarea>\r\n"
			+ "		<input type=\"hidden\" name=\"csrf\" value=\""+csrfToken+"\">\r\n"
			+ "		<input type=\"submit\" name=\"sent\" value=\"Send\">\r\n"
			+ "	</form>";
	}
	
	private String getHtmlForSent(String email) {
		List<Map<String,Object>> sent = db.select(
				"SELECT * FROM mail "
						+ "WHERE sender=?"
						+ "ORDER BY time DESC", email);
			
			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");

			sent.forEach((mail) -> {
				output.append("<div class='emails' style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				output.append("TO:&emsp;" + mail.get("receiver") + "&emsp;&emsp;AT:&emsp;" + mail.get("time"));
				output.append("</span>");
				output.append("<br><b>" + mail.get("subject") + "</b>\r\n");
				output.append("<br>" + mail.get("body"));
				output.append("</div>\r\n");
				
				output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
			});
			
			output.append("</div>");
			return output.toString();
	}
}
