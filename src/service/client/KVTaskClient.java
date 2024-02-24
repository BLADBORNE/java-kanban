package service.client;

import service.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiKey;
    private HttpClient client;
    private final String url;

    public KVTaskClient(String url) throws ManagerSaveException {
        this.client = HttpClient.newHttpClient();
        this.url = url;
        this.apiKey = generateApiKey();
    }

    private String generateApiKey() throws ManagerSaveException {
        URI uri = URI.create(url + "/register");
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
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiKey);
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
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.body().equals("Извините, у нас нет данного ключа")) {
                return null;
            }
            if (response.body().replace("[]", "{}").equals("{}")) {
                return null;
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }
    }
}
