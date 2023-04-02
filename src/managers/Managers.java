package managers;

import com.google.gson.Gson;

import utils.GsonFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Managers {

    public static TaskManager getDefault(URL url, String apiToken) throws MalformedURLException {
        return new HttpTaskManager(url, apiToken);
    }

    public static FileBackedTasksManager getFileBacked(String fileName) {
        File file = new File(fileName);
        return file.exists() ? new FileBackedTasksManager("labuda.txt").getLoadFromFile(file) : new FileBackedTasksManager(fileName);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return GsonFactory.getGson();
    }

}
