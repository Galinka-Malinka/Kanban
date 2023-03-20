package managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static managers.FileBackedTasksManager.loadFromFile;

class FileBackedTasksManagerTest extends TaskManagerTest {
    HistoryManager historyManager;
    File file = new File("test.txt");
    FileBackedTasksManager manager = file.exists() ? loadFromFile(file) : new FileBackedTasksManager(file);

    @BeforeEach
    void create() {
        super.create(manager);
    }

    @AfterEach
    void clear() {
        super.clear();
    }

    @AfterEach
    void clearFile() {
        manager.clearTask();
    }

    @Override
    @Test
    void shouldCreateTask() {  // Проверка создания задачи
        super.shouldCreateTask();
    }

    @Override
    @Test
    void shouldCreateSubtask() {  // Проверка создания подзадачи
        super.shouldCreateSubtask();
    }

    @Override
    @Test
    void shouldCreateEpic() {  // Проверка создания эпика
        super.shouldCreateEpic();
    }

    @Override
    @Test
    void shouldNotOverlapTasks() { // Проверка на пересечение задач
        super.shouldNotOverlapTasks();
    }

    @Override
    @Test
    void shouldUpdateTask() {  // Проверка обновления задачи
        super.shouldUpdateTask();
    }

    @Override
    @Test
    void shouldUpdateSubtask() {  // Проверка обновления подзадачи
        super.shouldUpdateSubtask();
    }

    @Override
    @Test
    void shouldUpdateEpic() {  // Проверка обновления эпика
        super.shouldUpdateEpic();
    }

    @Override
    @Test
    void shouldGetTaskById() {  // Проверка получения по идентификатору
        super.shouldGetTaskById();
    }

    @Override
    @Test
    void shouldGetListOfAllTasks() {  // Проверка получения списка всех задач
        super.shouldGetListOfAllTasks();
    }

    @Override
    @Test
    void shouldGetPrioritizedTasks() {  // Проверка получения списка задач по приоритету
        super.shouldGetPrioritizedTasks();
    }

    @Override
    @Test
    void shouldUpdatePrioritizedTasks() {  // Проверка обновления приоритетного списка после обновления задач
        super.shouldUpdatePrioritizedTasks();
    }

    @Override
    @Test
    void shouldRemoveTaskFromListPrioritizedTasks() {  // Проверка удаления задач из списка приоритетов и его очистки
        super.shouldRemoveTaskFromListPrioritizedTasks();
    }

    @Override
    @Test
    void shouldGetEpicById() {  // Проверка наличия эпика по id
        super.shouldGetEpicById();
    }

    @Override
    @Test
    void shouldGetSubtaskEpicId() {  // Проверка получения эпика подзадачи по id
        super.shouldGetSubtaskEpicId();
    }

    @Override
    @Test
    void shouldGetSubtasksByEpicId() {  // Проверка получения списка всех подзадач определённого эпика
        super.shouldGetSubtasksByEpicId();
    }

    @Override
    @Test
    void shouldRemoveById() {  // Проверка удаления по идентификатору
        super.shouldRemoveById();
    }

    @Override
    @Test
    void shouldClearAllTask() {   // Проверка удалениея всех задач
        super.shouldClearAllTask();
    }

    @Test
    void shouldSave() {  //  Проверка сохранения и загрузки файла
        task = new Task("Test Task", "Test Task description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        subtask = new Subtask("Test Subtask", "Test Subtask description",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        Task taskTest = manager.createTask(task);
        Epic epicTest = manager.createEpic(epic);
        Subtask subtaskTest = manager.createSubTask(subtask, epicTest.getId());

        assertNotNull(manager.getListOfAllTasks(), "К сохранению передан пустой список задач");
        assertNotNull(epicTest.getSubtasks(), "Эпик не содержит подзадачи");
        assertNotNull(manager.getHistory(), "История просмотренных задач пуста");

        manager.save();
        FileBackedTasksManager managerLoad = loadFromFile(file);
        Epic epicLoad = (Epic) managerLoad.getEpicById(epicTest.getId());

        assertNotNull(managerLoad.getEpicById(epicTest.getId()), "Эпик, переданный для сохранения отсутствует");
        assertNotNull(managerLoad.getListOfAllTasks(), "Загрузился пустой список задач");
        assertNotNull(epicLoad.getSubtasks(), "Отсутствуют подзадачи у загруженного эпика");
        assertNotNull(managerLoad.getHistory(), "Загрузилась пустая история просмотренных задач");

        assertEquals(manager.getTaskById(1), managerLoad.getTaskById(1),
                "Загруженная задача не соответствует сохранённой");
        assertEquals(manager.getTaskById(2), managerLoad.getTaskById(2),
                "Загруженный эпик не соответствует сохранённому");
        assertEquals(manager.getTaskById(3), managerLoad.getTaskById(3),
                "Загруженная подзадача не соответствует сохранённой");

        manager.clearTask();
        manager.save();
        FileBackedTasksManager managerLoadEmptyFile = loadFromFile(file);
        assertTrue(managerLoadEmptyFile.getHistory().isEmpty(), "Неверная загрузка файла с чистой историей");
    }

    @Override
    @Test
    void shouldGetHistory() {  // Проверка получения истории просмотренных задач
        super.historyManager = historyManager;
        super.shouldGetHistory();
    }
}