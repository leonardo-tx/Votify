package br.com.votify.console.menus;

import br.com.votify.console.menus.users.CurrentUserMenu;
import br.com.votify.console.menus.users.DeleteUserContextMenu;
import br.com.votify.console.menus.users.GetByIdUserMenu;

public class UsersMenu extends OptionsMenu {
    public UsersMenu() {
        menuCollection.addMenu(this);
        menuCollection.addMenu(new GetByIdUserMenu());
        menuCollection.addMenu(new CurrentUserMenu());
        menuCollection.addMenu(new DeleteUserContextMenu());
    }

    @Override
    public String getOptionName() {
        return "Usuários";
    }

    @Override
    protected String getExitName() {
        return "Voltar";
    }
}
