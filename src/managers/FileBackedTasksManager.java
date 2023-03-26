package managers;

import tasks.*;
import exception.ManagerSaveException;
import menu.Menu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите имя файла");
        String fileName = scanner.nextLine();

        File newFile = new File(fileName);
        FileBackedTasksManager manager = newFile.exists() ? loadFromFile(newFile) : new FileBackedTasksManager(newFile);

        Menu.workWithMenu(manager);  // Запуск меню
    }

    public void save() {  // Метод сохранения в файл
        List<Collection<? extends Task>> listOfOllTask = super.getListOfAllTasks();  //Получение списока всех задач

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName(), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic,startTime,duration");
            writer.newLine();
            if (!listOfOllTask.isEmpty()) {
                for (Collection<? extends Task> tasks : listOfOllTask) {
                    for (Task task : tasks) {
                        writer.write(toString(task));
                        writer.newLine();
                    }
                }
            }
            writer.newLine();
            writer.write(historyToString(super.historyManager));
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
            throw new ManagerSaveException();  // Кинули собственное исключение
        }
    }

    public String toString(Task task) {  // Строковое представление задач
        StringBuilder designerTaskDescription = new StringBuilder();
        if (task instanceof Epic) {
            designerTaskDescription.append(task.getId()).append(",").append(Type.EPIC).append(",")
                    .append(task.getName()).append(",").append(task.getStatus().toString()).append(",")
                    .append(task.getDescription()).append(",").append(" ").append(",")
                    .append(task.getStartTime()).append(",").append(task.getDuration()).append(",");
        } else if (task instanceof Subtask) {
            designerTaskDescription.append(task.getId()).append(",").append(Type.SUBTASK).append(",")
                    .append(task.getName()).append(",").append(task.getStatus().toString()).append(",")
                    .append(task.getDescription()).append(",").append(((Subtask) task).getEpicId()).append(",")
                    .append(task.getStartTime()).append(",").append(task.getDuration()).append(",");
        } else {
            designerTaskDescription.append(task.getId()).append(",").append(Type.TASK).append(",")
                    .append(task.getName()).append(",").append(task.getStatus().toString()).append(",")
                    .append(task.getDescription()).append(",").append(" ").append(",")
                    .append(task.getStartTime()).append(",").append(task.getDuration()).append(",");
        }
        return designerTaskDescription.toString();
    }

    static String historyToString(HistoryManager manager) { // Строка, содержащая список id задач из истории просмотров
        List<Task> listTask = manager.getHistory();
        if (listTask.isEmpty()) {
            return "История пуста";
        } else {
            StringBuilder listId = new StringBuilder();
            for (Task taskOfList : listTask) {
                listId.append(taskOfList.getId()).append(",");
            }
            listId.deleteCharAt(listId.lastIndexOf(","));

            return listId.toString();
        }
    }

    public FileBackedTasksManager getLoadFromFile(File file) {
        return loadFromFile(file);
    }

    static FileBackedTasksManager loadFromFile(File file) {  // Восстановление данных из файла
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try {
            String content = Files.readString(Path.of(file.getPath()));

            String[] allTasksFromFile = content.split("\n");
            for (String task : allTasksFromFile) {
                if (!task.equals(allTasksFromFile[0]) && !task.isBlank()) {
                    if (task.equals(allTasksFromFile[allTasksFromFile.length - 1])) {
                        HistoryManager historyManager = Managers.getDefaultHistory();

                        List<Integer> listIdTaskOfHistory = historyFromString(task);
                        for (int taskId : listIdTaskOfHistory) {
                            historyManager.add(fileBackedTasksManager.getTaskById(taskId));
                        }
                    } else {
                        Task element = fromString(task);
                        if (element instanceof Epic) {
                            fileBackedTasksManager.createEpicFromFile((Epic) element);
                        } else if (element instanceof Subtask) {
                            fileBackedTasksManager.createSubtaskFromFile((Subtask) element);
                        } else {
                            fileBackedTasksManager.createTaskFromFile(element);
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Такой тип Task не поддерживается");
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return fileBackedTasksManager;
    }

    static Task fromString(String value) throws IllegalArgumentException {  // Метод создания задачи из строки
        String[] elementsOfTask = value.split(",");
        //id,type,name,status,description,epic,startTime,duration,
        int id = Integer.parseInt(elementsOfTask[0]);
        String name = elementsOfTask[2];
        Status status;
        String description = elementsOfTask[4];
        String startTimeString = elementsOfTask[6];
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);
        String durationString = elementsOfTask[7];
        Duration duration = Duration.parse(durationString);

        if (elementsOfTask[3].equals(Status.NEW.toString())) {
            status = Status.NEW;
        } else if (elementsOfTask[3].equals(Status.IN_PROGRESS.toString())) {
            status = Status.IN_PROGRESS;
        } else {
            status = Status.DONE;
        }

        if (elementsOfTask[1].equals(Type.TASK.toString())) {
            Task newTask = new Task(name, description, status, startTime, duration);
            newTask.setId(id);
            return newTask;
        } else if (elementsOfTask[1].equals(Type.EPIC.toString())) {
            Epic newEpic = new Epic(name, description, status, startTime, duration);
            newEpic.setId(id);
            return newEpic;
        } else if (elementsOfTask[1].equals(Type.SUBTASK.toString())) {
            Subtask newSubtask = new Subtask(name, description, status, startTime, duration);
            newSubtask.setId(id);
            newSubtask.setEpicId(Integer.parseInt(elementsOfTask[5]));
            return newSubtask;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void createTaskFromFile(Task task) {  // Запись задачи из файла в общую таблицу задач
        if (super.getId() < task.getId()) {
            super.setId(task.getId());
        }
        super.addInTaskHashMap(task.getId(), task);
    }

    public void createSubtaskFromFile(Subtask subtask) {  // Запись подзадачи из файла в общую таблицу подзадач
        if (super.getId() < subtask.getId()) {
            super.setId(subtask.getId());
        }
        super.addInSubtaskHashMap(subtask.getId(), subtask);
    }

    public void createEpicFromFile(Epic epic) {  // Запись эпика из файла в общую таблицу эпиков
        if (super.getId() < epic.getId()) {
            super.setId(epic.getId());
        }
        super.addInEpicHashMap(epic.getId(), epic);
    }

    static List<Integer> historyFromString(String value) {  // Получение из файла списка id просмотренных задач
        List<Integer> listOfIdTasksFromHistory = new ArrayList<>();
        if (!value.equals("История пуста")) {
            String[] idTasksOfHistory = value.split(",");
            for (String idOfTask : idTasksOfHistory) {
                listOfIdTasksFromHistory.add(Integer.parseInt(idOfTask));
            }
        }
        return listOfIdTasksFromHistory;
    }

    @Override
    public Task createTask(Task task) {  // Создание задачи
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask, int epicId) { // Создание подзадачи
        super.createSubTask(subtask, epicId);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) { // Создание эпика
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(int firstId, Task task) { // Обновление задачи
        super.updateTask(firstId, task);
        save();
    }

    @Override
    public void updateSubtask(int firstId, Subtask subtask) { // Обновление подзадачи
        super.updateSubtask(firstId, subtask);
        save();
    }

    @Override
    public void updateEpic(int firstId, Epic epic) { // Обновление эпика
        super.updateEpic(firstId, epic);
        save();
    }

    @Override
    public Task getEpicById(int id) {  // Проверка наличия эпика по id
        return super.getEpicById(id);
    }

    @Override
    public Epic getSubtaskEpicId(int id) {  // Получение эпика по id
        return super.getSubtaskEpicId(id);
    }

    @Override
    public Map<Integer, Subtask> getSubtasksByEpicId(int id) {  // Получение списка всех подзадач определённого эпика.
        Map<Integer, Subtask> listOfSubtasks = super.getSubtasksByEpicId(id);
        save();
        return listOfSubtasks;
    }

    @Override
    public Task getTaskById(int id) {  // Получение по идентификатору
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public List<Collection<? extends Task>> getListOfAllTasks() {  // Получение списка всех задач.
        return super.getListOfAllTasks();
    }

    @Override
    public List<Task> getPrioritizedTasks() {  // Получение списка задач в порядке приоритета
        return super.getPrioritizedTasks();
    }

    @Override
    public void removeById(int id) {  // Удаление по идентификатору.
        super.removeById(id);
        save();
    }

    @Override
    public void clearTask() {  // Удаление всех задач.
        super.clearTask();
        save();
    }

    @Override
    public List<Task> getHistory() {  // История просмотров последних 10 задач
        return super.getHistory();
    }
}