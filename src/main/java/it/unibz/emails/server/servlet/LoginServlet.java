package it.unibz.emails.server.servlet;

import it.unibz.emails.server.persistence.Password;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request,response);
		response.setContentType("text/html");

		String email = request.getParameter("email");
		String pwd = request.getParameter("password");

		List<Map<String,Object>> res = db.select("SELECT * FROM users WHERE email=?", email);
		if (!res.isEmpty() && Password.compare(pwd, (String) res.get(0).get("password"))) {
			request.setAttribute("email", res.get(0).get("email"));
			request.setAttribute("password", res.get(0).get("password"));

			System.out.println("Login succeeded!");
			request.setAttribute("content", "");
			request.getRequestDispatcher("/home.jsp").forward(request, response);
		} else {
			UserError.handle(request,response,"Login failed", "/login.jsp");
		}
	}
}
