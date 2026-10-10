package com.grassi.partiturae.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthService authService;
    private final AuthProperties properties;

    public AuthController(AuthService authService, AuthProperties properties) {
        this.authService = authService;
        this.properties = properties;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return conCookie(authService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        return conCookie(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(authService.me(Long.valueOf(jwt.getSubject())));
    }

    private ResponseEntity<AuthResponse> conCookie(AuthService.AuthResult result) {
        ResponseCookie cookie = cookie(result.refreshToken(), Duration.ofDays(properties.refreshTokenDays()));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.risposta());
    }

    // Il refresh token sta in un cookie HttpOnly (il JavaScript della pagina non lo può leggere),
    // inviato solo alle rotte /api/auth e non nelle richieste che arrivano da altri siti (SameSite).
    private ResponseCookie cookie(String valore, Duration durata) {
        return ResponseCookie.from(REFRESH_COOKIE, valore)
                .httpOnly(true)
                .secure(properties.cookieSecure())
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(durata)
                .build();
    }
}