package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.ServicioTuristicoRequest;
import com.turismo.turismobackend.dto.response.ServicioTuristicoResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.ServicioTuristicoRepository;
import com.turismo.turismobackend.service.ServicioTuristicoService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicioTuristicoServiceTest {

    @Mock
    private ServicioTuristicoRepository servicioRepository;

    @Mock
    private EmprendedorRepository emprendedorRepository;

    @InjectMocks
    private ServicioTuristicoService servicioService;

    private Usuario usuario;
    private Emprendedor emprendedor;
    private ServicioTuristico servicio;

    @BeforeEach
    void setup() {
        // Simular usuario autenticado
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
        );

        // Emprendedor
        emprendedor = new Emprendedor();
        emprendedor.setId(10L);
        emprendedor.setUsuario(usuario);
        emprendedor.setNombreEmpresa("Turismo Juan");
        emprendedor.setMunicipalidad(new Municipalidad());
        emprendedor.getMunicipalidad().setId(100L);
        emprendedor.getMunicipalidad().setNombre("Puno");

        // Servicio de ejemplo
        servicio = new ServicioTuristico();
        servicio.setId(5L);
        servicio.setNombre("Tour Lago Titicaca");
        servicio.setDescripcion("Un tour increíble");
        servicio.setPrecio(BigDecimal.valueOf(150));
        servicio.setDuracionHoras(5);
        servicio.setCapacidadMaxima(20);
        servicio.setTipo(ServicioTuristico.TipoServicio.TOUR);
        servicio.setEstado(ServicioTuristico.EstadoServicio.ACTIVO);
        servicio.setLatitud(-15.84);
        servicio.setLongitud(-70.02);
        servicio.setEmprendedor(emprendedor);
    }

    // ============================================================
    // 1) LISTAR TODOS
    // ============================================================
    @Test
    @Order(1)
    void testGetAllServicios() {
        given(servicioRepository.findAll()).willReturn(List.of(servicio));

        List<ServicioTuristicoResponse> lista = servicioService.getAllServicios();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombre()).isEqualTo("Tour Lago Titicaca");
    }

    // ============================================================
    // 2) BUSCAR POR ID
    // ============================================================
    @Test
    @Order(2)
    void testGetServicioById() {
        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));

        ServicioTuristicoResponse res = servicioService.getServicioById(5L);
        assertThat(res.getNombre()).isEqualTo("Tour Lago Titicaca");
    }

    // ============================================================
    // 3) BUSCAR POR ID - NO EXISTE
    // ============================================================
    @Test
    @Order(3)
    void testGetServicioByIdNotFound() {
        given(servicioRepository.findById(50L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.getServicioById(50L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ============================================================
    // 4) LISTAR POR EMPRENDEDOR
    // ============================================================
    @Test
    @Order(4)
    void testGetByEmprendedor() {
        given(servicioRepository.findByEmprendedorId(10L)).willReturn(List.of(servicio));

        List<ServicioTuristicoResponse> lista = servicioService.getServiciosByEmprendedor(10L);

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 5) LISTAR POR MUNICIPALIDAD
    // ============================================================
    @Test
    @Order(5)
    void testGetByMunicipalidad() {
        given(servicioRepository.findByEmprendedorMunicipalidadId(100L)).willReturn(List.of(servicio));

        List<ServicioTuristicoResponse> lista = servicioService.getServiciosByMunicipalidad(100L);

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 6) LISTAR POR TIPO
    // ============================================================
    @Test
    @Order(6)
    void testGetByTipo() {
        given(servicioRepository.findByTipo(ServicioTuristico.TipoServicio.TOUR)).willReturn(List.of(servicio));

        var lista = servicioService.getServiciosByTipo(ServicioTuristico.TipoServicio.TOUR);

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 7) LISTAR POR ESTADO
    // ============================================================
    @Test
    @Order(7)
    void testGetByEstado() {
        given(servicioRepository.findByEstado(ServicioTuristico.EstadoServicio.ACTIVO)).willReturn(List.of(servicio));

        var lista = servicioService.getServiciosByEstado(ServicioTuristico.EstadoServicio.ACTIVO);

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 8) LISTAR POR PRECIO
    // ============================================================
    @Test
    @Order(8)
    void testGetByPrecio() {
        given(servicioRepository.findByPrecioBetween(BigDecimal.ZERO, BigDecimal.valueOf(200)))
                .willReturn(List.of(servicio));

        var lista = servicioService.getServiciosByPrecio(BigDecimal.ZERO, BigDecimal.valueOf(200));

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 9) BUSCAR TEXTO
    // ============================================================
    @Test
    @Order(9)
    void testBuscar() {
        given(servicioRepository.findByNombreOrDescripcionContaining("Tour", "Tour"))
                .willReturn(List.of(servicio));

        var lista = servicioService.searchServicios("Tour");

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 10) MIS SERVICIOS
    // ============================================================
    @Test
    @Order(10)
    void testGetMisServicios() {
        given(emprendedorRepository.findByUsuarioId(1L)).willReturn(Optional.of(emprendedor));
        given(servicioRepository.findByEmprendedorId(10L)).willReturn(List.of(servicio));

        var lista = servicioService.getMisServicios();

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    // 11) MIS SERVICIOS - NO TIENE EMPRENDEDOR
    // ============================================================
    @Test
    @Order(11)
    void testMisServicios_NotFound() {
        given(emprendedorRepository.findByUsuarioId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.getMisServicios())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ============================================================
    // 12) CREAR SERVICIO
    // ============================================================
    @Test
    @Order(12)
    void testCreateServicio() {
        ServicioTuristicoRequest req = ServicioTuristicoRequest.builder()
                .nombre("Kayak")
                .descripcion("Aventura en kayak")
                .precio(BigDecimal.valueOf(80))
                .duracionHoras(2)
                .capacidadMaxima(10)
                .tipo(ServicioTuristico.TipoServicio.AVENTURA)
                .latitud(-15.8)
                .longitud(-70.0)
                .build();

        given(emprendedorRepository.findByUsuarioId(1L)).willReturn(Optional.of(emprendedor));
        given(servicioRepository.save(any())).willReturn(servicio);

        ServicioTuristicoResponse res = servicioService.createServicio(req);

        assertThat(res).isNotNull();
    }

    // ============================================================
    // 13) CREAR SERVICIO - SIN EMPRENDEDOR
    // ============================================================
    @Test
    @Order(13)
    void testCreateServicio_NoEmprendedor() {
        ServicioTuristicoRequest req = ServicioTuristicoRequest.builder()
                .nombre("Kayak")
                .precio(BigDecimal.valueOf(80))
                .duracionHoras(2)
                .capacidadMaxima(10)
                .tipo(ServicioTuristico.TipoServicio.AVENTURA)
                .build();

        given(emprendedorRepository.findByUsuarioId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.createServicio(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ============================================================
    // 14) ACTUALIZAR SERVICIO
    // ============================================================
    @Test
    @Order(14)
    void testUpdateServicio() {
        ServicioTuristicoRequest req = ServicioTuristicoRequest.builder()
                .nombre("Nuevo nombre")
                .precio(BigDecimal.valueOf(200))
                .duracionHoras(3)
                .capacidadMaxima(15)
                .tipo(ServicioTuristico.TipoServicio.CULTURAL)
                .latitud(10.0)
                .longitud(20.0)
                .build();

        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));
        given(servicioRepository.save(any())).willReturn(servicio);

        ServicioTuristicoResponse res = servicioService.updateServicio(5L, req);

        assertThat(res.getNombre()).isEqualTo("Nuevo nombre");
    }

    // ============================================================
    // 15) ACTUALIZAR - NO ES SU SERVICIO
    // ============================================================
    @Test
    @Order(15)
    void testUpdate_NoPermiso() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(99L);
        servicio.getEmprendedor().setUsuario(otroUsuario);

        ServicioTuristicoRequest req = ServicioTuristicoRequest.builder()
                .nombre("Test")
                .precio(BigDecimal.valueOf(10))
                .duracionHoras(1)
                .capacidadMaxima(5)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .build();

        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));

        assertThatThrownBy(() -> servicioService.updateServicio(5L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tiene permisos");
    }

    // ============================================================
    // 16) ELIMINAR SERVICIO
    // ============================================================
    @Test
    @Order(16)
    void testDeleteServicio() {
        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));
        doNothing().when(servicioRepository).delete(servicio);

        servicioService.deleteServicio(5L);

        verify(servicioRepository, times(1)).delete(servicio);
    }

    // ============================================================
    // 17) ELIMINAR - NO ES SU SERVICIO
    // ============================================================
    @Test
    @Order(17)
    void testDelete_NoPermiso() {
        Usuario otro = new Usuario();
        otro.setId(99L);
        servicio.getEmprendedor().setUsuario(otro);

        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));

        assertThatThrownBy(() -> servicioService.deleteServicio(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tiene permisos");
    }

    // ============================================================
    // 18) CAMBIAR ESTADO
    // ============================================================
    @Test
    @Order(18)
    void testCambiarEstado() {
        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));
        given(servicioRepository.save(any())).willReturn(servicio);

        ServicioTuristicoResponse res =
                servicioService.cambiarEstado(5L, ServicioTuristico.EstadoServicio.INACTIVO);

        assertThat(res.getEstado()).isEqualTo(ServicioTuristico.EstadoServicio.INACTIVO);
    }

    // ============================================================
    // 19) CAMBIAR ESTADO SIN PERMISOS
    // ============================================================
    @Test
    @Order(19)
    void testCambiarEstado_NoPermiso() {
        Usuario otro = new Usuario();
        otro.setId(77L);
        servicio.getEmprendedor().setUsuario(otro);

        given(servicioRepository.findById(5L)).willReturn(Optional.of(servicio));

        assertThatThrownBy(() ->
                servicioService.cambiarEstado(5L, ServicioTuristico.EstadoServicio.INACTIVO))
                .isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    // 20) SERVICIOS CERCANOS
    // ============================================================
    @Test
    @Order(20)
    void testServiciosCercanos() {
        given(servicioRepository.findAll()).willReturn(List.of(servicio));

        List<ServicioTuristicoResponse> lista =
                servicioService.getServiciosCercanos(-15.84, -70.02, 5.0);

        assertThat(lista).hasSize(1);
    }
}
