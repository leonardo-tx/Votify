package br.com.votify.web.login;

import br.com.votify.test.SeleniumHelper;
import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseTest {
    private LoginPage page;
    private static final String TEST_EMAIL = "admin@votify.com.br";
    private static final String TEST_PASSWORD = "admin123";

    protected LoginTest() {
        super("/login");
    }

    @BeforeEach
    void setupBeforeEach() {
        page = new LoginPage(webDriver);
    }

    private void login(String email, String password) {
        page.emailInput.sendKeys(email);
        page.passwordInput.sendKeys(password);
        page.submitButton.click();
    }

    @Test
    public void checkLoginFormElements() {
        assertTrue(SeleniumHelper.isInViewport(page.emailInput, webDriver), "Email input should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.passwordInput, webDriver), "Password input should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.submitButton, webDriver), "Submit button should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.forgotPasswordLink, webDriver), "Forgot password link should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.createAccountLink, webDriver), "Create account link should be visible");
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

    @Test
    public void testSuccessfulLogin() {
        login(TEST_EMAIL, TEST_PASSWORD);

        wait.until(ExpectedConditions.urlContains("/home"));

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/home"), "Should redirect to home page after successful login");
    }

    @Test
    public void testLoginAlreadyLoggedIn() {
        login(TEST_EMAIL, TEST_PASSWORD);
        wait.until(ExpectedConditions.urlContains("/home"));

        webDriver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.urlContains("/login"));

        login(TEST_EMAIL, TEST_PASSWORD);
        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(SeleniumHelper.isInViewport(errorMessage, webDriver), "Error message should be displayed");
        assertEquals("Você já está logado.", errorMessage.getText(), "Should show already logged in message");
    }

    @Test
    public void testSuccessfulLoginAccessibility() {
        new Actions(webDriver)
                .sendKeys(page.emailInput, TEST_EMAIL)
                .sendKeys(Keys.TAB)
                .sendKeys(TEST_PASSWORD)
                .sendKeys(Keys.TAB)
                .sendKeys(Keys.TAB)
                .sendKeys(Keys.ENTER)
                .perform();

        wait.until(ExpectedConditions.urlContains("/home"));

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/home"), "Should redirect to home page after successful login");
    }

    @Test
    public void testLoginWithInvalidEmail() {
        login("invalid@email.com", "12345678");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(SeleniumHelper.isInViewport(errorMessage, webDriver), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @Test
    public void testLoginWithInvalidPassword() {
        login("123@gmail.com", "wrongpassword");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(SeleniumHelper.isInViewport(errorMessage, webDriver), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @Test
    public void testLoginWithEmptyEmail() {
        login("", "12345678");
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"), "Should stay on login page with empty email");
    }

    @Test
    public void testLoginWithEmptyPassword() {
        login("123@gmail.com", "");
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"), "Should stay on login page with empty password");
    }
} 