package org.greenflow.client.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-Roles");
        String email = request.getHeader("X-Email");

        if (userId != null && role != null && email != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities
            );
            putDetails(auth, email);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Authentication Success: {}", auth.getName());
            var details = (Map<String, String>) auth.getDetails();
            log.debug("Email: {}", details.get("email"));
        }
        chain.doFilter(request, response);
    }

    private static void putDetails(UsernamePasswordAuthenticationToken auth, String email) {
        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        auth.setDetails(details);
    }
}