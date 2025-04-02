package br.com.votify.web;

import br.com.votify.api.VotifyApiApplication;
import br.com.votify.test.SeleniumHelper;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VotifyApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseTest {
    public static final String BASE_URL = "http://localhost:3000";
    private static final ChromeOptions options = new ChromeOptions();

    protected final String url;
    protected WebDriver webDriver;

    protected BaseTest(String path) {
        url = BASE_URL + path;
    }

    @BeforeAll
    static void setupBeforeAll() {
        String ci = System.getenv("CI");
        if (ci == null || !ci.equals("true")) return;

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");

    }

    @BeforeEach
    void setupBeforeEach() {
        webDriver = new ChromeDriver(options);
        webDriver.manage().window().maximize();
        webDriver.get(url);
    }

    @AfterEach
    void afterEach() {
        webDriver.quit();
        webDriver = null;
    }

    @Test
    @Order(-1)
    public void checkVisibilityOfHeader() {
        WebElement header = webDriver.findElement(By.tagName("header"));
        for (int i = 0; i < 20; i++) {
            new Actions(webDriver).sendKeys(Keys.PAGE_DOWN).perform();
            assertTrue(SeleniumHelper.isInViewport(header, webDriver));
        }
        for (int i = 0; i < 20; i++) {
            new Actions(webDriver).sendKeys(Keys.PAGE_UP).perform();
            assertTrue(SeleniumHelper.isInViewport(header, webDriver));
        }
    }

    @Test
    @Order(-1)
    public void checkHeader() {
        List<WebElement> elements = webDriver.findElements(By.tagName("header"));
        assertEquals(1, elements.size());

        WebElement header = elements.get(0);

        assertEquals(1, header.findElements(By.id("logo-home-anchor")).size());
        assertEquals(1, header.findElements(By.id("signin-link")).size());
        assertEquals(1, header.findElements(By.id("signup-link")).size());

        List<WebElement> navigators = header.findElements(By.tagName("nav"));
        assertEquals(1, navigators.size());

        WebElement nav = navigators.get(0);

        assertEquals(1, nav.findElements(By.id("nav-about-anchor")).size());
        assertEquals(1, nav.findElements(By.id("nav-search-poll")).size());
    }

    @Test
    @Order(-1)
    public void checkFooter() {
        List<WebElement> elements = webDriver.findElements(By.tagName("footer"));
        assertEquals(1, elements.size());

        WebElement footer = elements.get(0);
        assertEquals(1, footer.findElements(By.id("git-repository-anchor")).size());

        WebElement githubRepositoryAnchor = footer.findElement(By.id("git-repository-anchor"));
        assertEquals("https://github.com/leonardo-tx/Votify", githubRepositoryAnchor.getDomAttribute("href"));
    }
}
