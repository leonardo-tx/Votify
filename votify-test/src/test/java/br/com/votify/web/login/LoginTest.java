package br.com.votify.web.login;

import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest extends SeleniumTest {
    private LoginPage page;
    private static final String TEST_EMAIL = "admin@votify.com.br";
    private static final String TEST_PASSWORD = "admin123";

    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.get("/login");
        page = new LoginPage(webDriver);
    }

    private void login(String email, String password) {
        page.emailInput.sendKeys(email);
        page.passwordInput.sendKeys(password);
        page.submitButton.click();
    }

    @TestTemplate
    void checkLoginFormElements() {
        assertTrue(seleniumHelper.isInViewport(page.emailInput), "Email input should be visible");
        assertTrue(seleniumHelper.isInViewport(page.passwordInput), "Password input should be visible");
        assertTrue(seleniumHelper.isInViewport(page.submitButton), "Submit button should be visible");
        assertTrue(seleniumHelper.isInViewport(page.forgotPasswordLink), "Forgot password link should be visible");
        assertTrue(seleniumHelper.isInViewport(page.createAccountLink), "Create account link should be visible");
    }

    @TestTemplate
    void checkLoginFormPlaceholders() {
        assertEquals("Email", page.emailInput.getDomAttribute("placeholder"), "Email input placeholder should be 'Email'");
        assertEquals("Senha", page.passwordInput.getDomAttribute("placeholder"), "Password input placeholder should be 'Senha'");
    }

    @TestTemplate
    void checkLoginFormRequiredFields() {
        assertNotNull(page.emailInput.getDomAttribute("required"), "Email input should be required");
        assertNotNull(page.passwordInput.getDomAttribute("required"), "Password input should be required");
    }

    @TestTemplate
    void checkLoginFormButtonText() {
        assertEquals("Entrar", page.submitButton.getText(), "Submit button text should be 'Entrar'");
    }

    @TestTemplate
    void testSuccessfulLogin() {
        login(TEST_EMAIL, TEST_PASSWORD);
        wait.until(ExpectedConditions.urlContains("/home"));

        String currentUrl = webDriver.getCurrentUrl();
        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/home"), "Should redirect to home page after successful login");
        assertEquals(2, webDriver.manage().getCookies().size());
    }

    @TestTemplate
    void testLoginAlreadyLoggedIn() {
        login(TEST_EMAIL, TEST_PASSWORD);
        wait.until(ExpectedConditions.urlContains("/home"));
        seleniumHelper.get("/login");
        page = new LoginPage(webDriver);
        login(TEST_EMAIL, TEST_PASSWORD);

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(seleniumHelper.isInViewport(errorMessage), "Error message should be displayed");
        assertEquals("Você já está logado.", errorMessage.getText(), "Should show already logged in message");
    }

    @TestTemplate
    void testSuccessfulLoginAccessibility() {
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
    void testLoginWithInvalidEmail() {
        login("invalid@email.com", "12345678");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(seleniumHelper.isInViewport(errorMessage), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @TestTemplate
    void testLoginWithInvalidPassword() {
        login("123@gmail.com", "wrongpassword");

        WebElement errorMessage = wait.until(d -> d.findElement(By.id("login-alert")));
        assertTrue(seleniumHelper.isInViewport(errorMessage), "Error message should be displayed");
        assertEquals("A conta não existe ou a senha está incorreta.", errorMessage.getText(), "Should show invalid credentials message");
    }

    @TestTemplate
    void testLoginWithEmptyEmail() {
        login("", "12345678");
        String currentUrl = webDriver.getCurrentUrl();

        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/login"), "Should stay on login page with empty email");
    }

    @TestTemplate
    void testLoginWithEmptyPassword() {
        login("123@gmail.com", "");
        String currentUrl = webDriver.getCurrentUrl();

        assertNotNull(currentUrl);
        assertTrue(currentUrl.endsWith("/login"), "Should stay on login page with empty password");
    }
} 