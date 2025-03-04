package br.com.votify.test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestScopeTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        RequestContextHolder.resetRequestAttributes();
    }
}