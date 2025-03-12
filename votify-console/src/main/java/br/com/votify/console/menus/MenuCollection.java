package br.com.votify.console.menus;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MenuCollection {
    private final List<Menu> menus = new ArrayList<>();

    public void addMenu(Menu menu) {
        menus.add(menu);
    }

    public void startMenu(int menuOption) {
        Menu menu = menus.get(menuOption);
        menu.run();
    }
}
