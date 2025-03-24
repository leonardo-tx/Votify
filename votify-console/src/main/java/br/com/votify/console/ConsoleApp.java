package br.com.votify.console;

import br.com.votify.console.menus.MainMenu;

public class ConsoleApp {
    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.run();
    }
}
