package it.unibz.emails.server.servlet;

import it.unibz.emails.client.ClientRequest;
import it.unibz.emails.client.encryption.HashAlgorithm;
import it.unibz.emails.server.persistence.entities.Mail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/navigation")
public class NavigationServlet extends AuthenticatedServlet {
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String content = "";
        if (request.getParameter("newMail") != null)
            content = getHtmlForNewMail((String)request.getSession().getAttribute("csrf"));
        else if (request.getParameter("inbox") != null)
            content = getHtmlForInbox(getEmail(request));
        else if (request.getParameter("sent") != null)
            content = getHtmlForSent(getEmail(request));

        request.setAttribute("content", content);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    private String getHtmlForInbox(String receiver) {
        return "<div class='section'><h3>Inbox</h3>" +
                Mail.inboxFor(receiver).stream().map(mail -> {
                    String decryptedBody = decryptBody(mail.getBody(), receiver);
                    return getHtmlForMail("From", mail.getSender(), mail.getStringTimestamp(), mail.getSubject(),
                            decryptedBody, getSignatureStatus(mail,decryptedBody));
                }).collect(Collectors.joining("")) +
                "</div>";
    }
    private String decryptBody(String ciphertext, String email) {
        return ClientRequest.to("/privKey")
                .with("operation", "decrypt")
                .with("text", ciphertext)
                .with("email", email)
                .send().get("result");
    }
    private String getSignatureStatus(Mail mail, String decryptedBody) {
        String out = "not present";

        if (mail.getDigitalSignature() != null) {
            String thisHash = new HashAlgorithm("SHA-256").hash(decryptedBody);
            if (mail.areSignaturesSame(thisHash)) out = "matches";
            else out = "doesn't match";
        }
        return out;
    }

    private String getHtmlForSent(String sender) {
        return "<div class='section'><h3>Sent</h3>" +
                Mail.sentFor(sender).stream().map(mail ->
                    getHtmlForMail("To", mail.getReceiver(), mail.getStringTimestamp(), mail.getSubject(), mail.getBody(), null)
                ).collect(Collectors.joining("")) +
                "</div>";
    }

    private String getHtmlForMail(String action, String email, String timestamp, String subject, String body, String signatureStatus) {
        String ret = "<div class='email-message'>" +
                       "<div class='grid'>" +
                         "<div>" + action + ":</div><div>" + email + "</div>" +
                         "<div>Date:</div><div>" + timestamp + "</div>";
                         if(signatureStatus!=null) ret += "<div>Signature:</div><div>" + signatureStatus + "</div>";
       return ret +    "<div class='subject'>Subject:</div><div class='subject'>" + subject + "</div>" +
                       "</div>" +
                       "<p>" + body + "</p>" +
                       "</div>";
    }

    private String getHtmlForNewMail(String csrfToken) {
        return
            "<form class='flex-col submit-email section' action='send' method='post'>"
            + "<h3>New Mail</h3>"
            + "<input class='single-row-input' type='email' name='receiver' placeholder='Receiver' minlength='4' required>"
            + "<input class='single-row-input' type='text' name='subject' placeholder='Subject' minlength='4' required>"
            + "<textarea name='body' placeholder='Body' wrap='hard' minlength='4' rows='10' required></textarea>"
            + "<div><label for='sign'>Digitally sign: </label><input type='checkbox' name='sign' id='sign' value='yes'></div>"
            + "<input type='hidden' name='csrf' value='"+csrfToken+"'>"
            + "<input type='submit' name='sent' value='Send'>"
            + "</form>";
    }
}
