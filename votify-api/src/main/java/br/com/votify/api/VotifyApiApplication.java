package br.com.votify.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"br.com.votify"})
@EnableJpaRepositories(basePackages = "br.com.votify.core.repository")
@EntityScan(basePackages = "br.com.votify.core.domain.entities")
@EnableScheduling
public class VotifyApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(VotifyApiApplication.class, args);
    }
}
