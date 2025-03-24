package br.com.votify.console.menus;

import br.com.votify.console.menus.auth.LogoutMenu;
import br.com.votify.console.menus.auth.LoginMenu;
import br.com.votify.console.menus.auth.RegisterMenu;
import br.com.votify.console.menus.auth.ResetPasswordMenu;
import br.com.votify.console.menus.auth.RefreshTokensMenu;

public class AuthMenu extends OptionsMenu {
    public AuthMenu() {
        menuCollection.addMenu(this);
        menuCollection.addMenu(new RegisterMenu());
        menuCollection.addMenu(new LoginMenu());
        menuCollection.addMenu(new ResetPasswordMenu());
        menuCollection.addMenu(new RefreshTokensMenu());
        menuCollection.addMenu(new LogoutMenu());
    }

    @Override
    public String getOptionName() {
        return "Autenticação";
    }

    @Override
    protected String getExitName() {
        return "Voltar";
    }
}
