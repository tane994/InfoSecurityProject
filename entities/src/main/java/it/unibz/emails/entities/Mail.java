package it.unibz.emails.entities;

import it.unibz.emails.entities.persistence.Db;
import it.unibz.emails.entities.persistence.Query;

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

    public static List<Mail> inboxFor(String email, String search) {
        return query(email, "receiver", search);
    }

    public static List<Mail> sentFor(String email, String search) {
        return query(email, "sender", search);
    }
    private static List<Mail> query(String email, String type, String search) {
        if (search==null || search.isBlank()) search = "";
        return new Query<>(Mail.class,
                "SELECT sender,receiver,subject,timestamp,body,digital_signature FROM mails "
                        + "WHERE "+type+"=? AND subject LIKE CONCAT('%',?,'%') "
                        + "ORDER BY timestamp DESC", email, search).run();
    }

    public static void send(User sender, User receiver, String subject, String encryptedBody, String digitalSignature) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Db.getInstance().update("INSERT INTO mails (sender, receiver, subject, body, timestamp, digital_signature) "
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
        if (digitalSignature != null) return digitalSignature;
        else return "";
    }
}
