package ru.labs.task7;

import ru.labs.DbHelper;
import ru.labs.task7.*;

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

            if (userChoice >= 3 && userChoice <= 5 && !isTableCreated) {
                System.out.println("Ошибка, таблица не создана.");
                continue;
            }

            switch (userChoice) {
                case 1 -> ListTablesExecutioner.execute();
                case 2 -> {
                    CreateTableExecutioner.execute(TABLE_NAME);
                    isTableCreated = true;
                }
                case 3 -> {
                    sort = new Sort();
                    InputArrayExecutioner.execute(scanner, TABLE_NAME, sort);
                }
                case 4 -> {
                    if (sort == null) {
                        System.out.println("Ошибка: массив не был введён ранее.");
                    } else {
                        SortArrayExecutioner.execute(TABLE_NAME, sort);
                    }
                }
                case 5 -> ExportToExcelExecutioner.execute(TABLE_NAME);
                case -1 -> System.out.println("Выход из программы.");
                default -> System.out.println("Неверный выбор. Повторите.");
            }
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
}
