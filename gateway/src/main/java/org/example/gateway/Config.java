package org.example.gateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.BaggageInScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public BaggageInScope clientBaggage(Tracer tracer) {
        return tracer.createBaggage("client-id");
    }

    @Bean
    public GlobalFilter customGlobalFilter(Tracer tracer) {
        return new CustomGlobalFilter(clientBaggage(tracer));
    }
}
