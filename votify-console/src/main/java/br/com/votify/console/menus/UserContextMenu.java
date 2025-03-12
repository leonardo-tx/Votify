package br.com.votify.console.menus;

import br.com.votify.console.menus.context.CurrentUserContextMenu;
import br.com.votify.console.menus.context.DeleteUserContextMenu;
import br.com.votify.console.menus.context.LogoutUserContextMenu;
import br.com.votify.console.menus.context.RegenerateTokensUserContextMenu;
import br.com.votify.console.utils.ConsoleUtils;

import java.util.List;
import java.util.Scanner;

public class UserContextMenu extends OptionsMenu {
    public UserContextMenu() {
        menuCollection.addMenu(this);
        menuCollection.addMenu(new CurrentUserContextMenu());
        menuCollection.addMenu(new RegenerateTokensUserContextMenu());
        menuCollection.addMenu(new LogoutUserContextMenu());
        menuCollection.addMenu(new DeleteUserContextMenu());
    }

    @Override
    public String getOptionName() {
        return "Minha conta";
    }

    @Override
    protected String getExitName() {
        return "Voltar";
    }
}
