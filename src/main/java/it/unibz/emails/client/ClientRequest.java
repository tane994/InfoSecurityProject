package it.unibz.emails.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientRequest {
    private final String path;
    private final Map<String,String> params;

    public ClientRequest(String path) {
        this.path = path;
        this.params = new HashMap<>();
    }

    public static ClientRequest to(String path) {
        return new ClientRequest(path);
    }

    public ClientRequest with(String key, String value) {
        params.put(key, value);
        return this;
    }

    public Map<String,String> send() {
        HttpClient client = HttpClient.newHttpClient();
        String form = params.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/security"+path))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        String response = "";
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return parse(response);
    }

    private static Map<String,String> parse(String text) {
        Map<String,String> result = new HashMap<>();

        Arrays.stream(text.split(",")).forEach(elem -> {
            String[] values = elem.split("=");
            assert values.length == 2;
            result.put(values[0].trim(), values[1].trim());
        });
        return result;
    }
}
