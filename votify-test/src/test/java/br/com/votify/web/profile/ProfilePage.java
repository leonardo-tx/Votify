package br.com.votify.web.profile;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class ProfilePage extends BasePage {

    @FindBy(id = "user-profile-name")
    private WebElement userNameText;

    @FindBy(id = "user-profile-username")
    private WebElement userUsernameText;

    @FindBy(id = "user-profile-created-polls-title")
    private WebElement createdPollsSectionTitle;

    @FindBy(css = "[id^='poll-card-']")
    private List<WebElement> pollCards;
    
    @FindBy(id = "profile-error-title")
    private WebElement errorLoadingProfileTitle;

    @FindBy(id = "profile-error-message")
    private WebElement errorMessageText;

    @FindBy(id = "user-profile-no-polls-message")
    private WebElement noPollsMessageText;

    public ProfilePage(WebDriver webDriver) {
        super(webDriver);
    }

    public WebElement getUserNameElement() {
        return userNameText;
    }

    public WebElement getErrorLoadingProfileTitleElement() {
        return errorLoadingProfileTitle;
    }

    public String getUserName() {
        return userNameText.getText();
    }

    public String getUserUsername() {
        return userUsernameText.getText();
    }

    public boolean isCreatedPollsSectionVisible() {
        return createdPollsSectionTitle.isDisplayed();
    }

    public int getPollsCount() {
        return pollCards.size();
    }
    
    public boolean isErrorLoadingProfileTitleVisible() {
        try {
            return errorLoadingProfileTitle.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public String getErrorMessageText() {
        try {
            return errorMessageText.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return "";
        }
    }

    public boolean isNoPollsMessageVisible() {
        try {
            return noPollsMessageText.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public String getNoPollsMessageText() {
        try {
            return noPollsMessageText.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return "";
        }
    }
} 