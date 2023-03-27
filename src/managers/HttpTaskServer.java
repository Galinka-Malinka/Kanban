package managers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HttpTaskServer {

    private static final int PORT = 8080;


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите имя файла");
        String fileName = scanner.nextLine();
        File file = new File(fileName);
        FileBackedTasksManager manager = Managers.getFileBacked(file);  // Передали реализацию FileBackedTasksManager

        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TasksHandler(manager));
        //httpServer.createContext("/tasks/task/id", new TasksHandler(manager));
        httpServer.createContext("/tasks/subtask", new SubtasksHandler(manager));
        httpServer.createContext("/tasks/epic", new EpicHandler(manager));
        httpServer.createContext("/tasks/", new AllTasksHandler(manager));
        httpServer.createContext("/tasks/history", new HistoryHandler(manager));
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    static class HttpHandlerUtils {
        public static void writeResponse(HttpExchange exchange, String responseString, int responseCode)
                throws IOException { //  Выведение ответа
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        public static Optional<Integer> getId(HttpExchange exchange) { // Получение id из запроса

            String[] pathParts = exchange.getRequestURI().getPath().split("/");

            if (pathParts.length > 3) {
                try {
                    return Optional.of(Integer.parseInt(pathParts[3]));
                } catch (NumberFormatException exception) {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }
    }

    static class TasksHandler implements HttpHandler {  // in Task

        private final FileBackedTasksManager manager;
        private final Gson gson;

        public TasksHandler(FileBackedTasksManager manager) {
            this.manager = manager;
            this.gson = GsonFactory.getGson();

        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            switch (httpExchange.getRequestMethod().toUpperCase()) {
                case "POST": {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                    Task task = gson.fromJson(reader, Task.class);

                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        manager.createTask(task);
                        HttpHandlerUtils.writeResponse(httpExchange, "Задача успешно создана", 200);
                    } else {
                        manager.updateTask(id.get(), task);
                        HttpHandlerUtils.writeResponse(httpExchange, "Задача успешно обновлена", 200);
                    }

                    break;
                }
                case "GET": {

                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Задачи с таким id не существует.", 404);
                    } else {
                        Task task = manager.getTaskById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(task), 200);
                    }
                    break;
                }
                case "DELETE": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Задачи с таким id не существует.", 404);
                    } else {
                        manager.removeById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, "Задача успешно удалена", 200);
                    }
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }
        }

    }

    static class SubtasksHandler implements HttpHandler {  // in Subtask
        private final FileBackedTasksManager manager;
        private final Gson gson;

        public SubtasksHandler(FileBackedTasksManager manager) {
            this.manager = manager;
            this.gson = GsonFactory.getGson();
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            switch (httpExchange.getRequestMethod().toUpperCase()) {
                case "POST": {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                    Subtask subtask = gson.fromJson(reader, Subtask.class);

                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);

                    if (id.isEmpty()) {

                        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");

                        int epicId = Integer.parseInt(pathParts[4]);
                        System.out.println(subtask);
                        manager.createSubTask(subtask, epicId);

                        HttpHandlerUtils.writeResponse(httpExchange, "Подзадача успешно создана", 200);
                    } else {
                        manager.updateSubtask(id.get(), subtask);
                        HttpHandlerUtils.writeResponse(httpExchange, "Подзадача успешно обновлена", 200);
                    }
                    break;
                }
                case "GET": {
                    String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
                    if (pathParts.length > 4 && pathParts[3].equals("epic")) {
                        try {
                            if (manager.getEpicById(Integer.parseInt(pathParts[4])) != null) {
                                Map<Integer, Subtask> subtaskMap = manager.getSubtasksByEpicId(Integer.parseInt(pathParts[4]));
                                List<Subtask> subtaskList = new ArrayList<>();
//                                subtaskList.addAll(subtaskMap.values());
                                for (Subtask subtask : subtaskMap.values()) {
                                    subtaskList.add(subtask);
                                }
                                HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(subtaskList), 200);
                            } else {
                                HttpHandlerUtils.writeResponse(httpExchange, "Эпика с заданным id не существует", 404);
                            }
                        } catch (NumberFormatException exception) {
                            HttpHandlerUtils.writeResponse(httpExchange, "Задан неправильный формат для id эпика", 400);
                        }

                    } else {
                        Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                        if (id.isEmpty()) {
                            HttpHandlerUtils.writeResponse(httpExchange, "Подзадачи с таким id не существует.", 404);
                        } else {
                            Subtask subtask = (Subtask) manager.getTaskById(id.get());
                            HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(subtask), 200);
                        }
                    }
                    break;
                }
                case "DELETE": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Подзадачи с таким id не существует.", 404);
                    } else {
                        manager.removeById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, "Подзадача успешно удалена", 200);
                    }
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }
        }

    }

    static class EpicHandler implements HttpHandler {  // in Epic
        private final FileBackedTasksManager manager;
        private final Gson gson;

        public EpicHandler(FileBackedTasksManager manager) {
            this.manager = manager;
            this.gson = GsonFactory.getGson();
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            switch (httpExchange.getRequestMethod().toUpperCase()) {
                case "POST": {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                    Epic epic = gson.fromJson(reader, Epic.class);
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        manager.createEpic(epic);
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпик успешно создан", 200);
                    } else {
                        manager.updateEpic(id.get(), epic);
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпик успешно обновлен", 200);
                    }
                    break;
                }
                case "GET": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпика с таким id не существует.", 404);
                    } else {
                        Epic epic = (Epic) manager.getTaskById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(epic), 200);
                    }
                    break;
                }
                case "DELETE": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпика с таким id не существует.", 404);
                    } else {
                        manager.removeById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпик успешно удален", 200);
                    }
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }
        }

    }

    static class AllTasksHandler implements HttpHandler { // Получение списка всех задач
        private final FileBackedTasksManager manager;
        private final Gson gson;

        public AllTasksHandler(FileBackedTasksManager manager) {
            this.manager = manager;
            this.gson = GsonFactory.getGson();
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            switch (httpExchange.getRequestMethod().toUpperCase()) {
                case "GET": {
//                    List<Task> list = manager.getPrioritizedTasks();
//                    HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(list), 200);
                    List<Collection<? extends Task>> oldList = manager.getListOfAllTasks();
                    HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(oldList), 200);
                    break;
                }
                case "DELETE": {
                    manager.clearTask();
                    HttpHandlerUtils.writeResponse(httpExchange, "Все задачи удалены", 200);
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }

        }

    }

    static class HistoryHandler implements HttpHandler {
        private final FileBackedTasksManager manager;
        private final Gson gson;

        public HistoryHandler(FileBackedTasksManager manager) {
            this.manager = manager;
            this.gson = GsonFactory.getGson();
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            List<Task> list = manager.getHistory();
            HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(list), 200);
        }

    }
}

class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.ENGLISH));
    }
}

class DurationDeserializer implements JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return Duration.ofMinutes(json.getAsLong());
    }
}

class LocalDateSerializer implements JsonSerializer < LocalDateTime > {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public JsonElement serialize(LocalDateTime localDate, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDate));
    }
}

class DurationSerializer implements JsonSerializer < Duration > {


    @Override
    public JsonElement serialize(Duration duration, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(duration.toMinutes());
    }
}

class GsonFactory {
    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateSerializer())
                .registerTypeAdapter(Duration.class, new DurationSerializer())
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting().create();
        return gson;
// gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        // gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
    }


}

//class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
//    private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    @Override
//    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
//        jsonWriter.value(localDateTime.format(formatterWriter));
//    }
//
//    @Override
//    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
//        return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
//    }
//}
//
//class DurationAdapter extends TypeAdapter<Duration> {
//    @Override
//    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
//        jsonWriter.value(duration.toMinutes());
//    }
//    @Override
//    public Duration read(final JsonReader jsonReader) throws IOException {
//        return Duration.parse(jsonReader.nextString());
//    }
//}
