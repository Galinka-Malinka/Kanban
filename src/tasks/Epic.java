package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        subtask.setEpicId(id);
        calculationOfDuration();
    }

    private void calculationOfDuration() {
        Map<Integer, Subtask> subtaskMap = getSubtasks();

        LocalDateTime earliestStartTime = getStartTime();
        LocalDateTime latestEndTime = getEndTime();

        for (Subtask subtask : subtaskMap.values()) {

            if (subtaskMap.size() == 1 || getStartTime() == null) {
                earliestStartTime = subtask.getStartTime();
                latestEndTime = subtask.getEndTime();
            }
            if (subtask.getStartTime().isBefore(earliestStartTime)) {
                earliestStartTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(latestEndTime)) {
                latestEndTime = subtask.getEndTime();
            }
        }
        if (earliestStartTime != null) {
            setStartTime(earliestStartTime);
            setEndTime(latestEndTime);

            Duration durationNew = Duration.between(earliestStartTime, latestEndTime);
            setDuration(durationNew);
        }
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
        if (getSubtasks().isEmpty()) {
            setStartTime(null);
            setDuration(null);
        }
        calculationOfDuration();
    }

    public void reviewStatus() {
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        int amountStatusNew = 0;

        int amountStatusDone = 0;

        for (Subtask title : subtasks.values()) {
            if (title.getStatus() == Status.NEW) {
                amountStatusNew = amountStatusNew + 1;
            } else if (title.getStatus() == Status.DONE) {
                amountStatusDone = amountStatusDone + 1;
            }
        }
        if (amountStatusNew == subtasks.size()) {
            this.setStatus(Status.NEW);
        } else if (amountStatusDone == subtasks.size()) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}