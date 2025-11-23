package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.MunicipalidadController;
import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
import com.turismo.turismobackend.dto.response.MunicipalidadResponse;
import com.turismo.turismobackend.service.MunicipalidadService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class MunicipalidadControllerTest {

    @Mock
    private MunicipalidadService municipalidadService;

    @InjectMocks
    private MunicipalidadController municipalidadController;

    private MunicipalidadResponse muni;
    private List<MunicipalidadResponse> lista;

    @BeforeEach
    void setUp() {
        muni = MunicipalidadResponse.builder()
                .id(1L)
                .nombre("Muni Puno")
                .departamento("Puno")
                .provincia("Puno")
                .distrito("Puno")
                .build();

        lista = List.of(muni);
    }

    // -----------------------------------------------------------
    // 1) GET ALL MUNICIPALIDADES
    // -----------------------------------------------------------
    @Test
    void testGetAllMunicipalidades_ok() {
        Mockito.when(municipalidadService.getAllMunicipalidades()).thenReturn(lista);

        var res = municipalidadController.getAllMunicipalidades();

        Assertions.assertEquals(1, res.getBody().size());
        Assertions.assertEquals("Muni Puno", res.getBody().get(0).getNombre());
    }

    @Test
    void testGetAllMunicipalidades_vacio() {
        Mockito.when(municipalidadService.getAllMunicipalidades()).thenReturn(List.of());

        var res = municipalidadController.getAllMunicipalidades();

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetAllMunicipalidades_null() {
        Mockito.when(municipalidadService.getAllMunicipalidades()).thenReturn(null);

        var res = municipalidadController.getAllMunicipalidades();
        Assertions.assertNull(res.getBody());
    }

    // -----------------------------------------------------------
    // 2) GET BY ID
    // -----------------------------------------------------------
    @Test
    void testGetById_ok() {
        Mockito.when(municipalidadService.getMunicipalidadById(1L)).thenReturn(muni);

        var res = municipalidadController.getMunicipalidadById(1L);

        Assertions.assertEquals("Muni Puno", res.getBody().getNombre());
    }

    @Test
    void testGetById_noEncontrado() {
        Mockito.when(municipalidadService.getMunicipalidadById(999L)).thenReturn(null);

        var res = municipalidadController.getMunicipalidadById(999L);

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testGetById_exception() {
        Mockito.when(municipalidadService.getMunicipalidadById(-1L))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () ->
                municipalidadController.getMunicipalidadById(-1L));
    }

    // -----------------------------------------------------------
    // 3) GET BY DEPARTAMENTO
    // -----------------------------------------------------------
    @Test
    void testGetByDepartamento_ok() {
        Mockito.when(municipalidadService.getMunicipalidadesByDepartamento("Puno"))
                .thenReturn(lista);

        var res = municipalidadController.getMunicipalidadesByDepartamento("Puno");

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetByDepartamento_vacio() {
        Mockito.when(municipalidadService.getMunicipalidadesByDepartamento("Lima"))
                .thenReturn(List.of());

        var res = municipalidadController.getMunicipalidadesByDepartamento("Lima");

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetByDepartamento_null() {
        Mockito.when(municipalidadService.getMunicipalidadesByDepartamento(null))
                .thenReturn(null);

        var res = municipalidadController.getMunicipalidadesByDepartamento(null);

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testGetByDepartamento_exception() {
        Mockito.when(municipalidadService.getMunicipalidadesByDepartamento("Puno"))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.getMunicipalidadesByDepartamento("Puno"));
    }

    // -----------------------------------------------------------
    // 4) GET BY PROVINCIA
    // -----------------------------------------------------------
    @Test
    void testGetByProvincia_ok() {
        Mockito.when(municipalidadService.getMunicipalidadesByProvincia("Puno"))
                .thenReturn(lista);

        var res = municipalidadController.getMunicipalidadesByProvincia("Puno");

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetByProvincia_vacio() {
        Mockito.when(municipalidadService.getMunicipalidadesByProvincia("Juliaca"))
                .thenReturn(List.of());

        var res = municipalidadController.getMunicipalidadesByProvincia("Juliaca");

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetByProvincia_null() {
        Mockito.when(municipalidadService.getMunicipalidadesByProvincia(null))
                .thenReturn(null);

        var res = municipalidadController.getMunicipalidadesByProvincia(null);

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testGetByProvincia_exception() {
        Mockito.when(municipalidadService.getMunicipalidadesByProvincia("Puno"))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.getMunicipalidadesByProvincia("Puno"));
    }

    // -----------------------------------------------------------
    // 5) GET BY DISTRITO
    // -----------------------------------------------------------
    @Test
    void testGetByDistrito_ok() {
        Mockito.when(municipalidadService.getMunicipalidadesByDistrito("Puno"))
                .thenReturn(lista);

        var res = municipalidadController.getMunicipalidadesByDistrito("Puno");

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetByDistrito_vacio() {
        Mockito.when(municipalidadService.getMunicipalidadesByDistrito("Acora"))
                .thenReturn(List.of());

        var res = municipalidadController.getMunicipalidadesByDistrito("Acora");

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetByDistrito_null() {
        Mockito.when(municipalidadService.getMunicipalidadesByDistrito(null))
                .thenReturn(null);

        var res = municipalidadController.getMunicipalidadesByDistrito(null);

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testGetByDistrito_exception() {
        Mockito.when(municipalidadService.getMunicipalidadesByDistrito("Puno"))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.getMunicipalidadesByDistrito("Puno"));
    }

    // -----------------------------------------------------------
    // 6) GET MI MUNICIPALIDAD
    // -----------------------------------------------------------
    @Test
    void testGetMiMunicipalidad_ok() {
        Mockito.when(municipalidadService.getMunicipalidadByUsuario()).thenReturn(muni);

        var res = municipalidadController.getMiMunicipalidad();

        Assertions.assertEquals("Muni Puno", res.getBody().getNombre());
    }

    @Test
    void testGetMiMunicipalidad_null() {
        Mockito.when(municipalidadService.getMunicipalidadByUsuario()).thenReturn(null);

        var res = municipalidadController.getMiMunicipalidad();

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testGetMiMunicipalidad_exception() {
        Mockito.when(municipalidadService.getMunicipalidadByUsuario())
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.getMiMunicipalidad());
    }

    // -----------------------------------------------------------
    // 7) CREAR MUNICIPALIDAD
    // -----------------------------------------------------------
    @Test
    void testCreateMunicipalidad_ok() {
        var req = new MunicipalidadRequest();
        Mockito.when(municipalidadService.createMunicipalidad(req)).thenReturn(muni);

        var res = municipalidadController.createMunicipalidad(req);

        Assertions.assertEquals("Muni Puno", res.getBody().getNombre());
    }

    @Test
    void testCreateMunicipalidad_null() {
        Mockito.when(municipalidadService.createMunicipalidad(Mockito.any()))
                .thenReturn(null);

        var res = municipalidadController.createMunicipalidad(new MunicipalidadRequest());

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testCreateMunicipalidad_exception() {
        Mockito.when(municipalidadService.createMunicipalidad(Mockito.any()))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.createMunicipalidad(new MunicipalidadRequest()));
    }

    // -----------------------------------------------------------
    // 8) UPDATE MUNICIPALIDAD
    // -----------------------------------------------------------
    @Test
    void testUpdateMunicipalidad_ok() {
        var req = new MunicipalidadRequest();

        Mockito.when(municipalidadService.updateMunicipalidad(1L, req)).thenReturn(muni);

        var res = municipalidadController.updateMunicipalidad(1L, req);

        Assertions.assertEquals("Muni Puno", res.getBody().getNombre());
    }

    @Test
    void testUpdateMunicipalidad_null() {
        Mockito.when(municipalidadService.updateMunicipalidad(Mockito.eq(1L), Mockito.any()))
                .thenReturn(null);

        var res = municipalidadController.updateMunicipalidad(1L, new MunicipalidadRequest());

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testUpdateMunicipalidad_exception() {
        Mockito.when(municipalidadService.updateMunicipalidad(Mockito.eq(1L), Mockito.any()))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.updateMunicipalidad(1L, new MunicipalidadRequest()));
    }

    // -----------------------------------------------------------
    // 9) DELETE MUNICIPALIDAD
    // -----------------------------------------------------------
    @Test
    void testDeleteMunicipalidad_ok() {
        Mockito.doNothing().when(municipalidadService).deleteMunicipalidad(1L);

        var res = municipalidadController.deleteMunicipalidad(1L);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }

    @Test
    void testDeleteMunicipalidad_exception() {
        Mockito.doThrow(new RuntimeException()).when(municipalidadService).deleteMunicipalidad(1L);

        Assertions.assertThrows(RuntimeException.class,
                () -> municipalidadController.deleteMunicipalidad(1L));
    }
}
