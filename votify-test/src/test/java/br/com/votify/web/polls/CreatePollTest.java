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

        webDriver.findElement(By.xpath("//button[contains(text(), 'Criar Nova Enquete')]")).click();
        createPollPage = new CreatePollPage(webDriver);
    }

    @TestTemplate
    public void shouldCreatePollSuccessfully() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.descriptionInput.sendKeys("Test Poll Description");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        createPollPage.endDateInput.sendKeys(endTime.format(formatter));
        createPollPage.choiceLimitInput.sendKeys("1");
        
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        createPollPage.addOptionButton.click();
        createPollPage.voteOptionInputs.get(1).sendKeys("Option 2");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.urlContains("/polls/"));
        
        assertTrue(webDriver.getCurrentUrl().contains("/polls/"));
    }

    @TestTemplate
    public void shouldCreatePollWithStartDate() {
        createPollPage.titleInput.sendKeys("Test Poll With Start Date");
        createPollPage.descriptionInput.sendKeys("Test Poll Description");
        
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);
        LocalDateTime endTime = startTime.plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        createPollPage.startDateInput.sendKeys(startTime.format(formatter));
        createPollPage.endDateInput.sendKeys(endTime.format(formatter));
        createPollPage.choiceLimitInput.sendKeys("1");
        
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.urlContains("/polls/"));
        
        assertTrue(webDriver.getCurrentUrl().contains("/polls/"));
    }

    @TestTemplate
    public void shouldShowValidationErrorsForEmptyRequiredFields() {
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O título deve ter entre 5 e 50 caracteres")));
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de término é obrigatória")));
    }

    @TestTemplate
    public void shouldValidateTitleLength() {
        createPollPage.titleInput.sendKeys("123");
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O título deve ter entre 5 e 50 caracteres")));
    }

    @TestTemplate
    public void shouldValidateStartDateInFuture() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.startDateInput.sendKeys(pastTime.format(formatter));
        createPollPage.endDateInput.sendKeys(LocalDateTime.now().plusHours(1).format(formatter));
        createPollPage.choiceLimitInput.sendKeys("1");
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de início deve ser maior que o momento atual")));
    }

    @TestTemplate
    public void shouldValidateEndDateAfterStartDate() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime endTime = startTime.minusMinutes(5); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.startDateInput.sendKeys(startTime.format(formatter));
        createPollPage.endDateInput.sendKeys(endTime.format(formatter));
        createPollPage.choiceLimitInput.sendKeys("1");
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de término deve ser posterior à data de início")));
    }

    @TestTemplate
    public void shouldValidateVoteOptionsCount() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.endDateInput.sendKeys(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        createPollPage.choiceLimitInput.sendKeys("1");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A enquete deve ter entre 1 e 5 opções de voto")));
    }

    @TestTemplate
    public void shouldValidateVoteOptionLength() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.endDateInput.sendKeys(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        createPollPage.choiceLimitInput.sendKeys("1");
        
        createPollPage.voteOptionInputs.get(0).sendKeys("AB");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("Cada opção deve ter entre 3 e 30 caracteres")));
    }

    @TestTemplate
    public void shouldValidateChoiceLimit() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.endDateInput.sendKeys(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        createPollPage.choiceLimitInput.sendKeys("5"); 
        
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        createPollPage.addOptionButton.click();
        createPollPage.voteOptionInputs.get(1).sendKeys("Option 2");
        
        createPollPage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O limite de escolhas deve estar entre 1 e 2")));
    }

    @TestTemplate
    public void shouldAddAndRemoveVoteOptions() {
        createPollPage.addOptionButton.click();
        createPollPage.addOptionButton.click();
        
        assertEquals(3, createPollPage.voteOptionInputs.size());
        
        createPollPage.removeOptionButtons.get(0).click();
        
        assertEquals(2, createPollPage.voteOptionInputs.size());
    }

    @TestTemplate
    public void shouldNotAllowMoreThanFiveVoteOptions() {
        for (int i = 0; i < 4; i++) {
            createPollPage.addOptionButton.click();
        }
        
        assertEquals(5, createPollPage.voteOptionInputs.size());
        
        assertFalse(createPollPage.addOptionButton.isDisplayed());
    }

    @TestTemplate
    public void shouldNotShowRemoveButtonForSingleOption() {
        assertEquals(1, createPollPage.voteOptionInputs.size());
        
        assertTrue(createPollPage.removeOptionButtons.isEmpty());
    }

    @TestTemplate
    public void shouldCancelPollCreation() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        
        createPollPage.cancelButton.click();
        
        assertTrue(webDriver.getCurrentUrl().contains("/home"));
        
        assertThrows(org.openqa.selenium.NoSuchElementException.class, () -> {
            webDriver.findElement(By.id("title"));
        });
    }
} 