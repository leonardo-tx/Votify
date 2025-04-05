package br.com.votify.web.login;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseTest {
    private LoginPage page;
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "Test123456";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_NAME = "Test User";
    private static final String API_BASE_URL = "http://localhost:8081/";

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

    private void registerUser() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
            "{\"userName\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
            TEST_USERNAME, TEST_NAME, TEST_EMAIL, TEST_PASSWORD
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        restTemplate.postForObject(API_BASE_URL + "/auth/register", request, String.class);
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

    @Test
    public void testSuccessfulLogin() throws InterruptedException {
        registerUser();
        login(TEST_EMAIL, TEST_PASSWORD);
        sleep(2000);
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/home"), "Should redirect to home page after successful login");
    }

    @Test
    public void testLoginWithInvalidEmail() throws InterruptedException {
        login("invalid@email.com", "12345678");
        sleep(2000);
        WebElement errorMessage = webDriver.findElement(By.className("text-red-500"));
        assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
        assertEquals("Credenciais inválidas", errorMessage.getText(), "Should show invalid credentials message");
    }

    @Test
    public void testLoginWithInvalidPassword() throws InterruptedException {
        login("123@gmail.com", "wrongpassword");
        sleep(2000);
        WebElement errorMessage = webDriver.findElement(By.className("text-red-500"));
        assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
        assertEquals("Credenciais inválidas", errorMessage.getText(), "Should show invalid credentials message");
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