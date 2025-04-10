package ru.labs.task7;

import ru.labs.DbHelper;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskSevenRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab7_array";
    private static boolean isTableCreated = false;

    private static Sort sort;

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        isTableCreated = false;

        int userChoice;
        do {
            displayMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            userChoice = scanner.nextInt();
            scanner.nextLine();
            handleAction(userChoice);
        } while (userChoice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести массив и сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Отсортировать массив, сохранить в MySQL и вывести в консоль.");
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
                        "array_name VARCHAR(255), " +
                        "index_pos INT, " +
                        "value INT)");
                isTableCreated = true;
                System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
            }
            case 3 -> {
                try {
                    sort = new Sort();
                    sort.inputArray();
                    sort.printArray(sort.array, "Исходный массив");
                    insertArray(sort.array, "original");
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода: необходимо вводить только целые числа.");
                    scanner.nextLine();
                }
            }
            case 4 -> {
                if (sort == null) {
                    System.out.println("Ошибка: массив не был введён ранее.");
                    return;
                }

                int[] asc = sort.sortAscending();
                sort.printArray(asc, "Сортировка по возрастанию");
                insertArray(asc, "asc");

                int[] desc = sort.sortDescending();
                sort.printArray(desc, "Сортировка по убыванию");
                insertArray(desc, "desc");
            }
            case 5 -> {
                DbHelper.exportToCsv(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                DbHelper.selectAllFromTable(TABLE_NAME, "id", "array_name", "index_pos", "value");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void insertArray(int[] array, String name) {
        String sql = "INSERT INTO " + TABLE_NAME + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        for (int i = 0; i < array.length; i++) {
            DbHelper.execute(sql, name, i, array[i]);
        }
    }
}
