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

    @FindBy(xpath = "//button[contains(text(), 'Criar Nova Enquete')]")
    public WebElement createPollButton;

    @FindBy(css = "[role='dialog']")
    public WebElement createPollModal;

    @FindBy(xpath = "//h2[contains(text(), 'Criar Nova Enquete')]")
    public WebElement modalTitle;

    @FindBy(id = "cancel-button")
    public WebElement cancelButton;

    @FindBy(id = "create-poll-button")
    public WebElement createButton;

    @FindBy(id = "title")
    public WebElement titleInput;

    @FindBy(id = "description")
    public WebElement descriptionInput;

    @FindBy(id = "endDate")
    public WebElement endDateInput;

    @FindBy(id = "choiceLimit")
    public WebElement choiceLimitInput;

    @FindBy(css = "[id^='voteOption-']")
    public List<WebElement> voteOptionInputs;

    @FindBy(css = ".text-red-500")
    public List<WebElement> errorMessages;

    @FindBy(css = "[data-testid='polls-list']")
    public WebElement pollsList;

    @FindBy(css = "[data-testid='pagination']")
    public WebElement pagination;

    @FindBy(xpath = "//button[contains(text(), 'Próximo')]")
    public WebElement nextPageButton;

    @FindBy(xpath = "//button[contains(text(), 'Anterior')]")
    public WebElement previousPageButton;

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
