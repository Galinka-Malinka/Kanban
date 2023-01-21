package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);  // Помечает задачи как просмотренные

    List<Task> getHistory();  // Возвращает список просмотренных задач

}