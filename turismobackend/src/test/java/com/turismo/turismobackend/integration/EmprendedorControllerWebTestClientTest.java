package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.EmprendedorRequest;
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
public class EmprendedorControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenAdmin;
    private Long emprendedorId;

    // ============================================
    // 1) LOGIN ADMIN
    // ============================================
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


    // ============================================
    // 2) CREAR EMPRENDEDOR (ADMIN)
    // ============================================
    @Test
    @Order(1)
    void testCrearEmprendedor() {

        EmprendedorRequest request = EmprendedorRequest.builder()
                .nombreEmpresa("Empresa Test")
                .rubro("Gastronomía")
                .direccion("Direccion test")
                .telefono("987654321")
                .email("empresa@test.com")
                .sitioWeb("https://empresa.com")
                .descripcion("Descripción test")
                .productos("Producto X")
                .servicios("Servicio Y")
                .municipalidadId(1L)
                .categoriaId(1L)
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/emprendedores")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> emprendedorId = Long.valueOf(id.toString()))
                .jsonPath("$.nombreEmpresa").isEqualTo("Empresa Test");

        System.out.println("ID EMPRENDEDOR CREADO: " + emprendedorId);
    }


    // ============================================
    // 3) LISTAR TODOS
    // ============================================
    @Test
    @Order(2)
    void testListarEmprendedores() {

        client.get()
                .uri("http://localhost:" + port + "/api/emprendedores")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }


    // ============================================
    // 4) OBTENER POR ID
    // ============================================
    @Test
    @Order(3)
    void testObtenerPorId() {

        client.get()
                .uri("http://localhost:" + port + "/api/emprendedores/" + emprendedorId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(emprendedorId.intValue());
    }


    // ============================================
    // 5) BUSCAR POR MUNICIPALIDAD
    // ============================================
    @Test
    @Order(4)
    void testBuscarPorMunicipalidad() {

        client.get()
                .uri("http://localhost:" + port + "/api/emprendedores/municipalidad/1")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }


    // ============================================
    // 6) BUSCAR POR RUBRO
    // ============================================
    @Test
    @Order(5)
    void testBuscarPorRubro() {

        client.get()
                .uri("http://localhost:" + port + "/api/emprendedores/rubro/Gastronomía")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }


    // ============================================
    // 7) BUSCAR POR CATEGORÍA
    // ============================================
    @Test
    @Order(6)
    void testBuscarPorCategoria() {

        client.get()
                .uri("http://localhost:" + port + "/api/emprendedores/categoria/1")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }


    // ============================================
    // 8) ACTUALIZAR EMPRENDEDOR
    // ============================================
    @Test
    @Order(7)
    void testActualizarEmprendedor() {

        EmprendedorRequest request = EmprendedorRequest.builder()
                .nombreEmpresa("Empresa Modificada")
                .rubro("Gastronomía")
                .direccion("Nueva Dirección")
                .telefono("999999999")
                .email("nuevo@test.com")
                .sitioWeb("https://modificado.com")
                .descripcion("Descripción modificada")
                .productos("Producto nuevo")
                .servicios("Servicio nuevo")
                .municipalidadId(1L)
                .categoriaId(1L)
                .build();

        client.put()
                .uri("http://localhost:" + port + "/api/emprendedores/" + emprendedorId)
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombreEmpresa").isEqualTo("Empresa Modificada");
    }


    // ============================================
    // 9) BUSCAR CERCANOS
    // ============================================
    @Test
    @Order(8)
    void testBuscarCercanos() {

        client.get()
                .uri("http://localhost:" + port + "/api/emprendedores/cercanos?latitud=-12.0&longitud=-77.0&radio=10")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }


    // ============================================
    // 10) ELIMINAR
    // ============================================
    @Test
    @Order(9)
    void testEliminarEmprendedor() {

        client.delete()
                .uri("http://localhost:" + port + "/api/emprendedores/" + emprendedorId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isNoContent();
    }
}
