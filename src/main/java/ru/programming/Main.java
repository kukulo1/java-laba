package ru.programming;

import ru.programming.problems.ProblemOneSolver;
import ru.programming.problems.ProblemThreeSolver;
import ru.programming.problems.ProblemTwoSolver;

import java.util.Scanner;

public class Main {

    /*
    Это создание объектов (приписка static означает, что переменная принадлежит классу, а не объекту. честно понятия не имею как это просто объяснить, поэтому погуглите)
    но static тут обязателен, т.к. без этой приписки вы не сможете пользоваться этими переменными в методе main, т.к он статический
    каждый объект(problemOneSolver, problemTwoSolver и т.д.) отвечает за выполнение соответствующего задания
     */
    private static final ProblemOneSolver problemOneSolver = new ProblemOneSolver();
    private static final ProblemTwoSolver problemTwoSolver = new ProblemTwoSolver();
    private static final ProblemThreeSolver problemThreeSolver = new ProblemThreeSolver();

    //метод, с которого начинается выполнение любой программы
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //это объект класса Scanner, он отвечает за считывание данных из консоли
        //обратите внимание на то, что System.in обязательно указывать в параметре, иначе капут. Погуглите если че
        System.out.println("Для выхода введите -1");
        try {
            int problemChoice = 0;
            int choice = 0;
            do {
                System.out.print("Введите номер лабораторной работы от 1 до 8: ");
                problemChoice = scanner.nextInt();
                //здесь короче считывается в переменную problemChoice номер лабы, после чего вызывается нужный блок case.
                //"почему в блоках case в конце не стоит break?" - с Java 14 поддерживается такой вид кейсов, брейк не нужен, если используется case N -> {}.
                //для case N: break нужен, учтите
                switch (problemChoice) {
                    case 1 -> {
                        do {
                            problemOneSolver.printMenu(); //тут вызывается метод объекта, содержимое метода смотрите в классе ProblemOneSolver
                            choice = scanner.nextInt();
                            problemOneSolver.execute(choice);
                        } while (choice != -1);
                    }
                    case 2 -> {
                        do {
                            problemTwoSolver.printMenu();
                            choice = scanner.nextInt();
                            problemTwoSolver.execute(choice);
                        } while (choice != -1);
                    }
                    case 3 -> {
                        do {
                            problemThreeSolver.printMenu();
                            choice = scanner.nextInt();
                            problemThreeSolver.execute(choice);
                        } while (choice != -1);
                    }
                    case 4 -> {
                        //тут короче будет 4 задание
                    }
                    case 5 -> {
                        //и так далее
                    }
                    case 6 -> {
                        // Пустой кейс 6
                    }
                    case 7 -> {
                        // Пустой кейс 7
                    }
                    case 8 -> {
                        // Пустой кейс 8
                    }
                }
            } while (problemChoice != -1);

        } catch (NumberFormatException e) {
            System.out.println("Некорректный ввод! Попробуйте еще раз: ");

        }
    }
}