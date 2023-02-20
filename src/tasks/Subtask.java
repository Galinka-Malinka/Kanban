package tasks;

import tasks.Task;

public class Subtask extends Task {

    private int id;
    private int epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
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


}
