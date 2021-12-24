package org.example.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloServiceApplication {

    private final static Logger log = LoggerFactory.getLogger(HelloServiceApplication.class);

    @Value("${hello-service.name}")
    private String name;

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }

    @GetMapping(value = {""})
    public String hello() {
        log.info("name is {}", this.name);
        return "Hello, My name is " + this.name;
    }
}
