package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.ServicioTuristicoController;
import com.turismo.turismobackend.dto.request.ServicioTuristicoRequest;
import com.turismo.turismobackend.dto.response.ServicioTuristicoResponse;
import com.turismo.turismobackend.model.ServicioTuristico;
import com.turismo.turismobackend.service.ServicioTuristicoService;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ServicioTuristicoControllerTest {

    @Mock
    private ServicioTuristicoService servicioService;

    @InjectMocks
    private ServicioTuristicoController controller;

    private ServicioTuristicoResponse servicio;
    private ServicioTuristicoRequest request;
    private List<ServicioTuristicoResponse> lista;

    @BeforeEach
    void setUp() {
        servicio = ServicioTuristicoResponse.builder()
                .id(1L)
                .nombre("Tour Lago Titicaca")
                .descripcion("Viaje en bote")
                .precio(new BigDecimal("120.00"))
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .duracionHoras(5)
                .build();

        lista = List.of(servicio);

        request = ServicioTuristicoRequest.builder()
                .nombre("Tour Lago Titicaca")
                .descripcion("Viaje en bote")
                .precio(new BigDecimal("120.00"))
                .duracionHoras(5)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .build();
    }

    // =====================================
    //               TESTS
    // =====================================

    @Test
    void getAllServicios_ReturnsOK() {
        BDDMockito.given(servicioService.getAllServicios()).willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res = controller.getAllServicios();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void getServicioById_ReturnsOK() {
        BDDMockito.given(servicioService.getServicioById(1L)).willReturn(servicio);

        ResponseEntity<ServicioTuristicoResponse> res = controller.getServicioById(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("Tour Lago Titicaca", res.getBody().getNombre());
    }

    @Test
    void getServicioById_ReturnsNull() {
        BDDMockito.given(servicioService.getServicioById(99L)).willReturn(null);

        ResponseEntity<ServicioTuristicoResponse> res = controller.getServicioById(99L);

        assertNull(res.getBody());
    }

    @Test
    void getServiciosByEmprendedor_ReturnsOK() {
        BDDMockito.given(servicioService.getServiciosByEmprendedor(10L)).willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByEmprendedor(10L);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getServiciosByEmprendedor_ListaVacia() {
        BDDMockito.given(servicioService.getServiciosByEmprendedor(10L))
                .willReturn(List.of());

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByEmprendedor(10L);

        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void getServiciosByMunicipalidad_ReturnsOK() {
        BDDMockito.given(servicioService.getServiciosByMunicipalidad(5L)).willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByMunicipalidad(5L);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getServiciosByTipo_ReturnsOK() {
        BDDMockito.given(servicioService.getServiciosByTipo(ServicioTuristico.TipoServicio.TOUR))
                .willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByTipo(ServicioTuristico.TipoServicio.TOUR);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getServiciosByTipo_ListaVacia() {
        BDDMockito.given(servicioService.getServiciosByTipo(ServicioTuristico.TipoServicio.TOUR))
                .willReturn(List.of());

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByTipo(ServicioTuristico.TipoServicio.TOUR);

        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void getServiciosByEstado_ReturnsOK() {
        BDDMockito.given(servicioService.getServiciosByEstado(ServicioTuristico.EstadoServicio.ACTIVO))
                .willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByEstado(ServicioTuristico.EstadoServicio.ACTIVO);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getServiciosByPrecio_ReturnsOK() {
        BDDMockito.given(servicioService.getServiciosByPrecio(
                        new BigDecimal("50"), new BigDecimal("200")))
                .willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosByPrecio(new BigDecimal("50"), new BigDecimal("200"));

        assertEquals(1, res.getBody().size());
    }

    @Test
    void searchServicios_ReturnsOK() {
        BDDMockito.given(servicioService.searchServicios("Lago"))
                .willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.searchServicios("Lago");

        assertEquals(1, res.getBody().size());
    }

    @Test
    void searchServicios_ListaVacia() {
        BDDMockito.given(servicioService.searchServicios("ABC"))
                .willReturn(List.of());

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.searchServicios("ABC");

        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void getMisServicios_ReturnsOK() {
        BDDMockito.given(servicioService.getMisServicios()).willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res = controller.getMisServicios();

        assertEquals(1, res.getBody().size());
    }

    @Test
    void createServicio_ReturnsCreated() {
        BDDMockito.given(servicioService.createServicio(request)).willReturn(servicio);

        ResponseEntity<ServicioTuristicoResponse> res = controller.createServicio(request);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("Tour Lago Titicaca", res.getBody().getNombre());
    }

    @Test
    void createServicio_Null_ReturnsNull() {
        BDDMockito.given(servicioService.createServicio(request)).willReturn(null);

        ResponseEntity<ServicioTuristicoResponse> res = controller.createServicio(request);

        assertNull(res.getBody());
    }

    @Test
    void updateServicio_ReturnsOK() {
        BDDMockito.given(servicioService.updateServicio(1L, request)).willReturn(servicio);

        ResponseEntity<ServicioTuristicoResponse> res =
                controller.updateServicio(1L, request);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void updateServicio_Null_ReturnsNull() {
        BDDMockito.given(servicioService.updateServicio(1L, request)).willReturn(null);

        ResponseEntity<ServicioTuristicoResponse> res =
                controller.updateServicio(1L, request);

        assertNull(res.getBody());
    }

    @Test
    void deleteServicio_ReturnsNoContent() {
        BDDMockito.willDoNothing().given(servicioService).deleteServicio(1L);

        ResponseEntity<Void> res = controller.deleteServicio(1L);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }

    @Test
    void cambiarEstado_ReturnsOK() {
        BDDMockito.given(
                        servicioService.cambiarEstado(1L, ServicioTuristico.EstadoServicio.INACTIVO))
                .willReturn(servicio);

        ResponseEntity<ServicioTuristicoResponse> res =
                controller.cambiarEstado(1L, ServicioTuristico.EstadoServicio.INACTIVO);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void cambiarEstado_Null_ReturnsNull() {
        BDDMockito.given(
                        servicioService.cambiarEstado(1L, ServicioTuristico.EstadoServicio.ACTIVO))
                .willReturn(null);

        ResponseEntity<ServicioTuristicoResponse> res =
                controller.cambiarEstado(1L, ServicioTuristico.EstadoServicio.ACTIVO);

        assertNull(res.getBody());
    }

    @Test
    void getServiciosCercanos_ReturnsOK() {
        BDDMockito.given(servicioService.getServiciosCercanos(10.0, 20.0, 5.0))
                .willReturn(lista);

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosCercanos(10.0, 20.0, 5.0);

        assertEquals(1, res.getBody().size());
    }

    @Test
    void getServiciosCercanos_ListaVacia() {
        BDDMockito.given(servicioService.getServiciosCercanos(10.0, 20.0, 5.0))
                .willReturn(List.of());

        ResponseEntity<List<ServicioTuristicoResponse>> res =
                controller.getServiciosCercanos(10.0, 20.0, 5.0);

        assertTrue(res.getBody().isEmpty());
    }
}
