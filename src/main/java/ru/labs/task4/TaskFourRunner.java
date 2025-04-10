package ru.labs.task4;

import ru.labs.DbHelper;

import java.util.Scanner;

public class TaskFourRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab4_strings";
    private static boolean isTableCreated = false;
    private static String inputOne = "", inputTwo = "";

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        isTableCreated = false;

        inputOne = readValidated("Введите первую строку (не менее 50 символов): ");
        inputTwo = readValidated("Введите вторую строку (не менее 50 символов): ");

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
        System.out.println("3. Возвращение подстроки по индексам, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Перевод строк в верхний и нижний регистры, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Поиск подстроки и определение окончания подстроки, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Сохранить все данные из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void handleAction(int choice) {
        if (choice >= 3 && choice <= 6 && !isTableCreated) {
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
                int start = readIndex("Введите начальный индекс подстроки: ");
                int end = readIndex("Введите конечный индекс подстроки: ");
                String sub1 = safeSubstring(inputOne, start, end);
                String sub2 = safeSubstring(inputTwo, start, end);
                String result = "Substring1: " + sub1 + "; Substring2: " + sub2;
                System.out.println(result);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Substring", inputOne, inputTwo, result);
            }
            case 4 -> {
                String result = "UP1: " + inputOne.toUpperCase() +
                        ", LOW1: " + inputOne.toLowerCase() +
                        ", UP2: " + inputTwo.toUpperCase() +
                        ", LOW2: " + inputTwo.toLowerCase();
                System.out.println(result);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "CaseConversion", inputOne, inputTwo, result);
            }
            case 5 -> {
                System.out.print("Введите подстроку для поиска: ");
                String sub = scanner.nextLine();
                boolean contains1 = inputOne.contains(sub);
                boolean ends1 = inputOne.endsWith(sub);
                boolean contains2 = inputTwo.contains(sub);
                boolean ends2 = inputTwo.endsWith(sub);
                String result = String.format(
                        "Substring: %s | str1: contains=%b, endsWith=%b | str2: contains=%b, endsWith=%b",
                        sub, contains1, ends1, contains2, ends2);
                System.out.println(result);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "SearchEndCheck", inputOne, inputTwo, result);
            }
            case 6 -> {
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

    private static int readIndex(String prompt) {
        int index;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                index = scanner.nextInt();
                scanner.nextLine();
                return index;
            } else {
                System.out.println("Введите целое число.");
                scanner.next();
            }
        }
    }

    private static String safeSubstring(String input, int start, int end) {
        if (start >= 0 && end <= input.length() && start < end) {
            return input.substring(start, end);
        } else {
            System.out.println("Недопустимые индексы (длина строки: " + input.length() + ")");
            return "";
        }
    }
}
