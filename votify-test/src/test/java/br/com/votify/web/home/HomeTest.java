package br.com.votify.web.home;

import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeTest extends SeleniumTest {
    private HomePage page;

    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.get("/home");
        page = new HomePage(webDriver);
    }

    @TestTemplate
    public void testSearchForA() {
        new Actions(webDriver)
                .sendKeys(page.navSearchPoll, "a")
                .sendKeys(Keys.ENTER)
                .perform();

        wait.until(ExpectedConditions.titleIs("Pesquisa - Votify"));

        int totalPollCount = countPolls();
        assertEquals(11, totalPollCount, "Total poll count across all pages should be 11.");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home/search?title=a&page=1"),
                "The URL must remain on the home page and at the page 1."
        );
    }

    @TestTemplate
    public void testSearchNoResults() {
        new Actions(webDriver)
                .sendKeys(page.navSearchPoll, "ashduiaguywdgauydgwyu72sadxkjhaiju")
                .sendKeys(Keys.ENTER)
                .perform();

        wait.until(ExpectedConditions.titleIs("Pesquisa - Votify"));

        WebElement alertMessage = webDriver.findElement(By.xpath("//p"));
        assertEquals(
                "Nenhuma enquete encontrada para \"ashduiaguywdgauydgwyu72sadxkjhaiju\"",
                alertMessage.getText().trim(),
                "Alert message should match expected text."
        );

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home/search?title=ashduiaguywdgauydgwyu72sadxkjhaiju&page=0"),
                "The URL must go to the search page and at the page 0."
        );
    }

    @TestTemplate
    public void testSearchForEmptyStringRedirectToHome() {
        new Actions(webDriver)
                .sendKeys(page.navSearchPoll, " ")
                .sendKeys(Keys.ENTER)
                .perform();

        wait.until(ExpectedConditions.titleIs("Home - Votify"));

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home"),
                "The URL must go to the home page."
        );
    }

    @TestTemplate
    public void testHomeActivePolls() {
        int totalPollCount = countPolls();
        assertEquals(11, totalPollCount, "Total poll count across all pages should be 11.");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home?page=1"),
                "The URL must remain on the home page and at the page 1."
        );
    }

    @TestTemplate
    public void testGoToFourthPoll() {
        page.pollAnchors.get(3).click();

        wait.until(ExpectedConditions.urlContains("/polls"));

        assertEquals("Enquete: Sino ang pinakagusto mong karakter sa Until Then? - Votify", webDriver.getTitle());
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/polls/11"),
                "The URL must be on the polls page at the id 11."
        );
    }

    @TestTemplate
    public void testGoToFirstPollAfterSearch() {
        page.navSearchPoll.sendKeys("pizza");
        page.navSearchPoll.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.urlContains("title=pizza"));
        page = new HomePage(webDriver);

        page.pollAnchors.get(0).click();
        wait.until(ExpectedConditions.urlContains("/polls"));

        assertEquals("Enquete: Pizza com abacaxi... - Votify", webDriver.getTitle());
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/polls/8"),
                "The URL must be on the polls page at the id 8."
        );
    }

    private int countPolls() {
        int totalPollCount = 0;
        int currentPage = 1;
        boolean endOfPagination = false;

        while (!endOfPagination) {
            List<WebElement> pollCards = page.pollList.findElements(By.xpath("./*"));
            totalPollCount += pollCards.size();

            try {
                if (!page.buttonNextPage.isEnabled()) {
                    endOfPagination = true;
                    continue;
                }
            } catch (NoSuchElementException e) {
                endOfPagination = true;
                continue;
            }

            page.buttonNextPage.click();
            wait.until(ExpectedConditions.urlContains("page=" + currentPage++));
        }
        return totalPollCount;
    }
}
