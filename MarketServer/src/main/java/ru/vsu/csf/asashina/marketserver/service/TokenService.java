package ru.vsu.csf.asashina.marketserver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    public final static String TOKEN_PREFIX = "Bearer ";

    private final static String EMAIL_CLAIM = "email";
    private final static String EXPIRATION_CLAIM = "Expiration";

    private final UserDetailsService userDetailsService;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public void authenticate(String authHeader) {
        DecodedJWT decodedJWT = decodeJWT(authHeader);
        Map<String, Claim> claims = decodedJWT.getClaims();
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get(EMAIL_CLAIM).asString());
        checkIfTokenIsExpired(claims.get(EXPIRATION_CLAIM).asDate());
        setAuthToken(userDetails);
    }

    private DecodedJWT decodeJWT(String authHeader) {
        String token = authHeader;
        if (authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(TOKEN_PREFIX.length());
        }
        var algorithm = Algorithm.HMAC256(secretKey.getBytes());
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    private void checkIfTokenIsExpired(Date tokenExpireDate) {
        if (tokenExpireDate.after(new Date())) {
            throw new TokenExpiredException("Token is already expired", Instant.now());
        }
    }

    private void setAuthToken(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
