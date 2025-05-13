package br.com.votify.web;

import br.com.votify.api.VotifyApiApplication;
import br.com.votify.test.SeleniumHelper;
import br.com.votify.test.extensions.ScreenshotExtension;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VotifyApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith({ BrowsersProviderExtension.class, ScreenshotExtension.class })
public abstract class BaseTest {
    @Value("${frontend.base.url}")
    public String baseUrl;

    protected SeleniumHelper seleniumHelper;
    protected WebDriver webDriver;
    protected WebDriverWait wait;

    @BeforeEach
    void setupBeforeEach(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        this.seleniumHelper = new SeleniumHelper(baseUrl, webDriver, wait);
    }

    @AfterEach
    void afterEach() {
        webDriver.quit();
        webDriver = null;
    }

    @TestTemplate
    @Order(-1)
    public void checkVisibilityOfHeader() {
        WebElement header = webDriver.findElement(By.tagName("header"));
        for (int i = 0; i < 20; i++) {
            new Actions(webDriver).sendKeys(Keys.PAGE_DOWN).perform();
            assertTrue(seleniumHelper.isInViewport(header));
        }
        for (int i = 0; i < 20; i++) {
            new Actions(webDriver).sendKeys(Keys.PAGE_UP).perform();
            assertTrue(seleniumHelper.isInViewport(header));
        }
    }

    @TestTemplate
    @Order(-1)
    public void checkHeader() {
        List<WebElement> elements = webDriver.findElements(By.tagName("header"));
        assertEquals(1, elements.size());

        WebElement header = elements.get(0);

        assertEquals(1, header.findElements(By.id("logo-home-anchor")).size());
        assertEquals(1, header.findElements(By.id("login-button")).size());
        assertEquals(1, header.findElements(By.id("signup-button")).size());

        List<WebElement> navigators = header.findElements(By.tagName("nav"));
        assertEquals(1, navigators.size());

        WebElement nav = navigators.get(0);

        assertEquals(1, nav.findElements(By.id("nav-about-anchor")).size());
        assertEquals(1, nav.findElements(By.id("nav-search-poll")).size());
    }

    @TestTemplate
    @Order(-1)
    public void checkFooter() {
        List<WebElement> elements = webDriver.findElements(By.tagName("footer"));
        assertEquals(1, elements.size());

        WebElement footer = elements.get(0);
        assertEquals(1, footer.findElements(By.id("git-repository-anchor")).size());

        WebElement githubRepositoryAnchor = footer.findElement(By.id("git-repository-anchor"));
        assertEquals("https://github.com/leonardo-tx/Votify", githubRepositoryAnchor.getDomAttribute("href"));
    }

    public void captureScreenshot() {
        try {
            String projectDir = System.getProperty("user.dir");
            String screenshotsDirPath = projectDir + "/target/screenshots";
            File screenshotsDir = new File(screenshotsDirPath);

            if (!screenshotsDir.exists() && !screenshotsDir.mkdirs()) {
                System.err.println("Failed to create screenshots directory: " + screenshotsDirPath);
                return;
            }

            String fileNameBase = Long.toString(Instant.now().getEpochSecond());
            File screenshotFile = new File(screenshotsDir, fileNameBase + ".png");

            File tempFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(tempFile, screenshotFile);

            System.out.println("Saved screenshot: " + screenshotFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Unable to capture the screenshot:  " + e.getMessage());
        }
    }
}
