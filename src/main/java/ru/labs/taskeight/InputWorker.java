package ru.labs.taskeight;

import ru.labs.taskeight.Worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class InputWorker extends Parent {
    public static void execute(Connection conn, Scanner scanner, String tableName, List<Worker> workers) {
        Worker worker = new Worker();
        System.out.print("Введите имя студента: ");
        worker.setName(scanner.nextLine());

        int age = -1;
        while (age < 0 || age > 100) {
            System.out.print("Введите возраст студента (0-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Ошибка. Введите целое число: ");
                scanner.next();
            }
            age = scanner.nextInt();
        }
        worker.setAge(age);

        double salary = -1;
        while (salary < 0) {
            System.out.print("Введите зарплату студента (неотрицательное число): ");
            while (!scanner.hasNextDouble()) {
                System.out.print("Ошибка. Введите число: ");
                scanner.next();
            }
            salary = scanner.nextDouble();
        }
        worker.setSalary(salary);
        scanner.nextLine();

        workers.add(worker);

        String sql = "INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, worker.getName());
            stmt.setInt(2, worker.getAge());
            stmt.setDouble(3, worker.getSalary());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                worker.getName(), worker.getAge(), worker.getSalary());
    }
}
