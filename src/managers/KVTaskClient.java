package managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class KVTaskClient {
    private URL url;
    private HttpClient client;
    private String API_TOKEN;

    public KVTaskClient(URL url, String apiToken) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        this.API_TOKEN = createAPI_TOKEN(apiToken);
    }

    String createAPI_TOKEN(String apiToken) {
        System.out.println("/createAPI_TOKEN ");
        if (apiToken.equals("Отсутствует")) {
            try {
                URI uri = URI.create(this.url + "/register");
                HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("Теперь Ваш API_TOKEN = " + response.body());
                    return response.body();
                } else {
                    System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                }
            } catch (IOException | InterruptedException exception) {
                System.out.println("Во время выполнения произошла ошибка.\n" +
                        "Проверьте, пожалуйста, адрес и повторите попытку");
            }
        }
        return this.API_TOKEN = apiToken;
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    void put(String key, String json) { //Сохранение состояния менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + this.API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Состояние мменеджера задач успешно сохранено");
            } else {
                System.out.println("Что-то пошло не так.Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException exception) {
            System.out.println("Во время выполнения произошла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    List<JsonObject> load(String key) { //Возвращение состояния менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + this.API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().length() < 1) {
                    System.out.println("Данные отсутствуют");
                    return new ArrayList<>();
                }
                JsonElement jsonElement = JsonParser.parseString(response.body());
                if (!jsonElement.isJsonObject()) {
                    List<JsonObject> listObjects = new ArrayList<>();

                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (jsonArray.get(i).isJsonObject()) {
                            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                            listObjects.add(jsonObject);
                        } else {
                            JsonArray internalJsonArray = (JsonArray) jsonArray.get(i);
                            for (int j = 0; j < internalJsonArray.size(); j++) {
                                JsonObject jsonObject = (JsonObject) internalJsonArray.get(j);
                                listObjects.add(jsonObject);
                            }
                        }
                    }
                    return listObjects;
                } else {
                    System.out.println("Запрашиваемый список для загрузки пуст.");
                }
            } else {
                System.out.println("Что-то пошло не так.Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            System.out.println("Во время выполнения произошла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            exception.printStackTrace();
        }
        return null;
    }
}


