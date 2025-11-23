package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.ReservaRequest;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import com.turismo.turismobackend.service.ReservaService;

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
import java.time.LocalDate;
import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private PlanTuristicoRepository planRepository;
    @Mock private ServicioPlanRepository servicioPlanRepository;
    @Mock private ReservaServicioRepository reservaServicioRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private PlanTuristico plan;
    private Reserva reserva;

    @BeforeEach
    void setUp() {

        Rol rolUser = Rol.builder()
                .id(1L)
                .nombre(Rol.RolNombre.ROLE_USER)
                .build();

        usuario = Usuario.builder()
                .id(10L)
                .nombre("Juan")
                .apellido("Pérez")
                .username("juan")
                .email("juan@test.com")
                .password("123456")
                .roles(Set.of(rolUser))   // 🔥 ESTA ES LA LÍNEA CORRECTA
                .build();

        plan = PlanTuristico.builder()
                .id(1L)
                .nombre("Tour Puno VIP")
                .precioTotal(BigDecimal.valueOf(150))
                .duracionDias(2)
                .capacidadMaxima(10)
                .estado(PlanTuristico.EstadoPlan.ACTIVO)
                .usuarioCreador(usuario)
                .municipalidad(Municipalidad.builder().id(1L).nombre("Muni Puno").usuario(usuario).build())
                .build();

        reserva = Reserva.builder()
                .id(100L)
                .plan(plan)
                .usuario(usuario)
                .fechaInicio(LocalDate.now())
                .numeroPersonas(2)
                .montoTotal(BigDecimal.valueOf(300))
                .estado(Reserva.EstadoReserva.PENDIENTE)
                .build();

        // 🔥 Mockear usuario autenticado
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
    }

    // ============================================================
    //                     CREAR RESERVA
    // ============================================================
    @Test
    @Order(1)
    @DisplayName("Crear reserva correctamente")
    void testCrearReserva() {

        ReservaRequest request = new ReservaRequest();
        request.setPlanId(1L);
        request.setFechaInicio(LocalDate.now().plusDays(3));
        request.setNumeroPersonas(3);

        given(planRepository.findById(1L)).willReturn(Optional.of(plan));
        given(reservaRepository.countPersonasByPlanAndDate(anyLong(), any())).willReturn(0L);

        given(reservaRepository.save(any(Reserva.class))).willAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(200L);
            return r;
        });

        var response = reservaService.createReserva(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(200L);
        assertThat(response.getNumeroPersonas()).isEqualTo(3);
    }

    @Test
    @Order(2)
    @DisplayName("No crear reserva cuando el plan está INACTIVO")
    void testCrearReservaPlanInactivo() {

        plan.setEstado(PlanTuristico.EstadoPlan.INACTIVO);

        ReservaRequest request = new ReservaRequest();
        request.setPlanId(1L);

        given(planRepository.findById(1L)).willReturn(Optional.of(plan));

        Assertions.assertThatThrownBy(() -> reservaService.createReserva(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no está disponible");
    }



    // ============================================================
    //                     OBTENER RESERVA POR ID
    // ============================================================
    @Test
    @Order(4)
    @DisplayName("Obtener reserva por ID existente")
    void testGetReservaById() {

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));

        var res = reservaService.getReservaById(100L);

        assertThat(res.getId()).isEqualTo(100L);
        assertThat(res.getNumeroPersonas()).isEqualTo(2);
    }



    // ============================================================
    //                     CONFIRMAR RESERVA
    // ============================================================
    @Test
    @Order(6)
    @DisplayName("Confirmar reserva correctamente")
    void testConfirmarReserva() {

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));
        given(reservaRepository.save(reserva)).willReturn(reserva);

        var res = reservaService.confirmarReserva(100L);

        assertThat(res.getEstado().name()).isEqualTo("CONFIRMADA");
    }

    @Test
    @Order(7)
    @DisplayName("No confirmar reserva si no está en estado PENDIENTE")
    void testConfirmarReservaEstadoIncorrecto() {

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));

        Assertions.assertThatThrownBy(() -> reservaService.confirmarReserva(100L))
                .isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    //                     CANCELAR RESERVA
    // ============================================================
    @Test
    @Order(8)
    @DisplayName("Cancelar reserva correctamente")
    void testCancelarReserva() {

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));
        given(reservaRepository.save(any())).willReturn(reserva);

        var res = reservaService.cancelarReserva(100L, "Prueba");

        assertThat(res.getEstado().name()).isEqualTo("CANCELADA");
    }

    @Test
    @Order(9)
    @DisplayName("No cancelar reserva COMPLETADA")
    void testCancelarReservaCompletada() {

        reserva.setEstado(Reserva.EstadoReserva.COMPLETADA);

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));

        Assertions.assertThatThrownBy(() ->
                        reservaService.cancelarReserva(100L, "X"))
                .isInstanceOf(RuntimeException.class);
    }

    // ============================================================
    //                     COMPLETAR RESERVA
    // ============================================================
    @Test
    @Order(10)
    @DisplayName("Completar reserva correctamente")
    void testCompletarReserva() {

        reserva.setEstado(Reserva.EstadoReserva.EN_PROCESO);

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));
        given(reservaRepository.save(any())).willReturn(reserva);

        var res = reservaService.completarReserva(100L);

        assertThat(res.getEstado().name()).isEqualTo("COMPLETADA");
    }

    @Test
    @Order(11)
    @DisplayName("No completar reserva en estado incorrecto")
    void testCompletarReservaEstadoIncorrecto() {

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);

        given(reservaRepository.findById(100L)).willReturn(Optional.of(reserva));

        Assertions.assertThatThrownBy(() -> reservaService.completarReserva(100L))
                .isInstanceOf(RuntimeException.class);
    }
}
