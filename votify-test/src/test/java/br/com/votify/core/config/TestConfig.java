package br.com.votify.core.config;

import br.com.votify.core.service.TestEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public TestEmailService testEmailService() {
        return new TestEmailService();
    }
} 