package ru.programming.problems.problemseven;

import java.util.Scanner;

public final class Sort extends ArrayPI {
    private final Scanner scanner = new Scanner(System.in);

    public void inputArray() {
        System.out.println("Введите 35 целых чисел:");
        for (int i = 0; i < array.length; i++) {
            while (true) {
                String input = scanner.nextLine();
                long value;
                try {
                    value = Long.parseLong(input);
                    array[i] = value;
                    break;
                } catch (NumberFormatException e) {
                    if (input.matches("-?\\d+")) {
                        System.out.println("Ошибка: число выходит за пределы типа long. Попробуйте еще раз:");
                    } else {
                        System.out.println("Ошибка ввода: введите целое число.");
                    }
                }
            }
        }
    }

    public long[] sortAscending() {
        long[] sorted = array.clone();
        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = 0; j < sorted.length - i - 1; j++) {
                if (sorted[j] > sorted[j + 1]) {
                    long temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        return sorted;
    }

    public long[] sortDescending() {
        long[] sorted = sortAscending();
        for (int i = 0; i < sorted.length / 2; i++) {
            long temp = sorted[i];
            sorted[i] = sorted[sorted.length - 1 - i];
            sorted[sorted.length - 1 - i] = temp;
        }
        return sorted;
    }

    public void printArray(long[] array, String message) {
        System.out.println(message + ":");
        for (long value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
