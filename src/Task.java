public class Task {

    private String name;
    private String description;
    private Main.Status status;
    private int id;


    public Task(String name, String description, Main.Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Main.Status getStatus() {
        return status;
    }

    public void setStatus(Main.Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'';
        if (description != null) {
            result = result + ", description.length='" + description.length() + '\'';
        } else {
            result = result + ", description.length='null'";
        }
        return result + ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
