package br.com.votify.web.home;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void checkPollListSize() {
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<WebElement> pollCards = page.pollList.findElements(By.xpath("./div"));
        assertEquals(0, pollCards.size());
    }

    // todo: Precisamos implementar alguns Polls para que possa ser possível testar a busca e inserção pela página.
}
