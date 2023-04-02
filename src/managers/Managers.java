package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;

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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateSerializer())
                .registerTypeAdapter(Duration.class, new DurationSerializer())
                .serializeNulls()
                .setPrettyPrinting().create();
        return gson;
    }

}
