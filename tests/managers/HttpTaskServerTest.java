package managers;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private final Gson gson = Managers.getGson();
    FileBackedTasksManager manager;
    HttpTaskServer httpTaskServer;
    private Task task;
    private Subtask subtask;
    private Epic epic;


    @BeforeEach
    void init() throws IOException {
        this.manager = Managers.getFileBacked("test.txt");
        this.httpTaskServer = new HttpTaskServer(manager);

        this.task = new Task("Test Task", "Test Task description", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        this.epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(30));
        this.subtask = new Subtask("Test Subtask", "Test Subtask description", Status.NEW,
                LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(30));

        httpTaskServer.start();
    }

    @AfterEach
    void stop() {
        manager.clearTask();
        httpTaskServer.stop();
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {  //Проверка создания и получения задачи
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании задачи");

        task.setId(1);

        URI urlWithId = URI.create("http://localhost:8080/tasks/task/1");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        Task taskGET = gson.fromJson(responseGET.body(), Task.class);

        assertEquals(200, responseGET.statusCode(), "Ошибка при получении задачи");
        assertNotNull(taskGET, "Не удалось получить задау");
        assertEquals(task, taskGET, "Задачи не совпадают");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {  //Проверка обновления задачи
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task");
        String json1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании задачи");

        task.setId(1);

        URI urlWithId = URI.create("http://localhost:8080/tasks/task/1");

        Task task2 = new Task("updatedTask", "updatedTask description",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(30));
        String json2 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(urlWithId).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Ошибка при обновлении задачи");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        Task updatedTask = gson.fromJson(responseGET.body(), Task.class);

        assertNotNull(updatedTask, "Не удалось получить обновлённую задачу");
        assertNotEquals(task, updatedTask, "Задача не обновилась");
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {  // Проверка удаления задачи
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task");
        String json1 = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании задачи");

        URI urlWithId = URI.create("http://localhost:8080/tasks/task/1");

        HttpRequest request2 = HttpRequest.newBuilder().uri(urlWithId).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Ошибка при удалении задачи");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        assertNotEquals(200, responseGET.statusCode(), "Задача не удалилась");
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {  //Проверка создания и получения эпика
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании эпика");

        epic.setId(1);

        URI urlWithId = URI.create("http://localhost:8080/tasks/epic/1");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        Epic epicGET = gson.fromJson(responseGET.body(), Epic.class);

        assertEquals(200, responseGET.statusCode(), "Ошибка при получении эпика");
        assertNotNull(epicGET, "Не удалось получить эпик");
        assertEquals(epic, epicGET, "Эпики не совпадают");
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {  //Проверка обновления эпика
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании эпика");

        epic.setId(1);

        URI urlWithId = URI.create("http://localhost:8080/tasks/epic/1");

        Epic epic2 = new Epic("updatedEpic", "updatedEpic description",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(30));

        String json2 = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(urlWithId).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Ошибка при обновлении эпика");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        Epic updatedEpic = gson.fromJson(responseGET.body(), Epic.class);

        assertNotNull(updatedEpic, "Не удалось получить обновлённый эпик");
        assertNotEquals(epic, updatedEpic, "Эпик не обновился");
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {  // Проверка удаления эпика
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании эпика");

        URI urlWithId = URI.create("http://localhost:8080/tasks/epic/1");

        HttpRequest request2 = HttpRequest.newBuilder().uri(urlWithId).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Ошибка при удалении эпика");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        assertNotEquals(200, responseGET.statusCode(), "Эпик не удалился");
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {  //Проверка создания и получения подзадачи
        HttpClient client = HttpClient.newHttpClient();

        URI urlForEpic = URI.create("http://localhost:8080/tasks/epic");
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlForEpic).POST(bodyEpic).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpic.statusCode(), "Ошибка при создании эпика");

        subtask.setEpicId(1);

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/1");
        String json = gson.toJson(subtask);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании подзадачи");

        subtask.setId(2);

        URI urlWithId = URI.create("http://localhost:8080/tasks/subtask/2");
        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());


        Subtask subtaskGET = gson.fromJson(responseGET.body(), Subtask.class);
        assertEquals(200, responseGET.statusCode(), "Ошибка при получении подзадачи");
        assertNotNull(subtaskGET, "Не удалось получить подзадачу");
        assertEquals(subtask, subtaskGET, "Подзадачи не совпадают");
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {  //Проверка обновления подзадачи
        HttpClient client = HttpClient.newHttpClient();

        URI urlForEpic = URI.create("http://localhost:8080/tasks/epic");
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlForEpic).POST(bodyEpic).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpic.statusCode(), "Ошибка при создании эпика");

        subtask.setEpicId(1);

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/1");
        String json = gson.toJson(subtask);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании подзадачи");

        subtask.setId(2);

        URI urlWithId = URI.create("http://localhost:8080/tasks/subtask/2");

        Subtask subtask2 = new Subtask("updatedSubtask", "updatedSubtask description",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(150),
                Duration.ofMinutes(30));
        subtask2.setEpicId(1);
        String json2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(urlWithId).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Ошибка при обновлении подзадачи");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        Subtask updatedTask = gson.fromJson(responseGET.body(), Subtask.class);

        assertNotNull(updatedTask, "Не удалось получить обновлённую подзадау");
        assertNotEquals(subtask, updatedTask, "Подзадача не обновилась");
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {  // Проверка удаления подзадачи
        HttpClient client = HttpClient.newHttpClient();

        URI urlForEpic = URI.create("http://localhost:8080/tasks/epic");
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlForEpic).POST(bodyEpic).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpic.statusCode(), "Ошибка при создании эпика");

        subtask.setEpicId(1);

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/1");
        String json = gson.toJson(subtask);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при создании подзадачи");


        URI urlWithId = URI.create("http://localhost:8080/tasks/subtask/2");

        HttpRequest request2 = HttpRequest.newBuilder().uri(urlWithId).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Ошибка при удалении подзадачи");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        assertNotEquals(200, responseGET.statusCode(), "Подзадача не удалилась");
    }

    @Test
    void shouldGetListOfAllTasks() throws IOException, InterruptedException {  //Проверка получения списка всех задач
        HttpClient client = HttpClient.newHttpClient();

        URI urlForEpic = URI.create("http://localhost:8080/tasks/epic");  //Создание эпика
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlForEpic).POST(bodyEpic).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpic.statusCode(), "Ошибка при создании эпика");

        subtask.setEpicId(1);

        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask/epic/1");  // Создание подзадачи
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).POST(bodySubtask).build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseSubtask.statusCode(), "Ошибка при создании подзадачи");

        subtask.setId(2);

        URI urlWithIdEpic = URI.create("http://localhost:8080/tasks/epic/1");  // Получаем эпик
        HttpRequest requestGETEpic = HttpRequest.newBuilder().uri(urlWithIdEpic).GET().build();
        HttpResponse<String> responseGETEpic = client.send(requestGETEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGETEpic.statusCode(), "Ошибка при получении эпика");

        Epic epicGET = gson.fromJson(responseGETEpic.body(), Epic.class);

        URI urlTask = URI.create("http://localhost:8080/tasks/task");  // Создание задачи
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).POST(bodyTask).build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseTask.statusCode(), "Ошибка при создании задачи");

        task.setId(3);

        URI url = URI.create("http://localhost:8080/tasks/");  // Получение списка всех задач
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGET.statusCode(), "Ошибка при получении списка всех задач");
        assertNotNull(responseGET.body(), "Не удалось получить список всех задач");

        JsonElement jsonElement = JsonParser.parseString(responseGET.body());

        List<JsonObject> listObjects = new ArrayList<>();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonArray jsonArrayInArray = jsonArray.get(i).getAsJsonArray();
            for (int j = 0; j < jsonArrayInArray.size(); j++) {
                JsonObject jsonObject = jsonArrayInArray.get(j).getAsJsonObject();
                listObjects.add(jsonObject);
            }
        }
        Task task1 = gson.fromJson(listObjects.get(0), Task.class);
        Epic epic1 = gson.fromJson(listObjects.get(1), Epic.class);
        Subtask subtask1 = gson.fromJson(listObjects.get(2), Subtask.class);

        assertEquals(task, task1, "Задачи не совпадают");
        assertEquals(subtask, subtask1, "Позадачи не совпадают");
        assertEquals(epicGET, epic1, "Эпики не совпадают");
    }

    @Test
    void shouldDeleteAllTasks() throws IOException, InterruptedException {  //Проверка удаления всех задач
        HttpClient client = HttpClient.newHttpClient();

        URI urlForEpic = URI.create("http://localhost:8080/tasks/epic");
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlForEpic).POST(bodyEpic).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpic.statusCode(), "Ошибка при создании эпика");

        subtask.setEpicId(1);

        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask/epic/1");
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).POST(bodySubtask).build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseSubtask.statusCode(), "Ошибка при создании подзадачи");

        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).POST(bodyTask).build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseTask.statusCode(), "Ошибка при создании задачи");

        URI url = URI.create("http://localhost:8080/tasks/");  // Удаляем все задачи
        HttpRequest requestDELETE = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> responseDELETE = client.send(requestDELETE, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDELETE.statusCode(), "Ошибка при удалении всех задач");

        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        assertNotEquals(200, responseGET.statusCode(), "Список задач не обнулился");
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {  //Проверка получения истории просмотра задач
        HttpClient client = HttpClient.newHttpClient();

        URI urlForEpic = URI.create("http://localhost:8080/tasks/epic");  // Создаём эпик
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlForEpic).POST(bodyEpic).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpic.statusCode(), "Ошибка при создании эпика");

        subtask.setEpicId(1);

        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask/epic/1");  // Создаём подзадачу
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).POST(bodySubtask).build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseSubtask.statusCode(), "Ошибка при создании подзадачи");

        URI urlWithIdEpic = URI.create("http://localhost:8080/tasks/epic/1");  // Получаем эпик
        HttpRequest requestGETEpic = HttpRequest.newBuilder().uri(urlWithIdEpic).GET().build();
        HttpResponse<String> responseGETEpic = client.send(requestGETEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGETEpic.statusCode(), "Ошибка при получении эпика");

        Epic epicGET = gson.fromJson(responseGETEpic.body(), Epic.class);

        URI urlWithIdSubtask = URI.create("http://localhost:8080/tasks/subtask/2");  // Получаем подзадачу
        HttpRequest requestGETSubtask = HttpRequest.newBuilder().uri(urlWithIdSubtask).GET().build();
        HttpResponse<String> responseGETSubtask = client.send(requestGETSubtask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGETSubtask.statusCode(), "Ошибка при получении подзадачи");
        Subtask subtaskGET = gson.fromJson(responseGETSubtask.body(), Subtask.class);

        URI urlTask = URI.create("http://localhost:8080/tasks/task");  // Создаём задачу
        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).POST(bodyTask).build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseTask.statusCode(), "Ошибка при создании задачи");

        URI urlWithIdTask = URI.create("http://localhost:8080/tasks/task/3");  // Получаем задачу
        HttpRequest requestGETTask = HttpRequest.newBuilder().uri(urlWithIdTask).GET().build();
        HttpResponse<String> responseGETTask = client.send(requestGETTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGETTask.statusCode(), "Ошибка при получении задачи");
        Task taskGET = gson.fromJson(responseGETTask.body(), Task.class);

        URI url = URI.create("http://localhost:8080/tasks/history");  // Получаем историю
        HttpRequest requestGET = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGET.statusCode(), "Ошибка при получении истории просмотра задач");
        assertNotNull(responseGET.body(), "Не удалось получить историю задач");

        JsonElement jsonElement = JsonParser.parseString(responseGET.body());
        List<JsonObject> listObjects = new ArrayList<>();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {

            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            listObjects.add(jsonObject);
        }
        Task task1 = gson.fromJson(listObjects.get(2), Task.class);
        Subtask subtask1 = gson.fromJson(listObjects.get(1), Subtask.class);
        Epic epic1 = gson.fromJson(listObjects.get(0), Epic.class);
        assertEquals(epicGET, epic1, "Эпик не был отражён в истории просмотра задач");
        assertEquals(taskGET, task1, "Задача не была отражена в истории просмотра задач");
        assertEquals(subtaskGET, subtask1, "Подзадача не была отражена в истории просмотра задач");
    }
}

