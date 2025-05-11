package ru.programming.problems.problemeight;

import java.sql.PreparedStatement;
import java.util.List;

public class InsertWorkerExecutioner  extends ProblemEightSolver{
    public static void execute(List<Worker> workers) {
        Worker worker = new Worker();
        System.out.print("Введите имя студента: ");
        worker.setName(scanner.nextLine());

        int age = -1;
        while (age < 0 || age > 100) {
            System.out.print("Введите возраст студента (0-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Введите возраст студента (0-100): ");
                scanner.next();
            }
            age = scanner.nextInt();
        }
        worker.setAge(age);

        double salary = -1;
        while (salary < 0) {
            System.out.print("Введите зарплату студента (неотрицательное число): ");
            while (!scanner.hasNextDouble()) {
                System.out.print("Введите зарплату студента (неотрицательное число): ");
                scanner.next();
            }
            salary = scanner.nextDouble();
        }
        worker.setSalary(salary);
        scanner.nextLine();

        workers.add(worker);

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)")) {
            stmt.setString(1, worker.getName());
            stmt.setInt(2, worker.getAge());
            stmt.setDouble(3, worker.getSalary());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                worker.getName(), worker.getAge(), worker.getSalary());
    }
}
