package ru.programming.problems;

import ru.programming.utils.Repository;

import java.util.List;

//
public interface ProblemSolver {
    void printMenu();

    void execute(int choice);

    default void printTables() {
        List<String> tables = Repository.getTables();
        if (tables.isEmpty()) {
            System.out.println("Таблицы не найдены.");
        } else {
            tables.forEach(System.out::println);
        }
    }
    void createTable(String tableName);
}
