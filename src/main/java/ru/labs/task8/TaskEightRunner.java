package ru.labs.task8;

import ru.labs.DbHelper;
import ru.labs.task8.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskEightRunner {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static final String TABLE_NAME = "lab8_workers";
    private static boolean isTableCreated = false;
    protected static final List<Worker> workers = new ArrayList<>();

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

            if (choice >= 3 && choice <= 5 && !isTableCreated) {
                System.out.println("Ошибка, таблица не создана.");
                continue;
            }

            switch (choice) {
                case 1 -> ListTablesExecutioner.execute();
                case 2 -> {
                    CreateTableExecutioner.execute();
                    isTableCreated = true;
                }
                case 3 -> InsertWorkerExecutioner.execute();
                case 4 -> PrintAllWorkersExecutioner.execute();
                case 5 -> ExportToExcelExecutioner.execute();
                case -1 -> System.out.println("Выход из программы.");
                default -> System.out.print("Введите корректный номер действия: ");
            }
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
}
