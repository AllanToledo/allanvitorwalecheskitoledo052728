package br.dev.allantoledo.psc;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

    @Value("${application.timezone:UTC}")
    private String applicationTimeZone;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void executeAfterMain() {
        TimeZone.setDefault(TimeZone.getTimeZone(applicationTimeZone));
    }
}
