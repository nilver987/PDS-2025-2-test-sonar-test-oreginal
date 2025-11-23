package com.turismo.turismobackend.Unit.websocket;

import com.turismo.turismobackend.controller.ChatWebSocketController;
import com.turismo.turismobackend.dto.request.ChatMensajeRequest;
import com.turismo.turismobackend.dto.response.ChatMensajeResponse;
import com.turismo.turismobackend.model.ChatMensaje;
import com.turismo.turismobackend.service.ChatService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatWebSocketControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ChatWebSocketController chatWebSocketController;

    private ChatMensajeRequest request;
    private ChatMensajeResponse response;

    @BeforeEach
    void init() {
        request = ChatMensajeRequest.builder()
                .conversacionId(1L)
                .mensaje("Hola desde WS!")
                .tipo(ChatMensaje.TipoMensaje.TEXTO)
                .build();

        response = ChatMensajeResponse.builder()
                .id(10L)
                .conversacionId(1L)
                .mensaje("Hola desde WS!")
                .tipo(ChatMensaje.TipoMensaje.TEXTO)
                .fechaEnvio(LocalDateTime.now())
                .esDeEmprendedor(false)
                .remitenteId(5L)
                .remitenteNombre("Nayder")
                .build();
    }

    // --------------------------------------------------------------------------------------
    // 1) enviarMensaje() → enviado correctamente
    @Test
    void testEnviarMensaje_OK() {
        when(chatService.enviarMensaje(request)).thenReturn(response);

        chatWebSocketController.enviarMensaje(request);

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/conversacion/1"),
                eq(response)
        );
    }

    // --------------------------------------------------------------------------------------
    // 2) enviarMensaje() → ocurre un error en el servicio y se envía un mensaje de error
    @Test
    void testEnviarMensaje_Error() {
        when(chatService.enviarMensaje(request)).thenThrow(new RuntimeException("Fallo interno"));

        chatWebSocketController.enviarMensaje(request);

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/conversacion/1/error"),
                eq("Error al enviar mensaje: Fallo interno")
        );
    }

    // --------------------------------------------------------------------------------------
    // 3) usuarioEscribiendo()
    @Test
    void testUsuarioEscribiendo() {
        String result = chatWebSocketController.usuarioEscribiendo(1L, "Nayder");

        assertEquals("Nayder está escribiendo...", result);
    }

    // --------------------------------------------------------------------------------------
    // 4) usuarioDejoDeEscribir()
    @Test
    void testUsuarioDejoDeEscribir() {
        String result = chatWebSocketController.usuarioDejoDeEscribir(1L, "Nayder");

        assertEquals("", result); // devuelve cadena vacía
    }

    // --------------------------------------------------------------------------------------
    // 5) unirseAConversacion()
    @Test
    void testUnirseAConversacion() {
        chatWebSocketController.unirseAConversacion(1L, "Nayder");

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/conversacion/1/usuarios"),
                eq("Nayder se unió a la conversación")
        );
    }

    // --------------------------------------------------------------------------------------
    // 6) salirDeConversacion()
    @Test
    void testSalirDeConversacion() {
        chatWebSocketController.salirDeConversacion(1L, "Nayder");

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/conversacion/1/usuarios"),
                eq("Nayder salió de la conversación")
        );
    }
}
