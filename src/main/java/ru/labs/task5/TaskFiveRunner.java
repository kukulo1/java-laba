package ru.labs.task5;

import ru.labs.DbHelper;

import java.util.Scanner;

public class TaskFiveRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab5_strings";
    private static boolean isTableCreated = false;

    private static StringBuffer inputOne;
    private static StringBuffer inputTwo;

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        isTableCreated = false;

        inputOne = new StringBuffer(readValidated("Введите первую строку длиной не менее 50 символов: "));
        inputTwo = new StringBuffer(readValidated("Введите вторую строку длиной не менее 50 символовЫ: "));

        int choice;
        do {
            displayMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            handleAction(choice);
        } while (choice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Изменить порядок символов строк на обратный, сохранить в MySQL и вывести в консоль");
        System.out.println("4. Добавить одну строку в другую, сохранить в MySQL и вывести в консоль");
        System.out.println("5. Сохранить все данные из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void handleAction(int choice) {
        if (choice >= 3 && choice <= 5 && !isTableCreated) {
            System.out.println("Ошибка, таблица не создана.");
            return;
        }

        switch (choice) {
            case 1 -> DbHelper.listTables();
            case 2 -> {
                DbHelper.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "operation VARCHAR(255), " +
                        "value1 TEXT, " +
                        "value2 TEXT, " +
                        "result TEXT)");
                isTableCreated = true;
                System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
            }
            case 3 -> {
                String reversed1 = new StringBuffer(inputOne).reverse().toString();
                String reversed2 = new StringBuffer(inputTwo).reverse().toString();

                System.out.println("Реверс первой строки: " + reversed1);
                System.out.println("Реверс второй строки: " + reversed2);

                String result = "Reversed1: " + reversed1 + "; Reversed2: " + reversed2;
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Reverse", inputOne.toString(), inputTwo.toString(), result);
            }
            case 4 -> {
                String beforeAppend = inputOne.toString();
                inputOne.append(inputTwo);
                String after = inputOne.toString();

                System.out.println("Результат после добавления: " + after);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Append", beforeAppend, inputTwo.toString(), after);
            }
            case 5 -> {
                DbHelper.exportToCsv(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                DbHelper.selectAllFromTable(TABLE_NAME, "id", "operation", "value1", "value2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static String readValidated(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.length() < 50) {
                System.out.printf("Ошибка: строка должна быть не менее 50 символов (у вас %d)%n", input.length());
            }
        } while (input.length() < 50);
        return input;
    }
}
