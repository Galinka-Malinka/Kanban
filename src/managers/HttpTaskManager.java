package managers;

import com.google.gson.*;
import tasks.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {

    private final URL url;
    private KVTaskClient client;

    public HttpTaskManager(URL url, String apiToken) throws MalformedURLException {
        super("http_tasks.txt");
        this.url = url;
        this.client = new KVTaskClient(this.url, apiToken);
        if (!apiToken.equals("Отсутствует")) {
            loadManagerData();
        }
    }

    Gson gson = Managers.getGson();

    public KVTaskClient getClient() {
        return client;
    }

    void loadManagerData() {
        List<JsonObject> listJson = this.client.load("listAllTasks");  // Восстановление задач
        for (JsonObject taskJson : listJson) {
            if (taskJson.get("typeTask").getAsString().equals("TASK")) {
                Task actual = gson.fromJson(taskJson, Task.class);
                createTaskFromFile(actual);
            } else if (taskJson.get("typeTask").getAsString().equals("SUBTASK")) {
                Subtask actual = gson.fromJson(taskJson, Subtask.class);
                createSubtaskFromFile(actual);
            } else if (taskJson.get("typeTask").getAsString().equals("EPIC")) {
                Epic actual = gson.fromJson(taskJson, Epic.class);
                createEpicFromFile(actual);
            }
        }

        List<JsonObject> historyJson = this.client.load("history");  //Восстановление истории
        for (JsonObject taskJson : historyJson) {

            if (taskJson.get("typeTask").getAsString().equals("TASK")) {
                Task actual = gson.fromJson(taskJson, Task.class);
                getTaskById(actual.getId());
            } else if (taskJson.get("typeTask").getAsString().equals("SUBTASK")) {
                Subtask actual = gson.fromJson(taskJson, Subtask.class);
                getTaskById(actual.getId());
            } else if (taskJson.get("typeTask").getAsString().equals("EPIC")) {
                Epic actual = gson.fromJson(taskJson, Epic.class);
                getTaskById(actual.getId());
            }
        }
    }

    void saveManagerData() {
        this.client.put("listAllTasks", gson.toJson(super.getListOfAllTasks()));
        this.client.put("prioritizedTasks", gson.toJson(super.getPrioritizedTasks()));
        this.client.put("history", gson.toJson(super.getHistory()));
    }

    @Override
    public void createTaskFromFile(Task task) {
        super.createTaskFromFile(task);
    }

    @Override
    public void createSubtaskFromFile(Subtask subtask) {
        super.createSubtaskFromFile(subtask);
    }

    @Override
    public void createEpicFromFile(Epic epic) {
        super.createEpicFromFile(epic);
    }

    @Override
    public Task createTask(Task task) {  // Создание задачи
        Task task1 = super.createTask(task);
        saveManagerData();
        return task1;
    }

    @Override
    public Subtask createSubTask(Subtask subtask, int epicId) { // Создание подзадачи
        Subtask subtask1 = super.createSubTask(subtask, epicId);
        saveManagerData();
        return subtask1;
    }

    @Override
    public Epic createEpic(Epic epic) { // Создание эпика

        Epic epic1 = super.createEpic(epic);

        saveManagerData();
        return epic1;
    }

    @Override
    public void updateTask(int firstId, Task task) { // Обновление задачи
        super.updateTask(firstId, task);
        saveManagerData();
    }

    @Override
    public void updateSubtask(int firstId, Subtask subtask) { // Обновление подзадачи
        super.updateSubtask(firstId, subtask);
        saveManagerData();
    }

    @Override
    public void updateEpic(int firstId, Epic epic) { // Обновление эпика
        super.updateEpic(firstId, epic);
        saveManagerData();
    }

    @Override
    public Epic getEpicById(int id) {  // Проверка наличия эпика по id
        return super.getEpicById(id);
    }

    @Override
    public Epic getSubtaskEpicId(int id) {  // Получение эпика подзадачи по id
        return super.getSubtaskEpicId(id);
    }

    @Override
    public Map<Integer, Subtask> getSubtasksByEpicId(int id) {  // Получение списка всех подзадач определённого эпика.
        Map<Integer, Subtask> subtaskMap = super.getSubtasksByEpicId(id);
        this.client.put("history", gson.toJson(super.getHistory()));
        return subtaskMap;
    }

    @Override
    public Task getTaskById(int id) {  // Получение по идентификатору
        Task task = super.getTaskById(id);
        this.client.put("history", gson.toJson(super.getHistory()));
        return task;
    }

    @Override
    public List<Collection<? extends Task>> getListOfAllTasks() {  // Получение списка всех задач.
        return super.getListOfAllTasks();
    }

    @Override
    public List<Task> getPrioritizedTasks() {  // Получение списка задач в порядке приоритета
        return super.getPrioritizedTasks();
    }

    @Override
    public void removeById(int id) {  // Удаление по идентификатору.
        super.removeById(id);
        saveManagerData();
    }

    @Override
    public void clearTask() {  // Удаление всех задач.
        super.clearTask();
        saveManagerData();
    }

    @Override
    public List<Task> getHistory() {  // История просмотров последних 10 задач
        return super.getHistory();
    }
}
