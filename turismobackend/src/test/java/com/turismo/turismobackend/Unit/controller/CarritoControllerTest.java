package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.CarritoController;
import com.turismo.turismobackend.dto.request.CarritoItemRequest;
import com.turismo.turismobackend.dto.response.CarritoResponse;
import com.turismo.turismobackend.service.CarritoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CarritoControllerTest {

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private CarritoController carritoController;

    private CarritoResponse carritoResponse;
    private CarritoItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = CarritoItemRequest.builder()
                .servicioId(10L)
                .cantidad(2)
                .fechaServicio(LocalDate.now())
                .notasEspeciales("Sin azúcar")
                .build();

        carritoResponse = CarritoResponse.builder()
                .id(1L)
                .usuarioId(99L)
                .totalItems(2)
                .build();
    }

    // -----------------------------------------------------
    // 1) Obtener carrito
    // -----------------------------------------------------
    @Test
    void testObtenerCarrito_ReturnsOk() {
        BDDMockito.given(carritoService.obtenerCarrito())
                .willReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response = carritoController.obtenerCarrito();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(carritoResponse);

        BDDMockito.then(carritoService).should().obtenerCarrito();
    }

    // -----------------------------------------------------
    // 2) Agregar item
    // -----------------------------------------------------
    @Test
    void testAgregarItem_ReturnsCarritoActualizado() {
        BDDMockito.given(carritoService.agregarItem(itemRequest))
                .willReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response = carritoController.agregarItem(itemRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1L);

        BDDMockito.then(carritoService).should().agregarItem(itemRequest);
    }

    // -----------------------------------------------------
    // 3) Actualizar cantidad de un item
    // -----------------------------------------------------
    @Test
    void testActualizarCantidad_ReturnsCarritoActualizado() {
        Long itemId = 5L;
        Integer nuevaCantidad = 3;

        BDDMockito.given(carritoService.actualizarCantidad(itemId, nuevaCantidad))
                .willReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response =
                carritoController.actualizarCantidad(itemId, nuevaCantidad);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(carritoResponse);

        BDDMockito.then(carritoService).should().actualizarCantidad(itemId, nuevaCantidad);
    }

    // -----------------------------------------------------
    // 4) Eliminar item
    // -----------------------------------------------------
    @Test
    void testEliminarItem_ReturnsCarritoActualizado() {
        Long itemId = 7L;

        BDDMockito.given(carritoService.eliminarItem(itemId))
                .willReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response = carritoController.eliminarItem(itemId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(carritoResponse);

        BDDMockito.then(carritoService).should().eliminarItem(itemId);
    }

    // -----------------------------------------------------
    // 5) Limpiar carrito
    // -----------------------------------------------------
    @Test
    void testLimpiarCarrito_ReturnsOk() {
        BDDMockito.willDoNothing().given(carritoService).limpiarCarrito();

        ResponseEntity<Void> response = carritoController.limpiarCarrito();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BDDMockito.then(carritoService).should().limpiarCarrito();
    }

    // -----------------------------------------------------
    // 6) Contar items
    // -----------------------------------------------------
    @Test
    void testContarItems_ReturnsLong() {
        BDDMockito.given(carritoService.contarItems())
                .willReturn(5L);

        ResponseEntity<Long> response = carritoController.contarItems();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(5L);

        BDDMockito.then(carritoService).should().contarItems();
    }

    // -----------------------------------------------------
    // 7) Obtener total del carrito (delegado a obtenerCarrito)
    // -----------------------------------------------------
    @Test
    void testObtenerTotalCarrito_ReturnsCarrito() {
        BDDMockito.given(carritoService.obtenerCarrito())
                .willReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response =
                carritoController.obtenerTotalCarrito();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(carritoResponse);

        BDDMockito.then(carritoService).should().obtenerCarrito();
    }

    // -----------------------------------------------------
    // 8) Cuando el service devuelve null (edge case)
    // -----------------------------------------------------
    @Test
    void testObtenerCarrito_ServiceDevuelveNull() {
        BDDMockito.given(carritoService.obtenerCarrito())
                .willReturn(null);

        ResponseEntity<CarritoResponse> response = carritoController.obtenerCarrito();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        BDDMockito.then(carritoService).should().obtenerCarrito();
    }

}
