package br.com.votify.test.extensions;

import br.com.votify.test.suites.SeleniumTest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class ScreenshotExtension implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Object testInstance = context.getRequiredTestInstance();
        if (!(testInstance instanceof SeleniumTest seleniumTest)) {
            throw throwable;
        }
        seleniumTest.captureScreenshot();
        throw throwable;
    }
}
