package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.AuthController;
import com.turismo.turismobackend.dto.request.LoginRequest;
import com.turismo.turismobackend.dto.request.RegisterRequest;
import com.turismo.turismobackend.dto.response.AuthResponse;
import com.turismo.turismobackend.service.AuthService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;

    private static final Logger logger =
            Logger.getLogger(AuthControllerTest.class.getName());

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("nilver", "123");

        registerRequest = new RegisterRequest(
                "nilver",
                "salcca",
                "nilver",
                "test@mail.com",
                "123",
                Set.of("user")
        );

        authResponse = AuthResponse.builder()
                .token("ABC123")
                .id(10L)
                .username("nilver")
                .email("test@mail.com")
                .roles(List.of("ROLE_USER"))
                .build();
    }

    // ---------------------------------------------------------
    // 1) LOGIN OK
    // ---------------------------------------------------------
    @Test
    void testLogin_ReturnsAuthResponse_WithStatusOK() {
        // Given
        BDDMockito.given(authService.login(loginRequest)).willReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response =
                authController.login(loginRequest);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("nilver", response.getBody().getUsername());
        Assertions.assertEquals("ABC123", response.getBody().getToken());

        BDDMockito.then(authService).should().login(loginRequest);
    }

    // ---------------------------------------------------------
    // 2) LOGIN LANZA EXCEPCIÓN
    // ---------------------------------------------------------
    @Test
    void testLogin_Error_ThrowsException() {
        // Given
        BDDMockito.given(authService.login(loginRequest))
                .willThrow(new RuntimeException("Credenciales inválidas"));

        // When - Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest);
        });

        BDDMockito.then(authService).should().login(loginRequest);
    }

    // ---------------------------------------------------------
    // 3) REGISTER OK
    // ---------------------------------------------------------
    @Test
    void testRegister_ReturnsAuthResponse_WithStatusOK() {
        // Given
        BDDMockito.given(authService.register(registerRequest))
                .willReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response =
                authController.register(registerRequest);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("nilver", response.getBody().getUsername());
        Assertions.assertEquals("ROLE_USER", response.getBody().getRoles().get(0));

        BDDMockito.then(authService).should().register(registerRequest);
    }

    // ---------------------------------------------------------
    // 4) REGISTER ERROR
    // ---------------------------------------------------------
    @Test
    void testRegister_Error_ThrowsException() {
        // Given
        BDDMockito.given(authService.register(registerRequest))
                .willThrow(new RuntimeException("Error al registrar"));

        // When - Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            authController.register(registerRequest);
        });

        BDDMockito.then(authService).should().register(registerRequest);
    }

    // ---------------------------------------------------------
    // 5) INIT ROLES OK
    // ---------------------------------------------------------
    @Test
    void testInitRoles_ReturnsOK() {
        // Given
        BDDMockito.willDoNothing().given(authService).initRoles();

        // When
        ResponseEntity<String> response = authController.initRoles();

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Roles inicializados correctamente", response.getBody());

        BDDMockito.then(authService).should().initRoles();

        logger.info("Roles inicializados correctamente");
    }
    @Test
    void testLogin_RetornaRolesCorrectos() {
        AuthResponse respuesta =
                AuthResponse.builder()
                        .token("X1")
                        .id(1L)
                        .username("nilver")
                        .email("test@mail.com")
                        .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                        .build();

        BDDMockito.given(authService.login(loginRequest)).willReturn(respuesta);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        Assertions.assertEquals(2, response.getBody().getRoles().size());
        Assertions.assertTrue(response.getBody().getRoles().contains("ROLE_ADMIN"));

        BDDMockito.then(authService).should().login(loginRequest);
    }
    @Test
    void testRegister_AsignaIdCorrecto() {
        BDDMockito.given(authService.register(registerRequest)).willReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        Assertions.assertEquals(10L, response.getBody().getId());
        BDDMockito.then(authService).should().register(registerRequest);
    }
    @Test
    void testLogin_TokenNoVacio() {
        BDDMockito.given(authService.login(loginRequest)).willReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        Assertions.assertNotNull(response.getBody().getToken());
        Assertions.assertFalse(response.getBody().getToken().isBlank());
    }
    @Test
    void testRegister_RetornaCorreoCorrecto() {
        BDDMockito.given(authService.register(registerRequest)).willReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        Assertions.assertEquals("test@mail.com", response.getBody().getEmail());
    }
    @Test
    void testLogin_DevuelveNull_ReturnsOkConBodyNull() {
        // Given
        BDDMockito.given(authService.login(loginRequest)).willReturn(null);

        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNull(response.getBody());

        BDDMockito.then(authService).should().login(loginRequest);
    }

    @Test
    void testRegister_SinRoles_ThrowsException() {
        RegisterRequest reqSinRoles = new RegisterRequest(
                "X", "Y", "userx", "x@mail.com", "123", null
        );

        BDDMockito.given(authService.register(reqSinRoles))
                .willThrow(new RuntimeException("Error: Rol no encontrado"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            authController.register(reqSinRoles);
        });

    }
    @Test
    void testInitRoles_RetornaResponseEntity() {
        BDDMockito.willDoNothing().given(authService).initRoles();

        ResponseEntity<String> response = authController.initRoles();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Roles inicializados correctamente", response.getBody());
    }







}
