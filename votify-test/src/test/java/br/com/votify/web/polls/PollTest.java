package br.com.votify.web.polls;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.test.SeleniumHelper;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PollTest extends SeleniumTest {
    private static List<Cookie> cookies = null;

    @BeforeEach
    void setupBeforeEach() {

        SeleniumHelper.goToPath(webDriver, wait, "/polls/1");
        if (cookies == null) {
            cookies = SeleniumHelper.getLoginCookies(new UserLoginDTO("common@votify.com.br", "password123"));
        }
        for (Cookie cookie : cookies) {
            webDriver.manage().addCookie(cookie);
        }
    }

    @TestTemplate
    public void shouldShowNotFoundForInvalidId() {
        SeleniumHelper.goToPath(webDriver, wait, "/polls/9999");
        PollPage page = new PollPage(webDriver);

        assertTrue(SeleniumHelper.isInViewport(page.noPollMessage, webDriver));
        assertEquals("Enquete não encontrada.", page.noPollMessage.getText());
        assertEquals("Enquete não encontrada - Votify", webDriver.getTitle());
    }

    @TestTemplate
    public void testAlreadyVotedPoll() {
        SeleniumHelper.goToPath(webDriver, wait, "/polls/7");
        PollPage page = new PollPage(webDriver);

        assertTrue(page.optionInputs.get(2).isSelected());
        assertEquals("Enquete: タイトル: 推しアニメは？教えてください！ - Votify", webDriver.getTitle());

        assertFalse(page.voteButton.isDisplayed());
    }
}