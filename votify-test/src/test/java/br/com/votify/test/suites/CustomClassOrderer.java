package br.com.votify.test.suites;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;

import java.util.Comparator;

public class CustomClassOrderer implements ClassOrderer {
    @Override
    public void orderClasses(ClassOrdererContext context) {
        context.getClassDescriptors().sort(Comparator.comparingInt(this::getOrder));
    }

    private int getOrder(ClassDescriptor descriptor) {
        Class<?> testClass = descriptor.getTestClass();

        if (ControllerTest.class.isAssignableFrom(testClass)) {
            return 1;
        }
        if (SeleniumTest.class.isAssignableFrom(testClass)) {
            return 2;
        }
        return 0;
    }
}
