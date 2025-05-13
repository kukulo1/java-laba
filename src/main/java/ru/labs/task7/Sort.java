package ru.labs.task7;

import java.util.Scanner;

public final class Sort extends ArrayPI {
    private final Scanner scanner = new Scanner(System.in);

    public void inputArray() {
        System.out.println("Введите 35 целых чисел:");

        for (int i = 0; i < array.length; i++) {
            while (true) {
                System.out.print("Элемент " + (i + 1) + ": ");
                String input = scanner.nextLine();

                if (!input.matches("-?\\d+")) {
                    System.out.println("Ошибка: введите целое число.");
                    continue;
                }

                try {
                    array[i] = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: число выходит за пределы допустимого диапазона int.");
                }
            }
        }
    }

    public int[] sortAscending() {
        int[] sorted = array.clone();
        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = 0; j < sorted.length - i - 1; j++) {
                if (sorted[j] > sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        return sorted;
    }

    public int[] sortDescending() {
        int[] sorted = sortAscending();
        for (int i = 0; i < sorted.length / 2; i++) {
            int temp = sorted[i];
            sorted[i] = sorted[sorted.length - 1 - i];
            sorted[sorted.length - 1 - i] = temp;
        }
        return sorted;
    }

    public void printArray(int[] array, String message) {
        System.out.println(message + ":");
        for (int value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
