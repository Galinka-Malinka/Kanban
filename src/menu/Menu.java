package menu;

import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Menu {
    public static void workWithMenu(TaskManager manager) {
        Scanner scanner = new Scanner(System.in);

        Task task;
        Epic epic;
        Subtask subtask;

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

                        scanner.nextLine();
                        LocalDateTime startTime = createStartTime();  // Создание времени начала выполнения задачи

                        Duration duration = createDuration();  // Создание продолжительности выполнения задачи

                        if (typeNumber == 1) {
                            task = new Task(name, description, status, startTime, duration);
                            if (manager.findingIntersectionsOfTasks(task).equals(task)) {
                                manager.createTask(task);
                            } else {
                                System.out.println("Заданное время выполнения задачи уже занято другой задачей: \n"
                                        + manager.findingIntersectionsOfTasks(task).toString());
                            }


                        } else if (typeNumber == 2) {
                            epic = new Epic(name, description, status, startTime, duration);
                            manager.createEpic(epic);
                        } else if (typeNumber == 3) {
                            subtask = new Subtask(name, description, status, startTime, duration);
                            if (manager.findingIntersectionsOfTasks(subtask).equals(subtask)) {
                                System.out.println("Введите id эпика, в который будет добавлена подзадача:");
                                int epicId = scanner.nextInt();

                                if (manager.getEpicById(epicId) == null) {
                                    System.out.println("Эпика с данным id не существует.");
                                    break;
                                }
                                Subtask generatedSubtask = manager.createSubTask(subtask, epicId);
                                manager.getSubtaskEpicId(epicId).addSubtask(generatedSubtask);
                                manager.getSubtaskEpicId(epicId).reviewStatus();
                            } else {
                                System.out.println("Заданное время выполнения задачи уже занято другой задачей: \n"
                                        + manager.findingIntersectionsOfTasks(subtask).toString());
                            }
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

                            scanner.nextLine();
                            LocalDateTime startTime = createStartTime();  // Создание времени начала выполнения задачи

                            Duration duration = createDuration();  // Создание продолжительности выполнения задачи

                            if (typeNumber == 1) {
                                task = new Task(name, description, status, startTime, duration);
                                manager.updateTask(id, task);
                            } else if (typeNumber == 2) {
                                epic = new Epic(name, description, status, startTime, duration);
                                manager.updateEpic(id, epic);
                            } else if (typeNumber == 3) {
                                subtask = new Subtask(name, description, status, startTime, duration);
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


            } else if (choice == 9) {  //  Получение списка всех задач в порядке приоритета.

                System.out.println(manager.getPrioritizedTasks());

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
                "9 - Получение списка всех задач в порядке приоритета.\n" +
                "0 - Выход из программы");
    }

    public static void printMenuStatusTask() {   // Выбор статуса
        System.out.println("Веведите статус задачи, где:\n" +
                "1 - задача только создана, но к её выполнению ещё не приступили\n" +
                "2 - над задачей ведётся работа\n" +
                "3 - задача выполнена");
    }

    public static void printMenuTypeTask() {  // Выбор типа задачи
        System.out.println("Выберите тип задачи: \n" +
                "1 - Простая задача \n" +
                "2 - Эпик\n" +
                "3 - Подзадача");
    }

    public static LocalDateTime createStartTime() {  // Создание времени начала выполнения задачи
        Scanner scanner = new Scanner(System.in);
        LocalDateTime startTime = null;
        System.out.println("Желаете ли Вы добавить дату начала выполнения задачи? \n"
                + "1 - Да\n"
                + "Другое число - Нет");

        int answer = scanner.nextInt();

        if (answer == 1) {
            System.out.println("Введите дату, когда предполагается приступить к выполнению задачи " +
                    "в числовом формате.\n"
                    + "Год:");
            int year = scanner.nextInt();

            System.out.println("Месяц:");
            int month = scanner.nextInt();
            while (true) {
                if (month < 1 || month > 12) {
                    System.out.println("Введите номер месяца от 1 до 12");
                    month = scanner.nextInt();
                    continue;
                }
                break;
            }
            System.out.println("День:");
            int dayOfMonth = scanner.nextInt();
            while (true) {
                if (dayOfMonth < 1 || dayOfMonth > 30) {
                    System.out.println("Введите номер дня от 1 до 30");
                    dayOfMonth = scanner.nextInt();
                    continue;
                }
                break;
            }
            System.out.println("Час:");
            int hour = scanner.nextInt();
            while (true) {
                if (hour < 0 || hour > 23) {
                    System.out.println("Введите час от 0 до 23");
                    hour = scanner.nextInt();
                    continue;
                }
                break;
            }
            System.out.println("Минуты:");
            int minute = scanner.nextInt();
            while (true) {
                if (minute < 0 || minute > 59) {
                    System.out.println("Введите час от 0 до 59");
                    minute = scanner.nextInt();
                    continue;
                }
                break;
            }

            startTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        }
        return startTime;
    }

    public static Duration createDuration() {    // Создание продолжительности выполнения задачи
        Scanner scanner = new Scanner(System.in);
        Duration duration = null;
        System.out.println("Желаете ли Вы добавить продолжительность выполнения задачи? \n"
                + "1 - Да\n"
                + "Другое число - Нет");

        int answerAboutCreate = scanner.nextInt();

        if (answerAboutCreate == 1) {
            System.out.println("Сколько времени займёт выполнение задачи в минутах?");
            long durationString = scanner.nextLong();
            duration = Duration.ofMinutes(durationString);
        }
        return duration;
    }
}