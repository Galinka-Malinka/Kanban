package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {
    InMemoryTaskManager manager;
    HistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;

    void create(InMemoryTaskManager manager) {
        this.manager = manager;
        task = new Task("Test Task", "Test Task description", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(30));
        subtask = new Subtask("Test Subtask", "Test Subtask description", Status.NEW,
                LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(30));
    }

    void clear() {
        manager.clearTask();
    }

    void shouldCreateTask() {  // Проверка создания задачи
        Task taskTest = manager.createTask(task);

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(taskTest, manager.getTaskById(taskTest.getId()), "Задача не была созданна");
    }

    void shouldCreateSubtask() {  // Проверка создания подзадачи
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());
        int epicIdAtSubtask = subtaskTest.getEpicId();

        assertNotNull(subtaskTest, "Подзадача не найдена");
        assertEquals(subtaskTest, manager.getTaskById(subtaskTest.getId()), "Подзадача не была созданна");
        assertEquals(epicTest.getId(), epicIdAtSubtask, "ID эпика не сохранился в подзадаче.");
    }

    void shouldCreateEpic() {  // Проверка создания эпика
        Epic epicTest = manager.createEpic(epic);

        assertNotNull(epicTest, "Эпик не найден");
        assertEquals(epicTest, manager.getEpicById(epicTest.getId()), "Эпик не был создан");
    }

    void shouldUpdateTask() {  // Проверка обновления задачи
        Task taskTest = manager.createTask(task);
        Task taskForUpdate = new Task("Test UpdatedTask", "Test UpdatedTask description",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(60),
                Duration.ofMinutes(15));
        manager.updateTask(taskTest.getId(), taskForUpdate);
        Task updatedTask = manager.getTaskById(taskTest.getId());

        assertNotNull(updatedTask, "Обновлённая задача не найдена");
        assertNotNull(updatedTask.getId(), "Неверный (нулевой) ID обновлённой задачи");
        assertNotEquals(taskTest, updatedTask, "Задача не обновилась");
        assertEquals(updatedTask.getName(), taskForUpdate.getName(), "Имя задачи не обновилось");
        assertEquals(updatedTask.getDescription(), taskForUpdate.getDescription(),
                "Описание задачи не обновилось");
        assertEquals(updatedTask.getStatus(), taskForUpdate.getStatus(), "Статус задачи не обновился");
        assertEquals(taskForUpdate.getStartTime(), updatedTask.getStartTime(), "Стартовое время не обновилось");
        assertEquals(taskForUpdate.getDuration(), updatedTask.getDuration(), "Время выполнения не обновилось");
    }

    void shouldUpdateSubtask() {  // Проверка обновления подзадачи
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());

        Subtask subtaskForUpdate = new Subtask("Test UpdatedSubtask",
                "Test UpdatedSubtask description", Status.IN_PROGRESS,
                LocalDateTime.now().plusMinutes(150), Duration.ofMinutes(15));
        manager.updateSubtask(subtaskTest.getId(), subtaskForUpdate);
        Subtask updatedSubtask = (Subtask) manager.getTaskById(subtaskTest.getId());

        assertNotNull(updatedSubtask, "Обновлённая подзадача не найдена");
        assertNotNull(updatedSubtask.getId(), "Неверный (нулевой) ID обновлённой подзадачи");
        assertNotEquals(subtaskTest, updatedSubtask, "Подзадача не обновилась");
        assertEquals(updatedSubtask.getName(), subtaskForUpdate.getName(), "Имя подзадачи не обновилось");
        assertEquals(updatedSubtask.getDescription(), subtaskForUpdate.getDescription(),
                "Описание подзадачи не обновилось");
        assertEquals(updatedSubtask.getStatus(), subtaskForUpdate.getStatus(), "Статус подзадачи не обновился");
        assertEquals(subtaskForUpdate.getStartTime(), updatedSubtask.getStartTime(), "Стартовое время не обновилось");
        assertEquals(subtaskForUpdate.getDuration(), updatedSubtask.getDuration(), "Время выполнения не обновилось");
    }

    void shouldUpdateEpic() {  // Проверка обновления эпика
        Epic epicTest = manager.createEpic(epic);

        Epic epicForUpdate = new Epic("Test UpdatedEpic", "Test UpdatedEpic description",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(180),
                Duration.ofMinutes(15));
        manager.updateEpic(epicTest.getId(), epicForUpdate);
        Epic updatedEpic = (Epic) manager.getEpicById(epicTest.getId());

        assertNotNull(updatedEpic, "Обновлённый эпик не найден");
        assertNotNull(updatedEpic.getId(), "Неверный (нулевой) ID обновлённого эпика");
        assertNotEquals(epicTest, updatedEpic, "Эпик не обновился");
        assertEquals(updatedEpic.getName(), epicForUpdate.getName(), "Имя эпика не обновилось");
        assertEquals(updatedEpic.getDescription(), epicForUpdate.getDescription(),
                "Описание эпика не обновилось");
        assertEquals(updatedEpic.getStatus(), epicForUpdate.getStatus(), "Статус эпика не обновился");
        assertEquals(epicForUpdate.getStartTime(), updatedEpic.getStartTime(), "Стартовое время не обновилось");
        assertEquals(epicForUpdate.getDuration(), updatedEpic.getDuration(), "Время выполнения не обновилось");
    }

    void shouldGetTaskById() {  // Проверка получения по идентификатору
        Task taskTest = manager.createTask(task);
        int taskId = taskTest.getId();
        Task desiredTask = manager.getTaskById(taskId);

        assertNotNull(taskId, "Неверный (нулевой) ID задачи");
        assertNotNull(desiredTask, "Задача не найдена");
        assertEquals(taskTest, desiredTask, "Найденная задача не соответствует искомой");

        Epic epicTest = manager.createEpic(epic);
        int epicId = epicTest.getId();
        Epic desiredEpic = (Epic) manager.getTaskById(epicId);

        assertNotNull(epicId, "Неверный (нулевой) ID эпика");
        assertNotNull(desiredEpic, "Эпик не найден");
        assertEquals(epicTest, desiredEpic, "Найденный эпик не соответствует искомому");

        Subtask subtaskTest = manager.createSubTask(subtask, epicId);
        int subtaskId = subtaskTest.getId();
        Subtask desiredSubtask = (Subtask) manager.getTaskById(subtaskId);

        assertNotNull(subtaskId, "Неверный (нулевой) ID подзадачи");
        assertNotNull(desiredSubtask, "Подзадача не найдена");
        assertEquals(subtaskTest, desiredSubtask, "Найденная подзадача не соответствует искомой");
    }

    void shouldGetListOfAllTasks() {  // Проверка получения списка всех задач
        Task taskTest = manager.createTask(task);
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());

        List<Collection<? extends Task>> listCollectionAllTasks = manager.getListOfAllTasks();

        assertNotNull(listCollectionAllTasks, "Задачи на возвращаются");
        assertEquals(3, listCollectionAllTasks.size(), "Неверное количество задач");

        Collection<? extends Task> tasks = listCollectionAllTasks.get(0);
        for (Task receivedTask : tasks) {
            assertEquals(receivedTask, taskTest, "В списке отсутствует созданная задача");
        }
        Collection<? extends Task> epics = listCollectionAllTasks.get(1);
        for (Task receivedEpic : epics) {
            assertEquals(receivedEpic, epicTest, "В списке отсутствует созданный эпик");
        }

        Collection<? extends Task> subtasks = listCollectionAllTasks.get(2);
        for (Task receivedSubtask : subtasks) {
            assertEquals(receivedSubtask, subtaskTest, "В списке отсутствует созданная подзадача");
        }
    }

    void shouldGetPrioritizedTasks() {  // Проверка получения списка задач по приоритету
        Task taskForTest1 = new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now().plusMinutes(45), Duration.ofMinutes(30));
        Task taskTest1 = manager.createTask(taskForTest1);
        manager.createTask(task);

        assertNotNull(manager.getPrioritizedTasks(), "задачи не были добавлены в список приоритетов");
        assertEquals(taskTest1, manager.getPrioritizedTasks().get(1), "Сортировка по startTime не произошла");
    }

    void shouldUpdatePrioritizedTasks() {  // Проверка обновления приоритетного списка после обновления задач
        Task task1 = new Task("Test Task1", "Test Task1 description", Status.NEW,
                null, null);
        Epic epic1 = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                LocalDateTime.now().plusMinutes(75), Duration.ofMinutes(3));
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description", Status.NEW,
                LocalDateTime.now().plusMinutes(150), Duration.ofMinutes(3));

        Task taskTest1 = manager.createTask(task1);  // nullList
        Epic epicTest1 = manager.createEpic(epic1);
        Subtask subtaskTest1 = manager.createSubTask(subtask1, epicTest1.getId()); //priorityList

        Task taskTest = manager.createTask(task); //priorityList
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());  //priorityList

        Task taskForUpdate1 = new Task("Test updatedTask", "Test updatedTask description", Status.NEW,
                LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(3));
        manager.updateTask(taskTest1.getId(), taskForUpdate1);

        Subtask subtaskForUpdate = new Subtask("Test updatedSubtask", "Test updatedSubtask description",
                Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(210), Duration.ofMinutes(30));
        manager.updateSubtask(subtaskTest.getId(), subtaskForUpdate);

        assertEquals(4, manager.getPrioritizedTasks().size(), "Не правильное обновление списка " +
                "приоритетов при переопределении задач");
        assertEquals(taskTest, manager.getPrioritizedTasks().get(0),
                "Не верная сортировка при переопределении задач");
        assertEquals(manager.getTaskById(taskTest1.getId()), manager.getPrioritizedTasks().get(1),
                "Не верная сортировка при переопределении задач");
        assertEquals(subtaskTest1, manager.getPrioritizedTasks().get(2),
                "Не верная сортировка при переопределении задач");
        assertEquals(manager.getTaskById(subtaskTest.getId()), manager.getPrioritizedTasks().get(3),
                "Не верная сортировка при переопределении задач");

    }

    void shouldRemoveTaskFromListPrioritizedTasks() {  // Проверка удаления задач из списка приоритетов и его очистки
        Task taskForTest1 = new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now().plusMinutes(100), Duration.ofMinutes(30));
        Task taskTest1 = manager.createTask(taskForTest1);
        Task taskForTest2 = new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now().plusMinutes(45), Duration.ofMinutes(30));
        Task taskTest2 = manager.createTask(taskForTest2);
        manager.createTask(task);

        manager.removeById(taskTest2.getId());

        assertNotNull(manager.getPrioritizedTasks(), "При удалении задачи очистился весь список приоритетов");
        assertEquals(2, manager.getPrioritizedTasks().size(), "Задача не была удалена из " +
                "приоритетного списка");

        manager.clearTask();
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Список приоритетов не очистился");
    }

    void shouldNotOverlapTasks() {  // Проверка на пересечение задач
        manager.clearTask();
        Task taskForTest1 = new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        Task taskTest1 = manager.createTask(taskForTest1);
        manager.createTask(task);
        assertEquals(1, manager.getPrioritizedTasks().size(), "Фильтр на пересечение не сработал");
        assertEquals(taskTest1, manager.getTaskById(1), "Сохранилась не та задача");
    }

    void shouldGetEpicById() {  // Проверка наличия эпика по id
        Epic epicTest = manager.createEpic(epic);
        int epicId = epicTest.getId();
        Epic desiredEpic = (Epic) manager.getEpicById(epicId);

        assertNotNull(epicId, "Неверный (нулевой) ID эпика");
        assertNotNull(desiredEpic, "По данному ID необходимый эпик не обнаружен");
        assertEquals(epicTest, desiredEpic, "Найденный эпик не соответствует искомому");
    }

    void shouldGetSubtaskEpicId() {  // Проверка получения эпика подзадачи по id
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());
        Epic epicSubtasks = manager.getSubtaskEpicId(subtaskTest.getEpicId());

        assertNotNull(subtaskTest.getEpicId(), "Неверный (нулевой) ID эпика");
        assertNotNull(epicSubtasks, "По данным подзадачи эпик отсутствует");
        assertEquals(epicTest, epicSubtasks, "Из данных подзадачи получен не верный эпик");
    }

    void shouldGetSubtasksByEpicId() {  // Проверка получения списка всех подзадач определённого эпика
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest1 = manager.createSubTask(subtask, epicTest.getId());
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description", Status.IN_PROGRESS,
                LocalDateTime.now().plusMinutes(180), Duration.ofMinutes(15));
        Subtask subtaskTest2 = manager.createSubTask(subtask2, epicTest.getId());

        Map<Integer, Subtask> listSubtasks = manager.getSubtasksByEpicId(epicTest.getId());

        Subtask subtask1FromList = listSubtasks.get(subtaskTest1.getId());
        Subtask subtask2FromList = listSubtasks.get(subtaskTest2.getId());

        assertNotNull(subtaskTest1.getEpicId(), "Неверный (нулевой) ID эпика");
        assertNotNull(listSubtasks, "Список подзадач эпика пуст");
        assertEquals(subtaskTest1, subtask1FromList, "Подзадача отсутствует в эпике");
        assertEquals(subtaskTest2, subtask2FromList, "Подзадача отсутствует в эпике");
    }

    void shouldRemoveById() {  // Проверка удаления по идентификатору
        Task taskTest = manager.createTask(task);

        assertNotNull(taskTest, "Задача не найдена");
        assertNotNull(taskTest.getId(), "Неверный (нулевой) ID задачи");
        manager.removeById(taskTest.getId());
        assertNull(manager.getTaskById(taskTest.getId()), "Задача не была удалена");

        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());

        assertNotNull(subtaskTest, "Подзадача не найдена");
        assertNotNull(subtaskTest.getId(), "Неверный (нулевой) ID подзадачи");
        manager.removeById(subtaskTest.getId());
        assertNull(manager.getTaskById(subtaskTest.getId()), "Подзадача не была удалена");

        assertNotNull(epicTest, "Эпик не найдена");
        assertNotNull(epicTest.getId(), "Неверный (нулевой) ID эпика");
        manager.removeById(epicTest.getId());
        assertNull(manager.getTaskById(epicTest.getId()), "Эпик не был удален");
    }

    void shouldClearAllTask() {   // Проверка удалениея всех задач
        Task taskTest = manager.createTask(task);
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());

        assertNotNull(manager.getListOfAllTasks(), "Созданные задачи отсутствуют");
        assertNotNull(taskTest.getId(), "Неверный (нулевой) ID задачи");
        assertNotNull(subtaskTest.getId(), "Неверный (нулевой) ID подзадачи");
        assertNotNull(epicTest.getId(), "Неверный (нулевой) ID эпика");

        manager.clearTask();

        assertNull(manager.getTaskById(taskTest.getId()), "Задача не была удалена");
        assertNull(manager.getTaskById(epicTest.getId()), "Эпик не был удален");
        assertNull(manager.getTaskById(subtaskTest.getId()), "Подзадача не была удалена");
    }

    void shouldGetHistory() {  // Проверка получения истории просмотренных задач
        Task taskTest = manager.createTask(task);
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());

        manager.getTaskById(taskTest.getId());
        manager.getTaskById(epicTest.getId());
        manager.getTaskById(subtaskTest.getId());

        List<Task> listHistory = manager.getHistory();

        assertNotNull(listHistory, "Задачи не были внесены в историю");

        assertEquals(taskTest, listHistory.get(0), "Неверное отображение истории задач");
        assertEquals(epicTest, listHistory.get(1), "Неверное отображение истории задач");
        assertEquals(subtaskTest, listHistory.get(2), "Неверное отображение истории задач");
    }
}