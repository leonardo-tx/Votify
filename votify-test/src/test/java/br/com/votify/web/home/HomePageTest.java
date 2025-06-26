package br.com.votify.web.home;

import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class HomePageTest extends SeleniumTest {
    private HomePage homePage;

    @BeforeEach
    void setupBeforeEach() throws Exception {
        seleniumHelper.get("/home");
        homePage = new HomePage(webDriver);
    }

    @TestTemplate
    void shouldDisplayCreatePollButton() {
        assertTrue(seleniumHelper.isInViewport(homePage.createPollButton));
        assertEquals("Criar Nova Enquete", homePage.createPollButton.getText());
    }

    @TestTemplate
    void shouldOpenCreatePollModalWhenButtonClicked() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        
        assertTrue(seleniumHelper.isInViewport(homePage.createPollModal));
        assertTrue(seleniumHelper.isInViewport(homePage.modalTitle));
        assertEquals("Criar Nova Enquete", homePage.modalTitle.getText());
    }

    @TestTemplate
    void shouldCloseModalWhenCancelButtonClicked() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));

        homePage.cancelButton.click();
        
        assertFalse(seleniumHelper.isInViewport(homePage.createPollModal));
    }

    @TestTemplate
    void shouldDisplayPollsList() {
        assertTrue(seleniumHelper.isInViewport(homePage.pollsList));
    }

    @TestTemplate
    void shouldDisplayPagination() {
        assertTrue(seleniumHelper.isInViewport(homePage.pagination));
    }

    @TestTemplate
    void shouldNavigateToNextPage() {
        if (homePage.nextPageButton.isDisplayed()) {
            homePage.nextPageButton.click();
            
            wait.until(ExpectedConditions.urlContains("page=1"));
            assertTrue(webDriver.getCurrentUrl().contains("page=1"));
        }
    }

    @TestTemplate
    void shouldNavigateToPreviousPage() {
        seleniumHelper.get("/home?page=1");
        
        if (homePage.previousPageButton.isDisplayed()) {
            homePage.previousPageButton.click();
            
            wait.until(ExpectedConditions.urlContains("page=0"));
            assertTrue(webDriver.getCurrentUrl().contains("page=0"));
        }
    }

    @TestTemplate
    void shouldCreatePollFromHomePage() {
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
    void shouldShowValidationErrorsInModal() {
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        
        homePage.createButton.click();
        
        wait.until(ExpectedConditions.visibilityOfAllElements(homePage.errorMessages));
        assertFalse(homePage.errorMessages.isEmpty());
    }

    @TestTemplate
    void shouldAllowCreatePollButtonWithoutAuthentication() {
        seleniumHelper.get("/home");
        
        assertTrue(seleniumHelper.isInViewport(homePage.createPollButton));
        assertTrue(homePage.createPollButton.isEnabled());
        
        String buttonTitle = homePage.createPollButton.getDomAttribute("title");
        assertTrue(buttonTitle == null || !buttonTitle.contains("Você precisa estar logado"));
    }

    @TestTemplate
    void shouldOpenModalWithoutAuthentication() {
        seleniumHelper.get("/home");
        
        homePage.createPollButton.click();
        
        wait.until(ExpectedConditions.visibilityOf(homePage.createPollModal));
        assertTrue(seleniumHelper.isInViewport(homePage.createPollModal));
    }
} 