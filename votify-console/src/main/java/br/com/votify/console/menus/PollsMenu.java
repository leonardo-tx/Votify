package br.com.votify.console.menus;

import br.com.votify.console.menus.polls.CreatePollMenu;
import br.com.votify.console.menus.polls.GetMyPollsMenu;
import br.com.votify.console.menus.polls.GetUserPollsMenu;

public class PollsMenu extends OptionsMenu {
    public PollsMenu() {
        menuCollection.addMenu(this);
        menuCollection.addMenu(new CreatePollMenu());
        menuCollection.addMenu(new GetUserPollsMenu());
        menuCollection.addMenu(new GetMyPollsMenu());
    }

    @Override
    public String getOptionName() {
        return "Enquetes";
    }

    @Override
    protected String getExitName() {
        return "Voltar";
    }
}
