package ru.labs.task8;

import ru.labs.DbHelper;


public class InsertWorkerExecutioner extends TaskEightRunner {
    public static void execute() {
        Worker worker = new Worker();
        System.out.print("Введите имя студента: ");
        worker.setName(scanner.nextLine());

        worker.setAge(readInt("Введите возраст студента (0-100): ", 0, 100));
        worker.setSalary(readDouble("Введите зарплату студента (неотрицательное число): ", 0));

        workers.add(worker);
        DbHelper.execute("INSERT INTO " + TABLE_NAME + " (name, age, salary) VALUES (?, ?, ?)",
                worker.getName(), worker.getAge(), worker.getSalary());

        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                worker.getName(), worker.getAge(), worker.getSalary());
    }

    private static int readInt(String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            // Проверка: только цифры (и возможный минус)
            if (!input.matches("-?\\d+")) {
                System.out.println("Ошибка. Введите целое число.");
                continue;
            }

            try {
                value = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка. Введённое число выходит за пределы типа int.");
                continue;
            }

            if (value < min || value > max) {
                System.out.printf("Ошибка. Введите число в диапазоне от %d до %d.%n", min, max);
                continue;
            }

            return value;
        }
    }

    private static double readDouble(String prompt, double min) {
        double value;

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                value = Double.parseDouble(input);

                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    System.out.println("Ошибка. Введённое значение не является допустимым числом.");
                    continue;
                }

                if (value < min) {
                    System.out.printf("Ошибка. Число должно быть не меньше %.2f.%n", min);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка. Введите корректное число (например: 3.14).");
            }
        }
    }
}
