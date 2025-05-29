package br.com.votify.web.polls;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

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
        List<String> expectedInitialTexts = List.of(
                "Hollow Knight\n3 (25.00%)",
                "Hades\n2 (16.67%)",
                "Baldur's Gate 3\n3 (25.00%)",
                "The Witcher 3\n2 (16.67%)",
                "It Takes Two\n2 (16.67%)"
        );
        List<String> expectedFinalTexts = List.of(
                "Hollow Knight\n3 (18.75%)",
                "Hades\n3 (18.75%)",
                "Baldur's Gate 3\n4 (25.00%)",
                "The Witcher 3\n3 (18.75%)",
                "It Takes Two\n3 (18.75%)"
        );

        seleniumHelper.get("/polls/4");
        PollPage page = new PollPage(webDriver);

        for (int i = 0; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);
            assertFalse(optionInput.isSelected());
            String labelText = webDriver.findElement(By.cssSelector("label[for='" + optionInput.getAttribute("id") + "']")).getText();
            assertEquals(expectedInitialTexts.get(i), labelText);
        }
        assertTrue(seleniumHelper.isInViewport(page.voteButton));

        for (int i = 1; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);

            optionInput.click();
            assertTrue(optionInput.isSelected());
        }
        page.voteButton.click();
        assertDoesNotThrow(() -> wait.until(d -> !seleniumHelper.isInViewport(page.voteButton)));

        for (int i = 0; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);
            String labelText = webDriver.findElement(By.cssSelector("label[for='" + optionInput.getAttribute("id") + "']")).getText();
            assertEquals(expectedFinalTexts.get(i), labelText);
        }
    }

    @TestTemplate
    public void testVoteOnSingleVotePoll() {
        List<String> expectedInitialTexts = List.of(
                "Java\n5 (33.33%)",
                "Python\n2 (13.33%)",
                "JavaScript\n3 (20.00%)",
                "C#\n5 (33.33%)"
        );
        List<String> expectedFinalTexts = List.of(
                "Java\n5 (31.25%)",
                "Python\n2 (12.50%)",
                "JavaScript\n3 (18.75%)",
                "C#\n6 (37.50%)"
        );

        seleniumHelper.get("/polls/1");
        PollPage page = new PollPage(webDriver);

        for (int i = 0; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);
            assertFalse(optionInput.isSelected());
            String labelText = webDriver.findElement(By.cssSelector("label[for='" + optionInput.getAttribute("id") + "']")).getText();
            assertEquals(expectedInitialTexts.get(i), labelText);
        }
        assertTrue(seleniumHelper.isInViewport(page.voteButton));

        for (int i = 0; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);

            optionInput.click();
            assertTrue(optionInput.isSelected());
        }
        for (int i = 0; i < page.optionInputs.size() - 1; i++) {
            WebElement optionInput = page.optionInputs.get(i);
            assertFalse(optionInput.isSelected());
        }

        page.voteButton.click();
        assertDoesNotThrow(() -> wait.until(d -> !seleniumHelper.isInViewport(page.voteButton)));

        for (int i = 0; i < page.optionInputs.size(); i++) {
            WebElement optionInput = page.optionInputs.get(i);
            String labelText = webDriver.findElement(By.cssSelector("label[for='" + optionInput.getAttribute("id") + "']")).getText();
            assertEquals(expectedFinalTexts.get(i), labelText);
        }
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