package br.com.votify.console;

import br.com.votify.console.menus.MainMenu;
import br.com.votify.console.menus.MenuCollection;
import br.com.votify.console.menus.UserContextMenu;
import br.com.votify.console.menus.UsersMenu;

import java.util.Scanner;

public class ConsoleApp {
    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.run();
    }
}
