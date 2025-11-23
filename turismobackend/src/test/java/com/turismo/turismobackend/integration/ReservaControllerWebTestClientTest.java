package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.ReservaRequest;
import com.turismo.turismobackend.model.Reserva;
import com.turismo.turismobackend.repository.ReservaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ReservaControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;
    @Autowired
    private ReservaRepository reservaRepository;


    private String tokenAdmin;
    private String tokenUser;

    private Long reservaId;
    private final Long planId = 1L;

    // ================================================================
    // LOGIN ADMIN
    // ================================================================
    @BeforeAll
    void loginAdmin() {
        String login = """
        {
          "username": "admin",
          "password": "admin123"
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token")
                .value(token -> tokenAdmin = "Bearer " + token.toString());
    }

    // ================================================================
    // LOGIN USUARIO NORMAL (juan_perez)
    // ================================================================
    @BeforeAll
    void loginUser() {
        String login = """
        {
          "username": "juan_perez",
          "password": "emp123"
        }
        """;

        client.post()
                .uri("http://localhost:" + port + "/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token")
                .value(token -> tokenUser = "Bearer " + token.toString());
    }

    // ================================================================
    // 1) LISTAR TODAS LAS RESERVAS (ADMIN)
    // ================================================================
    @Test
    @Order(1)
    void testListarReservas() {
        client.get()
                .uri("/api/reservas")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").exists();
    }

    // ================================================================
    // 2) CREAR RESERVA
    // ================================================================
    @Test
    @Order(2)
    void testCrearReserva() {

        ReservaRequest request = ReservaRequest.builder()
                .planId(planId)
                .fechaInicio(LocalDate.now().plusDays(3))
                .numeroPersonas(2)
                .observaciones("Reserva creada desde test")
                .contactoEmergencia("Juan Pérez")
                .telefonoEmergencia("999888777")
                .metodoPago(Reserva.MetodoPago.EFECTIVO)
                .build();

        client.post()
                .uri("/api/reservas")
                .header("Authorization", tokenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> reservaId = Long.valueOf(id.toString())); // CORREGIDO
    }

    // ================================================================
    // 3) OBTENER POR ID
    // ================================================================
    @Test
    @Order(3)
    void testObtenerPorId() {
        client.get()
                .uri("/api/reservas/" + reservaId)
                .header("Authorization", tokenUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.codigoReserva").exists();
    }

    // ================================================================
    // 4) OBTENER POR CÓDIGO
    // ================================================================
    @Test
    @Order(4)
    void testObtenerPorCodigo() {

        final String[] codigo = new String[1];

        client.get()
                .uri("/api/reservas/" + reservaId)
                .header("Authorization", tokenUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.codigoReserva")
                .value(c -> codigo[0] = c.toString());  // CORREGIDO

        client.get()
                .uri("/api/reservas/codigo/" + codigo[0])
                .header("Authorization", tokenUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(reservaId.intValue());
    }

    // ================================================================
    // 5) MIS RESERVAS
    // ================================================================
    @Test
    @Order(5)
    void testMisReservas() {
        client.get()
                .uri("/api/reservas/mis-reservas")
                .header("Authorization", tokenUser)
                .exchange()
                .expectStatus().isOk();
    }

    // ================================================================
    // 6) CONFIRMAR (ADMIN)
    // ================================================================
    @Test
    @Order(6)
    void testConfirmar() {
        client.patch()
                .uri("/api/reservas/" + reservaId + "/confirmar")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("CONFIRMADA");
    }

    // ================================================================
    // 7) CANCELAR (USER)
    // ================================================================
    @Test
    @Order(7)
    void testCancelar() {
        client.patch()
                .uri("/api/reservas/" + reservaId + "/cancelar?motivo=No+asistiré")
                .header("Authorization", tokenUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("CANCELADA");
    }
    @Test
    @Order(8)
    void testReservasPorPlan() {
        client.get()
                .uri("/api/reservas/plan/" + planId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].plan.id").isEqualTo(planId.intValue());
    }
    @Test
    @Order(9)
    void testReservasPorMunicipalidad() {
        client.get()
                .uri("/api/reservas/municipalidad/2")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }




    @Test
    @Order(10)
    void testObtenerInexistente() {
        client.get()
                .uri("/api/reservas/999999")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(11)
    void testCrearReservaInvalida() {

        ReservaRequest mala = ReservaRequest.builder()
                .planId(null)           // ❌ inválido
                .numeroPersonas(-1)     // ❌ inválido
                .build();

        client.post()
                .uri("/api/reservas")
                .header("Authorization", tokenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mala)
                .exchange()
                .expectStatus().isBadRequest();
    }



    @Test
    @Order(12)
    void testEliminarReservaFinal() {

        Assertions.assertNotNull(reservaId,
                "La reservaId no puede ser null al intentar eliminar.");

        // Primero verificar que existe
        Assertions.assertTrue(reservaRepository.findById(reservaId).isPresent(),
                "La reserva a eliminar NO existe.");

        // Eliminar
        reservaRepository.deleteById(reservaId);

        // Verificar que se eliminó
        Assertions.assertTrue(reservaRepository.findById(reservaId).isEmpty(),
                "La reserva NO fue eliminada correctamente.");
    }
    @Test





    @Order(13)
    void testCompletarReserva() {

        // 1) Crear una reserva nueva
        ReservaRequest req = ReservaRequest.builder()
                .planId(planId)
                .fechaInicio(LocalDate.now().plusDays(5))
                .numeroPersonas(1)
                .contactoEmergencia("Prueba")
                .telefonoEmergencia("999999999")
                .metodoPago(Reserva.MetodoPago.EFECTIVO)
                .build();

        final Long[] tmpId = new Long[1];

        client.post()
                .uri("/api/reservas")
                .header("Authorization", tokenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> tmpId[0] = Long.valueOf(id.toString()));

        Long nuevaReservaId = tmpId[0];

        // 2) Confirmar (PENDIENTE → CONFIRMADA)
        client.patch()
                .uri("/api/reservas/" + nuevaReservaId + "/confirmar")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("CONFIRMADA");

        // 3) Intentar completar sin estar en EN_PROCESO → debe fallar
        client.patch()
                .uri("/api/reservas/" + nuevaReservaId + "/completar")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Solo se pueden completar reservas en proceso");

        // 4) Simular cambio manual a EN_PROCESO
        reservaRepository.findById(nuevaReservaId).ifPresent(r -> {
            r.setEstado(Reserva.EstadoReserva.EN_PROCESO);
            reservaRepository.save(r);
        });

        // 5) Completar correctamente
        client.patch()
                .uri("/api/reservas/" + nuevaReservaId + "/completar")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("COMPLETADA");

        // 6) ELIMINAR LA RESERVA CREADA
        reservaRepository.deleteById(nuevaReservaId);
    }



}
