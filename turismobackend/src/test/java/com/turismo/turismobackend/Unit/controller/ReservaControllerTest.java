package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.ReservaController;
import com.turismo.turismobackend.dto.request.ReservaRequest;
import com.turismo.turismobackend.dto.response.ReservaResponse;
import com.turismo.turismobackend.model.Reserva;
import com.turismo.turismobackend.service.ReservaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReservaControllerTest {

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController reservaController;

    private ReservaResponse reserva;
    private ReservaRequest request;
    private List<ReservaResponse> lista;

    @BeforeEach
    void setUp() {
        reserva = ReservaResponse.builder()
                .id(1L)
                .codigoReserva("RES123")
                .fechaInicio(LocalDate.now().plusDays(5))
                .fechaFin(LocalDate.now().plusDays(7))
                .numeroPersonas(2)
                .montoTotal(new BigDecimal("300.00"))
                .montoDescuento(new BigDecimal("20.00"))
                .montoFinal(new BigDecimal("280.00"))
                .estado(Reserva.EstadoReserva.CONFIRMADA)
                .metodoPago(Reserva.MetodoPago.PAGO_MOVIL)
                .observaciones("Ninguna")
                .solicitudesEspeciales("Vegetariano")
                .contactoEmergencia("Juan Perez")
                .telefonoEmergencia("999999999")
                .fechaReserva(LocalDateTime.now())
                .fechaConfirmacion(LocalDateTime.now())
                .build();

        lista = List.of(reserva);

        request = ReservaRequest.builder()
                .planId(1L)
                .fechaInicio(LocalDate.now().plusDays(5))
                .numeroPersonas(2)
                .observaciones("Todo bien")
                .solicitudesEspeciales("Ninguna")
                .contactoEmergencia("Maria Lopez")
                .telefonoEmergencia("988888888")
                .metodoPago(Reserva.MetodoPago.PAGO_MOVIL)
                .build();
    }

    // ============================
    //           TESTS
    // ============================

    @Test
    void getAllReservas_ReturnsListOK() {
        BDDMockito.given(reservaService.getAllReservas()).willReturn(lista);

        ResponseEntity<List<ReservaResponse>> res = reservaController.getAllReservas();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void getReservaById_ReturnsOK() {
        BDDMockito.given(reservaService.getReservaById(1L)).willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.getReservaById(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("RES123", res.getBody().getCodigoReserva());
    }

    @Test
    void getReservaByCodigo_ReturnsOK() {
        BDDMockito.given(reservaService.getReservaByCodigo("RES123")).willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.getReservaByCodigo("RES123");

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void getMisReservas_ReturnsListOK() {
        BDDMockito.given(reservaService.getMisReservas()).willReturn(lista);

        ResponseEntity<List<ReservaResponse>> res = reservaController.getMisReservas();

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getReservasByPlan_ReturnsOK() {
        BDDMockito.given(reservaService.getReservasByPlan(1L)).willReturn(lista);

        ResponseEntity<List<ReservaResponse>> res = reservaController.getReservasByPlan(1L);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getReservasByMunicipalidad_ReturnsOK() {
        BDDMockito.given(reservaService.getReservasByMunicipalidad(5L)).willReturn(lista);

        ResponseEntity<List<ReservaResponse>> res = reservaController.getReservasByMunicipalidad(5L);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void createReserva_ReturnsCreated() {
        BDDMockito.given(reservaService.createReserva(request)).willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.createReserva(request);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("RES123", res.getBody().getCodigoReserva());
    }

    @Test
    void confirmarReserva_ReturnsOK() {
        BDDMockito.given(reservaService.confirmarReserva(1L)).willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.confirmarReserva(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void cancelarReserva_ReturnsOK() {
        BDDMockito.given(reservaService.cancelarReserva(1L, "Motivo Test"))
                .willReturn(reserva);

        ResponseEntity<ReservaResponse> res =
                reservaController.cancelarReserva(1L, "Motivo Test");

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void completarReserva_ReturnsOK() {
        BDDMockito.given(reservaService.completarReserva(1L)).willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.completarReserva(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void getReservaById_NotFound_ReturnsNullBody() {
        BDDMockito.given(reservaService.getReservaById(99L)).willReturn(null);

        ResponseEntity<ReservaResponse> res = reservaController.getReservaById(99L);

        assertNull(res.getBody());
    }

    @Test
    void createReserva_NullResponse_ReturnsNull() {
        BDDMockito.given(reservaService.createReserva(request)).willReturn(null);

        ResponseEntity<ReservaResponse> res = reservaController.createReserva(request);

        assertNull(res.getBody());
    }

    @Test
    void cancelarReserva_EmptyMotivo() {
        BDDMockito.given(reservaService.cancelarReserva(1L, ""))
                .willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.cancelarReserva(1L, "");

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void confirmarReserva_InvalidId_ReturnsNull() {
        BDDMockito.given(reservaService.confirmarReserva(999L)).willReturn(null);

        ResponseEntity<ReservaResponse> res = reservaController.confirmarReserva(999L);

        assertNull(res.getBody());
    }

    @Test
    void completarReserva_InvalidId_ReturnsNull() {
        BDDMockito.given(reservaService.completarReserva(999L)).willReturn(null);

        ResponseEntity<ReservaResponse> res = reservaController.completarReserva(999L);

        assertNull(res.getBody());
    }

    @Test
    void getReservasByPlan_ListaVacia() {
        BDDMockito.given(reservaService.getReservasByPlan(5L)).willReturn(List.of());

        ResponseEntity<List<ReservaResponse>> res = reservaController.getReservasByPlan(5L);

        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void getReservasByCodigo_Null_ReturnsNull() {
        BDDMockito.given(reservaService.getReservaByCodigo("XXX")).willReturn(null);

        ResponseEntity<ReservaResponse> res = reservaController.getReservaByCodigo("XXX");

        assertNull(res.getBody());
    }

    @Test
    void createReserva_RequestNull_ReturnsNull() {
        BDDMockito.given(reservaService.createReserva(null)).willReturn(null);

        ResponseEntity<ReservaResponse> res = reservaController.createReserva(null);

        assertNull(res.getBody());
    }

    @Test
    void cancelarReserva_MotivoNull() {
        BDDMockito.given(reservaService.cancelarReserva(1L, null)).willReturn(reserva);

        ResponseEntity<ReservaResponse> res = reservaController.cancelarReserva(1L, null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
