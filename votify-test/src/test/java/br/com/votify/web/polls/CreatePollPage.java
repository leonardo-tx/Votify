package br.com.votify.web.polls;

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

    @FindBy(xpath = "//button[contains(text(), 'Adicionar Opção')]")
    public WebElement addOptionButton;

    @FindBy(xpath = "//button[contains(text(), 'Remover')]")
    public List<WebElement> removeOptionButtons;

    @FindBy(xpath = "//button[contains(text(), 'Criar Enquete')]")
    public WebElement createButton;

    @FindBy(xpath = "//button[contains(text(), 'Cancelar')]")
    public WebElement cancelButton;

    @FindBy(css = ".text-red-500")
    public List<WebElement> errorMessages;
} 