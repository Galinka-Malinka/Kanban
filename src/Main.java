import managers.Managers;
import managers.TaskManager;
import menu.Menu;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите адрес сервера");
        String path = scanner.nextLine();
        URL url = new URL(path);

        System.out.println("У вас уже есть apiToken? \n" + "1 - да; другое число - нет");
        int answer = scanner.nextInt();

        String apiToken;
        scanner.nextLine();
        if (answer == 1) {
            System.out.println("Введите ваш apiToken");
            apiToken = scanner.nextLine();

            TaskManager manager = Managers.getDefault(url, apiToken);  // Объявление переменной, которая содержит
            //определённую реализацию ИФ managers.TaskManager

            Menu.workWithMenu(manager);  // Запуск меню
        } else {
            apiToken = "Отсутствует";
            System.out.println("Ваш apiToken " + apiToken);
            TaskManager manager = Managers.getDefault(url, apiToken);  // Объявление переменной, которая содержит
            //определённую реализацию ИФ managers.TaskManager

            Menu.workWithMenu(manager);  // Запуск меню
        }
    }
}
