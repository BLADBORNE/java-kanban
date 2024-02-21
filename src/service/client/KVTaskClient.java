package service.client;

import service.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final String URL = "http://localhost:8078";
    private String apiKey;
    private HttpClient client;

    public KVTaskClient() throws ManagerSaveException {
        this.client = HttpClient.newHttpClient();
        this.apiKey = generateApiKey();
    }

    private String generateApiKey() throws ManagerSaveException {
        URI uri = URI.create(URL + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ManagerSaveException("Ошибка регистрации. Статус кода " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }
    }

    public void put(String key, String json) throws ManagerSaveException {
        URI uri = URI.create(URL + "/save/" + key + "?API_TOKEN=" + apiKey);
        System.out.println(uri);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }
    }

    public String load(String key) throws ManagerSaveException {
        URI uri = URI.create(URL + "/load/" + key + "?API_TOKEN=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }
    }
}
