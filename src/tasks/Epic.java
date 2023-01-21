package tasks;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private int id;
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Subtask> getSubtasks() {

        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        subtask.setEpicId(id);
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
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
}

