package it.unibz.emails.client;

import com.google.gson.Gson;
import it.unibz.emails.entities.ErrorMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerRequest {
    private final String path;
    private final Map<String,String> params;
    private String token;
    private String response;

    private ServerRequest(String path) {
        this.path = path;
        this.params = new HashMap<>();
        this.token = "";
        this.response = "";
    }

    public static ServerRequest to(String path) {
        return new ServerRequest(path);
    }

    public ServerRequest with(String key, String value) {
        if (key.equals("token")) token = value;
        else params.put(key, value);
        return this;
    }
    public ServerRequest with(Map<String,String> params) {
        params.forEach(this::with);
        return this;
    }

    public void post() {
        send("POST");
    }

    public <R> R get(Class<R> returnClass) {
        send("GET");
        return new Gson().fromJson(response, returnClass);
    }

    public void send(String method) {
        HttpClient client = HttpClient.newHttpClient();
        String form = params.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        String url = "http://localhost:8080/securityApi"+path;
        if (method.equals("GET")) {
            url += "?"+form;
            form = "";
        }

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .setHeader("X-Token", token);

        if (method.equals("POST")) request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        request.method(method, HttpRequest.BodyPublishers.ofString(form));

        try {
            HttpResponse<String> resp = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
            response = resp.body();
            if (resp.statusCode() >= 400)
                throw new UserException(new Gson().fromJson(resp.body(), ErrorMessage.class).getMessage());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
