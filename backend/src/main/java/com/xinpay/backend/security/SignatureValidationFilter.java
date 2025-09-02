package com.xinpay.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Run this filter first
public class SignatureValidationFilter extends OncePerRequestFilter {

    // ✅ Replace with your actual APK SHA-256
    private static final String EXPECTED_SIGNATURE = "ECA9885A6765214580FD62F0AFF0AA186DFD3CB040591E39DDEF33EC5726F2B0";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        String path = request.getRequestURI();

        // ✅ Exclude admin endpoints
        if (path.startsWith("/api/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String signature = request.getHeader("X-App-Signature");

        if (signature == null || !EXPECTED_SIGNATURE.equalsIgnoreCase(signature)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid app signature");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
