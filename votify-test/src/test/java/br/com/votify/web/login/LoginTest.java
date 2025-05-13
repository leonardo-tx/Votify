package br.com.votify.web.login;

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

    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.goToPath("/login");
        page = new LoginPage(webDriver);
    }

    private void login(String email, String password) {
        page.emailInput.sendKeys(email);
        page.passwordInput.sendKeys(password);
        page.submitButton.click();
    }

    @TestTemplate
    public void checkLoginFormElements() {
        assertTrue(seleniumHelper.isInViewport(page.emailInput), "Email input should be visible");
        assertTrue(seleniumHelper.isInViewport(page.passwordInput), "Password input should be visible");
        assertTrue(seleniumHelper.isInViewport(page.submitButton), "Submit button should be visible");
        assertTrue(seleniumHelper.isInViewport(page.forgotPasswordLink), "Forgot password link should be visible");
        assertTrue(seleniumHelper.isInViewport(page.createAccountLink), "Create account link should be visible");
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
        assertEquals(2, webDriver.manage().getCookies().size());
    }

    @TestTemplate
    public void testLoginAlreadyLoggedIn() {
        login(TEST_EMAIL, TEST_PASSWORD);
        wait.until(ExpectedConditions.urlContains("/home"));
        seleniumHelper.goToPath("/login");
        page = new LoginPage(webDriver);
        login(TEST_EMAIL, TEST_PASSWORD);

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(seleniumHelper.isInViewport(errorMessage), "Error message should be displayed");
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
        assertTrue(seleniumHelper.isInViewport(errorMessage), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @TestTemplate
    public void testLoginWithInvalidPassword() {
        login("123@gmail.com", "wrongpassword");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(seleniumHelper.isInViewport(errorMessage), "Error message should be displayed");
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