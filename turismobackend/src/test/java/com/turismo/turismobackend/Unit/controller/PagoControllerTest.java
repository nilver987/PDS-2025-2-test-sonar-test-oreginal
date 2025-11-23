package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.PagoController;
import com.turismo.turismobackend.dto.request.PagoRequest;
import com.turismo.turismobackend.dto.response.PagoResponse;
import com.turismo.turismobackend.service.PagoService;
import com.turismo.turismobackend.model.Pago;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private PagoResponse pago;
    private List<PagoResponse> lista;
    private PagoRequest request;

    @BeforeEach
    void setUp() {

        pago = PagoResponse.builder()
                .id(1L)
                .codigoPago("COD123")
                .monto(BigDecimal.valueOf(100.00))
                .tipo(Pago.TipoPago.PAGO_COMPLETO)
                .estado(Pago.EstadoPago.PENDIENTE)
                .metodoPago(Pago.MetodoPago.TRANSFERENCIA)
                .numeroTransaccion("TXN-111")
                .numeroAutorizacion("AUTH-999")
                .observaciones("Sin observación")
                .fechaPago(LocalDateTime.now())
                .fechaConfirmacion(null)
                .build();

        lista = List.of(pago);

        request = new PagoRequest();
    }

    // -------------------------------------------------------------
    // 1. Obtener todos los pagos
    // -------------------------------------------------------------
    @Test
    void testGetAllPagos_OK() {
        BDDMockito.given(pagoService.getAllPagos()).willReturn(lista);

        ResponseEntity<List<PagoResponse>> response = pagoController.getAllPagos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        BDDMockito.then(pagoService).should().getAllPagos();
    }

    // -------------------------------------------------------------
    // 2. Obtener pago por ID
    // -------------------------------------------------------------
    @Test
    void testGetPagoById_OK() {
        BDDMockito.given(pagoService.getPagoById(1L)).willReturn(pago);

        ResponseEntity<PagoResponse> response = pagoController.getPagoById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("COD123", response.getBody().getCodigoPago());
    }

    // -------------------------------------------------------------
    // 3. Obtener pago por código
    // -------------------------------------------------------------
    @Test
    void testGetPagoByCodigo_OK() {
        BDDMockito.given(pagoService.getPagoByCodigo("COD123")).willReturn(pago);

        ResponseEntity<PagoResponse> response = pagoController.getPagoByCodigo("COD123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // -------------------------------------------------------------
    // 4. Pagos por reserva
    // -------------------------------------------------------------
    @Test
    void testGetPagosByReserva_OK() {
        BDDMockito.given(pagoService.getPagosByReserva(5L)).willReturn(lista);

        ResponseEntity<List<PagoResponse>> response = pagoController.getPagosByReserva(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // -------------------------------------------------------------
    // 5. Mis pagos
    // -------------------------------------------------------------
    @Test
    void testGetMisPagos_OK() {
        BDDMockito.given(pagoService.getMisPagos()).willReturn(lista);

        ResponseEntity<List<PagoResponse>> response = pagoController.getMisPagos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // -------------------------------------------------------------
    // 6. Pagos por municipalidad
    // -------------------------------------------------------------
    @Test
    void testGetPagosByMunicipalidad_OK() {
        BDDMockito.given(pagoService.getPagosByMunicipalidad(7L)).willReturn(lista);

        ResponseEntity<List<PagoResponse>> response =
                pagoController.getPagosByMunicipalidad(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // -------------------------------------------------------------
    // 7. Registrar pago
    // -------------------------------------------------------------
    @Test
    void testRegistrarPago_Created() {
        BDDMockito.given(pagoService.registrarPago(request)).willReturn(pago);

        ResponseEntity<PagoResponse> response = pagoController.registrarPago(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pago, response.getBody());
    }

    // -------------------------------------------------------------
    // 8. Confirmar pago
    // -------------------------------------------------------------
    @Test
    void testConfirmarPago_OK() {
        BDDMockito.given(pagoService.confirmarPago(1L)).willReturn(pago);

        ResponseEntity<PagoResponse> response = pagoController.confirmarPago(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // -------------------------------------------------------------
    // 9. Rechazar pago
    // -------------------------------------------------------------
    @Test
    void testRechazarPago_OK() {
        BDDMockito.given(pagoService.rechazarPago(1L, "Motivo")).willReturn(pago);

        ResponseEntity<PagoResponse> response =
                pagoController.rechazarPago(1L, "Motivo");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BDDMockito.then(pagoService).should().rechazarPago(1L, "Motivo");
    }

    // -------------------------------------------------------------
    // EXTRAS (para llegar a 16)
    // -------------------------------------------------------------

    @Test
    void testGetPagoById_NoNull() {
        BDDMockito.given(pagoService.getPagoById(1L)).willReturn(pago);

        assertNotNull(pagoController.getPagoById(1L).getBody());
    }

    @Test
    void testGetPagoByCodigo_NotEmpty() {
        BDDMockito.given(pagoService.getPagoByCodigo("COD123")).willReturn(pago);

        assertEquals("COD123", pagoController.getPagoByCodigo("COD123").getBody().getCodigoPago());
    }

    @Test
    void testMisPagos_Size() {
        BDDMockito.given(pagoService.getMisPagos()).willReturn(lista);

        assertEquals(1, pagoController.getMisPagos().getBody().size());
    }

    @Test
    void testRegistrarPago_NotNull() {
        BDDMockito.given(pagoService.registrarPago(request)).willReturn(pago);

        assertNotNull(pagoController.registrarPago(request).getBody());
    }

    @Test
    void testConfirmarPago_NotNull() {
        BDDMockito.given(pagoService.confirmarPago(1L)).willReturn(pago);

        assertNotNull(pagoController.confirmarPago(1L).getBody());
    }

    @Test
    void testRechazarPago_MotivoCorrecto() {
        BDDMockito.given(pagoService.rechazarPago(1L, "ERROR")).willReturn(pago);

        ResponseEntity<PagoResponse> response =
                pagoController.rechazarPago(1L, "ERROR");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
