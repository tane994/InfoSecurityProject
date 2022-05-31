package it.unibz.emails.client.encryption;

public class RSAKeys {
    private final int p;
    private final int q;

    public static RSAKeys withRandomPrimes() {
        return new RSAKeys(Primes.getRandomPrime(), Primes.getRandomPrime());
    }

    public RSAKeys(int p, int q) {
        this.p = p;
        this.q = q;
    }

    public int n() {
        return p*q;
    }

    private int phi() {
        return ((p-1)*(q-1));
    }

    public int e() {
        int phi = phi();
        int e = 2;
        while(!coprime(e,phi) && e<phi)
            e++;

        return e;
    }
    private int gcd(int a, int b) {
        if (a == 0 || b == 0) return 0; // Everything divides 0
        if (a == b) return a;
        if (a > b) return gcd(a-b, b);
        else return gcd(a, b-a);
    }
    private boolean coprime(int a, int b) {
        return gcd(a,b) == 1;
    }

    public int d() {
        int phi = phi();
        int quo1 = phi;
        int quo2 = e();
        int remainder = 1;
        int result = 0;
        int d = 0;
        int pi2 = 0;
        int pi1 = 1;

        while (remainder != 0) {
            result = quo1 / quo2;
            remainder = quo1 % quo2;

            int r = pi2-(pi1*result);
            while (r<0) r+=phi;
            d = r%phi;

            quo1 = quo2;
            quo2 = remainder;
            pi2 = pi1;
            pi1 = d;
        }
        return pi2;
    }
}
