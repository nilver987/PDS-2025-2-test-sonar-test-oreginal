package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.LoginRequest;
import com.turismo.turismobackend.dto.request.RegisterRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenAdmin;

    // ===========================================================
    // 1) LOGIN ADMIN — debe funcionar
    // ===========================================================
    @Test
    @Order(1)
    void testLoginAdmin() {

        String loginJson = """
        {
          "username": "admin",
          "password": "admin123"
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token")
                .value(t -> this.tokenAdmin = "Bearer " + t.toString());

        System.out.println("TOKEN ADMIN: " + tokenAdmin);
    }

    // ===========================================================
    // 2) LOGIN — Usuario no existente
    // ===========================================================
    @Test
    @Order(2)
    void testLoginUsuarioNoExiste() {

        String loginJson = """
        {
          "username": "no_existe",
          "password": "123456"
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginJson)
                .exchange()
                .expectStatus().isUnauthorized(); // correcto
    }

    // ===========================================================
    // 3) LOGIN — Contraseña incorrecta
    // ===========================================================
    @Test
    @Order(3)
    void testLoginPasswordIncorrecto() {

        String loginJson = """
        {
          "username": "admin",
          "password": "password_incorrecto"
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginJson)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ===========================================================
    // 4) REGISTER — Crear usuario normal
    // ===========================================================
    @Test
    @Order(4)
    void testRegisterUsuarioNormal() {

        RegisterRequest request = RegisterRequest.builder()
                .nombre("Test")
                .apellido("User")
                .username("user_test_1")
                .email("user_test_1@example.com")
                .password("123456")
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("user_test_1");
    }

    // ===========================================================
    // 5) REGISTER — Usuario duplicado
    // ===========================================================
    @Test
    @Order(5)
    void testRegisterDuplicado() {

        RegisterRequest request = RegisterRequest.builder()
                .nombre("Repetido")
                .apellido("Apellido")
                .username("user_test_1")  // YA EXISTE
                .email("otrocorreo@example.com")
                .password("123456")
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ===========================================================
    // 6) REGISTER — Campos faltantes
    // ===========================================================
    @Test
    @Order(6)
    void testRegisterCamposInvalidos() {

        String json = """
        {
          "username": "",
          "email": "",
          "password": ""
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest(); // validaciones
    }

    // ===========================================================
    // 7) REGISTER — Email inválido
    // ===========================================================
    @Test
    @Order(7)
    void testRegisterEmailInvalido() {

        String json = """
        {
          "nombre": "Juan",
          "apellido": "XD",
          "username": "usuario_email_malo",
          "email": "no-es-email",
          "password": "123456"
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ===========================================================
    // 8) INIT ROLES — Debe ejecutarse sin error
    // ===========================================================
    @Test
    @Order(8)
    void testInitRoles() {

        client.get()
                .uri("http://localhost:" + port + "/api/auth/init")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Roles inicializados correctamente");
    }
}
