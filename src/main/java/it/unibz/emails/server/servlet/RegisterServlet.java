package it.unibz.emails.server.servlet;

import it.unibz.emails.server.persistence.Password;
import it.unibz.emails.client.Primes;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		response.setContentType("text/html");
		
		// The replacement escapes apostrophe special character in order to store it in SQL
		String name = request.getParameter("name").replace("'", "''");
		String surname = request.getParameter("surname").replace("'", "''");
		String email = request.getParameter("email").replace("'", "''");
		String pwd = Password.encrypt(
				request.getParameter("password").replace("'", "''"));

		boolean alreadyRegistered = !db.select("SELECT email FROM users WHERE email=?", email).isEmpty();
		if (alreadyRegistered) {
			UserError.handle(request, response, "Email already present", "/register.jsp");

		} else {
			db.insert(
				"INSERT INTO users (name, surname, email, password, pubkey, privkey) "
				+ "VALUES (?, ?, ?, ?, ?, ?)",name, surname, email, pwd, Primes.getRandomPrime(), Primes.getRandomPrime());

			request.setAttribute("email", email);
			request.setAttribute("password", pwd);

			System.out.println("Registration succeeded!");
			request.getRequestDispatcher("/home.jsp").forward(request, response);
		}
	}
}
