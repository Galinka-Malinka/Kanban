package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TaskManager {

    List<Collection<? extends Task>> getListOfAllTasks();  // Получение списка всех задач
    List<Task> getPrioritizedTasks();  //Получение списка задач в порядке приоритета

    void clearTask();  //Удаление всех задач

    Task getTaskById(int id);  //Получение по идентификатору

    void removeById(int id);  // Удаление по идентификатору

    List<Task> getHistory();  // История просмотров задач

    Task createTask(Task task);  //Создание задачи

    Subtask createSubTask(Subtask subtask, int epicId);  //Создание подзадачи

    Epic createEpic(Epic epic);  //Создание эпика

    Task findingIntersectionsOfTasks(Task newTask);  //Проверка на пересечение

    Epic getSubtaskEpicId(int id);  // Получение эпика по id

    Epic getEpicById(int id);  //Проверка наличия эпика по id

    void updateTask(int firstId, Task task);  //Обновление задачи

    void updateSubtask(int firstId, Subtask subtask);  //Обновление подзадачи

    void updateEpic(int firstId, Epic epic);  //Обновление эпика

    Map<Integer, Subtask> getSubtasksByEpicId(int id);  //Получение списка всех подзадач определённого эпика.

}