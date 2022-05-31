package it.unibz.emails.server.persistence.entities;

import it.unibz.emails.client.encryption.RSA;
import it.unibz.emails.server.persistence.Db;
import it.unibz.emails.server.persistence.Password;
import it.unibz.emails.server.persistence.Query;

public class User {
    private final String name;
    private final String surname;
    private final String password;
    private final String email;
    private final Integer pubkey;
    private final Integer exponent;

    public static User get(String email) {
        return new Query<>(User.class,
                "SELECT name,surname,password,email,pubkey,exponent FROM users " +
                "WHERE email=?",
                 email
        ).run().stream().findFirst().orElse(null);
    }

    public static void set(String name, String surname, String email, String password, Integer pubkey, Integer exponent) {
        String encryptedPassword = Password.encrypt(password);
        Db.getInstance().insert(
        "INSERT INTO users (name, surname, email, password, pubkey, exponent) "
                + "VALUES (?, ?, ?, ?, ?, ?)", name, surname, email, encryptedPassword, pubkey, exponent);
    }

    public User(String name, String surname, String password, String email, Integer pubkey, Integer exponent) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.pubkey = pubkey;
        this.exponent = exponent;
    }

    public String encrypt(String plainText) {
        return RSA.encrypt(plainText, pubkey, exponent);
    }

    public String decrypt(String ciphertext) {
        return RSA.decrypt(ciphertext, pubkey, exponent);   //TODO
    }


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPubkey() {
        return pubkey;
    }

    public Integer getExponent() {
        return exponent;
    }
}
