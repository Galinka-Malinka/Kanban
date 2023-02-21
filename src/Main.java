import managers.Managers;
import managers.TaskManager;
import menu.Menu;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();  // Объявление переменной, которая содержит
        //определённую реализацию ИФ managers.TaskManager

        Menu.workWithMenu(manager);  // Запуск меню
    }
}
