public class Subtask extends Task {

    private int id;
    private int epicId;

    public Subtask(String name, String description, Main.Status status) {

        super(name, description, status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'';
        if (this.getDescription() != null) {
            result = result + ", description.length='" + this.getDescription().length() + '\'';
        } else {
            result = result + ", description.length='null'";
        }
        return result + ", status='" + this.getStatus() + '\'' +
                ", id=" + id +
                '}';
    }
}
