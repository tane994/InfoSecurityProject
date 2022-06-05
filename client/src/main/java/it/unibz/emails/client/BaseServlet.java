package it.unibz.emails.client;

import it.unibz.emails.entities.persistence.Password;
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
    protected final Map<String,String> parameters;
    protected String email;

    protected BaseServlet() {
        this.parameters = new HashMap<>();
        this.email = "";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("text/html");
            sanitizeParameters(request);
            email = (String) request.getSession().getAttribute("email");
            parameters.put("token", request.getSession().getId());
            setCsrfToken(request, response);
            Headers.set(response);

            handle(request, response);
        } catch (UserException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/home.jsp").forward(request, response);
        }
    }

    public static void setCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession().getAttribute("csrf") == null) {
            String token = Password.randomToken();
            request.getSession().setAttribute("csrf", token);

            Cookie csrf = new Cookie("X-CSRF", token);
            csrf.setHttpOnly(true);
            response.addCookie(csrf);
        }
    }

    protected void checkCsrf(HttpServletRequest request) {
        String cookieContent = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("X-CSRF"))
                .map(Cookie::getValue)
                .findFirst().orElse("");

        if ((!cookieContent.equals(request.getSession().getAttribute("csrf")) ||
             !cookieContent.equals(parameters.get("csrf")))
        )
            throw new UserException("CSRF check failed");
    }

    protected void deleteCookie(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) cookies = new Cookie[]{};
        Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals("X-CSRF"))
            .forEach(cookie -> {
                cookie.setValue("");
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            });
    }

    private void sanitizeParameters(HttpServletRequest request) {
        parameters.clear();
        request.getParameterNames().asIterator().forEachRemaining((parameter) -> {
            parameters.put(parameter, Jsoup.clean(request.getParameter(parameter), Safelist.basic()));
        });
        if (parameters.values().stream().anyMatch(Objects::isNull))
            throw new RuntimeException("Some parameters are missing. Expected " + parameters.keySet());
    }

    protected abstract void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected Integer getPrivkey() {
        String privKey = parameters.get("privkey");
        if (privKey == null || privKey.isBlank())
            throw new UserException("Private key not found in the browser local storage. You probably did the registration using another browser");
        else return Integer.valueOf(privKey);
    }
}
