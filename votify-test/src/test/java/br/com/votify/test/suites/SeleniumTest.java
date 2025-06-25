package br.com.votify.test.suites;

import br.com.votify.api.VotifyApiApplication;
import br.com.votify.test.SeleniumHelper;
import br.com.votify.test.extensions.ScreenshotExtension;
import org.apache.commons.io.FileUtils;
import br.com.votify.test.extensions.BrowsersProviderExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

@SpringBootTest(classes = VotifyApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith({ BrowsersProviderExtension.class, ScreenshotExtension.class })
public abstract class SeleniumTest {
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
