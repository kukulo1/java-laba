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
                System.out.println("Из какой строки извлекать подстроку?");
                System.out.println("1. inputOne");
                System.out.println("2. inputTwo");

                int selected = readSafeIndex("Выберите номер строки (1 или 2): ", 1, 2);
                String selectedInput = (selected == 1) ? inputOne : inputTwo;

                int start = readSafeIndex("Введите начальный индекс подстроки: ", 0, selectedInput.length());
                int end = readSafeIndex("Введите конечный индекс подстроки: ", start, selectedInput.length());

                String sub = safeSubstring(selectedInput, start, end);

                String result = sub;
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
                DbHelper.exportToXls(TABLE_NAME, TABLE_NAME);
                DbHelper.selectAllFromTable(TABLE_NAME, new String[]{"id", "operation", "value1", "value2", "result"}, new int[]{3, 20, 100, 100, 200});
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

    private static int readSafeIndex(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (!input.matches("\\d+")) {
                System.out.println("Ошибка: введите неотрицательное целое число.");
                continue;
            }

            try {
                int value = Integer.parseInt(input);

                if (value < min || value > max) {
                    System.out.printf("Ошибка: индекс должен быть в диапазоне от %d до %d.%n", min, max);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: число выходит за пределы допустимого диапазона int.");
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
