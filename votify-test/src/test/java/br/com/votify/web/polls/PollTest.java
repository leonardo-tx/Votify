package br.com.votify.web.polls;
import br.com.votify.web.BaseTest;
import br.com.votify.web.login.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class PollTest extends BaseTest {
    private PollPage page;
    private static final String TEST_EMAIL = "common3@votify.com.br";
    private static final String TEST_PASSWORD = "password123";

    private void login() {
        webDriver.get(BASE_URL + "/login");
        LoginPage loginPage = new LoginPage(webDriver);
        wait.until(ExpectedConditions.visibilityOf(loginPage.emailInput));
        loginPage.emailInput.sendKeys(TEST_EMAIL);
        loginPage.passwordInput.sendKeys(TEST_PASSWORD);
        loginPage.submitButton.click();
        wait.until(ExpectedConditions.urlContains("/home"));
    }

    public PollTest() {
        super("/polls/1");
    }

    @BeforeEach
    void initPage() {
        page = new PollPage(webDriver);
    }

    @Test
    public void shouldShowPollDetails() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("poll-title")));
        assertEquals("Melhor Linguagem de Programação", page.title.getText());
        assertTrue(page.description.isDisplayed());
        assertTrue(page.startDate.getText().contains("Início:"));
        assertTrue(page.endDate.getText().contains("Fim:"));
        assertFalse(page.voteButton.isEnabled(), "Sem seleção, botão deve iniciar desabilitado");
    }

    @Test
    public void shouldAllowVotingWhenNotVotedSingleChoice() {
        login();
        webDriver.get(BASE_URL + "/polls/3");
        page = new PollPage(webDriver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("poll-title")));
        page.optionInputs.get(0).click();
        page.voteButton.click();
        assertTrue(page.voteButton.isEnabled());
    }

    @Test
    public void shouldHandleMultipleChoicePoll() {
        login();
        webDriver.get(BASE_URL + "/polls/4");
        page = new PollPage(webDriver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("poll-title")));
        page.optionInputs.get(0).click();
        page.optionInputs.get(1).click();
        wait.until(ExpectedConditions.elementToBeClickable(page.voteButton));
        assertTrue(page.voteButton.isEnabled());
    }

    @Test
    public void ShouldNotAllowVoteWhenNotSigned(){
        webDriver.get(BASE_URL + "/polls/1");
        page = new PollPage(webDriver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("poll-title")));
        assertFalse(page.voteButton.isEnabled(), "Sem login, botão deve iniciar desabilitado");

    }



    @Test
    public void shouldShowNotFoundForInvalidId() {
        webDriver.get(BASE_URL + "/polls/9999");
        page = new PollPage(webDriver);
        assertTrue(page.noPollMessage.isDisplayed());
        assertEquals("Enquete não encontrada.", page.noPollMessage.getText());
    }
}