package ru.vsu.csf.asashina.marketserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.vsu.csf.asashina.marketserver.exception.TokenValidationException;
import ru.vsu.csf.asashina.marketserver.service.TokenService;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.startsWithIgnoreCase(authHeader, TokenService.TOKEN_PREFIX)) {
            try {
                tokenService.authenticate(authHeader);
            } catch (Exception e) {
                throw new TokenValidationException("Auth token validation went wrong");
            }
        }
        filterChain.doFilter(request, response);
    }
}
