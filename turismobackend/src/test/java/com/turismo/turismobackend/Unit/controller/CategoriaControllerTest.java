package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.CategoriaController;
import com.turismo.turismobackend.dto.request.CategoriaRequest;
import com.turismo.turismobackend.dto.response.CategoriaResponse;
import com.turismo.turismobackend.service.CategoriaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private CategoriaRequest request;
    private CategoriaResponse response;

    @BeforeEach
    void setup() {

        request = CategoriaRequest.builder()
                .nombre("Turismo")
                .descripcion("Servicios turísticos")
                .build();

        response = CategoriaResponse.builder()
                .id(1L)
                .nombre("Turismo")
                .descripcion("Servicios turísticos")
                .cantidadEmprendedores(0)
                .build();
    }

    // ============================================================
    // 1) GET ALL — lista con 1 elemento
    // ============================================================
    @Test
    void testGetAllCategorias_ListaConUnElemento() {
        List<CategoriaResponse> lista = List.of(response);

        BDDMockito.given(categoriaService.getAllCategorias())
                .willReturn(lista);

        ResponseEntity<List<CategoriaResponse>> res = categoriaController.getAllCategorias();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).hasSize(1);

        BDDMockito.then(categoriaService).should().getAllCategorias();
    }

    // ============================================================
    // 2) GET ALL — lista vacía
    // ============================================================
    @Test
    void testGetAllCategorias_ListaVacia() {
        BDDMockito.given(categoriaService.getAllCategorias())
                .willReturn(new ArrayList<>());

        ResponseEntity<List<CategoriaResponse>> res = categoriaController.getAllCategorias();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEmpty();
    }

    // ============================================================
    // 3) GET ALL — null devuelto
    // ============================================================
    @Test
    void testGetAllCategorias_NullRetornado() {
        BDDMockito.given(categoriaService.getAllCategorias()).willReturn(null);

        ResponseEntity<List<CategoriaResponse>> res = categoriaController.getAllCategorias();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNull();
    }

    // ============================================================
    // 4) GET BY ID — éxito
    // ============================================================
    @Test
    void testGetCategoriaById_Exito() {
        BDDMockito.given(categoriaService.getCategoriaById(1L))
                .willReturn(response);

        ResponseEntity<CategoriaResponse> res = categoriaController.getCategoriaById(1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    // ============================================================
    // 5) GET BY ID — null
    // ============================================================
    @Test
    void testGetCategoriaById_NullRetornado() {
        BDDMockito.given(categoriaService.getCategoriaById(1L))
                .willReturn(null);

        ResponseEntity<CategoriaResponse> res = categoriaController.getCategoriaById(1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNull();
    }

    // ============================================================
    // 6) GET BY ID — servicio lanza excepción
    // ============================================================
    @Test
    void testGetCategoriaById_LanzaExcepcion() {
        BDDMockito.given(categoriaService.getCategoriaById(1L))
                .willThrow(new RuntimeException("Error"));

        assertThatThrownBy(() -> categoriaController.getCategoriaById(1L))
                .isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 7) CREATE — éxito
    // ============================================================
    @Test
    void testCreateCategoria_Exito() {
        BDDMockito.given(categoriaService.createCategoria(request))
                .willReturn(response);

        ResponseEntity<CategoriaResponse> res =
                categoriaController.createCategoria(request);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getNombre()).isEqualTo("Turismo");
    }

    // ============================================================
    // 8) CREATE — null returned
    // ============================================================
    @Test
    void testCreateCategoria_Null() {
        BDDMockito.given(categoriaService.createCategoria(request))
                .willReturn(null);

        ResponseEntity<CategoriaResponse> res =
                categoriaController.createCategoria(request);

        assertThat(res.getBody()).isNull();
    }

    // ============================================================
    // 9) CREATE — excepción
    // ============================================================
    @Test
    void testCreateCategoria_Excepcion() {
        BDDMockito.given(categoriaService.createCategoria(request))
                .willThrow(new RuntimeException("Duplicado"));

        assertThatThrownBy(() -> categoriaController.createCategoria(request))
                .isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 10) UPDATE — éxito
    // ============================================================
    @Test
    void testUpdateCategoria_Exito() {
        CategoriaRequest reqUpd = CategoriaRequest.builder()
                .nombre("Modificado")
                .descripcion("Cambio")
                .build();

        CategoriaResponse respUpd = CategoriaResponse.builder()
                .id(1L)
                .nombre("Modificado")
                .descripcion("Cambio")
                .build();

        BDDMockito.given(categoriaService.updateCategoria(1L, reqUpd))
                .willReturn(respUpd);

        ResponseEntity<CategoriaResponse> res =
                categoriaController.updateCategoria(1L, reqUpd);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getNombre()).isEqualTo("Modificado");
    }

    // ============================================================
    // 11) UPDATE — null returned
    // ============================================================
    @Test
    void testUpdateCategoria_NullRetornado() {
        BDDMockito.given(categoriaService.updateCategoria(1L, request))
                .willReturn(null);

        ResponseEntity<CategoriaResponse> res =
                categoriaController.updateCategoria(1L, request);

        assertThat(res.getBody()).isNull();
    }

    // ============================================================
    // 12) UPDATE — lanza excepción
    // ============================================================
    @Test
    void testUpdateCategoria_Excepcion() {
        BDDMockito.given(categoriaService.updateCategoria(1L, request))
                .willThrow(new RuntimeException("Error"));

        assertThatThrownBy(() ->
                categoriaController.updateCategoria(1L, request)
        ).isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 13) DELETE — éxito
    // ============================================================
    @Test
    void testDeleteCategoria_Exito() {
        BDDMockito.willDoNothing().given(categoriaService).deleteCategoria(1L);

        ResponseEntity<Void> res = categoriaController.deleteCategoria(1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    // ============================================================
    // 14) DELETE — lanza excepción
    // ============================================================
    @Test
    void testDeleteCategoria_Excepcion() {
        BDDMockito.willThrow(new RuntimeException("No se puede eliminar"))
                .given(categoriaService).deleteCategoria(1L);

        assertThatThrownBy(() -> categoriaController.deleteCategoria(1L))
                .isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 15) CREATE — request vacío
    // ============================================================
    @Test
    void testCreateCategoria_RequestVacio() {
        CategoriaRequest reqVacio = new CategoriaRequest();

        BDDMockito.given(categoriaService.createCategoria(reqVacio))
                .willReturn(null);

        ResponseEntity<CategoriaResponse> res =
                categoriaController.createCategoria(reqVacio);

        assertThat(res.getBody()).isNull();
    }

    // ============================================================
    // 16) UPDATE — id negativo
    // ============================================================
    @Test
    void testUpdateCategoria_IdNegativo() {
        BDDMockito.given(categoriaService.updateCategoria(-5L, request))
                .willThrow(new RuntimeException("ID inválido"));

        assertThatThrownBy(() ->
                categoriaController.updateCategoria(-5L, request)
        ).isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 17) GET BY ID — ID negativo
    // ============================================================
    @Test
    void testGetCategoriaById_IdNegativo() {
        BDDMockito.given(categoriaService.getCategoriaById(-1L))
                .willThrow(new RuntimeException("ID inválido"));

        assertThatThrownBy(() ->
                categoriaController.getCategoriaById(-1L)
        ).isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 18) GET ALL — múltiples elementos
    // ============================================================
    @Test
    void testGetAllCategorias_Multiples() {
        List<CategoriaResponse> lista = List.of(
                response,
                CategoriaResponse.builder()
                        .id(2L)
                        .nombre("Gastronomía")
                        .descripcion("Comida")
                        .build()
        );

        BDDMockito.given(categoriaService.getAllCategorias())
                .willReturn(lista);

        ResponseEntity<List<CategoriaResponse>> res =
                categoriaController.getAllCategorias();

        assertThat(res.getBody()).hasSize(2);
    }

    // ============================================================
    // 19) CREATE — nombre nulo
    // ============================================================
    @Test
    void testCreateCategoria_NombreNulo() {
        CategoriaRequest req = new CategoriaRequest();
        req.setNombre(null);

        BDDMockito.given(categoriaService.createCategoria(req))
                .willThrow(new RuntimeException("Nombre requerido"));

        assertThatThrownBy(() ->
                categoriaController.createCategoria(req)
        ).isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 20) UPDATE — descripción nula
    // ============================================================
    @Test
    void testUpdateCategoria_DescripcionNula() {
        CategoriaRequest req = CategoriaRequest.builder()
                .nombre("X")
                .descripcion(null)
                .build();

        BDDMockito.given(categoriaService.updateCategoria(1L, req))
                .willThrow(new RuntimeException("Descripción inválida"));

        assertThatThrownBy(() ->
                categoriaController.updateCategoria(1L, req)
        ).isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 21) DELETE — id nulo simulando error
    // ============================================================
    @Test
    void testDeleteCategoria_IdNulo() {
        BDDMockito.willThrow(new RuntimeException("ID nulo"))
                .given(categoriaService).deleteCategoria(null);

        assertThatThrownBy(() ->
                categoriaController.deleteCategoria(null)
        ).isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 22) GET ALL — servicio lanza excepción
    // ============================================================
    @Test
    void testGetAllCategorias_Excepcion() {
        BDDMockito.given(categoriaService.getAllCategorias())
                .willThrow(new RuntimeException("Error crítico"));

        assertThatThrownBy(() ->
                categoriaController.getAllCategorias()
        ).isInstanceOf(RuntimeException.class);
    }
}
