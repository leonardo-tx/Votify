package br.com.votify.test;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class SeleniumHelper {
    public static Boolean isInViewport(WebElement element, WebDriver webDriver) {
        String script = "var elem = arguments[0], box = elem.getBoundingClientRect(), cx = box.left + box.width / 2, cy = box.top + box.height / 2, e = document.elementFromPoint(cx, cy); for (; e; e = e.parentElement) { if (e === elem) return true; } return false;";
        return (Boolean)((JavascriptExecutor)webDriver).executeScript(script, element);
    }
}
