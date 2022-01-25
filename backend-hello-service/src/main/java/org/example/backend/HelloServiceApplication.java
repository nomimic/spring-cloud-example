package org.example.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloServiceApplication {

    private final static Logger log = LoggerFactory.getLogger(HelloServiceApplication.class);

    @Autowired
    private HelloService helloService;

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }

    @GetMapping(value = {""})
    public String hello() {
        return this.helloService.hello();
    }

    @GetMapping(value = {"/tag-manual"})
    public String helloTag(@RequestParam(required = false, name = "userName", defaultValue = "java") String param) {
        return this.helloService.helloTag(param);
    }

    @GetMapping(value = {"/tag-annotation"})
    public String helloTagAnnotation(@RequestParam(required = false, name = "userName", defaultValue = "java") String param) {
        return this.helloService.helloTagAnnotation(param);
    }

    @GetMapping(value = {"/tag-annotation-new"})
    public String helloTagAnnotationNew(@RequestParam(required = false, name = "userName", defaultValue = "java") String param) {
        return this.helloService.helloTagAnnotationNew(param);
    }

    @GetMapping(value = {"/async"})
    public String helloAsync() {
        return this.helloService.helloAsync();
    }

    @GetMapping(value = {"/proxy"})
    public String helloProxy() {
        return this.helloService.helloProxy();
    }
}
