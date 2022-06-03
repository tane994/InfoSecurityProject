package it.unibz.emails.server.persistence.entities;

import it.unibz.emails.client.encryption.RSA;
import it.unibz.emails.server.persistence.Db;
import it.unibz.emails.server.persistence.Query;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Mail {
    private final String sender;
    private final String receiver;
    private final String subject;
    private final Timestamp timestamp;
    private final String body;
    private final String digitalSignature;

    public static List<Mail> inboxFor(String email) {
        return query(email, "receiver");
    }

    public static List<Mail> sentFor(String email) {
        return query(email, "sender");
    }
    private static List<Mail> query(String email, String type) {
        return new Query<>(Mail.class,
                "SELECT sender,receiver,subject,timestamp,body,digital_signature FROM mails "
                        + "WHERE "+type+"=?"
                        + "ORDER BY timestamp DESC", email).run();
    }

    public static void send(User sender, User receiver, String subject, String plainBody, String digitalSignature) {
        String encryptedBody = receiver.encrypt(plainBody);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Db.getInstance().insert("INSERT INTO mails (sender, receiver, subject, body, timestamp, digital_signature) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                sender.getEmail(), receiver.getEmail(), subject, encryptedBody, timestamp, digitalSignature);
    }

    public Mail(String sender, String receiver, String subject, Timestamp timestamp, String body, String digitalSignature) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.timestamp = timestamp;
        this.body = body;
        this.digitalSignature = digitalSignature;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getStringTimestamp() {
        ZoneId cet = ZoneId.of("Europe/Rome");
        ZoneOffset offset = cet.getRules().getOffset(Instant.now());
        return getTimestamp().toLocalDateTime().atOffset(offset).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public String getBody() {
        return body;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public boolean areSignaturesSame(String other) {
        User sender = User.get(getSender());
        String plainSignature = RSA.decrypt(getDigitalSignature(), sender.getPubkey(), sender.getExponent());

        return plainSignature.equals(other);
    }
}
