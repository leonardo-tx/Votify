package br.com.votify.console.menus;

import br.com.votify.console.menus.users.GetByIdUserMenu;
import br.com.votify.console.menus.users.LoginUserMenu;
import br.com.votify.console.menus.users.RegisterUserMenu;
import br.com.votify.console.menus.users.ResetPasswordUserMenu;

public class UsersMenu extends OptionsMenu {
    public UsersMenu() {
        menuCollection.addMenu(this);
        menuCollection.addMenu(new RegisterUserMenu());
        menuCollection.addMenu(new LoginUserMenu());
        menuCollection.addMenu(new ResetPasswordUserMenu());
        menuCollection.addMenu(new GetByIdUserMenu());
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
