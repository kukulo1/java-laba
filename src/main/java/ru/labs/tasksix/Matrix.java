package ru.labs.tasksix;

import java.util.Scanner;

public final class Matrix extends ArrayPI {
    public int[][] multiplyMatrices() {
        int[][] result = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    result[i][j] += arrayA[i][k] * arrayB[k][j];
                }
            }
        }
        return result;
    }
    public void inputMatricesFromKeyboard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите значения для первой матрицы:");
        for (int i = 0; i < 7; i++) {
            System.out.println("Строка " + (i + 1) + ":");
            for (int j = 0; j < 7; j++) {
                while (true) {
                    System.out.print("  arrayA [" + i + "][" + j + "]: ");
                    String input = scanner.nextLine();

                    if (!input.matches("-?\\d+")) {
                        System.out.println("Ошибка: введите целое число.");
                        continue;
                    }

                    try {
                        arrayA[i][j] = Integer.parseInt(input);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: значение выходит за пределы int.");
                    }
                }
            }
        }

        System.out.println("Введите значения для второй матрицы:");
        for (int i = 0; i < 7; i++) {
            System.out.println("Строка " + (i + 1) + ":");
            for (int j = 0; j < 7; j++) {
                while (true) {
                    System.out.print("  arrayB [" + i + "][" + j + "]: ");
                    String input = scanner.nextLine();

                    if (!input.matches("-?\\d+")) {
                        System.out.println("Ошибка: введите целое число.");
                        continue;
                    }

                    try {
                        arrayB[i][j] = Integer.parseInt(input);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: значение выходит за пределы int.");
                    }
                }
            }
        }
    }

    public void printMatrix(int[][] matrix, String name) {
        System.out.println("Матрица " + name + ":");
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.printf("%4d", value);
            }
            System.out.println();
        }
    }
}
