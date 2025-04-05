package br.com.votify.web.login;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseTest {
    private LoginPage page;

    protected LoginTest() {
        super("/login");
    }

    @BeforeEach
    void setupBeforeEach() {
        page = new LoginPage(webDriver);
    }

    @Test
    public void checkLoginFormElements() {
        assertTrue(page.emailInput.isDisplayed(), "Email input should be visible");
        assertTrue(page.passwordInput.isDisplayed(), "Password input should be visible");
        assertTrue(page.submitButton.isDisplayed(), "Submit button should be visible");
        assertTrue(page.forgotPasswordLink.isDisplayed(), "Forgot password link should be visible");
        assertTrue(page.createAccountLink.isDisplayed(), "Create account link should be visible");
    }

    @Test
    public void checkLoginFormPlaceholders() {
        assertEquals("Email", page.emailInput.getAttribute("placeholder"), "Email input placeholder should be 'Email'");
        assertEquals("Senha", page.passwordInput.getAttribute("placeholder"), "Password input placeholder should be 'Senha'");
    }

    @Test
    public void checkLoginFormRequiredFields() {
        assertTrue(page.emailInput.getAttribute("required") != null, "Email input should be required");
        assertTrue(page.passwordInput.getAttribute("required") != null, "Password input should be required");
    }

    @Test
    public void checkLoginFormButtonText() {
        assertEquals("Entrar", page.submitButton.getText(), "Submit button text should be 'Entrar'");
    }

} 