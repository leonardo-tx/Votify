package br.com.votify.web.profile;

import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class ProfilePageTest extends SeleniumTest {

    @BeforeEach
    public void setupBeforeEach() {
        seleniumHelper.get("/profile/admin");
    }

    @TestTemplate
    public void shouldDisplayUserProfileInformation() {
        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertEquals("admin", profilePage.getUserName(), "O nome do usuário não corresponde ao esperado.");
        assertEquals("@admin", profilePage.getUserUsername(), "O username não corresponde ao esperado.");
        assertTrue(profilePage.isCreatedPollsSectionVisible(), "A seção de enquetes criadas deveria estar visível.");
    }
} 