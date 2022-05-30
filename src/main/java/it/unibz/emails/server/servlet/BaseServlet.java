package it.unibz.emails.server.servlet;

import it.unibz.emails.server.persistence.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;

public class BaseServlet extends HttpServlet {
    protected Db db = Db.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getAttributeNames().asIterator().forEachRemaining((att) -> {
            request.setAttribute(att, Jsoup.clean((String)request.getAttribute(att), Safelist.basic()));
        });
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
    }
}
