package it.unibz.emails.client;

import java.util.List;

public class App {
    public static void main(String[] args) {
        RSA rsa = RSA.withKeys(17,23);

        List<Integer> cipherText = rsa.encryptToIntArray("security");
        String plainText = rsa.decryptFromIntArray(cipherText);

        System.out.println("Ciphertext: " + cipherText);
        System.out.println("Plaintext: " + plainText);
    }
}
