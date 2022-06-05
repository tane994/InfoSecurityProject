package it.unibz.emails.entities;

import it.unibz.emails.entities.persistence.Db;
import it.unibz.emails.entities.persistence.Password;
import it.unibz.emails.entities.persistence.Query;

public class User {
    private final String name;
    private final String surname;
    private final String password;
    private final String email;

    public static User get(String email, String password) {
        User user = get(email);
        if (user==null || !Password.areSame(password, user.password)) return null;
        else return user;
    }

    public static User get(String email) {
        return new Query<>(User.class,
            "SELECT name,surname,password,email " +
            "FROM users WHERE email=?", email)
            .run().stream().findFirst().orElse(null);
    }

    public static void set(String name, String surname, String email, String password) {
        String encryptedPassword = Password.encrypt(password);
        Db.getInstance().update(
        "INSERT INTO users (name, surname, email, password) "
                + "VALUES (?, ?, ?, ?)", name, surname, email, encryptedPassword);
    }

    public User(String name, String surname, String password, String email) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }
}
