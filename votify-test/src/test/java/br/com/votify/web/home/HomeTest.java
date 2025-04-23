package br.com.votify.web.home;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

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

        WebElement alertMessage = webDriver.findElement(By.xpath("//p"));
        assertEquals("Nenhuma enquete encontrada para \"a\"",
                alertMessage.getText().trim(),
                "Alert message should match expected text");

    }
    
    @Test
    public void testSearchForEmptyString() throws InterruptedException {
        WebElement searchInput = webDriver.findElement(By.id("nav-search-poll"));
        searchInput.clear();
        searchInput.sendKeys(" ");
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000);
        
        int totalPollCount = 0;
        boolean hasNextPage = true;
        
        while (hasNextPage) {
            List<WebElement> pollCards = page.pollList.findElements(By.xpath("./*"));
            totalPollCount += pollCards.size();
            
            List<WebElement> nextButtons = webDriver.findElements(By.xpath("//button[contains(text(), 'Próximo')]"));
            if (!nextButtons.isEmpty() && !nextButtons.get(0).getAttribute("class").contains("opacity-50")) {
                nextButtons.get(0).click();
                Thread.sleep(1000);
            } else {
                hasNextPage = false;
            }
        }
        
        assertEquals(24, totalPollCount, "Total poll count across all pages should be 24");
    }

    // todo: Precisamos implementar alguns Polls para que possa ser possível testar a busca e inserção pela página.
}
