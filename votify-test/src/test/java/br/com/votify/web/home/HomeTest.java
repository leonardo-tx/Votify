package br.com.votify.web.home;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
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
    
    @Test
    public void testSearchForA() throws InterruptedException {

        WebElement searchInput = webDriver.findElement(By.id("nav-search-poll"));
        searchInput.clear();
        searchInput.sendKeys("a");
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000);

        List<WebElement> pollCards = page.pollList.findElements(By.xpath("./*"));
        assertEquals(0, pollCards.size(), "Search for 'a' should return 0 polls");

    }
    
    @Test
    public void testSearchForEmptyString() throws InterruptedException {
        WebElement searchInput = webDriver.findElement(By.id("nav-search-poll"));
        searchInput.clear();
        searchInput.sendKeys("a");
        searchInput.sendKeys(Keys.ENTER);
        searchInput.clear();
        searchInput.sendKeys(" ");
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000);
        
        WebElement errorMessage = webDriver.findElement(By.xpath("//div[contains(@class, 'bg-red-100')]"));
        assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
        assertEquals("Por favor, informe um título não vazio ou nulo para pesquisa.", 
                errorMessage.getText().trim(), 
                "Error message should match expected text");
    }

    // todo: Precisamos implementar alguns Polls para que possa ser possível testar a busca e inserção pela página.
}
