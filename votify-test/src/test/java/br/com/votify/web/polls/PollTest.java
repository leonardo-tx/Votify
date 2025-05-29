package br.com.votify.web.polls;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PollTest extends SeleniumTest {
    private static List<Cookie> cookies = null;

    @BeforeEach
    void setupBeforeEach() throws Exception {
        seleniumHelper.get("/polls/1");
        if (cookies == null) {
            cookies = seleniumHelper.getLoginCookies(new UserLoginDTO("common@votify.com.br", "password123"));
        }
        for (Cookie cookie : cookies) {
            webDriver.manage().addCookie(cookie);
        }
    }

    @TestTemplate
    public void shouldShowNotFoundForInvalidId() {
        seleniumHelper.get("/polls/9999");
        PollPage page = new PollPage(webDriver);

        assertTrue(seleniumHelper.isInViewport(page.noPollMessage));
        assertEquals("Enquete não encontrada.", page.noPollMessage.getText());
        assertEquals("Enquete não encontrada - Votify", webDriver.getTitle());
    }

    @TestTemplate
    public void testAlreadyVotedPoll() {
        seleniumHelper.get("/polls/7");
        PollPage page = new PollPage(webDriver);
        for (int i = 0; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);
            if (i == 2) {
                assertTrue(optionInput.isSelected());
                continue;
            }
            assertFalse(optionInput.isSelected());
        }
        assertEquals("Enquete: タイトル: 推しアニメは？教えてください！ - Votify", webDriver.getTitle());
        assertFalse(seleniumHelper.isInViewport(page.voteButton));
    }

    @TestTemplate
    public void testVoteOnMultipleVotePoll() {
        seleniumHelper.get("/polls/4");
        PollPage page = new PollPage(webDriver);

        for (WebElement optionInput : page.optionInputs) {
            assertFalse(optionInput.isSelected());
        }
        assertTrue(seleniumHelper.isInViewport(page.voteButton));

        for (int i = 1; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);

            optionInput.click();
            assertTrue(optionInput.isSelected());
        }
        page.voteButton.click();
    }

    @TestTemplate
    public void shouldNotDisplayVotersList_whenRegistrationIsFalse() throws Exception {
        List<Cookie> adminCookies = seleniumHelper.getLoginCookies(new UserLoginDTO("admin@votify.com.br", "admin123"));
        seleniumHelper.get("/");
        for (Cookie cookie : adminCookies) {
            webDriver.manage().addCookie(cookie);
        }
        seleniumHelper.get("/polls/1");
        PollPage page = new PollPage(webDriver);
        try {
            page.votersSectionTitle.isDisplayed();
            fail("A seção de votantes foi encontrada, mas não deveria (userRegistration: false).");
        } catch (NoSuchElementException e) {
            assertTrue(true, "Seção de votantes corretamente não encontrada (userRegistration: false).");
        }
    }

    @TestTemplate
    public void shouldDisplayVotersList_forOwner_whenRegistrationIsEnabled() throws Exception {
        List<Cookie> adminCookies = seleniumHelper.getLoginCookies(new UserLoginDTO("admin@votify.com.br", "admin123"));
        assertNotNull(adminCookies, "Login como admin falhou.");
        seleniumHelper.get("/");
        for (Cookie cookie : adminCookies) {
            webDriver.manage().addCookie(cookie);
        }
        seleniumHelper.get("/polls/2");
        PollPage page = new PollPage(webDriver);
        assertTrue(page.votersSectionTitle.isDisplayed(), "Título da seção de votantes não está visível.");
        assertEquals("Participantes Registrados", page.votersSectionTitle.getText());
    }
}