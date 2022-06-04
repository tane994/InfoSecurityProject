package it.unibz.emails.client;

import it.unibz.emails.client.encryption.HashAlgorithm;
import it.unibz.emails.client.encryption.RSA;
import it.unibz.emails.entities.Keys;
import it.unibz.emails.entities.Mail;
import it.unibz.emails.entities.MailList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/navigation")
public class Navigation extends AuthenticatedServlet {
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String content = "";
        String search = parameters.get("search");

        if (search==null || search.isBlank()) search = "";

        if (parameters.get("newMail") != null)
            content = getHtmlForNewMail((String)request.getSession().getAttribute("csrf"));
        else if (parameters.get("inbox") != null)
            content = getHtmlForInbox(search);
        else if (parameters.get("sent") != null)
            content = getHtmlForSent(search);

        request.setAttribute("content", content);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    private String getHtmlForNewMail(String csrfToken) {
        return
            "<form class='flex-col submit-email section' action='send' method='post' onsubmit=\"getPrivkey('"+email+"');\">"
            + "<h3>New Mail</h3>"
            + "<input class='single-row-input' type='email' name='receiver' placeholder='Receiver' minlength='4' required>"
            + "<input class='single-row-input' type='text' name='subject' placeholder='Subject' minlength='4' required>"
            + "<textarea name='body' placeholder='Body' wrap='hard' minlength='4' rows='10' required></textarea></div>"
            + "<div><label for='sign'>Digitally sign: </label><input type='checkbox' name='sign' id='sign' value='yes'></div>"
            + "<input type='hidden' name='privkey' class='privkey'>"
            + "<input type='hidden' name='csrf' value='"+csrfToken+"'>"
            + "<input type='submit' name='sent' value='Send'>"
            + "</form>"
            + "";
    }

    private String getHtmlForInbox(String search) {
        return "<div class='section'><h3>Inbox</h3>" + getHtmlForSearchBox(search) +
                getMails("inbox",search).stream().map(mail -> {
                    String decryptedBody = decryptBody(mail.getBody());
                    return getHtmlForMail("From", mail.getSender(), mail.getStringTimestamp(), mail.getSubject(),
                            decryptedBody, getSignatureStatus(mail,decryptedBody));
                }).collect(Collectors.joining("")) +
                "</div>";
    }

    private String decryptBody(String ciphertext) {
        Keys receiverKeys = ServerRequest.to("/keys").with("token", parameters.get("token")).get(Keys.class);
        return RSA.decrypt(ciphertext, getPrivkey(), receiverKeys.getExponent());
    }
    private String getSignatureStatus(Mail mail, String decryptedBody) {
        String out = "not present";

        if (!mail.getDigitalSignature().isBlank()) {
            Keys senderKeys = ServerRequest.to("/keys").with("token", parameters.get("token")).with("user", mail.getSender()).get(Keys.class);
            String decryptedSignature = RSA.decrypt(mail.getDigitalSignature(), senderKeys.getPubKey(), senderKeys.getExponent());

            String thisHash = new HashAlgorithm("SHA-256").hash(decryptedBody);
            if (thisHash.equals(decryptedSignature)) out = "matches";
            else out = "doesn't match";
        }
        return out;
    }

    private String getHtmlForSent(String search) {
        return "<div class='section'><h3>Sent</h3>" + getHtmlForSearchBox(search) +
                getMails("sent",search).stream().map(mail ->
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

    private String getHtmlForSearchBox(String search) {
        String curAction = parameters.get("inbox")!=null ? "inbox" : "sent";
        String ret = "<form action='navigation' method='post' onsubmit=\"getPrivkey('"+email+"');\">";
        if(!search.isBlank()) ret += "<div>You searched for: "+ search +"</div>";
        ret += "<input id='newmail' class='green-input' type='text' placeholder='Search...' name='search'></input>" +
                "<input type='hidden' name='privkey' class='privkey'>" +
                "<input type='hidden' name='"+curAction+"'></input></form>";
        return ret;
    }

    private List<Mail> getMails(String type, String search) {
        return ServerRequest.to("/"+type)
                .with("token", parameters.get("token"))
                .with("search", search)
                .get(MailList.class).getMails();
    }
}
