import java.util.List;

public interface TaskManager {

    List getListOfOllTasks();  // Получение списка всех задач.

    void clearTask();  //Удаление всех задач.

    Task getTaskById(int id);  //Получение по идентификатору

    void removeById(int id);  // Удаление по идентификатору.

    List<Task> getHistory();  // История просмотров последних 10 задач

    Task createTask(Task task);  //Создание задачи

    Subtask createSubTask(Subtask subtask);  //Создание подзадачи

    Epic createEpic(Epic epic);  //Создание эпика

    Epic getSubtaskEpicId(int id);  // Получение эпика по id

    Task getEpicById(int id);  //Проверка наличия эпика по id

    void updateTask(int firstId, Task task);  //Обновление задачи

    void updateSubtask(int firstId, Subtask subtask);  //Обновление подзадачи

    void updateEpic(int firstId, Epic epic);  //Обновление эпика

    Object getArrayTask(int id);  //Получение списка всех подзадач определённого эпика.

}
