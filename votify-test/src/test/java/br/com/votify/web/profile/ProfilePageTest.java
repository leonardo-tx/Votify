package br.com.votify.web.profile;

import br.com.votify.dto.user.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfilePageTest extends SeleniumTest {
    private static List<Cookie> cookies = null;

    @BeforeEach
    void setupBeforeEach() {
        seleniumHelper.get("/home");
        if (cookies == null) {
            cookies = seleniumHelper.loginAndGetCookies(new UserLoginDTO("admin@votify.com.br", "admin123"));
        }
    }

    @TestTemplate
    void shouldDisplayUserProfileInformation() {
        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertTrue(seleniumHelper.isInViewport(profilePage.userNameText));
        assertTrue(seleniumHelper.isInViewport(profilePage.userUsernameText));
        assertEquals(
                "Administrator",
                profilePage.userNameText.getText(),
                "The user name does not match the expected value."
        );
        assertEquals(
                "@admin",
                profilePage.userUsernameText.getText(),
                "The username does not match the expected value."
        );
        assertTrue(
                seleniumHelper.isInViewport(profilePage.createdPollsSectionTitle),
                "The 'Created Polls' section should be visible."
        );
    }

    @TestTemplate
    void shouldDisplayErrorWhenProfileNotFound() {
        seleniumHelper.get("/profile/usuariodesconhecido99");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertTrue(
                seleniumHelper.isInViewport(profilePage.profilePageErrorMessage),
                "The error message for 'Profile not found' should be visible."
        );
        assertEquals(
                "Perfil não encontrado.",
                profilePage.profilePageErrorMessage.getText(),
                "The error message does not match the expected text."
        );
    }

    @TestTemplate
    void shouldDisplayMessageWhenUserHasNoPolls() {
        seleniumHelper.get("/profile/no-polls");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertTrue(
                seleniumHelper.isInViewport(profilePage.noPollsMessageText),
                "The message for a user with no polls should be visible."
        );
        assertEquals(
                "Esse usuário não criou nenhuma enquete.",
                profilePage.noPollsMessageText.getText(),
                "The text of the message for a user with no polls does not match the expected one."
        );
    }

    @TestTemplate
    void shouldSuccessfullyEditProfileInformation() {
        cookies.forEach(c -> webDriver.manage().addCookie(c));

        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        profilePage.editProfileButton.click();
        
        wait.until(ExpectedConditions.urlContains("/settings"));
        assertTrue(
                webDriver.getCurrentUrl().contains("/settings"),
                "Should have redirected to the edit page."
        );

        profilePage.nameInput.sendKeys(Keys.CONTROL + "A");
        profilePage.nameInput.sendKeys(Keys.DELETE);
        profilePage.nameInput.sendKeys("Novo Nome");
        profilePage.saveProfileButton.click();

        wait.until(d -> d.findElement(By.id("profile-form-success-message")));
        profilePage = new ProfilePage(webDriver);
        assertTrue(
                seleniumHelper.isInViewport(profilePage.successMessage),
                "The success message should be visible."
        );
        assertEquals(
                "Perfil atualizado com sucesso!",
                profilePage.successMessage.getText(),
                "The success message does not match the expected one."
        );
        
        seleniumHelper.get("/profile/admin");
        assertEquals(
                "Novo Nome",
                profilePage.userNameText.getText(),
                "The user's name was not updated correctly."
        );
    }

    @TestTemplate
    void shouldNotShowEditButtonForNonOwnerProfile() {
        seleniumHelper.get("/profile/no-polls");

        assertThrows(
                NoSuchElementException.class,
                () -> webDriver.findElement(By.id("edit-profile-button")),
                "The edit button should not be visible for other users' profiles."
        );
    }

    @TestTemplate
    void shouldSuccessfullyDeleteUserAccount() {
        cookies.forEach(c -> webDriver.manage().addCookie(c));

        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        profilePage.deleteAccountButton.click();

        WebElement confirmDeleteButton = wait.until(d -> d.findElement(By.id("confirm-delete-button")));
        confirmDeleteButton.click();

        wait.until(ExpectedConditions.urlContains("/home"));
        assertTrue(
                webDriver.getCurrentUrl().contains("/home"),
                "Should have redirected to the home page after deleting the account."
        );
    }

    @TestTemplate
    void shouldNotShowDeleteButtonForNonOwnerProfile() {
        cookies.forEach(c -> webDriver.manage().addCookie(c));

        seleniumHelper.get("/profile/no-polls");
        assertThrows(
                NoSuchElementException.class,
                () -> webDriver.findElement(By.id("delete-account-button")),
                "The delete button should not be visible on another user's profile."
        );
    }
}
