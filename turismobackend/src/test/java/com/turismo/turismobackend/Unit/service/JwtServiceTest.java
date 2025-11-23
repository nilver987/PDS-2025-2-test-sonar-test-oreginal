package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.service.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setup() {

        jwtService = new JwtService();

        // Secret key válida BASE64 para HS256
        setField(jwtService, "secretKey",
                "YWJqZGVrYWJkZWtyZndlZmZlZmZlZmZlZmVmZWZlZmVmZWZlZmZlZmZlZWZm");

        setField(jwtService, "jwtExpiration", 1000L * 60 * 60);  // 1 hora
        setField(jwtService, "refreshExpiration", 1000L * 60 * 60 * 24); // 1 día

        user = new User(
                "juan",
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // --------------------------------------------------------------------
    @Test
    @DisplayName("1) Genera token correctamente")
    void testGenerarToken() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // estructura JWT
    }

    // --------------------------------------------------------------------
    @Test
    @DisplayName("2) Extrae correctamente el username")
    void testExtraerUsername() {
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("juan");
    }

    // --------------------------------------------------------------------
    @Test
    @DisplayName("3) Token válido cuando usuario coincide")
    void testTokenValido() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    // --------------------------------------------------------------------
    @Test
    @DisplayName("4) Token inválido si el usuario no coincide")
    void testTokenInvalidoPorUsuario() {
        String token = jwtService.generateToken(user);

        User otro = new User("mario", "pass", List.of());

        assertThat(jwtService.isTokenValid(token, otro)).isFalse();
    }

    @Test
    @DisplayName("5) Token expirado es inválido")
    void testTokenExpirado() {

        // 🔥 Forzar expiración inmediata del token
        setField(jwtService, "jwtExpiration", -1L);

        String token = jwtService.generateToken(user);

        boolean valido = false;
        try {
            valido = jwtService.isTokenValid(token, user);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            valido = false; // Si expira → inválido
        }

        assertThat(valido).isFalse();
    }

    // --------------------------------------------------------------------
// UTILIDAD INTERNA PARA SETEAR CAMPOS PRIVADOS
// --------------------------------------------------------------------
    private void setField(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
