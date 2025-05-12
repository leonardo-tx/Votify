package br.com.votify.web.polls;

import br.com.votify.test.SeleniumHelper;
import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class PollTest extends BaseTest {
    @BeforeEach
    void setupBeforeEach() {
        webDriver.get(BASE_URL + "/polls/1");
    }

    @TestTemplate
    public void shouldShowNotFoundForInvalidId() {
        webDriver.get(BASE_URL + "/polls/9999");
        PollPage page = new PollPage(webDriver);

        assertTrue(SeleniumHelper.isInViewport(page.noPollMessage, webDriver));
        assertEquals("Enquete não encontrada.", page.noPollMessage.getText());
    }
}