package ru.zuzex.practice.accountms.config;

import feign.RequestInterceptor;
import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor feignTracingInterceptor(Tracer tracer) {
        return requestTemplate -> {
            if (tracer.currentSpan() != null) {
                requestTemplate.header("X-B3-TraceId", tracer.currentSpan().context().traceId());
                requestTemplate.header("X-B3-SpanId", tracer.currentSpan().context().spanId());
            }
        };
    }
}
