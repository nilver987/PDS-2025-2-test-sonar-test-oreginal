package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.EmprendedorController;
import com.turismo.turismobackend.dto.request.EmprendedorRequest;
import com.turismo.turismobackend.dto.response.EmprendedorResponse;
import com.turismo.turismobackend.service.EmprendedorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class EmprendedorControllerTest {

    @Mock
    private EmprendedorService emprendedorService;

    @InjectMocks
    private EmprendedorController emprendedorController;

    private EmprendedorResponse empResponse;
    private List<EmprendedorResponse> lista;

    @BeforeEach
    void setUp() {
        empResponse = EmprendedorResponse.builder()
                .id(1L)
                .nombreEmpresa("DoomDistribuidores")
                .rubro("Zapatillas")
                .build();

        lista = List.of(empResponse);
    }

    // -----------------------------------------------------------
    // 1) Obtener todos los emprendedores
    // -----------------------------------------------------------
    @Test
    void testGetAllEmprendedores_ok() {
        Mockito.when(emprendedorService.getAllEmprendedores()).thenReturn(lista);

        var response = emprendedorController.getAllEmprendedores();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAllEmprendedores_listaVacia() {
        Mockito.when(emprendedorService.getAllEmprendedores()).thenReturn(List.of());

        var response = emprendedorController.getAllEmprendedores();

        Assertions.assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetAllEmprendedores_serviceNull() {
        Mockito.when(emprendedorService.getAllEmprendedores()).thenReturn(null);

        var response = emprendedorController.getAllEmprendedores();

        Assertions.assertNull(response.getBody());
    }

    // -----------------------------------------------------------
    // 2) Obtener por ID
    // -----------------------------------------------------------
    @Test
    void testGetById_ok() {
        Mockito.when(emprendedorService.getEmprendedorById(1L)).thenReturn(empResponse);

        var response = emprendedorController.getEmprendedorById(1L);

        Assertions.assertEquals("DoomDistribuidores", response.getBody().getNombreEmpresa());
    }

    @Test
    void testGetById_notFound() {
        Mockito.when(emprendedorService.getEmprendedorById(999L)).thenReturn(null);

        var response = emprendedorController.getEmprendedorById(999L);

        Assertions.assertNull(response.getBody());
    }

    @Test
    void testGetById_exception() {
        Mockito.when(emprendedorService.getEmprendedorById(-1L))
                .thenThrow(new RuntimeException("Error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            emprendedorController.getEmprendedorById(-1L);
        });
    }

    // -----------------------------------------------------------
    // 3) Obtener por municipalidad
    // -----------------------------------------------------------
    @Test
    void testGetByMunicipalidad_ok() {
        Mockito.when(emprendedorService.getEmprendedoresByMunicipalidad(10L)).thenReturn(lista);

        var res = emprendedorController.getEmprendedoresByMunicipalidad(10L);

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetByMunicipalidad_vacio() {
        Mockito.when(emprendedorService.getEmprendedoresByMunicipalidad(10L))
                .thenReturn(List.of());

        var res = emprendedorController.getEmprendedoresByMunicipalidad(10L);
        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetByMunicipalidad_exception() {
        Mockito.when(emprendedorService.getEmprendedoresByMunicipalidad(10L))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () -> {
            emprendedorController.getEmprendedoresByMunicipalidad(10L);
        });
    }

    // -----------------------------------------------------------
    // 4) Obtener por rubro
    // -----------------------------------------------------------
    @Test
    void testGetByRubro_ok() {
        Mockito.when(emprendedorService.getEmprendedoresByRubro("Zapatillas"))
                .thenReturn(lista);

        var res = emprendedorController.getEmprendedoresByRubro("Zapatillas");

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetByRubro_vacio() {
        Mockito.when(emprendedorService.getEmprendedoresByRubro("Nada"))
                .thenReturn(List.of());

        var res = emprendedorController.getEmprendedoresByRubro("Nada");

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetByRubro_null() {
        Mockito.when(emprendedorService.getEmprendedoresByRubro(null))
                .thenReturn(null);

        var res = emprendedorController.getEmprendedoresByRubro(null);

        Assertions.assertNull(res.getBody());
    }

    // -----------------------------------------------------------
    // 5) Obtener por categoría
    // -----------------------------------------------------------
    @Test
    void testGetByCategoria_ok() {
        Mockito.when(emprendedorService.getEmprendedoresByCategoria(99L))
                .thenReturn(lista);

        var res = emprendedorController.getEmprendedoresByCategoria(99L);

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetByCategoria_vacio() {
        Mockito.when(emprendedorService.getEmprendedoresByCategoria(99L))
                .thenReturn(List.of());

        var res = emprendedorController.getEmprendedoresByCategoria(99L);

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testGetByCategoria_exception() {
        Mockito.when(emprendedorService.getEmprendedoresByCategoria(99L))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () -> {
            emprendedorController.getEmprendedoresByCategoria(99L);
        });
    }

    // -----------------------------------------------------------
    // 6) Mi emprendedor
    // -----------------------------------------------------------
    @Test
    void testGetMiEmprendedor_ok() {
        Mockito.when(emprendedorService.getEmprendedorByUsuario()).thenReturn(empResponse);

        var res = emprendedorController.getMiEmprendedor();

        Assertions.assertEquals("DoomDistribuidores", res.getBody().getNombreEmpresa());
    }

    @Test
    void testGetMiEmprendedor_null() {
        Mockito.when(emprendedorService.getEmprendedorByUsuario()).thenReturn(null);

        var res = emprendedorController.getMiEmprendedor();

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testGetMiEmprendedor_exception() {
        Mockito.when(emprendedorService.getEmprendedorByUsuario())
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> emprendedorController.getMiEmprendedor());
    }

    // -----------------------------------------------------------
    // 7) Crear emprendedor
    // -----------------------------------------------------------
    @Test
    void testCreate_ok() {
        EmprendedorRequest req = new EmprendedorRequest();
        Mockito.when(emprendedorService.createEmprendedor(req)).thenReturn(empResponse);

        var res = emprendedorController.createEmprendedor(req);

        Assertions.assertEquals(empResponse, res.getBody());
    }

    @Test
    void testCreate_null() {
        Mockito.when(emprendedorService.createEmprendedor(Mockito.any()))
                .thenReturn(null);

        var res = emprendedorController.createEmprendedor(new EmprendedorRequest());

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testCreate_exception() {
        Mockito.when(emprendedorService.createEmprendedor(Mockito.any()))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () ->
                emprendedorController.createEmprendedor(new EmprendedorRequest()));
    }

    // -----------------------------------------------------------
    // 8) Actualizar emprendedor
    // -----------------------------------------------------------
    @Test
    void testUpdate_ok() {
        EmprendedorRequest req = new EmprendedorRequest();
        Mockito.when(emprendedorService.updateEmprendedor(1L, req)).thenReturn(empResponse);

        var res = emprendedorController.updateEmprendedor(1L, req);

        Assertions.assertEquals(empResponse, res.getBody());
    }

    @Test
    void testUpdate_null() {
        Mockito.when(emprendedorService.updateEmprendedor(Mockito.eq(1L), Mockito.any()))
                .thenReturn(null);

        var res = emprendedorController.updateEmprendedor(1L, new EmprendedorRequest());

        Assertions.assertNull(res.getBody());
    }

    @Test
    void testUpdate_exception() {
        Mockito.when(emprendedorService.updateEmprendedor(Mockito.eq(1L), Mockito.any()))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () ->
                emprendedorController.updateEmprendedor(1L, new EmprendedorRequest()));
    }

    // -----------------------------------------------------------
    // 9) Eliminar emprendedor
    // -----------------------------------------------------------
    @Test
    void testDelete_ok() {
        Mockito.doNothing().when(emprendedorService).deleteEmprendedor(1L);

        var res = emprendedorController.deleteEmprendedor(1L);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }

    @Test
    void testDelete_exception() {
        Mockito.doThrow(new RuntimeException()).when(emprendedorService).deleteEmprendedor(1L);

        Assertions.assertThrows(RuntimeException.class,
                () -> emprendedorController.deleteEmprendedor(1L));
    }

    // -----------------------------------------------------------
    // 10) Emprendedores cercanos
    // -----------------------------------------------------------
    @Test
    void testCercanos_ok() {
        Mockito.when(emprendedorService.getEmprendedoresCercanos(1.0, 1.0, 5.0))
                .thenReturn(lista);

        var res = emprendedorController.getEmprendedoresCercanos(1.0, 1.0, 5.0);

        Assertions.assertEquals(1, res.getBody().size());
    }

    @Test
    void testCercanos_vacio() {
        Mockito.when(emprendedorService.getEmprendedoresCercanos(1.0, 1.0, 5.0))
                .thenReturn(List.of());

        var res = emprendedorController.getEmprendedoresCercanos(1.0, 1.0, 5.0);

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testCercanos_radioCero() {
        Mockito.when(emprendedorService.getEmprendedoresCercanos(1.0, 1.0, 0.0))
                .thenReturn(List.of());

        var res = emprendedorController.getEmprendedoresCercanos(1.0, 1.0, 0.0);

        Assertions.assertEquals(0, res.getBody().size());
    }

    @Test
    void testCercanos_exception() {
        Mockito.when(emprendedorService.getEmprendedoresCercanos(1.0, 1.0, 5.0))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class,
                () -> emprendedorController.getEmprendedoresCercanos(1.0, 1.0, 5.0));
    }
}
