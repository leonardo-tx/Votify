package br.com.votify.web.polls;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class PollPage extends BasePage {
    public PollPage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "poll-title")
    public WebElement title;

    @FindBy(id = "poll-description")
    public WebElement description;

    @FindBy(id = "poll-start-date")
    public WebElement startDate;

    @FindBy(id = "poll-end-date")
    public WebElement endDate;

    @FindBy(css = "[id^='poll-option-label-']")
    public List<WebElement> optionLabels;

    @FindBy(name = "poll-option")
    public List<WebElement> optionInputs;

    @FindBy(id = "vote-button")
    public WebElement voteButton;

    @FindBy(id = "no-poll-message")
    public WebElement noPollMessage;

    @FindBy(id="voters-list-ul" )
    public WebElement votersSectionTitle;

    @FindBy(id= "voters-list-ul")
    public WebElement votersListUL;
}