package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    final Map<Integer, Task> taskHashMap = new HashMap<>();
    final Map<Integer, Subtask> subtaskHashMap = new HashMap<>();
    final Map<Integer, Epic> epicHashMap = new HashMap<>();

    final Set<Task> resortedTasks = new java.util.TreeSet<>(Comparator.comparing(Task::getStartTime));
    final Set<Task> tasksWithNullStartTime = new TreeSet<>(Comparator.comparing(Task::getId));
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addInTaskHashMap(Integer id, Task task) {
        this.taskHashMap.put(id, task);

        if (task.getStartTime() == null) {
            this.tasksWithNullStartTime.add(task);
        } else {
            this.resortedTasks.add(task);
        }
    }

    public void addInSubtaskHashMap(Integer id, Subtask subtask) {
        this.subtaskHashMap.put(id, subtask);
        this.epicHashMap.get(subtask.getEpicId()).addSubtask(subtask);

        if (subtask.getStartTime() == null) {
            this.tasksWithNullStartTime.add(subtask);
        } else {
            this.resortedTasks.add(subtask);
        }
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
        listAllTask.add(epicHashMap.values());
        listAllTask.add(subtaskHashMap.values());

        return listAllTask;
    }

    @Override
    public List<Task> getPrioritizedTasks() {    //Получение списка задач в порядке приоритета
        List<Task> listPrioritizedTasks = new ArrayList<>(this.resortedTasks);
        List<Task> listSubtasksWithNullStartTime = new ArrayList<>(this.tasksWithNullStartTime);
        listPrioritizedTasks.addAll(listSubtasksWithNullStartTime);
        return listPrioritizedTasks;
    }

    @Override
    public Task findingIntersectionsOfTasks(Task newTask) {  //Поиск пересечений
       if (newTask.getStartTime() == null) {
           return newTask;
       }
        if (!getPrioritizedTasks().isEmpty()) {
            for (Task task : getPrioritizedTasks()) {
                if (task.getStartTime() != null) {
                    if ((newTask.getStartTime().isAfter(task.getStartTime())
                            && newTask.getStartTime().isBefore(task.getEndTime()))
                            || (newTask.getStartTime().isBefore(task.getStartTime())
                            && newTask.getEndTime().isAfter(task.getStartTime()))) {
                        return task;
                    }
                } else {
                    if (task.getId() == newTask.getId()) {
                        return task;
                    }
                }
            }
        }
        return newTask;

    }

    @Override
    public void clearTask() { //Удаление всех задач.
        taskHashMap.clear();
        subtaskHashMap.clear();
        epicHashMap.clear();
        id = 0;
        historyManager.removeHistory();
        this.resortedTasks.clear();
        this.tasksWithNullStartTime.clear();
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
    public Epic getEpicById(int id) {  //Проверка наличия эпика по id
        Epic newObject = null;
        if (epicHashMap.containsValue(epicHashMap.get(id))) {
            newObject = epicHashMap.get(id);
        }
        return newObject;
    }

    @Override
    public Task createTask(Task task) {  //Создание задачи
        if (findingIntersectionsOfTasks(task).equals(task)) {
            task.setId(id + 1);
            id = task.getId();
            taskHashMap.put(id, task);
            if (task.getStartTime() == null) {
                this.tasksWithNullStartTime.add(task);
            } else {
                this.resortedTasks.add(task);
            }
        }
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask, int epicId) { //Создание подзадачи
        if (findingIntersectionsOfTasks(subtask).equals(subtask)) {
            subtask.setId(id + 1);
            subtask.setEpicId(epicId);
            id = subtask.getId();
            subtaskHashMap.put(id, subtask);
            epicHashMap.get(epicId).addSubtask(subtask);
            if (subtask.getStartTime() == null) {
                this.tasksWithNullStartTime.add(subtask);
            } else {
                this.resortedTasks.add(subtask);
            }
        }
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
    public Epic getSubtaskEpicId(int id) {  // Получение эпика подзадачи по id

        return epicHashMap.get(id);
    }

    @Override
    public void updateTask(int firstId, Task task) { //Обновление задачи
        task.setId(firstId);

        if (taskHashMap.get(firstId).getStartTime() == null) {
            this.tasksWithNullStartTime.remove(taskHashMap.get(firstId));
            if (task.getStartTime() == null) {
                this.tasksWithNullStartTime.add(task);
            } else {
                this.resortedTasks.add(task);
            }
        } else {
            this.getPrioritizedTasks().remove(taskHashMap.get(firstId));
            if (task.getStartTime() == null) {
                this.tasksWithNullStartTime.add(task);
            } else {
                this.resortedTasks.add(task);
            }
        }
        taskHashMap.put(firstId, task);

    }

    @Override
    public void updateSubtask(int firstId, Subtask subtask) { //Обновление подзадачи
        subtask.setEpicId(subtaskHashMap.get(firstId).getEpicId());
        subtask.setId(firstId);

        if (subtaskHashMap.get(firstId).getStartTime() == null) {
            this.tasksWithNullStartTime.remove(subtaskHashMap.get(firstId));
            subtaskHashMap.put(firstId, subtask);
            epicHashMap.get(subtask.getEpicId()).addSubtask(subtask);
            epicHashMap.get(subtask.getEpicId()).reviewStatus();
            if (subtask.getStartTime() == null) {
                this.tasksWithNullStartTime.add(subtask);
            } else {
                this.resortedTasks.add(subtask);
            }
        } else {
            this.resortedTasks.remove(subtaskHashMap.get(firstId));
            subtaskHashMap.put(firstId, subtask);
            epicHashMap.get(subtask.getEpicId()).addSubtask(subtask);
            epicHashMap.get(subtask.getEpicId()).reviewStatus();
            this.resortedTasks.add(subtask);
        }
    }

    @Override
    public void updateEpic(int firstId, Epic epic) { //Обновление эпика
        epic.setId(firstId);
        epicHashMap.put(firstId, epic);
    }

    @Override
    public void removeById(int id) {// Удаление по идентификатору.

        if (taskHashMap.containsValue(taskHashMap.get(id))) {
            if (taskHashMap.get(id).getStartTime() == null) {
                this.tasksWithNullStartTime.remove(taskHashMap.get(id));
            } else {
                this.resortedTasks.remove(taskHashMap.get(id));
            }
            taskHashMap.remove(id);
        } else if (subtaskHashMap.containsValue(subtaskHashMap.get(id))) {
            Subtask subtask = subtaskHashMap.get(id);
            if (subtask.getStartTime() == null) {
                this.tasksWithNullStartTime.remove(subtask);
            } else {
                this.resortedTasks.remove(subtask);
            }
            epicHashMap.get(subtask.getEpicId()).removeSubtask(subtask.getId());
            subtaskHashMap.remove(id);
            epicHashMap.get(subtask.getEpicId()).reviewStatus();
        } else if (epicHashMap.containsValue(epicHashMap.get(id))) {
            Map<Integer, Subtask> subtasksFromEpic = epicHashMap.get(id).getSubtasks();
            for (Subtask subtask : subtasksFromEpic.values()) {
                if (subtask.getStartTime() == null) {
                    this.tasksWithNullStartTime.remove(subtask);
                } else {
                    this.resortedTasks.remove(subtask);
                }
                subtaskHashMap.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epicHashMap.remove(id);
        }

        if (!historyManager.getHistory().isEmpty()) {
            historyManager.remove(id);
        }

        if (taskHashMap.isEmpty() || subtaskHashMap.isEmpty() || epicHashMap.isEmpty()) {
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