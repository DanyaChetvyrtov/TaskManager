package ru.zuzex.practice.accountms.config;

import feign.RequestInterceptor;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zuzex.practice.accountms.security.jwt.JwtTokenUtils;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final Logger log = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    public RequestInterceptor feignInterceptor(Tracer tracer) {
        return requestTemplate -> {
            if (tracer.currentSpan() != null) {
                requestTemplate.header("X-B3-TraceId", tracer.currentSpan().context().traceId());
                requestTemplate.header("X-B3-SpanId", tracer.currentSpan().context().spanId());
            }

            try {
                String token = jwtTokenUtils.getToken();
                requestTemplate.header("Authorization", "Bearer " + token);
            } catch (IllegalStateException e) {
                log.warn("Failed to add JWT to Feign request: {}", e.getMessage());
            }
        };
    }
}
