package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
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
public class MunicipalidadControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenAdmin;
    private Long municipalidadId;

    // ===========================================================
    // 1) LOGIN ADMIN → Obtener token real
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
    // 2) LISTAR MUNICIPALIDADES (GET)
    // ===========================================================
    @Test
    @Order(1)
    void testListarMunicipalidades() {

        client.get()
                .uri("http://localhost:" + port + "/api/municipalidades")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    // ===========================================================
    // 3) CREAR MUNICIPALIDAD (POST)
    // ===========================================================
    @Test
    @Order(2)
    void testCrearMunicipalidad() {

        MunicipalidadRequest request = MunicipalidadRequest.builder()
                .nombre("Municipalidad Test")
                .departamento("Lima")
                .provincia("Lima")
                .distrito("Miraflores")
                .direccion("Av. Test 123")
                .telefono("987654321")
                .sitioWeb("www.testmunicipalidad.com")
                .descripcion("Municipalidad de prueba para test")
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/municipalidades")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> municipalidadId = Long.valueOf(id.toString()))
                .jsonPath("$.nombre").isEqualTo("Municipalidad Test");

        System.out.println("ID MUNICIPALIDAD CREADA: " + municipalidadId);
    }

    // ===========================================================
    // 4) OBTENER POR ID (GET)
    // ===========================================================
    @Test
    @Order(3)
    void testObtenerMunicipalidadPorId() {

        client.get()
                .uri("http://localhost:" + port + "/api/municipalidades/" + municipalidadId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(municipalidadId.intValue());
    }

    // ===========================================================
    // 5) BUSCAR POR DEPARTAMENTO
    // ===========================================================
    @Test
    @Order(4)
    void testBuscarPorDepartamento() {

        client.get()
                .uri("http://localhost:" + port + "/api/municipalidades/departamento/Lima")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    // ===========================================================
    // 6) BUSCAR POR PROVINCIA
    // ===========================================================
    @Test
    @Order(5)
    void testBuscarPorProvincia() {

        client.get()
                .uri("http://localhost:" + port + "/api/municipalidades/provincia/Lima")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    // ===========================================================
    // 7) BUSCAR POR DISTRITO
    // ===========================================================
    @Test
    @Order(6)
    void testBuscarPorDistrito() {

        client.get()
                .uri("http://localhost:" + port + "/api/municipalidades/distrito/Miraflores")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    // ===========================================================
    // 8) ACTUALIZAR MUNICIPALIDAD (PUT)
    // ===========================================================
    @Test
    @Order(7)
    void testActualizarMunicipalidad() {

        MunicipalidadRequest request = MunicipalidadRequest.builder()
                .nombre("Municipalidad Modificada")
                .departamento("Lima")
                .provincia("Lima")
                .distrito("Surco")
                .direccion("Av. Nueva 456")
                .telefono("999999999")
                .sitioWeb("www.municipalidadmod.com")
                .descripcion("Descripción modificada de test")
                .build();

        client.put()
                .uri("http://localhost:" + port + "/api/municipalidades/" + municipalidadId)
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Municipalidad Modificada");
    }

    // ===========================================================
    // 9) ELIMINAR MUNICIPALIDAD (DELETE)
    // ===========================================================
    @Test
    @Order(8)
    void testEliminarMunicipalidad() {

        client.delete()
                .uri("http://localhost:" + port + "/api/municipalidades/" + municipalidadId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isNoContent();
    }
}
