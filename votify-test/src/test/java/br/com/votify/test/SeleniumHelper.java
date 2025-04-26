package br.com.votify.test;

import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.web.login.LoginPage;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static br.com.votify.test.suites.SeleniumTest.BASE_URL;

public final class SeleniumHelper {
    public static Boolean isInViewport(WebElement element, WebDriver webDriver) {
        String script = "var elem = arguments[0], box = elem.getBoundingClientRect(), cx = box.left + box.width / 2, cy = box.top + box.height / 2, e = document.elementFromPoint(cx, cy); for (; e; e = e.parentElement) { if (e === elem) return true; } return false;";
        return (Boolean)((JavascriptExecutor)webDriver).executeScript(script, element);
    }

    public static void goToPath(WebDriver webDriver, WebDriverWait wait, String path) {
        String url = BASE_URL + path;
        webDriver.get(url);

        wait.until(ExpectedConditions.urlContains(path));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
    }

    public static List<Cookie> getLoginCookies(UserLoginDTO userLoginDTO) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless");

        WebDriver webDriver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        goToPath(webDriver, wait, "/login");
        LoginPage page = new LoginPage(webDriver);

        new Actions(webDriver)
                .sendKeys(page.emailInput, userLoginDTO.getEmail())
                .sendKeys(Keys.TAB)
                .sendKeys(userLoginDTO.getPassword())
                .sendKeys(Keys.TAB)
                .sendKeys(Keys.TAB)
                .sendKeys(Keys.ENTER)
                .perform();
        wait.until(ExpectedConditions.urlContains("/home"));

        List<Cookie> cookies = new ArrayList<>();
        for (Cookie cookie : webDriver.manage().getCookies()) {
            Cookie sanitizedCookie = new Cookie.Builder(cookie.getName(), cookie.getValue())
                    .path(cookie.getPath())
                    .expiresOn(cookie.getExpiry())
                    .isSecure(cookie.isSecure())
                    .isHttpOnly(cookie.isHttpOnly())
                    .build();
            cookies.add(sanitizedCookie);
        }
        webDriver.quit();
        return cookies;
    }
}
