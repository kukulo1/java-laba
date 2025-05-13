package ru.labs.task3;

import ru.labs.DbHelper;

import java.util.Scanner;

public class TaskThreeRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab3_integers";
    private static boolean isTableCreated = false;

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        isTableCreated = false;

        int choice;
        do {
            displayMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            handleChoice(choice);
        } while (choice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Проверить числа на целочисленность и чётность, результат сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Сохранить все данные из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void handleChoice(int choice) {
        if (choice >= 3 && choice <= 4 && !isTableCreated) {
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
                        "result TEXT)");
                isTableCreated = true;
                System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
            }
            case 3 -> runNumberCheck();
            case 4 -> {
                DbHelper.exportToXls(TABLE_NAME, TABLE_NAME);
                DbHelper.selectAllFromTable(TABLE_NAME, new String[]{"id", "operation", "value1", "result"}, new int[]{3, 20, 10, 20});
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void runNumberCheck() {
        System.out.println("Введите числа через пробел (для завершения введите 'q'):");
        while (true) {
            String input = scanner.next();
            if (input.equalsIgnoreCase("q")) break;

            try {
                double number = Double.parseDouble(input);
                if (number % 1 != 0) {
                    System.out.println(number + " — нецелое число");
                    continue;
                }
                if (number > Integer.MAX_VALUE || number < Integer.MIN_VALUE) {
                    System.out.println("Ошибка: число выходит за пределы int.");
                    continue;
                }
                int intVal = (int) number;
                boolean even = intVal % 2 == 0;
                String result = even ? "Even" : "Odd";
                System.out.println(intVal + " — целое " + (even ? "четное" : "нечетное") + " число");
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, result) VALUES (?, ?, ?)",
                        "Check", String.valueOf(intVal), result);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: '" + input + "' не является числом");
            }
        }
    }
}
