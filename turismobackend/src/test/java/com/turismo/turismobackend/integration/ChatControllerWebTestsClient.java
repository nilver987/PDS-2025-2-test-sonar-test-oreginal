package com.turismo.turismobackend.integration;

import com.turismo.turismobackend.dto.request.ChatMensajeRequest;
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
public class ChatControllerWebTestsClient {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    private String tokenAdmin;
    private String tokenEmp;
    private Long conversacionId;
    private Long emprendedorId;
    private Long reservaCarritoId = 1L;

    // ===========================================================
    // 1) LOGIN ADMIN (usuario)
    // ===========================================================
    @Test @Order(1)
    void testLoginAdmin() {

        String login = """
        {
          "username": "admin",
          "password": "admin123"
        }
        """;

        client.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token")
                .value(t -> tokenAdmin = "Bearer " + t.toString());
    }

    // ===========================================================
    // 2) LOGIN EMPRENDEDOR YA EXISTENTE
    // ===========================================================
    @Test @Order(2)
    void testLoginEmprendedor() {

        String login = """
        {
          "username": "juan_perez",
          "password": "emp123"
        }
        """;

        client.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> emprendedorId = Long.valueOf(id.toString()))
                .jsonPath("$.token")
                .value(t -> tokenEmp = "Bearer " + t.toString());
    }

    // ===========================================================
    // 3) ADMIN INICIA CONVERSACIÓN
    // ===========================================================
    @Test @Order(3)
    void testIniciarConversacion() {

        client.post()
                .uri("/api/chat/conversacion/iniciar?emprendedorId=" + emprendedorId)
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> conversacionId = Long.valueOf(id.toString()));
    }

    // ===========================================================
    // 4) ADMIN ENVÍA MENSAJE
    // ===========================================================
    @Test @Order(4)
    void testAdminEnviaMensaje() {

        ChatMensajeRequest req = ChatMensajeRequest.builder()
                .conversacionId(conversacionId)
                .mensaje("Hola Juan, tengo una consulta.")
                .build();

        client.post()
                .uri("/api/chat/mensaje")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 5) EMPRENDEDOR RESPONDE
    // ===========================================================


    // ===========================================================
    // 6) ADMIN OBTIENE MENSAJES
    // ===========================================================
    @Test @Order(6)
    void testAdminObtieneMensajes() {

        client.get()
                .uri("/api/chat/conversacion/" + conversacionId + "/mensajes")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 7) EMPRENDEDOR OBTIENE MENSAJES
    // ===========================================================


    // ===========================================================
    // 8) ARCHIVAR
    // ===========================================================
    @Test @Order(8)
    void testArchivar() {

        client.patch()
                .uri("/api/chat/conversacion/" + conversacionId + "/archivar")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 9) CERRAR
    // ===========================================================
    @Test @Order(9)
    void testCerrarConversacion() {

        client.patch()
                .uri("/api/chat/conversacion/" + conversacionId + "/cerrar")
                .header("Authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk();
    }

    // ===========================================================
    // 10) ENVIAR MENSAJE INVÁLIDO
    // ===========================================================
    @Test @Order(10)
    void testEnviarMensajeInvalido() {

        ChatMensajeRequest req = ChatMensajeRequest.builder()
                .mensaje(null)
                .build();

        client.post()
                .uri("/api/chat/mensaje")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
