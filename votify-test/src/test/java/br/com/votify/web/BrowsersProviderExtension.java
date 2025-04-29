package br.com.votify.web;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import java.util.List;
import java.util.stream.Stream;

public class BrowsersProviderExtension implements TestTemplateInvocationContextProvider {
    private final boolean isCI;

    public BrowsersProviderExtension() {
        String ci = System.getenv("CI");
        isCI = ci != null && ci.equals("true");
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
        if (isCI) {
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless");
        }
        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        if (isCI) {
            options.addArguments("--headless");
        }
        return new FirefoxDriver(options);
    }
}