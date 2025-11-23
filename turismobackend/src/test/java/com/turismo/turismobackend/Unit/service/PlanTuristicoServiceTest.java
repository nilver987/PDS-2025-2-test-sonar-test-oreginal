package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.PlanTuristicoRequest;
import com.turismo.turismobackend.dto.request.ServicioPlanRequest;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import com.turismo.turismobackend.service.PlanTuristicoService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.lenient;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanTuristicoServiceTest {

    @Mock private PlanTuristicoRepository planRepository;
    @Mock private ServicioTuristicoRepository servicioRepository;
    @Mock private ServicioPlanRepository servicioPlanRepository;
    @Mock private MunicipalidadRepository municipalidadRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks private PlanTuristicoService planService;

    private Usuario usuario;
    private Municipalidad municipalidad;
    private PlanTuristico plan;

    @BeforeEach
    void setUp() {

        Rol rolMuni = Rol.builder()
                .id(1L)
                .nombre(Rol.RolNombre.ROLE_MUNICIPALIDAD)
                .build();

        usuario = Usuario.builder()
                .id(10L)
                .nombre("Carlos")
                .apellido("Huarca")
                .username("cmuni")
                .email("c@test.com")
                .password("123456")
                .roles(Set.of(rolMuni))
                .build();

        municipalidad = Municipalidad.builder()
                .id(1L)
                .nombre("Muni Puno")
                .usuario(usuario)
                .build();

        plan = PlanTuristico.builder()
                .id(100L)
                .nombre("City Tour Puno")
                .descripcion("Visita turística")
                .duracionDias(2)
                .precioTotal(BigDecimal.valueOf(200))
                .capacidadMaxima(10)
                .estado(PlanTuristico.EstadoPlan.ACTIVO)
                .usuarioCreador(usuario)
                .municipalidad(municipalidad)
                .servicios(new ArrayList<>())
                .reservas(new ArrayList<>())
                .build();

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
    }

    // ─────────────────────────────────────────────────────────────
    //                     LISTAR Y CONSULTAR
    // ─────────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Listar todos los planes")
    void testListarPlanes() {
        given(planRepository.findAll()).willReturn(List.of(plan));
        assertThat(planService.getAllPlanes()).hasSize(1);
    }

    @Test @Order(2)
    @DisplayName("Obtener plan por ID existente")
    void testPlanPorID() {
        given(planRepository.findById(100L)).willReturn(Optional.of(plan));
        assertThat(planService.getPlanById(100L).getId()).isEqualTo(100L);
    }

    @Test @Order(3)
    @DisplayName("Error plan no encontrado")
    void testPlanNoEncontrado() {
        given(planRepository.findById(999L)).willReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> planService.getPlanById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @Order(4)
    @DisplayName("Listar por municipalidad")
    void testPlanesPorMunicipalidad() {
        given(planRepository.findByMunicipalidadId(1L)).willReturn(List.of(plan));
        assertThat(planService.getPlanesByMunicipalidad(1L)).hasSize(1);
    }

    @Test @Order(5)
    @DisplayName("Listar por estado")
    void testPlanesPorEstado() {
        given(planRepository.findByEstado(PlanTuristico.EstadoPlan.ACTIVO)).willReturn(List.of(plan));
        assertThat(planService.getPlanesByEstado(PlanTuristico.EstadoPlan.ACTIVO)).hasSize(1);
    }

    @Test
    @Order(6)
    @DisplayName("Listar por dificultad")
    void testPlanesPorDificultad() {
        lenient().when(planRepository.findByNivelDificultad(any()))
                .thenReturn(List.of(plan));

        assertThat(planService.getPlanesByNivelDificultad(
                PlanTuristico.NivelDificultad.DIFICIL
        )).hasSize(1);
    }


    @Test @Order(7)
    @DisplayName("Listar por duración")
    void testPlanesPorDuracion() {
        given(planRepository.findByDuracionDiasBetween(1, 3))
                .willReturn(List.of(plan));
        assertThat(planService.getPlanesByDuracion(1, 3)).hasSize(1);
    }

    @Test @Order(8)
    @DisplayName("Listar por precio")
    void testPlanesPorPrecio() {
        given(planRepository.findByPrecioTotalBetween(BigDecimal.ZERO, BigDecimal.valueOf(300)))
                .willReturn(List.of(plan));
        assertThat(planService.getPlanesByPrecio(BigDecimal.ZERO, BigDecimal.valueOf(300))).hasSize(1);
    }

    @Test @Order(9)
    @DisplayName("Buscar por nombre o descripción")
    void testBuscarTexto() {
        given(planRepository.findByNombreOrDescripcionContaining("city", "city"))
                .willReturn(List.of(plan));
        assertThat(planService.searchPlanes("city")).hasSize(1);
    }

    @Test @Order(10)
    @DisplayName("Obtener mis planes")
    void testMisPlanes() {
        given(planRepository.findByUsuarioCreadorId(10L)).willReturn(List.of(plan));
        assertThat(planService.getMisPlanes()).hasSize(1);
    }

    // ─────────────────────────────────────────────────────────────
    //                        CREAR PLAN
    // ─────────────────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("Crear plan exitosamente")
    void testCrearPlan() {

        ServicioTuristico st = ServicioTuristico.builder()
                .id(1L)
                .precio(BigDecimal.valueOf(50))
                .build();

        ServicioPlanRequest spr = ServicioPlanRequest.builder()
                .servicioId(1L)
                .esOpcional(false)
                .build();

        PlanTuristicoRequest req = PlanTuristicoRequest.builder()
                .nombre("Nuevo plan")
                .municipalidadId(1L)
                .servicios(List.of(spr))
                .duracionDias(1)
                .capacidadMaxima(10)
                .build();

        given(municipalidadRepository.findByUsuarioId(10L)).willReturn(Optional.of(municipalidad));
        given(servicioRepository.findById(1L)).willReturn(Optional.of(st));

        given(planRepository.save(any())).willAnswer(inv -> {
            PlanTuristico p = inv.getArgument(0);
            p.setId(200L);
            return p;
        });

        assertThat(planService.createPlan(req).getId()).isEqualTo(200L);
    }

    // ─────────────────────────────────────────────────────────────
    //                        ACTUALIZAR PLAN
    // ─────────────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("Actualizar plan exitosamente")
    void testActualizarPlan() {

        given(planRepository.findById(100L)).willReturn(Optional.of(plan));

        ServicioTuristico st = ServicioTuristico.builder()
                .id(1L)
                .precio(BigDecimal.valueOf(30))
                .build();

        given(servicioRepository.findById(1L)).willReturn(Optional.of(st));
        given(planRepository.save(any())).willReturn(plan);

        PlanTuristicoRequest req = PlanTuristicoRequest.builder()
                .nombre("Plan actualizado")
                .servicios(List.of(ServicioPlanRequest.builder()
                        .servicioId(1L)
                        .esOpcional(false)
                        .build()))
                .build();

        assertThat(planService.updatePlan(100L, req).getNombre())
                .isEqualTo("Plan actualizado");
    }

    // ─────────────────────────────────────────────────────────────
    //                        ELIMINAR PLAN
    // ─────────────────────────────────────────────────────────────

    @Test @Order(13)
    @DisplayName("Eliminar plan sin reservas activas")
    void testEliminarPlan() {
        given(planRepository.findById(100L)).willReturn(Optional.of(plan));
        willDoNothing().given(planRepository).delete(plan);
        planService.deletePlan(100L);
        verify(planRepository).delete(plan);
    }

    @Test @Order(14)
    @DisplayName("No eliminar plan con reservas activas")
    void testEliminarPlanConReservas() {
        plan.getReservas().add(Reserva.builder()
                .estado(Reserva.EstadoReserva.PENDIENTE)
                .build());

        given(planRepository.findById(100L)).willReturn(Optional.of(plan));

        Assertions.assertThatThrownBy(() -> planService.deletePlan(100L))
                .isInstanceOf(RuntimeException.class);
    }

    // ─────────────────────────────────────────────────────────────
    //                       CAMBIAR ESTADO
    // ─────────────────────────────────────────────────────────────

    @Test @Order(15)
    @DisplayName("Cambiar estado del plan")
    void testCambiarEstado() {
        given(planRepository.findById(100L)).willReturn(Optional.of(plan));
        given(planRepository.save(any())).willReturn(plan);

        assertThat(planService.cambiarEstado(100L, PlanTuristico.EstadoPlan.ACTIVO)
                .getEstado()).isEqualTo(PlanTuristico.EstadoPlan.ACTIVO);
    }
}
