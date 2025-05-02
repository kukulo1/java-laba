package ru.labs.task6;

import ru.labs.DbHelper;
import ru.labs.task6.*;

import java.util.Scanner;

public class TaskSixRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab6_matrix";
    private static boolean isTableCreated = false;
    private static final Matrix matrix = new Matrix();

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);

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
                case 3 -> InputMatricesExecutioner.execute(scanner, TABLE_NAME, matrix);
                case 4 -> MultiplyMatricesExecutioner.execute(TABLE_NAME, matrix);
                case 5 -> ExportToExcelExecutioner.execute(TABLE_NAME);
                case -1 -> System.out.println("Выход из программы.");
                default -> System.out.println("Неверный выбор. Повторите.");
            }
        } while (userChoice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести две матрицы с клавиатуры и сохранить их в MySQL.");
        System.out.println("4. Перемножить матрицы, сохранить перемноженную матрицу в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести в консоль.");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }
}
