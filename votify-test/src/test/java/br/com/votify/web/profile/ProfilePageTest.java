package br.com.votify.web.profile;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProfilePageTest extends SeleniumTest {
    private UserLoginDTO credentials;

    @BeforeEach
    public void setupBeforeEach() {
        seleniumHelper.get("/profile/admin");
        credentials = new UserLoginDTO("admin@votify.com.br", "admin123");
    }

    @TestTemplate
    public void shouldDisplayUserProfileInformation() {
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
    public void shouldDisplayErrorWhenProfileNotFound() {
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
    public void shouldDisplayMessageWhenUserHasNoPolls() {
        seleniumHelper.get("/profile/noPolls"); 
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
    public void shouldSuccessfullyEditProfileInformation() throws Exception {
        List<Cookie> cookies = seleniumHelper.getLoginCookies(credentials);
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
    public void shouldNotShowEditButtonForNonOwnerProfile() {
        seleniumHelper.get("/profile/noPolls");

        assertThrows(
                NoSuchElementException.class,
                () -> webDriver.findElement(By.id("edit-profile-button")),
                "The edit button should not be visible for other users' profiles."
        );
    }

    @TestTemplate
    public void shouldSuccessfullyDeleteUserAccount() throws Exception {
        List<Cookie> cookies = seleniumHelper.getLoginCookies(credentials);
        cookies.forEach(c -> webDriver.manage().addCookie(c));

        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        profilePage.deleteAccountButton.click();;

        WebElement confirmDeleteButton = wait.until(d -> d.findElement(By.id("confirm-delete-button")));
        confirmDeleteButton.click();

        wait.until(ExpectedConditions.urlContains("/home"));
        assertTrue(
                webDriver.getCurrentUrl().contains("/home"),
                "Should have redirected to the home page after deleting the account."
        );
    }

    @TestTemplate
    public void shouldNotShowDeleteButtonForNonOwnerProfile() throws Exception {
        List<Cookie> cookies = seleniumHelper.getLoginCookies(credentials);
        cookies.forEach(c -> webDriver.manage().addCookie(c));

        seleniumHelper.get("/profile/noPolls");
        assertThrows(
                NoSuchElementException.class,
                () -> webDriver.findElement(By.id("delete-account-button")),
                "The delete button should not be visible on another user's profile."
        );
    }
}
