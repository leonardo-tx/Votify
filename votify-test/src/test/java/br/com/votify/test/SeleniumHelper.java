package br.com.votify.test;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import br.com.votify.dto.users.UserLoginDTO;
import org.openqa.selenium.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public final class SeleniumHelper {
    private final String baseUrl;
    private final WebDriver webDriver;
    private final WebDriverWait wait;
    private final MockMvcHelper mockMvcHelper;

    public Boolean isInViewport(WebElement element) {
        String script = "var elem = arguments[0], box = elem.getBoundingClientRect(), cx = box.left + box.width / 2, cy = box.top + box.height / 2, e = document.elementFromPoint(cx, cy); for (; e; e = e.parentElement) { if (e === elem) return true; } return false;";
        return (Boolean)((JavascriptExecutor)webDriver).executeScript(script, element);
    }

    public void get(String path) {
        String url = baseUrl + path;
        webDriver.get(url);

        wait.until(ExpectedConditions.urlContains(path));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
    }

    public List<Cookie> getLoginCookies(UserLoginDTO userLoginDTO) throws Exception {
        jakarta.servlet.http.Cookie[] cookies = mockMvcHelper.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
        List<Cookie> cookiesToReturn = new ArrayList<>();

        for (jakarta.servlet.http.Cookie cookie : cookies) {
            Cookie newCookie = new Cookie.Builder(cookie.getName(), cookie.getValue())
                    .path(cookie.getPath())
                    .expiresOn(Date.from(Instant.now().plusSeconds(cookie.getMaxAge())))
                    .isSecure(cookie.getSecure())
                    .isHttpOnly(cookie.isHttpOnly())
                    .build();
            cookiesToReturn.add(newCookie);
        }
        return cookiesToReturn;
    }
}
