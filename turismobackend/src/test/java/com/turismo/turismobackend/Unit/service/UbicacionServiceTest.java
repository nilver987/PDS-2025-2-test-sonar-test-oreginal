package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.UbicacionRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.ServicioTuristicoRepository;
import com.turismo.turismobackend.repository.MunicipalidadRepository;
import com.turismo.turismobackend.service.UbicacionService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UbicacionServiceTest {

    @Mock
    private EmprendedorRepository emprendedorRepository;

    @Mock
    private ServicioTuristicoRepository servicioRepository;

    @Mock
    private MunicipalidadRepository municipalidadRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UbicacionService ubicacionService;

    private Usuario usuario;
    private Emprendedor empr1, empr2;
    private ServicioTuristico serv1, serv2;

    @BeforeEach
    void setUp() {

        // Simular usuario autenticado
        usuario = Usuario.builder()
                .id(10L)
                .nombre("Juan")
                .apellido("Pérez")
                .username("juan")
                .password("123")
                .roles(Set.of(
                        Rol.builder().id(1L)
                                .nombre(Rol.RolNombre.ROLE_USER).build()
                ))
                .build();

        when(authentication.getPrincipal()).thenReturn(usuario);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Emprendedor 1
        empr1 = new Emprendedor();
        empr1.setId(1L);
        empr1.setNombreEmpresa("Aventura SAC");
        empr1.setRubro("Aventura");
        empr1.setLatitud(-15.5);
        empr1.setLongitud(-70.5);
        empr1.setUsuario(usuario);
        empr1.setMunicipalidad(new Municipalidad());
        empr1.setCategoria(new Categoria());

        // Emprendedor 2 sin ubicación
        empr2 = new Emprendedor();
        empr2.setId(2L);
        empr2.setNombreEmpresa("Gastronomía SAC");
        empr2.setLatitud(null);
        empr2.setLongitud(null);
        empr2.setUsuario(usuario);
        empr2.setMunicipalidad(new Municipalidad());
        empr2.setCategoria(new Categoria());

        // Servicio 1
        serv1 = new ServicioTuristico();
        serv1.setId(1L);
        serv1.setNombre("Canotaje");
        serv1.setDescripcion("Aventura extrema");
        serv1.setPrecio(new BigDecimal("150.00"));
        serv1.setDuracionHoras(4);
        serv1.setCapacidadMaxima(10);
        serv1.setTipo(ServicioTuristico.TipoServicio.AVENTURA);
        serv1.setEstado(ServicioTuristico.EstadoServicio.ACTIVO);
        serv1.setLatitud(-15.51);
        serv1.setLongitud(-70.49);
        serv1.setEmprendedor(empr1);

        // Servicio 2 sin ubicación
        serv2 = new ServicioTuristico();
        serv2.setId(2L);
        serv2.setNombre("Tour gastronómico");
        serv2.setLatitud(null);
        serv2.setLongitud(null);
        serv2.setEmprendedor(empr1);
    }


    @Test
    @Order(6)
    void testActualizarUbicacionEmprendedor() {

        UbicacionRequest request = new UbicacionRequest(-15.5, -70.5, "Calle A 123");

        given(emprendedorRepository.findById(1L)).willReturn(Optional.of(empr1));
        given(emprendedorRepository.save(empr1)).willReturn(empr1);

        var result = ubicacionService.actualizarUbicacionEmprendedor(1L, request);

        assertThat(result.getLatitud()).isEqualTo(-15.5);
    }

    // ============================================================================
    // 7) Actualizar ubicación de emprendedor - no propietario
    // ============================================================================
    @Test
    @Order(7)
    void testActualizarUbicacionEmprendedorSinPermiso() {

        Usuario otro = Usuario.builder().id(99L).username("otro").roles(Set.of()).build();
        empr1.setUsuario(otro);

        UbicacionRequest req = new UbicacionRequest(-12.0, -77.0, "Av X");

        given(emprendedorRepository.findById(1L)).willReturn(Optional.of(empr1));

        assertThatThrownBy(() -> ubicacionService.actualizarUbicacionEmprendedor(1L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("permisos");
    }

    // ============================================================================
    // 8) Actualizar ubicación Servicio - éxito
    // ============================================================================
    @Test
    @Order(8)
    void testActualizarUbicacionServicio() {

        UbicacionRequest request = new UbicacionRequest(-15.4, -70.4, null);

        given(servicioRepository.findById(1L)).willReturn(Optional.of(serv1));
        given(servicioRepository.save(serv1)).willReturn(serv1);

        var result = ubicacionService.actualizarUbicacionServicio(1L, request);

        assertThat(result.getLatitud()).isEqualTo(-15.4);
    }

    // ============================================================================
    // 9) Actualizar ubicación servicio sin permisos
    // ============================================================================
    @Test
    @Order(9)
    void testActualizarUbicacionServicioSinPermisos() {

        Usuario otro = Usuario.builder().id(50L).username("otro").roles(Set.of()).build();
        serv1.getEmprendedor().setUsuario(otro);

        UbicacionRequest request = new UbicacionRequest(-10.0, -75.0, null);

        given(servicioRepository.findById(1L)).willReturn(Optional.of(serv1));

        assertThatThrownBy(() -> ubicacionService.actualizarUbicacionServicio(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("permisos");
    }

    // ============================================================================
    // 10) Validar coordenadas correctas
    // ============================================================================

}
