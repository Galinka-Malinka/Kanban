public class Task {

    String name;
    String description;
    String status;
    int id;


    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
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
