package ru.labs.task1;

import ru.labs.DbHelper;

import java.util.Scanner;

public class TaskOneRunner {
    private static final Scanner inputReader = new Scanner(System.in);
    private static boolean isTableCreated = false;
    private static final String TABLE_NAME = "lab1_results";

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        isTableCreated = false;

        int userChoice;
        do {
            displayMenu();
            while (!inputReader.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                inputReader.next();
            }
            userChoice = inputReader.nextInt();
            handleUserAction(userChoice);
        } while (userChoice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Сложение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Вычитание чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Умножение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Деление чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("7. Деление чисел по модулю (остаток), результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("8. Возведение числа в модуль, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("9. Возведение числа в степень, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("10. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void handleUserAction(int choice) {
        if (choice >= 3 && choice <= 10 && !isTableCreated) {
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
            case 3 -> executeBinary("Addition", (a, b) -> a + b);
            case 4 -> executeBinary("Subtraction", (a, b) -> a - b);
            case 5 -> executeBinary("Multiplication", (a, b) -> a * b);
            case 6 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                if (b == 0) {
                    System.out.println("Ошибка: деление на ноль.");
                } else {
                    double result = a / b;
                    System.out.println("Результат: " + result);
                    DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                            "Division", String.valueOf(a), String.valueOf(b), String.valueOf(result));
                }
            }
            case 7 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                if (b == 0) {
                    System.out.println("Ошибка: деление по модулю на ноль.");
                } else {
                    double result = a % b;
                    System.out.println("Результат: " + result);
                    DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                            "Modulus", String.valueOf(a), String.valueOf(b), String.valueOf(result));
                }
            }
            case 8 -> {
                double num = readDouble("Введите число: ");
                double result = Math.abs(num);
                System.out.println("Результат: " + result);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Absolute", String.valueOf(num), null, String.valueOf(result));
            }
            case 9 -> {
                double base = readDouble("Введите число: ");
                double power = readDouble("Введите степень: ");
                double result = Math.pow(base, power);
                System.out.println("Результат: " + result);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                        "Power", String.valueOf(base), String.valueOf(power), String.valueOf(result));
            }
            case 10 -> {
                DbHelper.exportToCsv(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                DbHelper.selectAllFromTable(TABLE_NAME, "id", "operation", "value1", "value2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!inputReader.hasNextDouble()) {
            System.out.print("Ошибка ввода. Повторите: ");
            inputReader.next();
        }
        return inputReader.nextDouble();
    }

    private static void executeBinary(String operation, BinaryOperation op) {
        double a = readDouble("Введите первое число: ");
        double b = readDouble("Введите второе число: ");
        double result = op.apply(a, b);
        System.out.println("Результат: " + result);
        DbHelper.execute("INSERT INTO " + TABLE_NAME + " (operation, value1, value2, result) VALUES (?, ?, ?, ?)",
                operation, String.valueOf(a), String.valueOf(b), String.valueOf(result));
    }

    @FunctionalInterface
    interface BinaryOperation {
        double apply(double a, double b);
    }
}
