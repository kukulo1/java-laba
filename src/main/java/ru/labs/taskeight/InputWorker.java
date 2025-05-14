package ru.labs.taskeight;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InputWorker extends TaskEight {
    public static void execute() {
        Worker worker = new Worker();

        while (true) {
            System.out.print("Введите имя студента: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Ошибка: имя должно содержать только буквы, пробелы и дефисы.");
                continue;
            }// || !name.matches("([A-z]||[А-я]||[0-9]||,||!||\\.||\\?|| )*")

            worker.setName(name);
            break;
        }

        while (true) {
            System.out.print("Введите возраст студента (0-100): ");
            String input = scanner.nextLine();

            if (!input.matches("[-\\d]+")) {
                System.out.println("Ошибка! Возраст должен быть целым числом.");
                continue;
            }

            try {
                int age = Integer.parseInt(input);
                if (age < 0 || age > 100) {
                    System.out.println("Ошибка! Возраст должен быть от 0 до 100.");
                    continue;
                }
                worker.setAge(age);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Число выходит за пределы типа int.");
            }
        }

        while (true) {
            System.out.print("Введите зарплату студента (неотрицательное число): ");
            String input = scanner.nextLine().replace(",", ".");

            try {
                double salary = Double.parseDouble(input);
                if (salary < 0) {
                    System.out.println("Ошибка: зарплата не может быть отрицательной.");
                    continue;
                }
                worker.setSalary(salary);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число, например 15000.50");
            }
        }

        workers.add(worker);

        String sql = "INSERT INTO " + TABLE_NAME + " (name, age, salary) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, worker.getName());
            stmt.setInt(2, worker.getAge());
            stmt.setDouble(3, worker.getSalary());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в базу данных: " + e.getMessage());
        }

        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                worker.getName(), worker.getAge(), worker.getSalary());
    }
}
