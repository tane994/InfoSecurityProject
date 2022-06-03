package it.unibz.emails.server.persistence;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Password {
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean areSame(String hash1, String hash2) {
        return BCrypt.checkpw(hash1, hash2);
    }

    public static String randomToken() {
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] data = new byte[16];
            secureRandom.nextBytes(data);

            return Base64.getEncoder().encodeToString(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
