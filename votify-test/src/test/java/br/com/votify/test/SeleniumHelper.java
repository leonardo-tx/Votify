package br.com.votify.test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class SeleniumHelper {
    private final String baseUrl;
    private final WebDriver webDriver;
    private final WebDriverWait wait;

    public SeleniumHelper(String baseUrl, WebDriver webDriver, WebDriverWait wait) {
        this.baseUrl = baseUrl;
        this.webDriver = webDriver;
        this.wait = wait;
    }

    public Boolean isInViewport(WebElement element) {
        String script = "var elem = arguments[0], box = elem.getBoundingClientRect(), cx = box.left + box.width / 2, cy = box.top + box.height / 2, e = document.elementFromPoint(cx, cy); for (; e; e = e.parentElement) { if (e === elem) return true; } return false;";
        return (Boolean)((JavascriptExecutor)webDriver).executeScript(script, element);
    }

    public void goToPath(String path) {
        String url = baseUrl + path;
        webDriver.get(url);

        wait.until(ExpectedConditions.urlContains(path));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
    }
}
