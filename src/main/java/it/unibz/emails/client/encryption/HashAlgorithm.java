package it.unibz.emails.client.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashAlgorithm {
    private final String algorithm;

    public HashAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String hash(String plainText) {
        try {
            return toHexString(
                    MessageDigest.getInstance(algorithm).digest(plainText.getBytes())
            );
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
