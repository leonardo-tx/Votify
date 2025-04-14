package br.com.votify.web.login;

import br.com.votify.test.SeleniumHelper;
import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    @TestTemplate
    public void checkLoginFormElements() {
        assertTrue(SeleniumHelper.isInViewport(page.emailInput, webDriver), "Email input should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.passwordInput, webDriver), "Password input should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.submitButton, webDriver), "Submit button should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.forgotPasswordLink, webDriver), "Forgot password link should be visible");
        assertTrue(SeleniumHelper.isInViewport(page.createAccountLink, webDriver), "Create account link should be visible");
    }

    @TestTemplate
    public void checkLoginFormPlaceholders() {
        assertEquals("Email", page.emailInput.getAttribute("placeholder"), "Email input placeholder should be 'Email'");
        assertEquals("Senha", page.passwordInput.getAttribute("placeholder"), "Password input placeholder should be 'Senha'");
    }

    @TestTemplate
    public void checkLoginFormRequiredFields() {
        assertNotNull(page.emailInput.getAttribute("required"), "Email input should be required");
        assertNotNull(page.passwordInput.getAttribute("required"), "Password input should be required");
    }

    @TestTemplate
    public void checkLoginFormButtonText() {
        assertEquals("Entrar", page.submitButton.getText(), "Submit button text should be 'Entrar'");
    }

    @TestTemplate
    public void testSuccessfulLogin() {
        login(TEST_EMAIL, TEST_PASSWORD);

        wait.until(ExpectedConditions.urlContains("/home"));

        String currentUrl = webDriver.getCurrentUrl();
        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/home"), "Should redirect to home page after successful login");
    }

    @TestTemplate
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

    @TestTemplate
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

        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/home"), "Should redirect to home page after successful login");
    }

    @TestTemplate
    public void testLoginWithInvalidEmail() {
        login("invalid@email.com", "12345678");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(SeleniumHelper.isInViewport(errorMessage, webDriver), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @TestTemplate
    public void testLoginWithInvalidPassword() {
        login("123@gmail.com", "wrongpassword");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(SeleniumHelper.isInViewport(errorMessage, webDriver), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @TestTemplate
    public void testLoginWithEmptyEmail() {
        login("", "12345678");
        String currentUrl = webDriver.getCurrentUrl();

        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/login"), "Should stay on login page with empty email");
    }

    @TestTemplate
    public void testLoginWithEmptyPassword() {
        login("123@gmail.com", "");
        String currentUrl = webDriver.getCurrentUrl();

        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/login"), "Should stay on login page with empty password");
    }
} 