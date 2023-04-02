package managers;

import com.google.gson.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utils.GsonFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


import java.util.*;


public class HttpTaskServer {

    private static final int PORT = 8080;
    HttpServer httpServer;
    FileBackedTasksManager manager;

    public HttpTaskServer(FileBackedTasksManager manager) throws IOException {
        this.manager = manager;

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TasksHandler(manager));
        httpServer.createContext("/tasks/subtask", new SubtasksHandler(manager));
        httpServer.createContext("/tasks/epic", new EpicHandler(manager));
        httpServer.createContext("/tasks/", new AllTasksHandler(manager));
        httpServer.createContext("/tasks/history", new HistoryHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите имя файла");
        String fileName = scanner.nextLine();
        FileBackedTasksManager manager = Managers.getFileBacked(fileName); //Передали реализацию FileBackedTasksManager
        HttpTaskServer httpServer = new HttpTaskServer(manager);

        httpServer.start(); // запускаем сервер
    }

    public void start() {
        this.httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        this.httpServer.stop(0);
        System.out.println("HTTP-сервер остановил свою работу на \" + PORT + \" порту!");
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

    static class TasksHandler implements HttpHandler {  // Обработка запроса "/tasks/task"
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
                        HttpHandlerUtils.writeResponse(httpExchange, "Задача успешно создана",
                                200);
                    } else {
                        manager.updateTask(id.get(), task);
                        HttpHandlerUtils.writeResponse(httpExchange, "Задача успешно обновлена",
                                200);
                    }
                    break;
                }
                case "GET": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Задачи с таким id не существует.",
                                404);
                    } else {
                        Task task = manager.getTaskById(id.get());
                        if (task == null) {
                            HttpHandlerUtils.writeResponse(httpExchange, "Задача с данным id отсутствует",
                                    404);
                        }
                        HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(task), 200);
                    }
                    break;
                }
                case "DELETE": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Задачи с таким id не существует.",
                                404);
                    } else {
                        manager.removeById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, "Задача успешно удалена",
                                200);
                    }
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }
        }
    }

    static class SubtasksHandler implements HttpHandler {  //Обработка запроса "/tasks/subtask"
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
                        manager.createSubTask(subtask, epicId);
                        HttpHandlerUtils.writeResponse(httpExchange, "Подзадача успешно создана",
                                200);
                    } else {
                        manager.updateSubtask(id.get(), subtask);
                        HttpHandlerUtils.writeResponse(httpExchange, "Подзадача успешно обновлена",
                                200);
                    }
                    break;
                }
                case "GET": {
                    String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
                    if (pathParts.length > 4 && pathParts[3].equals("epic")) {
                        try {
                            if (manager.getEpicById(Integer.parseInt(pathParts[4])) != null) {
                                Map<Integer, Subtask> subtaskMap =
                                        manager.getSubtasksByEpicId(Integer.parseInt(pathParts[4]));
                                List<Subtask> subtaskList = new ArrayList<>();
                                for (Subtask subtask : subtaskMap.values()) {
                                    subtaskList.add(subtask);
                                }
                                HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(subtaskList), 200);
                            } else {

                                HttpHandlerUtils.writeResponse(httpExchange,
                                        "Эпика с заданным id не существует", 404);
                            }
                        } catch (NumberFormatException exception) {
                            HttpHandlerUtils.writeResponse(httpExchange,
                                    "Задан неправильный формат для id эпика", 400);
                        }
                    } else {
                        Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                        if (id.isEmpty()) {
                            HttpHandlerUtils.writeResponse(httpExchange,
                                    "Подзадачи с таким id не существует.", 404);
                        } else {
                            Subtask subtask = (Subtask) manager.getTaskById(id.get());
                            if (subtask == null) {
                                HttpHandlerUtils.writeResponse(httpExchange, "Подзадача с данным id отсутствует",
                                        404);
                            } else {
                                HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(subtask), 200);
                            }
                        }
                    }
                    break;
                }
                case "DELETE": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange,
                                "Подзадачи с таким id не существует.", 404);
                    } else {
                        manager.removeById(id.get());

                        HttpHandlerUtils.writeResponse(httpExchange,
                                "Подзадача успешно удалена", 200);
                    }
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }
        }
    }

    static class EpicHandler implements HttpHandler {  // Обработка запроса "/tasks/epic"
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
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпик успешно создан",
                                200);
                    } else {
                        manager.updateEpic(id.get(), epic);
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпик успешно обновлен",
                                200);
                    }
                    break;
                }
                case "GET": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпика с таким id не существует.",
                                404);
                    } else {
                        Epic epic = (Epic) manager.getTaskById(id.get());
                        if (epic == null) {
                            HttpHandlerUtils.writeResponse(httpExchange, "Эпика с данным id отсутствует",
                                    404);
                        }
                        HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(epic), 200);
                    }
                    break;
                }
                case "DELETE": {
                    Optional<Integer> id = HttpHandlerUtils.getId(httpExchange);
                    if (id.isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпика с таким id не существует.",
                                404);
                    } else {
                        manager.removeById(id.get());
                        HttpHandlerUtils.writeResponse(httpExchange, "Эпик успешно удален",
                                200);
                    }
                    break;
                }
                default: {
                    HttpHandlerUtils.writeResponse(httpExchange, "Неподдерживаемый метод", 400);
                }
            }
        }
    }

    static class AllTasksHandler implements HttpHandler { // Получение списка всех задач по запросу "/tasks/"
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
                    List<Collection<? extends Task>> oldList = manager.getListOfAllTasks();


                    if (oldList.get(0).isEmpty() && oldList.get(1).isEmpty() && oldList.get(2).isEmpty()) {
                        HttpHandlerUtils.writeResponse(httpExchange, "Список задач пуст",
                                404);
                    } else {
                        HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(oldList), 200);
                    }
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

    static class HistoryHandler implements HttpHandler {  // Получение истории по запросу "/tasks/history"
        private final FileBackedTasksManager manager;
        private final Gson gson;

        public HistoryHandler(FileBackedTasksManager manager) {
            this.manager = manager;
            this.gson = Managers.getGson();
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            List<Task> list = manager.getHistory();
            HttpHandlerUtils.writeResponse(httpExchange, gson.toJson(list), 200);
        }
    }
}