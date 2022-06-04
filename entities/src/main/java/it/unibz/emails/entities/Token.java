package it.unibz.emails.entities;

import it.unibz.emails.entities.persistence.Db;
import it.unibz.emails.entities.persistence.Query;

import java.util.List;

public class Token {
    private final String token;

    public static List<Token> getForEmail(String email) {
        return new Query<>(Token.class,
                "SELECT token FROM tokens WHERE email=?", email).run();
    }

    public static Token getEmailFrom(String token) {
        return new Query<>(Token.class,
                "SELECT email FROM tokens WHERE token=? LIMIT 1", token)
                .run().stream().findFirst().orElse(null);
    }

    public static void delete(String token) {
        Db.getInstance().update("DELETE FROM tokens WHERE token=?", token);
    }

    public static void set(String email, String token) {
        Token existing = getEmailFrom(token);
        if (existing == null)
            Db.getInstance().update("INSERT INTO tokens(email,token) VALUES(?,?)", email, token);
    }

    public Token(String token) {
        this.token = token;
    }

    public String get() {
        return token;
    }
}
