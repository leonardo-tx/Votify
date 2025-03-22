package br.com.votify.console.menus;

import br.com.votify.console.utils.ConsoleUtils;

public abstract class Menu {
    public abstract void run();
    public abstract String getOptionName();

    public void printBanner() {
        String optionName = getOptionName();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Math.max(46, optionName.length()); i++) {
            if (i % 2 == 0) {
                stringBuilder.append('-');
                continue;
            }
            stringBuilder.append('=');
        }
        String bannerDecorator = stringBuilder.toString();
        stringBuilder.delete(0, bannerDecorator.length());

        int spaceOnEachEdge = (bannerDecorator.length() - optionName.length()) / 2;
        stringBuilder.append(" ".repeat(Math.max(0, spaceOnEachEdge)));
        stringBuilder.append(optionName);
        stringBuilder.append(" ".repeat(Math.max(0, spaceOnEachEdge)));

        ConsoleUtils.clear();
        System.out.println(bannerDecorator);
        System.out.println(stringBuilder);
        System.out.println(bannerDecorator);
    }
}
