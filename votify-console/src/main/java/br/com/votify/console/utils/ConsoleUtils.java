package br.com.votify.console.utils;

import java.util.Scanner;

public final class ConsoleUtils {
    public static void clear() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            }
            else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch (Exception ignored) {

        }
    }

    public static int getOptionFromInput(Scanner scanner) {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean getBooleanFromInput(Scanner scanner) {
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("s");
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print("Pressione enter para continuar. . .");
        scanner.nextLine();
    }
}
