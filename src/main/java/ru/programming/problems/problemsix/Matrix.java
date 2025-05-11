package ru.programming.problems.problemsix;

import java.util.Scanner;

public final class Matrix extends ArrayPI {
    public long[][] multiplyMatrices() {
        long[][] result = new long[7][7];
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
            System.out.print("Строка " + (i + 1) + ": ");
            for (int j = 0; j < 7; j++) {
                while (true) {
                    String input = scanner.next();
                    if (!input.matches("-?\\d+")) {
                        System.out.println("Ошибка: введите только целые числа.");
                        continue;
                    }
                    try {
                        arrayA[i][j] = Long.parseLong(input);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: число выходит за пределы допустимого диапазона long.");
                    }
                }
            }
        }

        System.out.println("Введите значения для второй матрицы:");
        for (int i = 0; i < 7; i++) {
            System.out.print("Строка " + (i + 1) + ": ");
            for (int j = 0; j < 7; j++) {
                while (true) {
                    String input = scanner.next();
                    if (!input.matches("-?\\d+")) {
                        System.out.println("Ошибка: введите только целые числа.");
                        continue;
                    }
                    try {
                        long value = Long.parseLong(input);
                        arrayB[i][j] = value;
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: число выходит за пределы допустимого диапазона long.");
                    }
                }
            }
        }
    }
    public void printMatrix(long[][] matrix, String name) {
        System.out.println("Матрица " + name + ":");
        for (long[] row : matrix) {
            for (long value : row) {
                System.out.printf("%4d", value);
            }
            System.out.println();
        }
    }
}
