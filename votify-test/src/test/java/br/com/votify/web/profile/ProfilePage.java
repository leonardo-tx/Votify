package br.com.votify.web.profile;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class ProfilePage extends BasePage {

    @FindBy(xpath = "//div[contains(@class, 'bg-white') and contains(@class, 'shadow-xl')]//div[contains(@class, 'text-center')]/h1")
    private WebElement userNameText;

    @FindBy(xpath = "//div[contains(@class, 'bg-white') and contains(@class, 'shadow-xl')]//div[contains(@class, 'text-center')]/p")
    private WebElement userUsernameText;

    @FindBy(xpath = "//h2[contains(text(), 'Enquetes Criadas')]")
    private WebElement createdPollsSectionTitle;

    private List<WebElement> pollCards;
    
    @FindBy(xpath = "//h1[contains(text(), 'Erro ao carregar perfil')]")
    private WebElement errorLoadingProfileTitle;

    @FindBy(xpath = "//p[contains(@class, 'text-gray-700')]")
    private WebElement errorMessageText;

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
} 