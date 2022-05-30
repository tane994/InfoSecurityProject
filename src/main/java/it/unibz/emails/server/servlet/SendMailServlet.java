package it.unibz.emails.server.servlet;

import it.unibz.emails.server.persistence.exceptions.ConstraintViolatedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

@WebServlet("/send")
public class SendMailServlet extends BaseServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request,response);
		response.setContentType("text/html");
		
		String sender = request.getParameter("email").replace("'", "''");
		String receiver = request.getParameter("receiver").replace("'", "''");
		String subject = request.getParameter("subject").replace("'", "''");
		String body = request.getParameter("body").replace("'", "''");
		String csrf = request.getParameter("csrf").replace("'", "''");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		String cookieContent = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals("X-CRSF"))
				.map(cookie -> cookie.getValue())
				.findFirst()
				.orElse("");
		if (!csrf.equals(cookieContent)) {
			request.setAttribute("email", sender);
			UserError.handle(request,response,"CRSF check failed", "/home.jsp");
		} else {
			/*
			1) Get pub of receiver
			2) encrypt body
			3) Check if need to digitally sign and do so with priv of sender
			4) send (sent emails are now unreadable)
			(should be done in "client")

			===INBOX===
			1) Check if need to digitally sign
			2) decrypt body: get priv key
			 */
			try {
				db.insert("INSERT INTO mail (sender, receiver, subject, body, time) "
								+ "VALUES (?, ?, ?, ?, ?)",
						sender, receiver, subject, body, timestamp);
			} catch (ConstraintViolatedException e) {
				request.setAttribute("email", sender);
				UserError.handle(request,response,"User not found","/home.jsp");
			}

			request.setAttribute("email", sender);
			request.getRequestDispatcher("home.jsp").forward(request, response);
		}
	}
}
