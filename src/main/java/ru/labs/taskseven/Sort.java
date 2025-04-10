package ru.labs.taskseven;

import java.util.Scanner;

public final class Sort extends ArrayPI {
    private final Scanner scanner = new Scanner(System.in);

    public void inputArray() {
        System.out.println("Введите 35 целых чисел:");
        for (int i = 0; i < array.length; i++) {
            array[i] = scanner.nextInt();
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
