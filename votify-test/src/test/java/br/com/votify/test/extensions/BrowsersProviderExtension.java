package br.com.votify.test.extensions;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

public class BrowsersProviderExtension implements TestTemplateInvocationContextProvider {
    private final boolean isDocker;

    public BrowsersProviderExtension() {
        isDocker = new File("/.dockerenv").exists();
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Stream.of(chromeContext(), firefoxContext());
    }

    private TestTemplateInvocationContext chromeContext() {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return "Chrome";
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(new WebDriverParameterResolver(createChromeDriver()));
            }
        };
    }

    private TestTemplateInvocationContext firefoxContext() {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return "Firefox";
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(new WebDriverParameterResolver(createFirefoxDriver()));
            }
        };
    }

    static class WebDriverParameterResolver implements ParameterResolver {
        private final WebDriver driver;

        WebDriverParameterResolver(WebDriver driver) {
            this.driver = driver;
        }

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return parameterContext.getParameter().getType().equals(WebDriver.class);
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return driver;
        }
    }

    private WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        if (!isDocker) {
            return new ChromeDriver(options);
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless");

        try {
            return new RemoteWebDriver(new URL("http://selenium-hub:4444/wd/hub"), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL do Selenium Grid inválida", e);
        }
    }

    private WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        if (!isDocker) {
            return new FirefoxDriver(options);
        }
        options.addArguments("--headless");

        try {
            return new RemoteWebDriver(new URL("http://selenium-hub:4444/wd/hub"), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL do Selenium Grid inválida", e);
        }
    }
}