package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    Task task;
    Epic epic;
    Subtask subtask;

    InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    void shouldAddTaskInHistory() {  // Проверка добавления задачи в историю просмотров
        task = manager.createTask(new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        epic = manager.createEpic(new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        manager.getTaskById(task.getId());
        assertNotNull(manager.getHistory(), "Задача не была добавленна в историю");
        assertEquals(task, manager.getHistory().get(0));

        manager.getTaskById(task.getId());
        assertEquals(1, manager.getHistory().size(), "Продублировалась запись просмотренной задачи");

        manager.getTaskById(epic.getId());
        assertEquals(2, manager.getHistory().size(), "Не записалась вторая задача(эпик)");

    }

    @Test
    void shouldRemoveTaskOfHistory() {  // Проверка удаления задачи из истории просмотров
        task = manager.createTask(new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        epic = manager.createEpic(
                new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                        LocalDateTime.now(), Duration.ofMinutes(30)));
        subtask = manager.createSubTask(
                new Subtask("Test Subtask", "Test Subtask description", Status.NEW,
                        LocalDateTime.now(), Duration.ofMinutes(30)), epic.getId());
        Task task1 = manager.createTask(new Task("Test Task1", "Test Task1 description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        Task task2 = manager.createTask(new Task("Test Task2", "Test Task2 description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));

        manager.getTaskById(task.getId());
        manager.getTaskById(epic.getId());
        manager.getTaskById(subtask.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        manager.removeById(task.getId());
        assertNotNull(manager.getHistory(), "Вместо удаления одной задачи, удалилась вся история");
        assertEquals(4, manager.getHistory().size(), "Неверное удаление задачи из начала истории ");
        assertEquals(epic, manager.getHistory().get(0), "Не верное отображение истории после удаления");

        manager.removeById(task2.getId());
        assertNotNull(manager.getHistory(), "Вместо удаления одной задачи, удалилась вся история");
        assertEquals(3, manager.getHistory().size(), "Неверное удаление задачи из конца истории ");
        assertEquals(task1, manager.getHistory().get(2), "Не верное отображение истории после удаления");

        manager.removeById(subtask.getId());
        assertNotNull(manager.getHistory(), "Вместо удаления одной задачи, удалилась вся история");
        assertEquals(2, manager.getHistory().size(), "Неверное удаление задачи из конца истории ");
        assertEquals(task1, manager.getHistory().get(1), "Не верное отображение истории после удаления");
    }

    @Test
    void shouldRemoveHistory() {  // Проверка удаления всей истории
        task = manager.createTask(new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        epic = manager.createEpic(
                new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                        LocalDateTime.now(), Duration.ofMinutes(30)));
        subtask = manager.createSubTask(
                new Subtask("Test Subtask", "Test Subtask description", Status.NEW,
                        LocalDateTime.now(), Duration.ofMinutes(30)), epic.getId());

        manager.getTaskById(task.getId());
        manager.getTaskById(epic.getId());
        manager.getTaskById(subtask.getId());

        manager.clearTask();

        assertEquals(manager.getHistory(), new ArrayList<>(), "Не произошла очистка истории");
    }

    @Test
    void shouldGetListOfHistory() {  // Проверка получения истории
        task = manager.createTask(new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        epic = manager.createEpic(
                new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                        LocalDateTime.now(), Duration.ofMinutes(30)));
        subtask = manager.createSubTask(
                new Subtask("Test Subtask", "Test Subtask description", Status.NEW,
                        LocalDateTime.now(), Duration.ofMinutes(30)), epic.getId());

        manager.getTaskById(task.getId());
        manager.getTaskById(epic.getId());
        manager.getTaskById(subtask.getId());

        assertNotNull(manager.getHistory(), "Список просмотренных задач не сформирован");
        assertEquals(3, manager.getHistory().size(), "Неверное колличество просмотренных задач");
        assertEquals(task, manager.getHistory().get(0), "Неверный порядок просмотренных задач");
        assertEquals(epic, manager.getHistory().get(1), "Неверный порядок просмотренных задач");
        assertEquals(subtask, manager.getHistory().get(2), "Неверный порядок просмотренных задач");
    }
}