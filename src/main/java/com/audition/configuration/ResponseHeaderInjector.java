package com.audition.configuration;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;


@Component
@RequiredArgsConstructor
public class ResponseHeaderInjector implements HandlerInterceptor {

    // TODO Inject openTelemetry trace and span Ids in the response headers.

    private final Tracer tracer;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            TraceContext context = currentSpan.context();
            response.setHeader("X-Trace-Id", context.traceId());
            response.setHeader("X-Span-Id", context.spanId());
        }
        return true;
    }
}
