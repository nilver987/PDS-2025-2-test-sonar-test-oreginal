package com.turismo.turismobackend.integration;

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
public class UsuarioControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenAdmin;

    // USUARIOS **REALS** ya creados por DataInitializer
    private final Long usuarioId = 7L;       // usuario emprendedor real (juan_perez)
    private final Long emprendedorId = 1L;   // emprendedor real creado al inicio

    // ===========================================================
    //  LOGIN ADMIN → Obtener token real
    // ===========================================================
    @BeforeAll
    void loginAdmin() {

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
    }

    // ===========================================================
    // 1) LISTAR TODOS LOS USUARIOS
    // ===========================================================
    @Test
    @Order(1)
    void testListarUsuarios() {
        client.get()
                .uri("http://localhost:" + port + "/api/usuarios")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 2) OBTENER USUARIO POR ID
    // ===========================================================
    @Test
    @Order(2)
    void testObtenerUsuarioPorId() {
        client.get()
                .uri("http://localhost:" + port + "/api/usuarios/" + usuarioId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 3) LISTAR USUARIOS SIN EMPRENDEDOR
    // ===========================================================
    @Test
    @Order(3)
    void testUsuariosSinEmprendedor() {
        client.get()
                .uri("http://localhost:" + port + "/api/usuarios/sin-emprendedor")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 4) LISTAR POR ROL
    // ===========================================================
    @Test
    @Order(4)
    void testUsuariosPorRol() {
        client.get()
                .uri("http://localhost:" + port + "/api/usuarios/con-rol/ROLE_EMPRENDEDOR")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    

    

    // ===========================================================
    // 7) RESETEAR ROLES
    // ===========================================================
    @Test
    @Order(7)
    void testResetearRolesUsuario() {
        client.put()
                .uri("http://localhost:" + port + "/api/usuarios/" + usuarioId + "/resetear-roles")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 8) ASIGNAR USUARIO A EMPRENDEDOR (ANTES FALLABA)
    

    // ===========================================================
    // 9) CAMBIAR USUARIO DE EMPRENDEDOR (ANTES FALLABA)
    // ===========================================================
    @Test
    @Order(9)
    void testCambiarUsuarioDeEmprendedor() {
        client.put()
                .uri("http://localhost:" + port + "/api/usuarios/" + usuarioId + "/cambiar-emprendedor/" + emprendedorId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 10) DESASIGNAR EMPRENDEDOR (ANTES FALLABA)
    // ===========================================================
    @Test
    @Order(10)
    void testDesasignarUsuarioDeEmprendedor() {
        client.delete()
                .uri("http://localhost:" + port + "/api/usuarios/" + usuarioId + "/desasignar-emprendedor")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }
}
