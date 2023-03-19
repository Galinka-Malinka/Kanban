package tasks;

import java.time.Duration;
import java.util.Objects;
import java.time.LocalDateTime;

public class Task {
    private String name;
    private String description;
    private Status status;

    protected int id;

    protected LocalDateTime startTime;

    protected Duration duration;
    protected LocalDateTime endTime;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        getEndTime();
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        } else if (duration == null) {
            endTime = startTime;
            return endTime;
        }
        endTime = startTime.plus(duration);
        return endTime;
    }

    public LocalDateTime getStartTime() {
        if (startTime == null) {
            return null;
        }
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        if (duration == null) {
            return null;
        }
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result = "Tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'';
        if (description != null) {
            result = result + ", description.length='" + description.length() + '\'';
        } else {
            result = result + ", description.length='null'";
        }
        return result + ", status='" + status + '\'' +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration) && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }
}