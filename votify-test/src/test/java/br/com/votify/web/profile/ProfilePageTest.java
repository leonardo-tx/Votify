package br.com.votify.web.profile;

import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ProfilePageTest extends SeleniumTest {

    private WebDriverWait wait;

    @BeforeEach
    public void setupBeforeEach() {
        seleniumHelper.get("/profile/admin");
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    private void fazerLogin() {
        seleniumHelper.get("/login");
        webDriver.findElement(By.id("login-email")).sendKeys("admin@votify.com.br");
        webDriver.findElement(By.id("login-password")).sendKeys("admin123");
        webDriver.findElement(By.id("login-submit-button")).click();
        
        // Espera o redirecionamento para a página inicial
        wait.until(ExpectedConditions.urlContains("/home"));
    }

    @TestTemplate
    public void shouldDisplayUserProfileInformation() {
        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertEquals("Administrator", profilePage.getUserName(), "O nome do usuário não corresponde ao esperado.");
        assertEquals("@admin", profilePage.getUserUsername(), "O username não corresponde ao esperado.");
        assertTrue(profilePage.isCreatedPollsSectionVisible(), "A seção de enquetes criadas deveria estar visível.");
    }

    @TestTemplate
    public void shouldDisplayErrorWhenProfileNotFound() {
        seleniumHelper.get("/profile/usuariodesconhecido99");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertTrue(profilePage.isErrorLoadingProfileTitleVisible(), "O título de erro para perfil não encontrado deveria estar visível.");
        assertEquals("Perfil não encontrado.", profilePage.getProfilePageErrorMessageText(), "A mensagem de erro não corresponde à esperada para perfil não encontrado.");
    }

    @TestTemplate
    public void shouldDisplayMessageWhenUserHasNoPolls() {
        seleniumHelper.get("/profile/noPolls"); 
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertTrue(profilePage.isNoPollsMessageVisible(), "A mensagem para usuário sem enquetes deveria estar visível.");
        assertEquals("Você ainda não criou nenhuma enquete.", profilePage.getNoPollsMessageText(), "O texto da mensagem para usuário sem enquetes não corresponde ao esperado.");
    }

    @TestTemplate
    public void shouldSuccessfullyEditProfileInformation() {
        fazerLogin();
        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        profilePage.clickEditProfileButton();
        
        wait.until(ExpectedConditions.urlContains("/settings/profile/edit"));
        assertTrue(webDriver.getCurrentUrl().contains("/settings/profile/edit"), "Deveria ter redirecionado para a página de edição.");
        
        profilePage.setNewName("Novo Nome");
        profilePage.clickSaveProfileButton();

        assertTrue(profilePage.isSuccessMessageVisible(), "A mensagem de sucesso deveria estar visível.");
        assertEquals("Perfil atualizado com sucesso!", profilePage.getSuccessMessageText(), "A mensagem de sucesso não corresponde à esperada.");
        
        seleniumHelper.get("/profile/admin");
        assertEquals("Novo Nome", profilePage.getUserName(), "O nome do usuário não foi atualizado corretamente.");
    }

    @TestTemplate
    public void shouldNotShowEditButtonForNonOwnerProfile() {
        seleniumHelper.get("/profile/noPolls");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertFalse(profilePage.isEditProfileButtonVisible(), "O botão de edição não deveria estar visível para perfis de outros usuários.");
    }

    @TestTemplate
    public void shouldSuccessfullyDeleteUserAccount() {
        fazerLogin();
        seleniumHelper.get("/profile/admin");
        ProfilePage profilePage = new ProfilePage(webDriver);

        profilePage.clickDeleteAccountButton();
        wait.until(ExpectedConditions.alertIsPresent());
        webDriver.switchTo().alert().accept();

        wait.until(ExpectedConditions.alertIsPresent());
        webDriver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlContains("/home"));
        assertTrue(webDriver.getCurrentUrl().contains("/home"), "Deveria ter redirecionado para a página de home após deletar a conta.");
    }

    @TestTemplate
    public void shouldNotShowDeleteButtonForNonOwnerProfile() {
        fazerLogin(); // Loga como admin
        seleniumHelper.get("/profile/noPolls");
        ProfilePage profilePage = new ProfilePage(webDriver);

        assertFalse(profilePage.isDeleteAccountButtonVisible(), "O botão de deletar conta não deveria estar visível no perfil de outro usuário.");
    }
}
