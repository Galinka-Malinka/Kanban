package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);  // Помечает задачи как просмотренные

    void remove(int id);  // Удаление задачи из списка просмотренных задач

    List<Task> getHistory();  // Возвращает список просмотренных задач

}