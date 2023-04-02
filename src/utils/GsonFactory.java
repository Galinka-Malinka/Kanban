package utils;

import com.google.gson.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonFactory {
    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateSerializer())
                .registerTypeAdapter(Duration.class, new DurationSerializer())
                .setPrettyPrinting().create();
        return gson;
    }
}








