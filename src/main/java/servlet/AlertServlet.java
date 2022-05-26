package main.java.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/* Servlet implementation class HelloWorldServlet
 */
@WebServlet("/AlertServlet")
public class AlertServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlertServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alert = "<script>alert(1)</script>";
        response.setContentType(alert);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
