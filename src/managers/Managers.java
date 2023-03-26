package managers;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static FileBackedTasksManager getFileBacked(File file) {
        return file.exists() ? new FileBackedTasksManager(new File("labuda")).getLoadFromFile(file) : new FileBackedTasksManager(file);

    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
