package it.unibz.emails.client.encryption;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class RSA {
    private static int asciiOfExclamationMark = '!';

    private RSA() {}

    public static List<Integer> encryptToIntArray(String plainText, int key, int exponent) {
        BigInteger exp = BigInteger.valueOf(exponent);
        List<Integer> positions = Arrays
                .stream(plainText.split(""))
                .map(character -> getAlphabetPosition(character.charAt(0)))
                .toList();

        return positions.stream()
                .map(position -> BigInteger.valueOf(position).pow(key).mod(exp).intValue())
                .toList();
    }
    public static String encrypt(String plainText, int key, int exponent) {
        List<Integer> encrypted = encryptToIntArray(plainText, key, exponent);
        String csvList = encrypted.stream().map(Object::toString).collect(Collectors.joining(","));
        return Base64.getEncoder().encodeToString(csvList.getBytes());
    }

    private static int getAlphabetPosition(char character) {
        return ((int) character - asciiOfExclamationMark)+1;
    }

    public static String decrypt(String ciphertext, int key, int exponent) {
        byte[] decoded = Base64.getDecoder().decode(ciphertext);
        String csvList = new String(decoded);
        List<Integer> array = Arrays.stream(csvList.split(","))
                .map(elem -> Integer.valueOf(elem.trim()))
                .toList();

        return decryptFromIntArray(array, key, exponent);
    }

    public static String decryptFromIntArray(List<Integer> ciphertext, int key, int exponent) {
        BigInteger exp = BigInteger.valueOf(exponent);
        return ciphertext.stream()
                .map(encrypted -> BigInteger.valueOf(encrypted).pow(key).mod(exp).intValue())
                .map(position -> String.valueOf(getLetterFrom(position)))
                .collect(Collectors.joining());
    }

    private static char getLetterFrom(int ascii) {
        return (char) (asciiOfExclamationMark + ascii - 1);
    }
}
