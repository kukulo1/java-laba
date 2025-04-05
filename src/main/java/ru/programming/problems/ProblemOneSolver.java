package ru.programming.problems;

import ru.programming.utils.Repository;

import java.util.Scanner;

//implements означает то, что класс реализует интерфейс
//так то тут можно спокойно обойтись без этого, но добавил для своего удобства, да и чтобы потом при создании 3 разных версий кода было больше пространства для вариативности
public class ProblemOneSolver implements ProblemSolver{
    private final Scanner scanner = new Scanner(System.in);

    //@override - аннотация, которая означает, что метод класса переопределяет метод интерфейса
    @Override
    public void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Сложение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Вычитание чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Умножение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Деление чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("7. Деление чисел по модулю (остаток), результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("8. Возведение числа в модуль, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("9. Возведение числа в степень, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("10. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран");
        System.out.print("Выберите действие: ");
    }

    @Override
    public void execute(int choice) {
        //просто название таблицы, используется для создания таблицы и для запросов к ней
        String tableName = "problem_one_table";
        switch (choice) {
            case 1 -> {
                //this означает ссылку на объект, в котором вызывается этот блок кода
                //в данном случае вызывается метод printTables() в интерфейсе ProblemSolver
                //такое возможно из-за двух вещей: наш класс реализует интерфейс, и в этом интерфейсе есть метод printTables() со стандартной реализацией
                //стандартная реализация интерфейса подразумевает, что если вы не переопределяете метод интерфейса, который имеет стандартную реализацию
                //то используется как раз эта реализация
                this.printTables();
            }

            case 2 -> {
                createTable(tableName);
            }
            case 3 -> {
                System.out.print("Введите первое число: ");
                double firstNum = scanner.nextDouble();
                System.out.print("Введите второе число: ");
                double secondNum = scanner.nextDouble();
                double result = firstNum + secondNum;
                System.out.println("Результат: " + result);

                String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                Repository.executeStatement(query, result);
            }

            case 4 -> {
                System.out.print("Введите первое число: ");
                double firstNum = scanner.nextDouble();
                System.out.print("Введите второе число: ");
                double secondNum = scanner.nextDouble();
                double result = firstNum - secondNum;
                System.out.println("Результат: " + result);

                String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                Repository.executeStatement(query, result);
            }

            case 5 -> {
                System.out.print("Введите первое число: ");
                double firstNum = scanner.nextDouble();
                System.out.print("Введите второе число: ");
                double secondNum = scanner.nextDouble();
                double result = firstNum * secondNum;
                System.out.println("Результат: " + result);

                // Вставляем только результат
                String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                Repository.executeStatement(query, result);
            }

            case 6 -> {
                System.out.print("Введите первое число: ");
                double firstNum = scanner.nextDouble();
                System.out.print("Введите второе число: ");
                double secondNum = scanner.nextDouble();

                if (secondNum != 0) {
                    double result = firstNum / secondNum;
                    System.out.println("Результат: " + result);

                    String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                    Repository.executeStatement(query, result);
                } else {
                    System.out.println("Ошибка: деление на ноль.");
                }
            }

            case 7 -> {
                System.out.print("Введите первое число: ");
                double firstNum = scanner.nextDouble();
                System.out.print("Введите второе число: ");
                double secondNum = scanner.nextDouble();

                if (secondNum != 0) {
                    double result = firstNum % secondNum;
                    System.out.println("Результат: " + result);

                    String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                    Repository.executeStatement(query, result);
                } else {
                    System.out.println("Ошибка: деление по модулю на ноль.");
                }
            }

            case 8 -> {
                System.out.print("Введите число: ");
                double num = scanner.nextDouble();
                double result = Math.abs(num);
                System.out.println("Результат: " + result);

                String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                Repository.executeStatement(query, result);
            }

            case 9 -> {
                System.out.print("Введите число: ");
                double num = scanner.nextDouble();
                System.out.print("Введите степень: ");
                double power = scanner.nextDouble();
                double result = Math.pow(num, power);
                System.out.println("Результат: " + result);

                String query = "INSERT INTO " + tableName + " (result) VALUES (?)";
                Repository.executeStatement(query, result);
            }


            case 10 -> {
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
                    "result DOUBLE)"
            );
            System.out.println("Таблица " + tableName + " успешно создана!");
    }
}

