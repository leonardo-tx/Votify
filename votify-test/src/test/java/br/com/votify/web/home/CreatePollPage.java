package br.com.votify.web.home;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class CreatePollPage extends BasePage {
    public CreatePollPage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "title")
    public WebElement titleInput;

    @FindBy(id = "description")
    public WebElement descriptionInput;

    @FindBy(id = "startDate")
    public WebElement startDateInput;

    @FindBy(id = "endDate")
    public WebElement endDateInput;

    @FindBy(id = "choiceLimit")
    public WebElement choiceLimitInput;

    @FindBy(css = "[id^='voteOption-']")
    public List<WebElement> voteOptionInputs;

    @FindBy(id = "add-option-button")
    public WebElement addOptionButton;

    @FindBy(css = "[id^='remove-option-']")
    public List<WebElement> removeOptionButtons;

    @FindBy(id = "create-poll-button")
    public WebElement createButton;

    @FindBy(id = "cancel-button")
    public WebElement cancelButton;

    @FindBy(css = ".text-red-500")
    public List<WebElement> errorMessages;
} 