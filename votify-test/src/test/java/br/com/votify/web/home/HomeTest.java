package br.com.votify.web.home;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
        List<WebElement> pollCards = page.pollList.findElements(By.xpath("./*"));
        assertEquals(24, pollCards.size());
    }
}
