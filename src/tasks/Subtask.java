package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        super.setId(id);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return id == subtask.id && epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, epicId);
    }
}