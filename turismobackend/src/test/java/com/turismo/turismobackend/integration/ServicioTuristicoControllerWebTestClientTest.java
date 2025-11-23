package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.ServicioTuristicoRequest;
import com.turismo.turismobackend.model.ServicioTuristico;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ServicioTuristicoControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenEmp;
    private Long servicioId;

    // ===========================================================
    // 1) LOGIN EMPRENDEDOR
    // ===========================================================
    @BeforeAll
    void loginEmprendedor() {

        String loginJson = """
        {
          "username": "juan_perez",
          "password": "emp123"
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
                .value(t -> this.tokenEmp = "Bearer " + t.toString());

        System.out.println("TOKEN EMPRENDEDOR: " + tokenEmp);
    }

    // ===========================================================
    // 2) CREAR SERVICIO
    // ===========================================================
    @Test
    @Order(2)
    void testCrearServicio() {

        ServicioTuristicoRequest request = ServicioTuristicoRequest.builder()
                .nombre("Tour Miraflores Test")
                .descripcion("Recorrido turístico por Miraflores")
                .precio(BigDecimal.valueOf(150.50))
                .duracionHoras(3)
                .capacidadMaxima(20)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .ubicacion("Miraflores, Lima")
                .latitud(-12.1211)
                .longitud(-77.0290)
                .requisitos("Llevar agua")
                .incluye("Guía turístico")
                .noIncluye("Almuerzo")
                .imagenUrl("http://imagen.com/foto.png")
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/servicios")
                .header("Authorization", tokenEmp)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> servicioId = Long.valueOf(id.toString()))
                .jsonPath("$.nombre").isEqualTo("Tour Miraflores Test");

        System.out.println("ID SERVICIO CREADO: " + servicioId);
    }

    // ===========================================================
    // 3) LISTAR SERVICIOS
    // ===========================================================
    @Test
    @Order(3)
    void testListarServicios() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    // ===========================================================
    // 4) OBTENER POR ID
    // ===========================================================
    @Test
    @Order(4)
    void testObtenerServicioPorId() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/" + servicioId)
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(servicioId.intValue());
    }

    // ===========================================================
    // 5) ACTUALIZAR SERVICIO
    // ===========================================================
    @Test
    @Order(5)
    void testActualizarServicio() {

        ServicioTuristicoRequest request = ServicioTuristicoRequest.builder()
                .nombre("Servicio Actualizado Test")
                .descripcion("Modificado")
                .precio(BigDecimal.valueOf(180.00))
                .duracionHoras(4)
                .capacidadMaxima(25)
                .tipo(ServicioTuristico.TipoServicio.AVENTURA)
                .ubicacion("Larcomar")
                .latitud(-12.1291)
                .longitud(-77.0330)
                .build();

        client.put()
                .uri("http://localhost:" + port + "/api/servicios/" + servicioId)
                .header("Authorization", tokenEmp)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Servicio Actualizado Test");
    }

    // ===========================================================
    // 6) CAMBIAR ESTADO
    // ===========================================================
    @Test
    @Order(6)
    void testCambiarEstado() {

        client.patch()
                .uri("http://localhost:" + port + "/api/servicios/" + servicioId + "/estado?estado=INACTIVO")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("INACTIVO");
    }

    // ===========================================================
    // 7) SERVICIOS CERCANOS
    // ===========================================================
    @Test
    @Order(7)
    void testServiciosCercanos() {

        client.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(port)
                        .path("/api/servicios/cercanos")
                        .queryParam("latitud", -12.1200)
                        .queryParam("longitud", -77.0300)
                        .queryParam("radio", 5.0)
                        .build())
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    // ===========================================================
    // 8) ELIMINAR SERVICIO
    // ===========================================================
    @Test
    @Order(8)
    void testEliminarServicio() {

        client.delete()
                .uri("http://localhost:" + port + "/api/servicios/" + servicioId)
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isNoContent();
    }

    // ===========================================================
    // 9) CREAR SERVICIO SIN CAMPOS OBLIGATORIOS
    // ===========================================================
    @Test
    @Order(9)
    void testCrearServicioSinCampos() {

        ServicioTuristicoRequest request = ServicioTuristicoRequest.builder()
                .precio(BigDecimal.valueOf(10))
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/servicios")
                .header("Authorization", tokenEmp)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ===========================================================
    // 10) CREAR SERVICIO CON PRECIO NEGATIVO
    // ===========================================================
    @Test
    @Order(10)
    void testCrearPrecioNegativo() {

        ServicioTuristicoRequest request = ServicioTuristicoRequest.builder()
                .nombre("Negativo")
                .precio(BigDecimal.valueOf(-50))
                .duracionHoras(2)
                .capacidadMaxima(10)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .build();

        client.post()
                .uri("http://localhost:" + port + "/api/servicios")
                .header("Authorization", tokenEmp)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ===========================================================
    // 11) CREAR SIN TOKEN
    // ===========================================================


    // ===========================================================
    // 12) OBTENER INEXISTENTE
    // ===========================================================
    @Test
    @Order(12)
    void testObtenerInexistente() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/999999")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ===========================================================
    // 13) ACTUALIZAR INEXISTENTE
    // ===========================================================
    @Test
    @Order(13)
    void testActualizarInexistente() {

        ServicioTuristicoRequest request = ServicioTuristicoRequest.builder()
                .nombre("Nada")
                .precio(BigDecimal.valueOf(50))
                .duracionHoras(1)
                .capacidadMaxima(5)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .build();

        client.put()
                .uri("http://localhost:" + port + "/api/servicios/999999")
                .header("Authorization", tokenEmp)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ===========================================================
    // 14) ELIMINAR INEXISTENTE
    // ===========================================================
    @Test
    @Order(14)
    void testEliminarInexistente() {

        client.delete()
                .uri("http://localhost:" + port + "/api/servicios/999999")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ===========================================================
    // 15) BUSCAR POR NOMBRE EXISTENTE
    // ===========================================================
    @Test
    @Order(15)
    void testBuscarPorNombre() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/search?termino=Miraflores")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 16) BUSCAR POR NOMBRE INEXISTENTE
    // ===========================================================
    @Test
    @Order(16)
    void testBuscarNombreInexistente() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/search?termino=ZZZZZ")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    // ===========================================================
    // 17) FILTRAR POR TIPO
    // ===========================================================
    @Test
    @Order(17)
    void testFiltrarPorTipo() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/tipo/TOUR")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 18) FILTRAR POR ESTADO
    // ===========================================================
    @Test
    @Order(18)
    void testFiltrarPorEstado() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/estado/ACTIVO")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 19) FILTRAR POR PRECIO
    // ===========================================================
    @Test
    @Order(19)
    void testFiltrarPorPrecio() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/precio?precioMin=0&precioMax=999")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 20) MIS SERVICIOS
    // ===========================================================
    @Test
    @Order(20)
    void testMisServicios() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/mis-servicios")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk();
    }





    // ===========================================================
    // 23) SERVICIOS CERCANOS FUERA DE RANGO
    // ===========================================================
    @Test
    @Order(23)
    void testServiciosCercanosFueraRango() {

        client.get()
                .uri("/api/servicios/cercanos?latitud=-90&longitud=-90&radio=1")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }




    // ===========================================================
    // 25) LISTAR POR MUNICIPALIDAD
    // ===========================================================
    @Test
    @Order(25)
    void testServiciosPorMunicipalidad() {

        client.get()
                .uri("http://localhost:" + port + "/api/servicios/municipalidad/1")
                .header("Authorization", tokenEmp)
                .exchange()
                .expectStatus().isOk();
    }
}
