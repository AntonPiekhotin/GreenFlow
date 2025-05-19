package org.greenflow.leasing.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.CustomHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String userId = request.getHeader(CustomHeaders.X_USER_ID);
        String roles = request.getHeader(CustomHeaders.X_ROLES);
        String email = request.getHeader(CustomHeaders.X_EMAIL);

        if (userId != null && roles != null && email != null) {
            Set<GrantedAuthority> authorities = Set.of(
                    roles.split(",")).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

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