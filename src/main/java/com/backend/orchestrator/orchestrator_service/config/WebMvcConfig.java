package com.backend.orchestrator.orchestrator_service.config;

import com.backend.orchestrator.orchestrator_service.interceptors.SessionInterceptor;
import com.backend.orchestrator.orchestrator_service.interceptors.TraceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TraceInterceptor traceInterceptor;
    private final SessionInterceptor sessionInterceptor;

    public WebMvcConfig(TraceInterceptor traceInterceptor,
                        SessionInterceptor sessionInterceptor) {
        this.traceInterceptor = traceInterceptor;
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceInterceptor);

        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns(
                        "/accounts/**",
                        "/transfers/**",
                        "/movements/**",
                        "/notifications/**"
                );
    }
}
