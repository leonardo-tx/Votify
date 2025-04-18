package br.com.votify.console.menus;

import br.com.votify.console.utils.ConsoleUtils;

import java.util.List;
import java.util.Scanner;

public abstract class OptionsMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);
    protected final MenuCollection menuCollection = new MenuCollection();

    @Override
    public final void run() {
        List<Menu> menus = menuCollection.getMenus();
        int option = 0;
        while (option != menus.size()) {
            printBanner();

            for (int i = 1; i < menus.size(); i++) {
                System.out.println(i + " - " + menus.get(i).getOptionName());
            }
            System.out.println(menus.size() + " - " + getExitName());
            System.out.print("\nInsira o número da opção: ");

            option = ConsoleUtils.getOptionFromInput(scanner);
            if (option > 0 && option < menus.size()) {
                try {
                    menus.get(option).run();
                } catch (Exception e) {
                    ConsoleUtils.clear();
                    System.out.println("Ocorreu um erro: " + e.getMessage());
                    ConsoleUtils.pressEnterToContinue(scanner);
                }
            }
        }
    }

    protected abstract String getExitName();
}
