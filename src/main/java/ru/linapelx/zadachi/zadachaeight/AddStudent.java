package ru.linapelx.zadachi.zadachaeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AddStudent extends ZadachaEight{
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z�-��-���\\s-]+$");

    private static boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public static void execute(Connection conn, Scanner scanner, String tableName) {
        System.out.print("������� ���: ");
        String input = scanner.nextLine();
        String name;
        while (!isValidName(input))  {
            System.out.println("������� �������� ���! (� ��� ������ ���� ������ �����)");
            input = scanner.nextLine();
        }
        name = input;
        int age = -1;
        while (age < 0 || age > 100) {
            System.out.print("������� ������� (����� ����� � ��������� 0-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("������� ������� (����� ����� � ��������� 0-100): ");
                scanner.next();
            }
            age = scanner.nextInt();
        }

        double salary = -1;
        while (salary < 0) {
            System.out.print("������� ��������: ");
            while (!scanner.hasNextDouble()) {
                System.out.print("������. ������� �����: ");
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
            System.out.printf("��������: %s, %d ���, �������� %.2f\n", name, age, salary);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
