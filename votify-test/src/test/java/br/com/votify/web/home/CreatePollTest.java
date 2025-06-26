package br.com.votify.web.home;

import br.com.votify.dto.user.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreatePollTest extends SeleniumTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy'\t'hhmma");
    private static List<Cookie> cookies = null;

    private CreatePollPage createPollPage;

    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.get("/login");
        if (cookies == null) {
            cookies = seleniumHelper.loginAndGetCookies(new UserLoginDTO("common@votify.com.br", "password123"));
            return;
        }
        cookies.forEach(c -> webDriver.manage().addCookie(c));
        seleniumHelper.get("/home");
        WebElement openPollCreateModal = webDriver.findElement(By.id("open-poll-create-modal"));
        wait.until(d -> openPollCreateModal.isEnabled());

        openPollCreateModal.click();

        wait.until(d -> d.findElement(By.id("create-poll-button")));
        createPollPage = new CreatePollPage(webDriver);
    }

    @TestTemplate
    @Order(-1)
    void canaryTest() {
        assertTrue(true);
    }

    @TestTemplate
    void shouldCreatePollSuccessfully() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.descriptionInput.sendKeys("Test Poll Description");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(1);
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : endTime.format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();

        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        createPollPage.addOptionButton.click();
        createPollPage.voteOptionInputs.get(1).sendKeys("Option 2");

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.urlContains("/polls/"));

        assertTrue(webDriver.getCurrentUrl().contains("/polls/"));
    }

    @TestTemplate
    void shouldCreatePollWithStartDate() {
        createPollPage.titleInput.sendKeys("Test Poll With Start Date");
        createPollPage.descriptionInput.sendKeys("Test Poll Description");

        LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);
        LocalDateTime endTime = startTime.plusHours(1);
        
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.startDateInput, 0, createPollPage.startDateInput.getSize().height / 2)
                .click();
        for (char c : startTime.format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : endTime.format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();

        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.urlContains("/polls/"));

        assertTrue(webDriver.getCurrentUrl().contains("/polls/"));
    }

    @TestTemplate
    void shouldShowValidationErrorsForEmptyRequiredFields() {
        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O título deve ter entre 5 e 50 caracteres")));
        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de término é obrigatória")));
    }

    @TestTemplate
    void shouldValidateTitleLength() {
        createPollPage.titleInput.sendKeys("123");
        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O título deve ter entre 5 e 50 caracteres")));
    }

    @TestTemplate
    void shouldValidateStartDateInFuture() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(5);
        
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.startDateInput, 0, createPollPage.startDateInput.getSize().height / 2)
                .click();
        for (char c : pastTime.format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : LocalDateTime.now().plusHours(1).format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();
        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de início deve ser maior que o momento atual")));
    }

    @TestTemplate
    void shouldValidateEndDateAfterStartDate() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime endTime = startTime.minusMinutes(5);
        
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.startDateInput, 0, createPollPage.startDateInput.getSize().height / 2)
                .click();
        for (char c : startTime.format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : endTime.format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();

        createPollPage.titleInput.sendKeys("Test Poll Title");
        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A data de término deve ser posterior à data de início")));
    }

    @TestTemplate
    void shouldValidateVoteOptionsCount() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : LocalDateTime.now().plusHours(1).format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("A enquete deve ter entre 1 e 5 opções de voto")));
    }

    @TestTemplate
    void shouldValidateVoteOptionLength() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : LocalDateTime.now().plusHours(1).format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();

        createPollPage.voteOptionInputs.get(0).sendKeys("AB");

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("Cada opção deve ter entre 3 e 30 caracteres")));
    }

    @TestTemplate
    void shouldValidateChoiceLimit() {
        createPollPage.titleInput.sendKeys("Test Poll Title");
        Actions actions = new Actions(webDriver);
        actions.moveToElement(createPollPage.endDateInput, 0, createPollPage.endDateInput.getSize().height / 2)
                .click();
        for (char c : LocalDateTime.now().plusHours(1).format(formatter).toCharArray()) {
            actions.sendKeys(String.valueOf(c))
                    .pause(Duration.ofMillis(100));
        }
        actions.build().perform();
        createPollPage.choiceLimitInput.sendKeys(Keys.BACK_SPACE + "5");

        createPollPage.voteOptionInputs.get(0).sendKeys("Option 1");
        createPollPage.addOptionButton.click();
        createPollPage.voteOptionInputs.get(1).sendKeys("Option 2");

        createPollPage.createButton.click();

        wait.until(ExpectedConditions.visibilityOfAllElements(createPollPage.errorMessages));
        createPollPage = new CreatePollPage(webDriver);

        assertTrue(createPollPage.errorMessages.stream()
            .anyMatch(msg -> msg.getText().contains("O limite de escolhas deve estar entre 1 e 2")));
    }

    @TestTemplate
    void shouldAddAndRemoveVoteOptions() {
        createPollPage.addOptionButton.click();
        createPollPage.addOptionButton.click();

        assertEquals(3, createPollPage.voteOptionInputs.size());

        createPollPage.removeOptionButtons.get(0).click();

        assertEquals(2, createPollPage.voteOptionInputs.size());
    }

    @TestTemplate
    void shouldNotAllowMoreThanFiveVoteOptions() {
        for (int i = 0; i < 4; i++) {
            createPollPage.addOptionButton.click();
        }

        assertEquals(5, createPollPage.voteOptionInputs.size());

        assertFalse(createPollPage.addOptionButton.isDisplayed());
    }

    @TestTemplate
    void shouldNotShowRemoveButtonForSingleOption() {
        assertEquals(1, createPollPage.voteOptionInputs.size());

        assertTrue(createPollPage.removeOptionButtons.isEmpty());
    }
}