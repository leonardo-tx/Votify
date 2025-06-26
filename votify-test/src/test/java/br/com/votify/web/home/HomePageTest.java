package br.com.votify.web.home;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import br.com.votify.web.polls.CreatePollPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HomePageTest extends SeleniumTest {
    private HomePage homePage;

    @BeforeEach
    void setupBeforeEach() throws Exception {
        seleniumHelper.get("/home");
        homePage = new HomePage(webDriver);
    }

    @TestTemplate
    public void shouldDisplayCreatePollButton() {
        assertTrue(seleniumHelper.isInViewport(homePage.createPollButton));
        assertEquals("Criar Nova Enquete", homePage.createPollButton.getText());
    }

    @TestTemplate
    public void shouldOpenCreatePollModalWhenButtonClicked() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        
        assertTrue(seleniumHelper.isInViewport(homePage.createPollModal));
        assertTrue(seleniumHelper.isInViewport(homePage.modalTitle));
        assertEquals("Criar Nova Enquete", homePage.modalTitle.getText());
    }

    @TestTemplate
    public void shouldCloseModalWhenCancelButtonClicked() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));

        homePage.cancelButton.click();
        
        assertFalse(seleniumHelper.isInViewport(homePage.createPollModal));
    }

    @TestTemplate
    public void shouldDisplayPollsList() {
        assertTrue(seleniumHelper.isInViewport(homePage.pollsList));
    }

    @TestTemplate
    public void shouldDisplayPagination() {
        assertTrue(seleniumHelper.isInViewport(homePage.pagination));
    }

    @TestTemplate
    public void shouldNavigateToNextPage() {
        if (homePage.nextPageButton.isDisplayed()) {
            homePage.nextPageButton.click();
            
            wait.until(ExpectedConditions.urlContains("page=1"));
            assertTrue(webDriver.getCurrentUrl().contains("page=1"));
        }
    }

    @TestTemplate
    public void shouldNavigateToPreviousPage() {
        seleniumHelper.get("/home?page=1");
        
        if (homePage.previousPageButton.isDisplayed()) {
            homePage.previousPageButton.click();
            
            wait.until(ExpectedConditions.urlContains("page=0"));
            assertTrue(webDriver.getCurrentUrl().contains("page=0"));
        }
    }

    @TestTemplate
    public void shouldCreatePollFromHomePage() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        
        homePage.titleInput.sendKeys("Test Poll from Home");
        homePage.descriptionInput.sendKeys("Test Description");
        homePage.endDateInput.sendKeys("2024-12-31T23:59");
        homePage.choiceLimitInput.sendKeys("1");
        homePage.voteOptionInputs.get(0).sendKeys("Test Option");
        
        homePage.createButton.click();
        
        wait.until(ExpectedConditions.urlContains("/polls/"));
        assertTrue(webDriver.getCurrentUrl().contains("/polls/"));
    }

    @TestTemplate
    public void shouldShowValidationErrorsInModal() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        
        homePage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(homePage.errorMessages));
        assertFalse(homePage.errorMessages.isEmpty());
    }

    @TestTemplate
    public void shouldAllowCreatePollButtonWithoutAuthentication() {
        seleniumHelper.get("/home");
        
        assertTrue(seleniumHelper.isInViewport(homePage.createPollButton));
        assertTrue(homePage.createPollButton.isEnabled());
        
        String buttonTitle = homePage.createPollButton.getAttribute("title");
        assertTrue(buttonTitle == null || !buttonTitle.contains("Você precisa estar logado"));
    }

    @TestTemplate
    public void shouldOpenModalWithoutAuthentication() {
        seleniumHelper.get("/home");
        
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        assertTrue(seleniumHelper.isInViewport(homePage.createPollModal));
    }
} 