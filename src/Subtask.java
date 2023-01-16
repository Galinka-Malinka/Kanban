public class Subtask extends Task {

    int id;
    int epicId;

    public Subtask(String name, String description, String status) {
        super(name, description, status);
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
