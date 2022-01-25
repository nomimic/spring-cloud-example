package org.example.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.ContinueSpan;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
public class HelloService {

    private final static Logger log = LoggerFactory.getLogger(HelloService.class);

    @Value("${hello-service.name}")
    private String name;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private int serverPort;

    public String hello() {
        log.info("name is {}", this.name);
        return "Hello, My name is " + this.name;
    }

    public String helloTag(String param) {
        tracer.currentSpan().tag("user.name", param);

        return "Hello, My name is " + param;
    }

    @ContinueSpan
    public String helloTagAnnotation(@SpanTag(key = "user.name") String param) {
        return "Hello, My name is " + param;
    }

    @NewSpan
    public String helloTagAnnotationNew(@SpanTag(key = "user.name") String param) {
        return "Hello, My name is " + param;
    }

    public String helloAsync() {
        log.info("name is {}", this.name);
        threadPoolTaskExecutor.execute(() -> {
            log.info("async...");
        });

        return "Hello, My name is " + this.name;
    }

    public String helloProxy() {
        tracer.currentSpan().tag("UUID-PROXY", UUID.randomUUID().toString());

        URI uri = UriComponentsBuilder.fromHttpUrl("http://127.0.0.1:" + serverPort)
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri, String.class);
    }
}
