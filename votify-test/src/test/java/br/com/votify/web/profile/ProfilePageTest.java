package br.com.votify.web.profile;

import br.com.votify.web.BaseTest;
import br.com.votify.web.login.LoginPage; // Precisaremos para o login
import br.com.votify.web.profile.ProfilePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By; // Importar By
import org.openqa.selenium.TimeoutException; // Importar TimeoutException
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class ProfilePageTest extends BaseTest {

    private ProfilePage profilePage;
    private LoginPage loginPage;

    private static final String TEST_USER_USERNAME = "admin";
    private static final String TEST_USER_NAME = "Administrator";
    private static final String TEST_LOGIN_EMAIL = "admin@votify.com.br";
    private static final String TEST_LOGIN_PASSWORD = "admin123";

    private static final String NON_EXISTENT_USERNAME = "usuarioinexistente12345";


    @BeforeEach
    void setupProfileTest() {
        webDriver.get(BASE_URL + "/login");
        loginPage = new LoginPage(webDriver);
        loginPage.emailInput.sendKeys(TEST_LOGIN_EMAIL);
        loginPage.passwordInput.sendKeys(TEST_LOGIN_PASSWORD);
        loginPage.submitButton.click();
        wait.until(ExpectedConditions.urlContains("/home"));

        String profileUrl = BASE_URL + "/profile/" + TEST_USER_USERNAME;
        webDriver.get(profileUrl);

        profilePage = new ProfilePage(webDriver);
        
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'bg-white') and contains(@class, 'shadow-xl')]//div[contains(@class, 'text-center')]/h1")));
            wait.until(ExpectedConditions.visibilityOf(profilePage.getUserNameElement()));
        } catch (TimeoutException e) {
            System.err.println("DEBUG: Timeout waiting for user name element. Current URL: " + webDriver.getCurrentUrl() + ", Title: " + webDriver.getTitle());
            throw e;
        }
    }

    @TestTemplate
    public void shouldDisplayUserProfileInformation() {
        assertEquals(TEST_USER_NAME, profilePage.getUserName(), "O nome do usuário não corresponde ao esperado.");
        assertEquals("@" + TEST_USER_USERNAME, profilePage.getUserUsername(), "O username não corresponde ao esperado.");
        assertTrue(profilePage.isCreatedPollsSectionVisible(), "A seção de enquetes criadas deveria estar visível.");
    }
} 