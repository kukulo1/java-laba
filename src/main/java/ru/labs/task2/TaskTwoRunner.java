package ru.labs.task2;

import ru.labs.DbHelper;

import java.util.Scanner;

public class TaskTwoRunner {
    private static String inputOne;
    private static String inputTwo;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab2_strings";
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
            handleAction(choice);
        } while (choice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Ввести две строки с клавиатуры, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("5. Объединить две строки в единое целое, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("6. Сравнить две ранее введенные строки, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("7. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void handleAction(int option) {
        if (option >= 3 && option <= 7 && !isTableCreated) {
            System.out.println("Ошибка, таблица не создана.");
            return;
        }

        switch (option) {
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
                inputOne = readString("Введите первую строку (не менее 50 символов): ");
                inputTwo = readString("Введите вторую строку (не менее 50 символов): ");
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "StringInput", inputOne, inputTwo, null);
                System.out.println("Строки сохранены:");
                System.out.println("1: " + inputOne);
                System.out.println("2: " + inputTwo);
            }
            case 4 -> {
                if (!stringsEntered()) break;
                String result = "Length1: " + inputOne.length() + ", Length2: " + inputTwo.length();
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Length", inputOne, inputTwo, result);
                System.out.println("Длины строк: " + result);
            }
            case 5 -> {
                if (!stringsEntered()) break;
                String combined = inputOne + inputTwo;
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Concatenation", inputOne, inputTwo, combined);
                System.out.println("Объединение: " + combined);
            }
            case 6 -> {
                if (!stringsEntered()) break;
                String comparison = inputOne.equals(inputTwo) ? "equal" : "not equal";
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Comparison", inputOne, inputTwo, comparison);
                System.out.println("Сравнение: " + comparison);
            }
            case 7 -> {
                DbHelper.exportToCsv(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                DbHelper.selectAllFromTable(TABLE_NAME, "id", "operation", "value1", "value2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.length() < 50) {
                System.out.println("Строка слишком короткая. Нужно не менее 50 символов.");
            }
        } while (input.length() < 50);
        return input;
    }

    private static boolean stringsEntered() {
        if (inputOne == null || inputTwo == null) {
            System.out.println("Сначала введите строки (пункт 3).");
            return false;
        }
        return true;
    }
}
