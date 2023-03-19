package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest extends TaskManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    void create() {
        super.create(new InMemoryTaskManager());
    }

    @AfterEach
    void clear() {
        super.clear();
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

    @Override
    @Test
    void shouldGetHistory() {  // Проверка получения истории просмотренных задач
        super.historyManager = historyManager;
        super.shouldGetHistory();
    }
}