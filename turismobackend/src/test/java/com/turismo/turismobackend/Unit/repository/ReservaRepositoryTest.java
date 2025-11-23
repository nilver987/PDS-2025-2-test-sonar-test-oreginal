package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipalidadRepository municipalidadRepository;

    @Autowired
    private PlanTuristicoRepository planRepository;

    private Usuario usuario;
    private Municipalidad municipalidad;
    private PlanTuristico plan;
    private Reserva reserva;

    @BeforeEach
    void setUp() {

        usuario = usuarioRepository.save(
                Usuario.builder()
                        .nombre("Carlos")
                        .apellido("Test")
                        .username("user" + System.currentTimeMillis())
                        .email("mail" + System.currentTimeMillis() + "@test.com")
                        .password("123456")
                        .build()
        );

        municipalidad = municipalidadRepository.save(
                Municipalidad.builder()
                        .nombre("Muni Test")
                        .departamento("Puno")
                        .provincia("San Román")
                        .distrito("Juliaca")
                        .usuario(usuario)
                        .build()
        );

        plan = planRepository.save(
                PlanTuristico.builder()
                        .nombre("Plan Aventura 3D")
                        .descripcion("Desc")
                        .precioTotal(new BigDecimal("500"))
                        .duracionDias(3)
                        .capacidadMaxima(10)
                        .estado(PlanTuristico.EstadoPlan.ACTIVO)
                        .nivelDificultad(PlanTuristico.NivelDificultad.FACIL)
                        .municipalidad(municipalidad)
                        .usuarioCreador(usuario)
                        .build()
        );

        reserva = reservaRepository.save(
                Reserva.builder()
                        .codigoReserva("RES-TEST")
                        .plan(plan)
                        .usuario(usuario)
                        .fechaInicio(LocalDate.now().plusDays(5))
                        .fechaFin(LocalDate.now().plusDays(7))
                        .numeroPersonas(2)
                        .montoTotal(new BigDecimal("300"))
                        .montoFinal(new BigDecimal("300"))
                        .estado(Reserva.EstadoReserva.PENDIENTE)
                        .metodoPago(Reserva.MetodoPago.EFECTIVO)
                        .build()
        );
    }

    // 1
    @Test @Order(1)
    void testFindByCodigoReserva() {
        var result = reservaRepository.findByCodigoReserva("RES-TEST");
        assertTrue(result.isPresent());
    }

    // 2
    @Test @Order(2)
    void testFindByUsuarioId() {
        List<Reserva> list = reservaRepository.findByUsuarioId(usuario.getId());
        assertEquals(1, list.size());
    }

    // 3
    @Test @Order(3)
    void testFindByPlanId() {
        List<Reserva> list = reservaRepository.findByPlanId(plan.getId());
        assertEquals(1, list.size());
    }

    // 4
    @Test @Order(4)
    void testFindByEstado() {
        List<Reserva> list = reservaRepository.findByEstado(Reserva.EstadoReserva.PENDIENTE);
        assertFalse(list.isEmpty());
    }

    // 5
    @Test @Order(5)
    void testFindByUsuarioIdAndEstado() {
        List<Reserva> list = reservaRepository.findByUsuarioIdAndEstado(
                usuario.getId(),
                Reserva.EstadoReserva.PENDIENTE
        );
        assertEquals(1, list.size());
    }

    // 6
    @Test @Order(6)
    void testFindByPlanIdAndEstado() {
        List<Reserva> list = reservaRepository.findByPlanIdAndEstado(
                plan.getId(),
                Reserva.EstadoReserva.PENDIENTE
        );
        assertEquals(1, list.size());
    }

    // 7
    @Test @Order(7)
    void testFindByFechaInicioBetween() {
        List<Reserva> list = reservaRepository.findByFechaInicioBetween(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );
        assertEquals(1, list.size());
    }

    // 8
    @Test @Order(8)
    void testFindByFechaReservaBetween() {
        List<Reserva> list = reservaRepository.findByFechaReservaBetween(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );
        assertFalse(list.isEmpty());
    }

    // 9
    @Test @Order(9)
    void testFindByMunicipalidadId() {
        List<Reserva> list = reservaRepository.findByMunicipalidadId(municipalidad.getId());
        assertEquals(1, list.size());
    }

    // 10
    @Test @Order(10)
    void testFindByMunicipalidadIdAndEstado() {
        List<Reserva> list = reservaRepository.findByMunicipalidadIdAndEstado(
                municipalidad.getId(),
                Reserva.EstadoReserva.PENDIENTE
        );
        assertEquals(1, list.size());
    }

    // 11
    @Test @Order(11)
    void testFindByFechaInicioAndPlanId() {
        List<Reserva> list = reservaRepository.findByFechaInicioAndPlanId(
                reserva.getFechaInicio(),
                plan.getId()
        );
        assertEquals(1, list.size());
    }

    // 12
    @Test @Order(12)
    void testCountActiveReservasByPlanAndDate() {
        Long count = reservaRepository.countActiveReservasByPlanAndDate(
                plan.getId(),
                reserva.getFechaInicio()
        );
        assertEquals(1, count);
    }

    // 13
    @Test @Order(13)
    void testCountPersonasByPlanAndDate() {
        Long total = reservaRepository.countPersonasByPlanAndDate(
                plan.getId(),
                reserva.getFechaInicio()
        );
        assertEquals(2L, total);
    }

    // 14
    @Test @Order(14)
    void testFindByUsuarioIdOrderByFechaReservaDesc() {
        List<Reserva> list = reservaRepository.findByUsuarioIdOrderByFechaReservaDesc(usuario.getId());
        assertFalse(list.isEmpty());
    }

    // 15
    @Test @Order(15)
    void testCountReservasCompletadasByUsuario() {
        // marcar como completada
        reserva.setEstado(Reserva.EstadoReserva.COMPLETADA);
        reservaRepository.save(reserva);

        Long total = reservaRepository.countReservasCompletadasByUsuario(usuario.getId());
        assertEquals(1L, total);
    }

    // 16
    @Test @Order(16)
    void testGuardarReserva() {
        assertNotNull(reserva.getId());
    }
}
