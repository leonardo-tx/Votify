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

import static org.junit.jupiter.api.Assertions.*;

class HomeTest extends SeleniumTest {
    private HomePage page;

    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.get("/home");
        page = new HomePage(webDriver);
    }

    @TestTemplate
    void checkVisibilityOfHeader() {
        WebElement header = webDriver.findElement(By.tagName("header"));
        for (int i = 0; i < 20; i++) {
            new Actions(webDriver).sendKeys(Keys.PAGE_DOWN).perform();
            assertTrue(seleniumHelper.isInViewport(header));
        }
        for (int i = 0; i < 20; i++) {
            new Actions(webDriver).sendKeys(Keys.PAGE_UP).perform();
            assertTrue(seleniumHelper.isInViewport(header));
        }
    }

    @TestTemplate
    void checkHeader() {
        List<WebElement> elements = webDriver.findElements(By.tagName("header"));
        assertEquals(1, elements.size());

        WebElement header = elements.get(0);

        assertEquals(1, header.findElements(By.id("logo-home-anchor")).size());
        assertEquals(1, header.findElements(By.id("login-button")).size());
        assertEquals(1, header.findElements(By.id("signup-button")).size());

        List<WebElement> navigators = header.findElements(By.tagName("nav"));
        assertEquals(1, navigators.size());

        WebElement nav = navigators.get(0);

        assertEquals(1, nav.findElements(By.id("nav-about-anchor")).size());
        assertEquals(1, nav.findElements(By.id("nav-search-poll")).size());
    }

    @TestTemplate
    void checkFooter() {
        List<WebElement> elements = webDriver.findElements(By.tagName("footer"));
        assertEquals(1, elements.size());

        WebElement footer = elements.get(0);
        assertEquals(1, footer.findElements(By.id("git-repository-anchor")).size());

        WebElement githubRepositoryAnchor = footer.findElement(By.id("git-repository-anchor"));
        assertEquals("https://github.com/leonardo-tx/Votify", githubRepositoryAnchor.getDomAttribute("href"));
    }

    @TestTemplate
    void testSearchForA() {
        new Actions(webDriver)
                .sendKeys(page.navSearchPoll, "a")
                .sendKeys(Keys.ENTER)
                .perform();

        wait.until(ExpectedConditions.titleIs("Pesquisa - Votify"));

        int totalPollCount = countPolls();
        assertEquals(12, totalPollCount, "Total poll count across all pages should be 12.");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home/search?title=a&page=1"),
                "The URL must remain on the home page and at the page 1."
        );
    }

    @TestTemplate
    void testSearchNoResults() {
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
    void testSearchForEmptyStringRedirectToHome() {
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
    void testHomeActivePolls() {
        int totalPollCount = countPolls();
        assertEquals(12, totalPollCount, "Total poll count across all pages should be 12.");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(
                currentUrl.endsWith("/home?page=1"),
                "The URL must remain on the home page and at the page 1."
        );
    }

    @TestTemplate
    void testGoToFourthPoll() {
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
    void testGoToFirstPollAfterSearch() {
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

    @TestTemplate
    void shouldNavigateToNextPage() {
        if (page.buttonNextPage.isDisplayed()) {
            page.buttonNextPage.click();

            wait.until(ExpectedConditions.urlContains("page=1"));
            assertTrue(webDriver.getCurrentUrl().contains("page=1"));
        }
    }

    @TestTemplate
    void shouldNavigateToPreviousPage() {
        seleniumHelper.get("/home?page=1");

        if (page.buttonPreviousPage.isDisplayed()) {
            page.buttonPreviousPage.click();

            wait.until(ExpectedConditions.urlContains("page=0"));
            assertTrue(webDriver.getCurrentUrl().contains("page=0"));
        }
    }

    @TestTemplate
    void shouldDisplayCreatePollButton() {
        assertTrue(seleniumHelper.isInViewport(page.createPollButton));
        assertEquals("Criar Nova Enquete", page.createPollButton.getText());
    }

    @TestTemplate
    void shouldNotAllowCreatePollButtonWithoutAuthentication() {
        assertTrue(seleniumHelper.isInViewport(page.createPollButton));
        assertFalse(page.createPollButton.isEnabled());
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
