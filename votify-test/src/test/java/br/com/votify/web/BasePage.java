package br.com.votify.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BasePage {
    protected BasePage(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
    }

    @FindBy(id = "logo-home-anchor")
    public WebElement logoHomeAnchor;

    @FindBy(id = "nav-about-anchor")
    public WebElement navAboutAnchor;

    @FindBy(id = "nav-search-poll")
    public WebElement navSearchPoll;

    @FindBy(id = "poll-search-submit")
    public WebElement buttonPollSearchSubmit;

    @FindBy(id = "signin-link")
    public WebElement signinLink;

    @FindBy(id = "signup-link")
    public WebElement signupLink;

    @FindBy(id = "git-repository-anchor")
    public WebElement gitRepositoryAnchor;
}
