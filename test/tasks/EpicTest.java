package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    Epic epic;
    Subtask subtaskNEW;
    Subtask subtaskIN_PROGRESS;
    Subtask subtaskDONE;

    @BeforeEach
    public void create() {
        epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        subtaskNEW = new Subtask("Test SubtaskNEW", "Test SubtaskNEW description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        subtaskIN_PROGRESS = new Subtask("Test SubtaskIN_PROGRESS",
                "Test SubtaskIN_PROGRESS description", Status.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(30));
        subtaskDONE = new Subtask("Test SubtaskDONE", "Test SubtaskDONE description", Status.DONE,
                LocalDateTime.now(), Duration.ofMinutes(30));
    }

    @Test
    void shouldBeStatusNewWithoutSubtasks() {  //  Если пустой список подзадач.
        Map<Integer, Subtask> listSubtask = epic.getSubtasks();
        assertTrue(listSubtask.isEmpty(), "Список подзадач не пустой.");
    }

    @Test
    void shouldChangeStatusIfOlTasksNew() {  // Если все подзадачи со статусом NEW.
        epic.setStatus(Status.IN_PROGRESS);
        epic.addSubtask(subtaskNEW);
        epic.reviewStatus();
        String status = epic.getStatus().toString();
        assertEquals("NEW", status, "Статус эпика не изменился на NEW.");
    }

    @Test
    void shouldChangeStatusIfOlTasksInProgress() {  // Если все подзадачи со статусом IN_PROGRESS.
        epic.addSubtask(subtaskIN_PROGRESS);
        epic.reviewStatus();
        String status = epic.getStatus().toString();
        assertEquals("IN_PROGRESS", status, "Статус эпика не изменился на IN_PROGRESS.");
    }

    @Test
    void shouldChangeStatusIfOlTasksDone() {  // Если все подзадачи со статусом DONE.
        epic.addSubtask(subtaskDONE);
        epic.reviewStatus();
        String status = epic.getStatus().toString();
        assertEquals("DONE", status, "Статус эпика не изменился на DONE.");
    }

    @Test
    void shouldChangeStatusIfTasksNewAndDone() {  // Если подзадачи со статусами NEW и DONE.
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic managerEpic = manager.createEpic(epic);
        Subtask managerSubTask1 = manager.createSubTask(subtaskDONE, managerEpic.getId());
        Subtask managerSubTask2 = manager.createSubTask(subtaskNEW, managerEpic.getId());

        managerEpic.addSubtask(managerSubTask1);
        managerEpic.addSubtask(managerSubTask2);
        managerEpic.reviewStatus();
        String status = managerEpic.getStatus().toString();
        assertEquals("IN_PROGRESS", status, "Статус эпика не изменился на IN_PROGRESS.");
    }
}