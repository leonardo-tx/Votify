package br.com.votify.web.home;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class HomePage extends BasePage {
    protected HomePage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "poll-list")
    public WebElement pollList;

    @FindBy(css = "[id^='poll-card-link-']")
    public List<WebElement> pollAnchors;

    @FindBy(id = "next-page")
    public WebElement buttonNextPage;

    @FindBy(id = "previous-page")
    public WebElement buttonPreviousPage;
}
