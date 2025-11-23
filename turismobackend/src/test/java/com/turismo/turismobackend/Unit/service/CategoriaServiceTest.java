package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.CategoriaRequest;
import com.turismo.turismobackend.dto.response.CategoriaResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Categoria;
import com.turismo.turismobackend.repository.CategoriaRepository;
import com.turismo.turismobackend.service.CategoriaService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setup() {
        categoria = Categoria.builder()
                .id(1L)
                .nombre("Aventura")
                .descripcion("Tours extremos")
                .emprendedores(new ArrayList<>())
                .build();
    }

    // ======================================================
    // 1) Obtener todas las categorías vacías
    // ======================================================
    @Test @Order(1)
    void testGetAllCategoriasVacio() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        var res = categoriaService.getAllCategorias();

        assertThat(res).isEmpty();
    }

    // ======================================================
    // 2) Obtener todas con datos
    // ======================================================
    @Test @Order(2)
    void testGetAllCategoriasConDatos() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        var res = categoriaService.getAllCategorias();

        assertThat(res).hasSize(1);
        assertThat(res.get(0).getNombre()).isEqualTo("Aventura");
    }

    // ======================================================
    // 3) Buscar categoría por ID existente
    // ======================================================
    @Test @Order(3)
    void testGetCategoriaByIdOK() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        var res = categoriaService.getCategoriaById(1L);

        assertThat(res.getNombre()).isEqualTo("Aventura");
    }

    // ======================================================
    // 4) Buscar categoría por ID inexistente
    // ======================================================
    @Test @Order(4)
    void testGetCategoriaByIdNoExiste() {
        when(categoriaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.getCategoriaById(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ======================================================
    // 5) Crear categoría correctamente
    // ======================================================
    @Test @Order(5)
    void testCreateCategoriaOK() {
        CategoriaRequest req = new CategoriaRequest("Playas", "Arena y sol");

        when(categoriaRepository.existsByNombre("Playas")).thenReturn(false);

        categoria = Categoria.builder()
                .id(99L)
                .nombre("Playas")
                .descripcion("Arena y sol")
                .emprendedores(new ArrayList<>())
                .build();

        when(categoriaRepository.save(any())).thenReturn(categoria);

        var res = categoriaService.createCategoria(req);

        assertThat(res.getNombre()).isEqualTo("Playas");
    }

    // ======================================================
    // 6) Crear categoría duplicada
    // ======================================================
    @Test @Order(6)
    void testCreateCategoriaDuplicada() {
        CategoriaRequest req = new CategoriaRequest("Aventura", "x");

        when(categoriaRepository.existsByNombre("Aventura")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.createCategoria(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una categoría");
    }

    // ======================================================
    // 7) Actualizar categoría OK
    // ======================================================
    @Test @Order(7)
    void testUpdateCategoriaOK() {
        CategoriaRequest req = new CategoriaRequest("Naturaleza", "Montañas");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Naturaleza")).thenReturn(false);

        var res = categoriaService.updateCategoria(1L, req);

        assertThat(res.getNombre()).isEqualTo("Naturaleza");
        verify(categoriaRepository).save(any());
    }

    // ======================================================
    // 8) Actualizar categoría inexistente
    // ======================================================
    @Test @Order(8)
    void testUpdateCategoriaNoExiste() {
        when(categoriaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoriaService.updateCategoria(5L, new CategoriaRequest("x", "y"))
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    // ======================================================
    // 9) Actualizar categoría duplicando nombre
    // ======================================================
    @Test @Order(9)
    void testUpdateCategoriaNombreDuplicado() {
        CategoriaRequest req = new CategoriaRequest("Montaña", "x");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Montaña")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.updateCategoria(1L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una categoría");
    }

    // ======================================================
    // 10) Eliminar categoría OK
    // ======================================================
    @Test @Order(10)
    void testDeleteCategoriaOK() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.deleteCategoria(1L);

        verify(categoriaRepository).delete(categoria);
    }

    // ======================================================
    // 11) Eliminar categoría inexistente
    // ======================================================
    @Test @Order(11)
    void testDeleteCategoriaNoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.deleteCategoria(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ======================================================
    // 12) Eliminar categoría con emprendedores
    // ======================================================
    @Test @Order(12)
    void testDeleteCategoriaConEmprendedores() {
        categoria.getEmprendedores().add(null); // simular que tiene 1

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatThrownBy(() -> categoriaService.deleteCategoria(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede eliminar");
    }

    // ======================================================
    // 13) Test de mapeo → cantidad de emprendedores = 0
    // ======================================================
    @Test @Order(13)
    void testMapeoSinEmprendedores() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        var res = categoriaService.getCategoriaById(1L);

        assertThat(res.getCantidadEmprendedores()).isEqualTo(0);
    }

    // ======================================================
    // 14) Test de mapeo → cantidad de emprendedores > 0
    // ======================================================
    @Test @Order(14)
    void testMapeoConEmprendedores() {
        categoria.getEmprendedores().add(null); // simular

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        var res = categoriaService.getCategoriaById(1L);

        assertThat(res.getCantidadEmprendedores()).isEqualTo(1);
    }

    // ======================================================
    // 15) Crear categoría: save es llamado
    // ======================================================
    @Test @Order(15)
    void testCreateCategoriaVerificaSave() {
        CategoriaRequest req = new CategoriaRequest("Nueva", "desc");
        when(categoriaRepository.existsByNombre("Nueva")).thenReturn(false);

        categoriaService.createCategoria(req);

        verify(categoriaRepository).save(any());
    }

    // ======================================================
    // 16) Update: verificar save
    // ======================================================
    @Test @Order(16)
    void testUpdateCategoriaSaveLlamado() {
        CategoriaRequest req = new CategoriaRequest("Cambio", "desc");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Cambio")).thenReturn(false);

        categoriaService.updateCategoria(1L, req);

        verify(categoriaRepository).save(any());
    }

    // ======================================================
    // 17) Crear categoría: nombre vacío (debería permitir, no se valida)
    // ======================================================
    @Test @Order(17)
    void testCreateNombreVacio() {
        CategoriaRequest req = new CategoriaRequest("", "desc");

        when(categoriaRepository.existsByNombre("")).thenReturn(false);

        assertThatCode(() -> categoriaService.createCategoria(req))
                .doesNotThrowAnyException();
    }

    // ======================================================
    // 18) Update con nombre igual al actual → no debe fallar
    // ======================================================
    @Test @Order(18)
    void testUpdateMismoNombre() {
        CategoriaRequest req = new CategoriaRequest("Aventura", "nuevo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatCode(() -> categoriaService.updateCategoria(1L, req))
                .doesNotThrowAnyException();
    }

    // ======================================================
    // 19) GetAll sin mapear a null
    // ======================================================
    @Test @Order(19)
    void testGetAllNoNull() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        var list = categoriaService.getAllCategorias();

        assertThat(list).isNotNull();
    }

    // ======================================================
    // 20) Crear categoría: descripción nula
    // ======================================================
    @Test @Order(20)
    void testCreateDescripcionNula() {
        CategoriaRequest req = new CategoriaRequest("Especial", null);

        when(categoriaRepository.existsByNombre("Especial")).thenReturn(false);

        assertThatCode(() -> categoriaService.createCategoria(req))
                .doesNotThrowAnyException();
    }

    // ======================================================
    // 21) Update descripción nula
    // ======================================================
    @Test @Order(21)
    void testUpdateDescripcionNula() {
        CategoriaRequest req = new CategoriaRequest("Aventura", null);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatCode(() -> categoriaService.updateCategoria(1L, req))
                .doesNotThrowAnyException();
    }

    // ======================================================
    // 22) Eliminar → verificar delete()
    // ======================================================
    @Test @Order(22)
    void testDeleteVerificaDelete() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.deleteCategoria(1L);

        verify(categoriaRepository).delete(categoria);
    }

    // ======================================================
    // 23) getCategoriaById retorna nombre correcto
    // ======================================================
    @Test @Order(23)
    void testGetByIdNombreCorrecto() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        var res = categoriaService.getCategoriaById(1L);

        assertThat(res.getNombre()).isEqualTo("Aventura");
    }

    // ======================================================
    // 24) Comprobar que mapeo no retorna null
    // ======================================================
    @Test @Order(24)
    void testMapeoNotNull() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        var res = categoriaService.getCategoriaById(1L);

        assertThat(res).isNotNull();
    }

    // ======================================================
    // 25) Crear categoría con nombre largo
    // ======================================================
    @Test @Order(25)
    void testCreateNombreLargo() {
        String largo = "x".repeat(100);

        CategoriaRequest req = new CategoriaRequest(largo, "desc");

        when(categoriaRepository.existsByNombre(largo)).thenReturn(false);

        assertThatCode(() -> categoriaService.createCategoria(req))
                .doesNotThrowAnyException();
    }

    // ======================================================
    // 26) Update categoría → ID diferente
    // ======================================================
    @Test @Order(26)
    void testUpdateIdDiferente() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Nueva")).thenReturn(false);

        var res = categoriaService.updateCategoria(1L, new CategoriaRequest("Nueva", "x"));

        assertThat(res.getNombre()).isEqualTo("Nueva");
    }

    // ======================================================
    // 27) Eliminar categoría → no debe lanzar excepción válida
    // ======================================================
    @Test @Order(27)
    void testDeleteOKSinExcepciones() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatCode(() -> categoriaService.deleteCategoria(1L))
                .doesNotThrowAnyException();
    }
}
