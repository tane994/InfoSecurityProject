package it.unibz.emails.server.servlet;

import it.unibz.emails.server.Headers;
import it.unibz.emails.server.persistence.Db;
import it.unibz.emails.server.persistence.Password;
import it.unibz.emails.server.persistence.exceptions.DbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseServlet extends HttpServlet {
    protected final Db db;
    protected final Map<String,String> parameters;

    protected BaseServlet() {
        this.db = Db.getInstance();
        this.parameters = new HashMap<>();
    }

    protected BaseServlet(String... parameters) {
        this();
        Arrays.stream(parameters).forEach(parameter -> this.parameters.put(parameter, null));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        setCsrfToken(request, response);

        try {
            sanitizeParameters(request);
            Headers.set(response);
            handle(request,response);
        } catch (UserException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher(e.redirectPath).forward(request, response);
        } catch (DbException e) {
            response.setStatus(500);
            response.getWriter().println("Database error");
            System.err.println(e.getMessage());
        }
    }

    private void setCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession().getAttribute("csrf") == null) {
            String token = Password.randomToken();
            request.getSession().setAttribute("csrf", token);

            Cookie csrf = new Cookie("X-CRSF", token);
            csrf.setHttpOnly(true);
            response.addCookie(csrf);
        }
    }

    private void sanitizeParameters(HttpServletRequest request) {
        parameters.clear();
        request.getParameterNames().asIterator().forEachRemaining((parameter) -> {
            parameters.put(parameter, Jsoup.clean(request.getParameter(parameter), Safelist.basic()));
        });
        if (parameters.values().stream().anyMatch(Objects::isNull))
            throw new UserException("Some parameters are missing. Expected " + parameters.keySet(), "/home.jsp");

        System.out.println("Received parameters: " + parameters);
    }

    protected abstract void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected String getEmail(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("email");
    }
}
