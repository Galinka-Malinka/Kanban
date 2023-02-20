package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);  // Помечает задачи как просмотренные

    void remove(int id);  // Удаление задачи из списка просмотренных задач

    void removeHistory();  //Очистка истории просмотров задач
    List<Task> getHistory();  // Возвращает список просмотренных задач

}