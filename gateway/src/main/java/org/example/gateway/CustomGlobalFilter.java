package org.example.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.BaggageInScope;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private final BaggageInScope clientBaggage;

    public CustomGlobalFilter(BaggageInScope clientBaggage) {
        this.clientBaggage = clientBaggage;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        List<String> customHeaders = exchange.getRequest().getHeaders().get("X-Client-Id");

        if (customHeaders != null && !customHeaders.isEmpty()) {
            clientBaggage.set(customHeaders.get(0));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
