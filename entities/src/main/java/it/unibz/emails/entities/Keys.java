package it.unibz.emails.entities;

import it.unibz.emails.entities.persistence.Db;
import it.unibz.emails.entities.persistence.Query;

public class Keys {
    private final Integer pubKey;
    private final Integer exponent;

    public static Keys get(String email) {
        return new Query<>(Keys.class,
                "SELECT pubkey,exponent FROM keys WHERE email=?", email)
                .run().stream().findFirst().orElse(null);
    }

    public static void set(String email, Integer pubKey, Integer exponent) {
        Db db = Db.getInstance();
        boolean alreadySet = !db.select("SELECT * FROM keys WHERE email=?", email).isEmpty();
        if (alreadySet) throw new RuntimeException("Keys already set");

        db.update("INSERT INTO keys(email,pubkey,exponent) VALUES (?,?,?)", email, pubKey, exponent);
    }

    public Keys(Integer pubKey, Integer exponent) {
        this.pubKey = pubKey;
        this.exponent = exponent;
    }

    public Integer getPubKey() {
        return pubKey;
    }

    public Integer getExponent() {
        return exponent;
    }
}
