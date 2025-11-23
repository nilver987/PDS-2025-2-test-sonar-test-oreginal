package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.ChatController;
import com.turismo.turismobackend.dto.request.ChatMensajeRequest;
import com.turismo.turismobackend.dto.response.ChatConversacionResponse;
import com.turismo.turismobackend.dto.response.ChatMensajeResponse;
import com.turismo.turismobackend.service.ChatService;
import com.turismo.turismobackend.repository.ChatConversacionRepository;

import com.turismo.turismobackend.model.ChatMensaje;
import com.turismo.turismobackend.model.ChatConversacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private ChatConversacionRepository conversacionRepository;

    @InjectMocks
    private ChatController chatController;

    private ChatConversacionResponse conversacion;
    private ChatMensajeResponse mensaje;
    private ChatMensajeRequest mensajeRequest;

    @BeforeEach
    void setUp() {

        mensaje = ChatMensajeResponse.builder()
                .id(1L)
                .conversacionId(1L)
                .mensaje("Hola!")
                .tipo(ChatMensaje.TipoMensaje.TEXTO)
                .fechaEnvio(LocalDateTime.now())
                .leido(false)
                .esDeEmprendedor(false)
                .remitenteId(10L)
                .remitenteNombre("Usuario")
                .archivoUrl(null)
                .build();

        conversacion = ChatConversacionResponse.builder()
                .id(1L)
                .usuarioId(10L)
                .emprendedorId(20L)
                .reservaId(99L)
                .reservaCarritoId(888L)
                .codigoReservaAsociada("RES-001")
                .fechaCreacion(LocalDateTime.now())
                .fechaUltimoMensaje(LocalDateTime.now())
                .estado(ChatConversacion.EstadoConversacion.ACTIVA)
                .ultimoMensaje(mensaje)
                .mensajesNoLeidos(0L)
                .mensajesRecientes(List.of(mensaje))
                .build();

        mensajeRequest = ChatMensajeRequest.builder()
                .conversacionId(1L)
                .mensaje("Hola!")
                .tipo(ChatMensaje.TipoMensaje.TEXTO)
                .archivoUrl(null)
                .archivoNombre(null)
                .archivoTipo(null)
                .build();
    }

    // ======================================================================================
    // 1) Obtener conversaciones
    @Test
    void testObtenerConversaciones_OK() {
        BDDMockito.given(chatService.obtenerConversaciones()).willReturn(List.of(conversacion));

        ResponseEntity<List<ChatConversacionResponse>> response = chatController.obtenerConversaciones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ======================================================================================
    // 2) Iniciar conversacion
    @Test
    void testIniciarConversacion_OK() {
        BDDMockito.given(chatService.iniciarConversacion(20L, 99L)).willReturn(conversacion);

        ResponseEntity<ChatConversacionResponse> response =
                chatController.iniciarConversacion(20L, 99L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    // ======================================================================================
    // 3) Enviar mensaje
    @Test
    void testEnviarMensaje_OK() {
        BDDMockito.given(chatService.enviarMensaje(mensajeRequest)).willReturn(mensaje);

        ResponseEntity<ChatMensajeResponse> response =
                chatController.enviarMensaje(mensajeRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hola!", response.getBody().getMensaje());
    }

    // ======================================================================================
    // 4) Obtener mensajes
    @Test
    void testObtenerMensajes_OK() {
        BDDMockito.given(chatService.obtenerMensajes(1L, 0, 20)).willReturn(List.of(mensaje));

        ResponseEntity<List<ChatMensajeResponse>> response =
                chatController.obtenerMensajes(1L, 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ======================================================================================
    // 5) Marcar mensajes como leídos
    @Test
    void testMarcarMensajesComoLeidos_OK() {
        BDDMockito.willDoNothing().given(chatService).marcarMensajesComoLeidos(1L);

        ResponseEntity<Void> response =
                chatController.marcarMensajesComoLeidos(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ======================================================================================
    // 6) Contar mensajes no leídos
    @Test
    void testContarMensajesNoLeidos_OK() {
        BDDMockito.given(chatService.contarMensajesNoLeidos()).willReturn(3L);

        ResponseEntity<Long> response = chatController.contarMensajesNoLeidos();

        assertEquals(3L, response.getBody());
    }

    // ======================================================================================
    // 7) Cerrar conversación
    @Test
    void testCerrarConversacion_OK() {
        BDDMockito.willDoNothing().given(chatService).cerrarConversacion(1L);

        ResponseEntity<Void> response = chatController.cerrarConversacion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ======================================================================================
    // 8) Obtener conversacion por ID
    @Test
    void testObtenerConversacionPorId_OK() {
        BDDMockito.given(chatService.obtenerConversacionPorId(1L))
                .willReturn(conversacion);

        ResponseEntity<ChatConversacionResponse> response =
                chatController.obtenerConversacion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    // ======================================================================================
    // 9) Archivar conversación
    @Test
    void testArchivarConversacion_OK() {
        BDDMockito.willDoNothing().given(chatService).archivarConversacion(1L);

        ResponseEntity<Void> response =
                chatController.archivarConversacion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ======================================================================================
    // 10) Iniciar conversacion desde reserva carrito
    @Test
    void testIniciarConversacionConReservaCarrito_OK() {
        BDDMockito.given(chatService.iniciarConversacionConReservaCarrito(20L, 888L))
                .willReturn(conversacion);

        ResponseEntity<ChatConversacionResponse> response =
                chatController.iniciarConversacionConReservaCarrito(20L, 888L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    // ======================================================================================
    // 11) Obtener conversaciones por reserva carrito
    @Test
    void testObtenerConversacionesPorReservaCarrito_OK() {
        BDDMockito.given(chatService.obtenerConversacionesPorReservaCarrito(888L))
                .willReturn(List.of(conversacion));

        ResponseEntity<List<ChatConversacionResponse>> response =
                chatController.obtenerConversacionesPorReservaCarrito(888L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // ======================================================================================
    // 12) Enviar mensaje rápido a una reserva
    @Test
    void testEnviarMensajeRapido_OK() {
        BDDMockito.given(chatService.enviarMensajeRapidoAReserva(888L, "Hola"))
                .willReturn(List.of(mensaje));

        ResponseEntity<List<ChatMensajeResponse>> response =
                chatController.enviarMensajeRapidoAReserva(888L, "Hola");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
