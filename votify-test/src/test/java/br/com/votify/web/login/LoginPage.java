package br.com.votify.web.login;

import br.com.votify.web.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "login-email")
    public WebElement emailInput;

    @FindBy(id = "login-password")
    public WebElement passwordInput;

    @FindBy(id = "login-submit-button")
    public WebElement submitButton;

    @FindBy(id = "forgot-password-link")
    public WebElement forgotPasswordLink;

    @FindBy(id = "create-account-link")
    public WebElement createAccountLink;
} 