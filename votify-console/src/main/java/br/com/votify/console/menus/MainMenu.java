package br.com.votify.console.menus;

public class MainMenu extends OptionsMenu {
    public MainMenu() {
        menuCollection.addMenu(this);
        menuCollection.addMenu(new AuthMenu());
        menuCollection.addMenu(new UsersMenu());
        menuCollection.addMenu(new PollsMenu());
    }

    @Override
    protected String getExitName() {
        return "Sair";
    }

    @Override
    public String getOptionName() {
        return "Aplicativo Votify";
    }
}
