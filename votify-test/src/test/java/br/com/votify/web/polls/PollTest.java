package br.com.votify.web.polls;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class PollTest extends BaseTest {
    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.goToPath("/polls/1");
    }

    @TestTemplate
    public void shouldShowNotFoundForInvalidId() {
        seleniumHelper.goToPath("/polls/9999");
        PollPage page = new PollPage(webDriver);

        assertTrue(seleniumHelper.isInViewport(page.noPollMessage));
        assertEquals("Enquete não encontrada.", page.noPollMessage.getText());
    }
}