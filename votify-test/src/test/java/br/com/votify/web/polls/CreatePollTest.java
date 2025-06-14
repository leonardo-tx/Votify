package br.com.votify.web.polls;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreatePollTest extends SeleniumTest {
    private CreatePollPage createPollPage;

    @BeforeEach
    void setupBeforeEach() throws Exception {
        seleniumHelper.get("/home");
        List<Cookie> cookies = seleniumHelper.getLoginCookies(
                new UserLoginDTO("common@votify.com.br", "password123")
        );
        cookies.forEach(c -> webDriver.manage().addCookie(c));

        // Click create poll button to open modal
        webDriver.findElement(By.xpath("//button[contains(text(), 'Criar Nova Enquete')]")).click();
        createPollPage = new CreatePollPage(webDriver);
    }

    @TestTemplate
    public void shouldCreatePollSuccessfully() {
        // Fill in the form
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.descriptionInput.sendKeys("Test Poll Description");
        
        // Set dates (start now, end in 1 hour)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        createPollPage.startDateInput.sendKeys(now.format(formatter));
        createPollPage.endDateInput.sendKeys(endTime.format(formatter));
        createPollPage.choiceLimitInput.sendKeys("1");
        
        // Add vote options
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        createPollPage.addOptionButton.click();
        createPollPage.voteOptionInputs.get(1).sendKeys("Option 2");
        
        // Submit form
        createPollPage.createButton.click();
        
        // Wait for redirect to poll page
        wait.until(ExpectedConditions.urlContains("/polls/"));
        
        // Verify we're on a poll page
        assertTrue(webDriver.getCurrentUrl().contains("/polls/\\d+"));
    }

    @TestTemplate
    public void shouldShowValidationErrorsForEmptyFields() {
        // Try to submit without filling any fields
        createPollPage.createButton.click();
        
        // Wait for error messages
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        // Verify error messages
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O título deve ter entre 5 e 50 caracteres")));
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de início é obrigatória")));
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de término é obrigatória")));
    }

    @TestTemplate
    public void shouldValidateTitleLength() {
        // Try with short title
        createPollPage.titleInput.sendKeys("123");
        createPollPage.createButton.click();
        
        // Wait for error message
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        // Verify error message
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O título deve ter entre 5 e 50 caracteres")));
    }

    @TestTemplate
    public void shouldValidateEndDateAfterStartDate() {
        // Fill in dates with end date before start date
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        createPollPage.startDateInput.sendKeys(now.format(formatter));
        createPollPage.endDateInput.sendKeys(pastTime.format(formatter));
        createPollPage.createButton.click();
        
        // Wait for error message
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        // Verify error message
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de término deve ser posterior à data de início")));
    }

    @TestTemplate
    public void shouldAddAndRemoveVoteOptions() {
        // Add two options
        createPollPage.addOptionButton.click();
        createPollPage.addOptionButton.click();
        
        // Verify we have 3 options (1 default + 2 added)
        assertEquals(3, createPollPage.voteOptionInputs.size());
        
        // Remove one option
        createPollPage.removeOptionButtons.get(0).click();
        
        // Verify we have 2 options
        assertEquals(2, createPollPage.voteOptionInputs.size());
    }
} 