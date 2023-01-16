import java.util.*;

public class Manager {
    HashMap<Integer, Task> taskHashMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    HashMap<Integer, Epic> epicHashMap = new HashMap<>();


    int id = 0;

    public ArrayList getListOfOllTasks() { // Получение списка всех задач.

        ArrayList<Object> listOllTask = new ArrayList<>();
        listOllTask.add(taskHashMap.values());
        listOllTask.add(subtaskHashMap.values());
        listOllTask.add(epicHashMap.values());

        return listOllTask;
    }

    public void clearTask() { //Удаление всех задач.
        taskHashMap.clear();
        subtaskHashMap.clear();
        epicHashMap.clear();
    }

    public Task getTaskById(int id) {  //Получение по идентификатору
        Task newObject = null;
        if (taskHashMap.containsValue(taskHashMap.get(id))) {
            newObject = taskHashMap.get(id);
        } else if (subtaskHashMap.containsValue(subtaskHashMap.get(id))) {
            newObject = subtaskHashMap.get(id);
        } else if (epicHashMap.containsValue(epicHashMap.get(id))) {
            newObject = epicHashMap.get(id);
        }
        return newObject;
    }

    public Task createTask(Task task) {  //Создание задачи
        task.id = id + 1;
        id = task.id;
        taskHashMap.put(id, task);
        return task;
    }

    public Subtask createSubTask(Subtask subtask) { //Создание подзадачи
        subtask.id = id + 1;
        id = subtask.id;
        subtaskHashMap.put(id, subtask);
        return subtask;
    }

    public Epic createEpic(Epic epic) { //Создание эпика
        epic.id = id + 1;
        id = epic.id;
        epicHashMap.put(id, epic);
        return epic;
    }

    public Epic getSubtaskEpicId(int id) {  // Получение эпика по id
        return epicHashMap.get(id);
    }

    public void updateTask(int firstId, Task task) { //Обновление задачи
        task.id = firstId;
        taskHashMap.put(firstId, task);
    }

    public void updateSubtask(int firstId, Subtask subtask) { //Обновление подзадачи
        subtask.epicId = subtaskHashMap.get(firstId).epicId;

        subtask.id = firstId;

        subtaskHashMap.put(firstId, subtask);
        epicHashMap.get(subtask.epicId).getSubTask(subtask);
        epicHashMap.get(subtask.epicId).reviewStatus();
    }

    public void updateEpic(int firstId, Epic epic) { //Обновление эпика
        epic.id = firstId;
        epicHashMap.put(firstId, epic);
    }

    public void removeById(int id) {// Удаление по идентификатору.

        if (taskHashMap.containsValue(taskHashMap.get(id))) {
            taskHashMap.remove(id);
        } else if (subtaskHashMap.containsValue(subtaskHashMap.get(id))) {
            Subtask subtask = subtaskHashMap.get(id);

            epicHashMap.get(subtask.epicId).removeSubtask(subtask.id);
            subtaskHashMap.remove(id);
        } else if (epicHashMap.containsValue(epicHashMap.get(id))) {
            epicHashMap.remove(id);
        }
    }

    public Object getArrayTask(int id) {//Получение списка всех подзадач определённого эпика.
        Epic epic = epicHashMap.get(id);
        HashMap<Integer, Subtask> subtasks = epic.subtasks;
        return subtasks;
    }
}
