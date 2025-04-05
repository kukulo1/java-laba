package ru.programming.problems;

import ru.programming.utils.Repository;

import java.sql.SQLException;
import java.util.Scanner;

public class ProblemThreeSolver implements ProblemSolver{
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Выполнение задачи базового варианта, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("4. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.");
        System.out.print("Выберите действие: ");
    }

    @Override
    public void execute(int choice) {
        String tableName = "problem_three_table";
        switch (choice) {
            case 1 -> {
                this.printTables();
            }
            case 2 -> {
                createTable(tableName);
            }
            case 3 -> {
                System.out.println("Введите числа через пробел (для завершения введите 'q'):");
                while (true) {
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("q")) {
                        break;
                    }
                    try {
                        int number = Integer.parseInt(input);
                        boolean even = (number % 2) == 0;
                        if (even) {
                            System.out.println(number + " - целое четное число");
                        } else {
                            System.out.println(number + " - целое нечетное число");
                        }
                        String query = "INSERT INTO " + tableName + " (result, even) VALUES (?,?)";
                        Repository.executeStatement(query, number, even);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: '" + input + "' не является целым числом");
                    }
                }
            }
            case 4 -> {
                Repository.exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                Repository.selectAllFromTable(tableName, "id", "result", "even");
            }
        }
    }

    @Override
    public void createTable(String tableName) {
        Repository.executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "result INT, " +
                "even BOOL)"
        );
        System.out.println("Таблица " + tableName + " успешно создана!");
    }
}
