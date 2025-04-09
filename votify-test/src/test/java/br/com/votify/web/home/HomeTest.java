package br.com.votify.web.home;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeTest extends BaseTest {
    private HomePage page;

    protected HomeTest() {
        super("/home");
    }

    @BeforeEach
    void setupBeforeEach() {
        page = new HomePage(webDriver);
    }

    @TestTemplate
    public void testSearchForA() {
        new Actions(webDriver)
                .sendKeys(page.navSearchPoll, "a")
                .sendKeys(Keys.ENTER)
                .perform();

        wait.until(ExpectedConditions.titleIs("Pesquisa - Votify"));

        WebElement alertMessage = webDriver.findElement(By.xpath("//p"));
        assertEquals(
                "Nenhuma enquete encontrada para \"a\"",
                alertMessage.getText().trim(),
                "Alert message should match expected text."
        );

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home/search?title=a&page=0"),
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
        int totalPollCount = 0;
        int currentPage = 1;
        boolean endOfPagination = false;

        while (!endOfPagination) {
            List<WebElement> pollCards = page.pollList.findElements(By.xpath("./*"));
            totalPollCount += pollCards.size();

            if (!page.buttonNextPage.isEnabled()) {
                endOfPagination = true;
                continue;
            }
            page.buttonNextPage.click();
            wait.until(ExpectedConditions.urlContains("/home?page=" + currentPage++));
        }
        assertEquals(24, totalPollCount, "Total poll count across all pages should be 24.");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home?page=2"),
                "The URL must remain on the home page and at the page 2."
        );
    }
}
