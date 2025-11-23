package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.CategoriaRequest;
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
public class CategoriaControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenAdmin;
    private Long categoriaId;

    // ===========================================================
    // 1) LOGIN ADMIN → Obtener token real de tu sistema
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

        System.out.println("TOKEN ADMIN: " + tokenAdmin);
    }
    // ===========================================================
    // 3) LISTAR TODAS (GET)
    // ===========================================================
    @Test
    @Order(1)
    void testListarCategorias() {

        client.get()
                .uri("http://localhost:" + port + "/api/categorias")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }
    // ===========================================================
    // 2) CREAR CATEGORÍA (POST)
    // ===========================================================
    @Test
    @Order(2)
    void testCrearCategoria() {

        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Categoria Test")
                .descripcion("Descripcion test")
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/categorias")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> categoriaId = Long.valueOf(id.toString()))
                .jsonPath("$.nombre").isEqualTo("Categoria Test");

        System.out.println("ID CATEGORIA CREADA: " + categoriaId);
    }



    // ===========================================================
    // 4) OBTENER POR ID (GET)
    // ===========================================================
    @Test
    @Order(3)
    void testObtenerCategoriaPorId() {

        client.get()
                .uri("http://localhost:" + port + "/api/categorias/" + categoriaId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(categoriaId.intValue());
    }

    // ===========================================================
    // 5) ACTUALIZAR (PUT)
    // ===========================================================
    @Test
    @Order(4)
    void testActualizarCategoria() {

        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Categoria Modificada")
                .descripcion("Descripcion Modificada")
                .build();

        client.put()
                .uri("http://localhost:" + port + "/api/categorias/" + categoriaId)
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Categoria Modificada");
    }

    // ===========================================================
    // 6) ELIMINAR (DELETE)
    // ===========================================================
    @Test
    @Order(5)
    void testEliminarCategoria() {

        client.delete()
                .uri("http://localhost:" + port + "/api/categorias/" + categoriaId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isNoContent();
    }
}
