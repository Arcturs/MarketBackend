package ru.vsu.csf.asashina.marketservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketservice.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketservice.model.dto.TokensDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.RefreshToken;
import ru.vsu.csf.asashina.marketservice.model.request.RefreshTokenRequest;
import ru.vsu.csf.asashina.marketservice.repository.RefreshTokenRepository;
import ru.vsu.csf.asashina.marketservice.util.UuidUtil;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    public final static String TOKEN_PREFIX = "Bearer ";

    private final static String EMAIL_CLAIM = "email";
    private final static String EXPIRATION_CLAIM = "exp";

    private final UserDetailsService userDetailsService;
    private final UserService userService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UuidUtil uuidUtil;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-token.expire-date-ms}")
    private Integer accessTokenExpireTimeInMs;

    @Value("${security.jwt.refresh-token.expire-date-days}")
    private Integer refreshTokenExpireTimeInDays;

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
        var algorithm = getAlgorithm();
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey.getBytes());
    }

    private void checkIfTokenIsExpired(Date tokenExpireDate) {
        if (tokenExpireDate.before(new Date())) {
            throw new TokenExpiredException("Token is already expired", Instant.now());
        }
    }

    private void checkIfTokenIsExpired(Instant tokenExpireDate) {
        if (tokenExpireDate.isBefore(Instant.now())) {
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

    @Transactional
    public TokensDTO createTokens(UserDTO user) {
        return new TokensDTO(generateAccessToken(user), generateRefreshToken(user));
    }

    private String generateAccessToken(UserDTO user) {
        return JWT.create()
                .withSubject(user.getUserId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpireTimeInMs))
                .withIssuer("market-server")
                .withClaim("roles", user.getRoles().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                )
                .withClaim("email", user.getEmail())
                .withClaim("fullName", user.getName().concat(" ").concat(user.getSurname()))
                .sign(getAlgorithm());
    }

    private String generateRefreshToken(UserDTO user) {
        String refreshToken = uuidUtil.generateRandomUUIDInString();
        refreshTokenRepository.saveNewRefreshToken(
                refreshToken,
                Instant.now().plusSeconds(fromDaysToSeconds(refreshTokenExpireTimeInDays)),
                user.getUserId());
        return refreshToken;
    }

    private int fromDaysToSeconds(int days) {
        return days * 24 * 60 * 60;
    }

    public TokensDTO refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = findRefreshTokenById(request.getRefreshToken());
        checkIfTokenIsExpired(refreshToken.getExpireDate());
        return new TokensDTO(
                generateAccessToken(userService.getUserByEmail(refreshToken.getUser().getEmail())),
                refreshToken.getRefreshToken()
        );
    }

    private RefreshToken findRefreshTokenById(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken).orElseThrow(
                () -> new ObjectNotExistException("Following refresh token does not exist")
        );
    }
}
