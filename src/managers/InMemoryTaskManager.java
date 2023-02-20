package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> taskHashMap = new HashMap<>();
    private Map<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private Map<Integer, Epic> epicHashMap = new HashMap<>();

    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addInTaskHashMap(Integer id, Task task) {
        this.taskHashMap.put(id, task);
    }

    public void addInSubtaskHashMap(Integer id, Subtask subtask) {
        this.subtaskHashMap.put(id, subtask);
    }

    public void addInEpicHashMap(Integer id, Epic epic) {
        this.epicHashMap.put(id, epic);
    }

    HistoryManager historyManager = Managers.getDefaultHistory();//Объявление переменной, которая содержит
                                                                 //определённую реализацию ИФ managers.HistoryManager
    @Override
    public List<Collection<? extends Task>> getListOfAllTasks() { // Получение списка всех задач.

        List<Collection<? extends Task>> listAllTask = new ArrayList<>(3);
        listAllTask.add(taskHashMap.values());
        listAllTask.add(subtaskHashMap.values());
        listAllTask.add(epicHashMap.values());

        return listAllTask;
    }

    @Override
    public void clearTask() { //Удаление всех задач.
        taskHashMap.clear();
        subtaskHashMap.clear();
        epicHashMap.clear();
        id = 0;
        historyManager.removeHistory();
    }

    @Override
    public Task getTaskById(int id) {  //Получение по идентификатору
        Task newObject = null;
        if (taskHashMap.containsValue(taskHashMap.get(id))) {
            newObject = taskHashMap.get(id);

            historyManager.add(newObject);

        } else if (subtaskHashMap.containsValue(subtaskHashMap.get(id))) {
            newObject = subtaskHashMap.get(id);

            historyManager.add(newObject);

        } else if (epicHashMap.containsValue(epicHashMap.get(id))) {
            newObject = epicHashMap.get(id);

            historyManager.add(newObject);
        }
        return newObject;
    }

    @Override
    public Task getEpicById(int id) {  //Проверка наличия эпика по id
        Task newObject = null;
        if (epicHashMap.containsValue(epicHashMap.get(id))) {
            newObject = epicHashMap.get(id);
        }
        return newObject;
    }

    @Override
    public Task createTask(Task task) {  //Создание задачи
        task.setId(id + 1);
        id = task.getId();
        taskHashMap.put(id, task);
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask, int epicId) { //Создание подзадачи
        subtask.setId(id + 1);
        subtask.setEpicId(epicId);
        id = subtask.getId();
        subtaskHashMap.put(id, subtask);

        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) { //Создание эпика
        epic.setId(id + 1);
        id = epic.getId();
        epicHashMap.put(id, epic);
        return epic;
    }

    @Override
    public Epic getSubtaskEpicId(int id) {  // Получение эпика по id

        return epicHashMap.get(id);
    }

    @Override
    public void updateTask(int firstId, Task task) { //Обновление задачи
        task.setId(firstId);
        taskHashMap.put(firstId, task);
    }

    @Override
    public void updateSubtask(int firstId, Subtask subtask) { //Обновление подзадачи
        subtask.setEpicId(subtaskHashMap.get(firstId).getEpicId());

        subtask.setId(firstId);

        subtaskHashMap.put(firstId, subtask);
        epicHashMap.get(subtask.getEpicId()).addSubtask(subtask);
        epicHashMap.get(subtask.getEpicId()).reviewStatus();
    }

    @Override
    public void updateEpic(int firstId, Epic epic) { //Обновление эпика
        epic.setId(firstId);
        epicHashMap.put(firstId, epic);
    }

    @Override
    public void removeById(int id) {// Удаление по идентификатору.

        if (taskHashMap.containsValue(taskHashMap.get(id))) {
            taskHashMap.remove(id);
        } else if (subtaskHashMap.containsValue(subtaskHashMap.get(id))) {
            Subtask subtask = subtaskHashMap.get(id);

            epicHashMap.get(subtask.getEpicId()).removeSubtask(subtask.getId());
            subtaskHashMap.remove(id);
            epicHashMap.get(subtask.getEpicId()).reviewStatus();

        } else if (epicHashMap.containsValue(epicHashMap.get(id))) {
            Map<Integer, Subtask> subtasksFromEpic = epicHashMap.get(id).getSubtasks();
            for (Subtask subtask : subtasksFromEpic.values()){
                subtaskHashMap.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epicHashMap.remove(id);
        }

       if(!historyManager.getHistory().isEmpty()) {
           historyManager.remove(id);
       }

        if(taskHashMap.isEmpty() || subtaskHashMap.isEmpty() || epicHashMap.isEmpty()) {
            setId(0);
        }
    }

    @Override
    public Map<Integer, Subtask> getSubtasksByEpicId(int id) {  // Получение списка всех подзадач определённого эпика.
        Epic epic = epicHashMap.get(id);

        historyManager.add(epic);

        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {  // Получение истории просмотренных задач.
        return historyManager.getHistory();
    }
}
