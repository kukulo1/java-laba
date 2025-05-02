package ru.linapelx.zadachi.zadachaeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddStudent extends Command {
    public static void execute(Connection conn, Scanner scanner, String tableName) {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        int age = -1;
        while (age < 0 || age > 100) {
            System.out.print("Введите возраст (0-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Ошибка. Введите целое число: ");
                scanner.next();
            }
            age = scanner.nextInt();
        }

        double salary = -1;
        while (salary < 0) {
            System.out.print("Введите зарплату: ");
            while (!scanner.hasNextDouble()) {
                System.out.print("Ошибка. Введите число: ");
                scanner.next();
            }
            salary = scanner.nextDouble();
        }
        scanner.nextLine();

        String sql = "INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setDouble(3, salary);
            stmt.executeUpdate();
            System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n", name, age, salary);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
