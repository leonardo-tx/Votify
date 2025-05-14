package br.com.votify.test.extensions;

import br.com.votify.web.BaseTest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class ScreenshotExtension implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Object testInstance = context.getRequiredTestInstance();
        if (!(testInstance instanceof BaseTest seleniumTest)) {
            throw throwable;
        }
        seleniumTest.captureScreenshot();
        throw throwable;
    }
}
