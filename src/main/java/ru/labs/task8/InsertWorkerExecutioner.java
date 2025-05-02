package ru.labs.task8;

import ru.labs.DbHelper;
import ru.labs.task8.Worker;

import java.util.List;
import java.util.Scanner;

public class InsertWorkerExecutioner extends Executioner {
    public static void execute(Scanner scanner, String tableName, List<Worker> workers) {
        Worker worker = new Worker();
        System.out.print("Введите имя студента: ");
        worker.setName(scanner.nextLine());

        worker.setAge(readInt(scanner, "Введите возраст студента (0-100): ", 0, 100));
        worker.setSalary(readDouble(scanner, "Введите зарплату студента (неотрицательное число): ", 0));

        workers.add(worker);
        DbHelper.execute("INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)",
                worker.getName(), worker.getAge(), worker.getSalary());

        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                worker.getName(), worker.getAge(), worker.getSalary());
    }

    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        int value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                System.out.print("Ошибка. Введите целое число: ");
                scanner.next();
            }
            value = scanner.nextInt();
        } while (value < min || value > max);
        scanner.nextLine();
        return value;
    }

    private static double readDouble(Scanner scanner, String prompt, double min) {
        double value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextDouble()) {
                System.out.print("Ошибка. Введите число: ");
                scanner.next();
            }
            value = scanner.nextDouble();
        } while (value < min);
        scanner.nextLine();
        return value;
    }
}
