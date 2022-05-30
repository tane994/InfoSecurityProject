package it.unibz.emails.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Primes {
    private static List<Integer> primes = new ArrayList<>();

    public static int getRandomPrime() {
        if (primes.isEmpty()) populateList();

        return primes.get(ThreadLocalRandom.current().nextInt(primes.size()));
    }

    private static void populateList() {
        primes = primeNumbersTill(1000);
    }
    private static List<Integer> primeNumbersTill(int n) {
        return IntStream.rangeClosed(2, n)
                .filter(Primes::isPrime).boxed()
                .toList();
    }
    private static boolean isPrime(int x) {
        return IntStream.rangeClosed(2, (int) (Math.sqrt(x)))
                .allMatch(n -> x % n != 0);
    }
}
