package ru.programming.problems;

import ru.programming.utils.Repository;

import java.util.Scanner;


public class ProblemTwoSolver implements ProblemSolver{
    private String string1;
    private String string2;
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Ввести две строки с клавиатуры, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("5. Объединить две строки в единое целое, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("6. Сравнить две ранее введенные строки, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("7. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.");
        System.out.print("Выберите действие: ");
    }

    @Override
    public void execute(int choice) {
        String tableName = "problem_two_table";
        switch (choice) {
            case 1 -> {
                this.printTables();
            }
            case 2 -> {
                createTable(tableName);
            }
            case 3 -> {
                System.out.print("Введите первую строку: ");
                string1 = scanner.nextLine();
                System.out.print("Введите вторую строку: ");
                string2 = scanner.nextLine();

                Repository.executeStatement("INSERT INTO " + tableName + " (result) VALUES (?)", string1);
                Repository.executeStatement("INSERT INTO " + tableName + " (result) VALUES (?)", string2);

                System.out.println("Строки сохранены в БД:");
                System.out.println("1: " + string1);
                System.out.println("2: " + string2);
            }

            case 4 -> {
                if (string1 == null || string2 == null) {
                    System.out.println("Сначала введите строки (пункт 3)");
                    break;
                }
                int totalLength = string1.length() + string2.length();

                Repository.executeStatement(
                        "INSERT INTO " + tableName + " (result) VALUES (?)",
                        String.valueOf(totalLength)
                );
                System.out.println("Результат: " + totalLength);
            }

            case 5 -> {
                if (string1 == null || string2 == null) {
                    System.out.println("Сначала введите строки (пункт 3)");
                    break;
                }
                String concatenated = string1 + string2;

                Repository.executeStatement(
                        "INSERT INTO " + tableName + " (result) VALUES (?)",
                        concatenated
                );
                System.out.println("Результат: " + concatenated);
            }

            case 6 -> {
                if (string1 == null || string2 == null) {
                    System.out.println("Сначала введите строки (пункт 3)");
                    break;
                }
                String comparisonResult = string1.equals(string2) ? "equal" : "not equal";

                Repository.executeStatement(
                        "INSERT INTO " + tableName + " (result) VALUES (?)",
                        comparisonResult
                );
                System.out.println("Результат: " + comparisonResult);
            }

            case 7 -> {
                Repository.exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                Repository.selectAllFromTable(tableName, "id", "result");
            }
        }
    }

    @Override
    public void createTable(String tableName) {
        Repository.executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "result VARCHAR(255))"
        );
        System.out.println("Таблица " + tableName + " успешно создана!");
    }
}
