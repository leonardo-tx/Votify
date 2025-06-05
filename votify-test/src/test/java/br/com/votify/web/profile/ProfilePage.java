package br.com.votify.web.profile;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class ProfilePage extends BasePage {
    public ProfilePage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "user-profile-name")
    public WebElement userNameText;

    @FindBy(id = "user-profile-username")
    public WebElement userUsernameText;

    @FindBy(id = "user-profile-created-polls-title")
    public WebElement createdPollsSectionTitle;

    @FindBy(css = "[id^='poll-card-']")
    public List<WebElement> pollCards;

    @FindBy(id = "profile-error-message")
    public WebElement profilePageErrorMessage;

    @FindBy(id = "user-profile-no-polls-message")
    public WebElement noPollsMessageText;

    @FindBy(id = "edit-profile-button")
    public WebElement editProfileButton;

    @FindBy(id = "name")
    public WebElement nameInput;

    @FindBy(id = "save-profile-button")
    public WebElement saveProfileButton;

    @FindBy(id = "profile-form-success-message")
    public WebElement successMessage;

    @FindBy(id = "profile-form-error-message")
    public WebElement formErrorMessage;

    @FindBy(id = "delete-account-button")
    public WebElement deleteAccountButton;
} 