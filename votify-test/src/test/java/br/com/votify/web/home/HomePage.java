package br.com.votify.web.home;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class HomePage extends BasePage {
    public HomePage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "open-poll-create-modal")
    public WebElement createPollButton;

    @FindBy(css = "[data-testid='polls-list']")
    public WebElement pollsList;

    @FindBy(id = "pagination")
    public WebElement pagination;

    @FindBy(id = "poll-list")
    public WebElement pollList;

    @FindBy(css = "[id^='poll-card-link-']")
    public List<WebElement> pollAnchors;

    @FindBy(id = "next-page")
    public WebElement buttonNextPage;

    @FindBy(id = "previous-page")
    public WebElement buttonPreviousPage;

    @FindBy(id = "nav-search-poll")
    public WebElement navSearchPoll;
}
