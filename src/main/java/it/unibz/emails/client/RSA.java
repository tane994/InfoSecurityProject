package it.unibz.emails.client;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RSA {
    private final int e;
    private final int d;
    private final BigInteger n;

    public static RSA withRandomKeys() {
        return withKeys(Primes.getRandomPrime(), Primes.getRandomPrime());
    }

    public static RSA withKeys(int p, int q) {
        RSAKeys keys = new RSAKeys(p,q);
        return new RSA(keys.e(), keys.d(), keys.n());
    }

    public RSA(int e, int d, int n) {
        this.e = e;
        this.d = d;
        this.n = BigInteger.valueOf(n);
    }

    public List<Integer> encryptToIntArray(String plainText) {
        List<Integer> positions = Arrays
                .stream(plainText.split(""))
                .map(character -> getAlphabetPosition(character.toLowerCase().charAt(0)))
                .toList();

        return positions.stream()
                .map(position -> BigInteger.valueOf(position).pow(e).mod(n).intValue())
                .toList();
    }
    public String encrypt(String plainText) {
        return encryptToIntArray(plainText).toString();
    }

    private int getAlphabetPosition(char character) {
        int positionOfA = 'a';
        return ((int) character - positionOfA)+1;
    }

    public String decrypt(String ciphertext) {
        List<Integer> array = Arrays.stream(ciphertext.split(","))
                .map(elem -> Integer.valueOf(elem.trim()))
                .collect(Collectors.toList());

        return decryptFromIntArray(array);
    }

    public String decryptFromIntArray(List<Integer> ciphertext) {
        return ciphertext.stream()
                .map(encrypted -> BigInteger.valueOf(encrypted).pow(d).mod(n).intValue())
                .map(position -> String.valueOf(getLetterFrom(position)))
                .collect(Collectors.joining());
    }

    private char getLetterFrom(int ascii) {
        int positionOfA = 'a';
        return (char) (positionOfA + ascii - 1);
    }
}
