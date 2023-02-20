package managers;

import tasks.*;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Task task;
        Epic epic;
        Subtask subtask;

        System.out.println("Введите имя файла");
        String fileName = scanner.nextLine();

        File newFile = new File(fileName);
        FileBackedTasksManager manager = newFile.exists() ? loadFromFile(newFile) : new FileBackedTasksManager(newFile);

        printMenu();
        int choice = scanner.nextInt();

        while (choice != 0) {

            if (choice == 1) {   // Создание задачи
                scanner.nextLine();
                System.out.println("Выберите тип задачи: \n" +
                        "1 - Простая задача \n" +
                        "2 - Эпик\n" +
                        "3 - Подзадача");

                int typeNumber = scanner.nextInt();

                while (true) {
                    if (typeNumber > 3) {
                        System.out.println("Такой команды нет. Выберите действие 1, 2 или 3.");
                        printMenuTypeTask();
                        typeNumber = scanner.nextInt();
                        continue;
                    } else if (typeNumber < 1) {
                        System.out.println("Такой команды нет. Выберите действие 1, 2 или 3.");
                        printMenuTypeTask();
                        typeNumber = scanner.nextInt();
                        continue;
                    } else {
                        scanner.nextLine();
                        System.out.println("Веведите название задачи");
                        String name = scanner.nextLine();

                        System.out.println("Веведите описание задачи");
                        String description = scanner.nextLine();

                        printMenuStatusTask();  // Выбор статуса задачи

                        Status status;

                        int input = scanner.nextInt();
                        while (true) {
                            if (input == 1) {
                                status = Status.NEW;
                            } else if (input == 2) {
                                status = Status.IN_PROGRESS;
                            } else if (input == 3) {
                                status = Status.DONE;
                            } else {
                                System.out.println("Такой команды нет. Выберите действие от 1 до 3.");
                                printMenuStatusTask();
                                input = scanner.nextInt();
                                continue;
                            }
                            break;
                        }

                        if (typeNumber == 1) {
                            task = new Task(name, description, status);
                            manager.createTask(task);

                        } else if (typeNumber == 2) {
                            epic = new Epic(name, description, status);
                            manager.createEpic(epic);
                        } else if (typeNumber == 3) {
                            subtask = new Subtask(name, description, status);

                            System.out.println("Введите id эпика, в который будет добавлена подзадача:");
                            int epicId = scanner.nextInt();

                            if (manager.getEpicById(epicId) == null) {
                                System.out.println("Эпика с данным id не существует.");
                                break;
                            }
                            Subtask generatedSubtask = manager.createSubTask(subtask, epicId);
                            manager.getSubtaskEpicId(epicId).addSubtask(generatedSubtask);
                            manager.getSubtaskEpicId(epicId).reviewStatus();
                        }
                        break;
                    }
                }

            } else if (choice == 2) {  // Получение задачи/эпика/подзадачи по id

                System.out.println("Введите id задачи.");
                int id = scanner.nextInt();
                System.out.println(manager.getTaskById(id));

            } else if (choice == 3) {  // Обновление задачи/эпика/подзадачи по id
                scanner.nextLine();
                System.out.println("Выберите тип задачи: \n" +
                        "1 - Простая задача \n" +
                        "2 - Эпик\n" +
                        "3 - Подзадача");

                int typeNumber = scanner.nextInt();

                while (true) {
                    if (typeNumber > 3) {
                        System.out.println("Такой команды нет. Выберите действие 1, 2 или 3.");
                        printMenuTypeTask();
                        typeNumber = scanner.nextInt();
                        continue;
                    } else if (typeNumber < 1) {
                        System.out.println("Такой команды нет. Выберите действие 1, 2 или 3.");
                        printMenuTypeTask();
                        typeNumber = scanner.nextInt();
                        continue;
                    } else {
                        System.out.println("Введите id задачи.");
                        int id = scanner.nextInt();

                        if (manager.getTaskById(id) == null) {  // Проверка на наличия задачи/эпика/подзадачи по id
                            System.out.println("Задачи с данным id не существует.");
                            break;
                        }

                        scanner.nextLine();
                        System.out.println("Веведите название задачи");
                        String name = scanner.nextLine();

                        System.out.println("Веведите описание задачи");
                        String description = scanner.nextLine();

                        printMenuStatusTask(); // Выбор статуса

                        Status status;

                        int input = scanner.nextInt();
                        while (true) {

                            if (input == 1) {
                                status = Status.NEW;
                            } else if (input == 2) {
                                status = Status.IN_PROGRESS;
                            } else if (input == 3) {
                                status = Status.DONE;
                            } else {
                                System.out.println("Такой команды нет. Выберите действие от 1 до 3.");
                                printMenuStatusTask();
                                input = scanner.nextInt();
                                continue;
                            }

                            if (typeNumber == 1) {
                                task = new Task(name, description, status);
                                manager.updateTask(id, task);
                            } else if (typeNumber == 2) {
                                epic = new Epic(name, description, status);
                                manager.updateEpic(id, epic);
                            } else if (typeNumber == 3) {
                                subtask = new Subtask(name, description, status);
                                manager.updateSubtask(id, subtask);
                            }
                            break;
                        }
                    }
                    break;
                }
            } else if (choice == 4) {  // Удаление по id
                System.out.println("Введите id задачи.");
                int id = scanner.nextInt();
                manager.removeById(id);

            } else if (choice == 5) {  // Получение списка всех задач

                System.out.println(manager.getListOfAllTasks());

            } else if (choice == 6) {  //  Удаление всех задач

                manager.clearTask();

            } else if (choice == 7) {  // Получение списка всех подзадач определённого эпика
                System.out.println("Введите id эпика.");
                int id = scanner.nextInt();

                if (manager.getEpicById(id) == null) {
                    System.out.println("Эпика с данным id не существует.");
                    printMenu();
                    choice = scanner.nextInt();
                    continue;
                }
                System.out.println(manager.getSubtasksByEpicId(id));

            } else if (choice == 8) {  //  Получение истории просмотренных задач.

                System.out.println(manager.getHistory());

            } else {
                System.out.println("Такой команды нет. Выберите действие от 0 до 7");
                printMenu();
                choice = scanner.nextInt();
                continue;
            }
            printMenu();
            choice = scanner.nextInt();
        }
    }

    public static void printMenu() {
        System.out.println("Выберите действие:\n" +
                "1 - Создание.\n" +
                "2 - Получение по идентификатору.\n" +
                "3 - Обновление.\n" +
                "4 - Удаление по идентификатору.\n" +
                "5 - Получение списка всех задач.\n" +
                "6 - Удаление всех задач.\n" +
                "7 - Получение списка всех подзадач определённого эпика.\n" +
                "8 - Получение истории просмотренных задач.\n" +
                "0 - Выход из программы");
    }

    public static void printMenuStatusTask() {
        System.out.println("Веведите статус задачи, где:\n" +
                "1 - задача только создана, но к её выполнению ещё не приступили\n" +
                "2 - над задачей ведётся работа\n" +
                "3 - задача выполнена");
    }

    public static void printMenuTypeTask() {
        System.out.println("Выберите тип задачи: \n" +
                "1 - Простая задача \n" +
                "2 - Эпик\n" +
                "3 - Подзадача");
    }

    public void save() {  // Метод сохранения в файл
        List<Collection<? extends Task>> listOfOllTask = super.getListOfAllTasks();  //Получение списока всех задач

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName(), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
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
            throw new ManagerSaveException();  //Кинули собственное исключение
        }
    }

    public String toString(Task task) {  //Строковое представление задач
        StringBuilder designerTaskDescription = new StringBuilder();
        if (task instanceof Epic) {
            designerTaskDescription.append(task.getId() + ","
                    + Type.EPIC + "," + task.getName() + ","
                    + task.getStatus().toString() + "," + task.getDescription() + ",");
        } else if (task instanceof Subtask) {
            designerTaskDescription.append(task.getId() + ","
                    + Type.SUBTASK + "," + task.getName() + ","
                    + task.getStatus().toString() + "," + task.getDescription() + ","
                    + ((Subtask) task).getEpicId() + ",");
        } else {
            designerTaskDescription.append(task.getId() + ","
                    + Type.TASK + "," + task.getName() + ","
                    + task.getStatus().toString() + "," + task.getDescription() + ",");
        }

        return designerTaskDescription.toString();
    }

    static String historyToString(HistoryManager manager) { //Строка, содержащая список id задач из истории просмотров
        List<Task> listTask = manager.getHistory();
        if (listTask.isEmpty()) {
            return "История пуста";
        } else {
            StringBuilder listId = new StringBuilder();
            for (Task taskOfList : listTask) {
                listId.append(taskOfList.getId() + ",");
            }
            listId.deleteCharAt(listId.lastIndexOf(","));

            return listId.toString();
        }
    }

    static FileBackedTasksManager loadFromFile(File file) {  //Восстановление данных из файла
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try {
            String content = Files.readString(Path.of(file.getPath()));

            String[] allTasksFromFile = content.split("\n");
            for (String task : allTasksFromFile) {
                if (task.equals(allTasksFromFile[0])) {
                    continue;
                } else if (task.isBlank()) {
                    continue;
                } else if (task.equals(allTasksFromFile[allTasksFromFile.length - 1])) {
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
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return fileBackedTasksManager;
    }

    static Task fromString(String value) {  //Метод создания задачи из строки
        String[] elementsOfTask = value.split(",");

        int id = Integer.parseInt(elementsOfTask[0]);
        String name = elementsOfTask[2];
        String description = elementsOfTask[4];
        Status status;

        if (elementsOfTask[3].equals(Status.NEW.toString())) {
            status = Status.NEW;
        } else if (elementsOfTask[3].equals(Status.IN_PROGRESS.toString())) {
            status = Status.IN_PROGRESS;
        } else {
            status = Status.DONE;
        }

        if (elementsOfTask[1].equals(Type.TASK.toString())) {
            Task newTask = new Task(name, description, status);
            newTask.setId(id);
            return newTask;
        } else if (elementsOfTask[1].equals(Type.EPIC.toString())) {
            Epic newEpic = new Epic(name, description, status);
            newEpic.setId(id);
            return newEpic;
        } else if (elementsOfTask[1].equals(Type.SUBTASK.toString())) {
            Subtask newSubtask = new Subtask(name, description, status);
            newSubtask.setId(id);
            newSubtask.setEpicId(Integer.parseInt(elementsOfTask[5]));
            return newSubtask;
        }
        return null;
    }

    public void createTaskFromFile(Task task) {  //Запись задачи из файла в общую таблицу задач
        if (super.getId() < task.getId()) {
            super.setId(task.getId());
        }
        super.addInTaskHashMap(task.getId(), task);
    }

    public void createSubtaskFromFile(Subtask subtask) {  //Запись подзадачи из файла в общую таблицу подзадач
        if (super.getId() < subtask.getId()) {
            super.setId(subtask.getId());
        }
        super.addInSubtaskHashMap(subtask.getId(), subtask);
    }

    public void createEpicFromFile(Epic epic) {  //Запись эпика из файла в общую таблицу эпиков
        if (super.getId() < epic.getId()) {
            super.setId(epic.getId());
        }
        super.addInEpicHashMap(epic.getId(), epic);
    }

    static List<Integer> historyFromString(String value) {  //Получение из файла списка id просмотренных задач
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
    public Task createTask(Task task) {  //Создание задачи
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask, int epicId) { //Создание подзадачи
        super.createSubTask(subtask, epicId);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) { //Создание эпика
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(int firstId, Task task) { //Обновление задачи
        super.updateTask(firstId, task);
        save();
    }

    @Override
    public void updateSubtask(int firstId, Subtask subtask) { //Обновление подзадачи
        super.updateSubtask(firstId, subtask);
        save();
    }

    @Override
    public void updateEpic(int firstId, Epic epic) { //Обновление эпика
        super.updateEpic(firstId, epic);
        save();
    }

    @Override
    public Task getEpicById(int id) {  //Проверка наличия эпика по id
        return super.getEpicById(id);
    }

    @Override
    public Epic getSubtaskEpicId(int id) {  // Получение эпика по id
        return super.getSubtaskEpicId(id);
    }

    @Override
    public Map<Integer, Subtask> getSubtasksByEpicId(int id) {  //Получение списка всех подзадач определённого эпика.
        Map<Integer, Subtask> listOfSubtasks = super.getSubtasksByEpicId(id);
        save();
        return listOfSubtasks;
    }

    @Override
    public Task getTaskById(int id) {  //Получение по идентификатору
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public List<Collection<? extends Task>> getListOfAllTasks() {  // Получение списка всех задач.
        return super.getListOfAllTasks();
    }

    @Override
    public void removeById(int id) {  // Удаление по идентификатору.
        super.removeById(id);
        save();
    }

    @Override
    public void clearTask() {  //Удаление всех задач.
        super.clearTask();
        save();
    }

    public List<Task> getHistory() {  // История просмотров последних 10 задач
        return super.getHistory();
    }
}
