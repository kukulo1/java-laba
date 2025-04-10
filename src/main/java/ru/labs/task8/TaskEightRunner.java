package ru.labs.task8;

import ru.labs.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskEightRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab8_workers";
    private static boolean isTableCreated = false;
    private static final List<Worker> workers = new ArrayList<>();

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
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввод значений ВСЕХ полей, сохранить их в MySQL с выводом в консоль.");
        System.out.println("4. Сохранение всех результатов из MySQL с последующим выводом в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
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
                        "name VARCHAR(255), " +
                        "age INT, " +
                        "salary DOUBLE)");
                isTableCreated = true;
                System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
            }
            case 3 -> {
                Worker worker = new Worker();
                System.out.print("Введите имя студента: ");
                worker.setName(scanner.nextLine());

                worker.setAge(readInt("Введите возраст студента (0-100): ", 0, 100));
                worker.setSalary(readDouble("Введите зарплату студента (неотрицательное число): ", 0));

                workers.add(worker);
                DbHelper.execute("INSERT INTO " + TABLE_NAME + " (name, age, salary) VALUES (?, ?, ?)",
                        worker.getName(), worker.getAge(), worker.getSalary());

                System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                        worker.getName(), worker.getAge(), worker.getSalary());
            }
            case 4 -> DbHelper.selectAllFromTable(TABLE_NAME, "id", "name", "age", "salary");
            case 5 -> {
                DbHelper.exportToCsv(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                DbHelper.selectAllFromTable(TABLE_NAME, "id", "name", "age", "salary");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        int value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                System.out.print("Ошибка. Введите целое число: ");
                scanner.next();
            }
            value = scanner.nextInt();
        } while (value < min || value > max);
        scanner.nextLine();
        return value;
    }

    private static double readDouble(String prompt, double min) {
        double value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextDouble()) {
                System.out.print("Ошибка. Введите число: ");
                scanner.next();
            }
            value = scanner.nextDouble();
        } while (value < min);
        scanner.nextLine();
        return value;
    }
}
