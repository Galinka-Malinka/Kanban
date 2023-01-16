import java.util.HashMap;

public class Epic extends Task {
    int id;
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    void getSubTask(Subtask subtask) {
        subtasks.put(subtask.id, subtask);
        subtask.epicId = id;
    }

    void removeSubtask(int id) {
        subtasks.remove(id);
    }

    void reviewStatus() {
        if (subtasks.isEmpty()) {
            status = "NEW";
        }

        int amountStatusNew = 0;

        int amountStatusDone = 0;

        for (Subtask title : subtasks.values()) {
            if (title.status == "NEW") {
                amountStatusNew = amountStatusNew + 1;
            } else if (title.status == "DONE") {
                amountStatusDone = amountStatusDone + 1;
            }
        }
        if (amountStatusNew == subtasks.size()) {
            status = "NEW";
        } else if (amountStatusDone == subtasks.size()) {
            status = "DONE";
        } else {
            status = "IN_PROGRESS";
        }
    }

    @Override
    public String toString() {
        String result = "Post{" + "name='" + name + '\'';

        if (description != null) {
            result = result + ", description.length='" + description.length() + '\'';
        } else {
            result = result + ", description.length='null'";
        }

        return result + ", status=" + status +
                ", id=" + id + '}';
    }
}

