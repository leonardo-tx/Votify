package br.com.votify.web.profile;

import br.com.votify.web.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ProfilePage extends BasePage {

    private final WebDriverWait wait;

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
    private WebElement profilePageErrorMessageText;

    @FindBy(id = "user-profile-no-polls-message")
    private WebElement noPollsMessageText;

    @FindBy(id = "edit-profile-button")
    private WebElement editProfileButton;

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "save-profile-button")
    private WebElement saveProfileButton;

    @FindBy(id = "profile-form-success-message")
    private WebElement successMessage;

    @FindBy(id = "profile-form-error-message")
    private WebElement formErrorMessage;

    @FindBy(id = "delete-account-button")
    private WebElement deleteAccountButton;

    public ProfilePage(WebDriver webDriver) {
        super(webDriver);
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    public WebElement getUserNameElement() {
        return userNameText;
    }

    public WebElement getErrorLoadingProfileTitleElement() {
        return errorLoadingProfileTitle;
    }

    public String getUserName() {
        return wait.until(ExpectedConditions.visibilityOf(userNameText)).getText();
    }

    public String getUserUsername() {
        return wait.until(ExpectedConditions.visibilityOf(userUsernameText)).getText();
    }

    public boolean isCreatedPollsSectionVisible() {
        return wait.until(ExpectedConditions.visibilityOf(createdPollsSectionTitle)).isDisplayed();
    }

    public int getPollsCount() {
        return pollCards.size();
    }
    
    public boolean isErrorLoadingProfileTitleVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(errorLoadingProfileTitle)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getProfilePageErrorMessageText() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(profilePageErrorMessageText)).getText();
        } catch (org.openqa.selenium.TimeoutException e) {
            return "";
        }
    }

    public boolean isNoPollsMessageVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noPollsMessageText)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getNoPollsMessageText() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noPollsMessageText)).getText();
        } catch (org.openqa.selenium.TimeoutException e) {
            return "";
        }
    }

    public void clickEditProfileButton() {
        wait.until(ExpectedConditions.visibilityOf(editProfileButton));
        wait.until(ExpectedConditions.elementToBeClickable(editProfileButton));
        editProfileButton.click();
    }

    public void setNewName(String name) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(nameInput));
        input.clear();
        input.sendKeys(name);
    }

    public void clickSaveProfileButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(saveProfileButton));
        button.click();
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickDeleteAccountButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(deleteAccountButton));
        button.click();
    }

    public boolean isSuccessMessageVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(successMessage)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getSuccessMessageText() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(successMessage)).getText();
        } catch (org.openqa.selenium.TimeoutException e) {
            return "";
        }
    }

    public boolean isFormErrorMessageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(formErrorMessage));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getFormErrorMessageText() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(formErrorMessage)).getText();
        } catch (org.openqa.selenium.TimeoutException e) {
            return "";
        }
    }

    public boolean isEditProfileButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(editProfileButton)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isDeleteAccountButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(deleteAccountButton)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }  catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
} 