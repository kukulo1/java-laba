package ru.labs.task7;

import ru.labs.DbHelper;

import java.util.Scanner;

public class TaskSevenRunner {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static final String TABLE_NAME = "lab7_array";
    private static boolean isTableCreated = false;

    protected static Sort sort;

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
                    CreateTableExecutioner.execute();
                    isTableCreated = true;
                }
                case 3 -> {
                    sort = new Sort();
                    InputArrayExecutioner.execute();
                }
                case 4 -> {
                    if (sort == null) {
                        System.out.println("Ошибка: массив не был введён ранее.");
                    } else {
                        SortArrayExecutioner.execute();
                    }
                }
                case 5 -> ExportToExcelExecutioner.execute();
                case -1 -> System.out.println("Выход из программы.");
                default -> System.out.print("Введите корректный номер действия: ");
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
