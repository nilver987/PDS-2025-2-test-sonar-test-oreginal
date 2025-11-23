package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanTuristicoRepositoryTest {

    @Autowired
    private PlanTuristicoRepository planRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipalidadRepository municipalidadRepository;

    @Autowired
    private ServicioTuristicoRepository servicioRepository;

    @Autowired
    private EmprendedorRepository emprendedorRepository;

    private Usuario usuario;
    private Municipalidad muni;
    private PlanTuristico plan;
    private Emprendedor emprendedor;
    private ServicioTuristico servicio;

    @BeforeEach
    void setUp() {

        usuario = usuarioRepository.save(
                Usuario.builder()
                        .nombre("Carlos")
                        .apellido("Tester")
                        .username("user" + System.nanoTime())
                        .email("mail" + System.nanoTime() + "@test.com")
                        .password("123456")
                        .build()
        );

        muni = municipalidadRepository.save(
                Municipalidad.builder()
                        .nombre("Municipalidad Test")
                        .departamento("Puno")
                        .provincia("San Román")
                        .distrito("Juliaca")
                        .usuario(usuario)
                        .build()
        );

        plan = planRepository.save(
                PlanTuristico.builder()
                        .nombre("Plan Titicaca")
                        .descripcion("Tour completo")
                        .precioTotal(new BigDecimal("299.99"))
                        .duracionDias(3)
                        .capacidadMaxima(20)
                        .estado(PlanTuristico.EstadoPlan.ACTIVO)
                        .nivelDificultad(PlanTuristico.NivelDificultad.MODERADO)
                        .fechaCreacion(LocalDateTime.now())
                        .municipalidad(muni)
                        .usuarioCreador(usuario)
                        .build()
        );

        // Crear un emprendedor + servicio
        emprendedor = emprendedorRepository.save(
                Emprendedor.builder()
                        .nombreEmpresa("Tours SRL")
                        .rubro("Turismo")
                        .municipalidad(muni)
                        .usuario(usuario)
                        .build()
        );

        servicio = servicioRepository.save(
                ServicioTuristico.builder()
                        .nombre("Servicio Lancha")
                        .descripcion("Lancha rápida")
                        .precio(new BigDecimal("50"))
                        .duracionHoras(1)
                        .capacidadMaxima(10)
                        .tipo(ServicioTuristico.TipoServicio.TRANSPORTE)
                        .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                        .emprendedor(emprendedor)
                        .build()
        );
    }

    // 1
    @Test @Order(1)
    void testFindByMunicipalidadId() {
        List<PlanTuristico> list = planRepository.findByMunicipalidadId(muni.getId());
        assertEquals(1, list.size());
    }

    // 2
    @Test @Order(2)
    void testFindByUsuarioCreadorId() {
        List<PlanTuristico> list = planRepository.findByUsuarioCreadorId(usuario.getId());
        assertEquals(1, list.size());
    }

    // 3
    @Test @Order(3)
    void testFindByEstado() {
        List<PlanTuristico> list = planRepository.findByEstado(PlanTuristico.EstadoPlan.ACTIVO);
        assertFalse(list.isEmpty());
    }

    // 4
    @Test @Order(4)
    void testFindByNivelDificultad() {
        List<PlanTuristico> list = planRepository.findByNivelDificultad(PlanTuristico.NivelDificultad.MODERADO);
        assertEquals(1, list.size());
    }

    // 5
    @Test @Order(5)
    void testFindByDuracionDiasBetween() {
        List<PlanTuristico> list = planRepository.findByDuracionDiasBetween(1, 5);
        assertEquals(1, list.size());
    }

    // 6
    @Test @Order(6)
    void testFindByPrecioTotalBetween() {
        List<PlanTuristico> list = planRepository.findByPrecioTotalBetween(
                new BigDecimal("100"), new BigDecimal("500")
        );
        assertEquals(1, list.size());
    }

    // 7
    @Test @Order(7)
    void testFindByCapacidadMaximaGreaterThanEqual() {
        List<PlanTuristico> list = planRepository.findByCapacidadMaximaGreaterThanEqual(10);
        assertEquals(1, list.size());
    }

    // 8
    @Test @Order(8)
    void testFindByNombreOrDescripcionContaining() {
        List<PlanTuristico> list = planRepository.findByNombreOrDescripcionContaining("Titicaca", "Tour");
        assertEquals(1, list.size());
    }

    // 9
    @Test @Order(9)
    void testFindByMunicipalidadAndEstado() {
        List<PlanTuristico> list = planRepository.findByMunicipalidadAndEstado(
                muni.getId(),
                PlanTuristico.EstadoPlan.ACTIVO
        );
        assertEquals(1, list.size());
    }

    // 10 - requiere agregar servicio-plan
    @Test @Order(10)
    void testFindByTipoServicio() {

        // ServicioPlan real
        ServicioPlan sp = ServicioPlan.builder()
                .plan(plan)
                .servicio(servicio)
                .diaDelPlan(1)
                .ordenEnElDia(1)
                .esOpcional(false)
                .esPersonalizable(false)
                .build();

        plan.getServicios().add(sp);
        planRepository.save(plan);

        List<PlanTuristico> list = planRepository.findByTipoServicio(ServicioTuristico.TipoServicio.TRANSPORTE);
        assertEquals(1, list.size());
    }

    // 11
    @Test @Order(11)
    void testFindMostPopular_EmptyInitially() {
        List<PlanTuristico> list = planRepository.findMostPopular();
        assertTrue(list.isEmpty());
    }

    // 12 - probar popularidad
    @Test @Order(12)
    void testFindMostPopular() {

        // Crear reservita falsa
        Reserva reserva = Reserva.builder()
                .codigoReserva("R-" + System.nanoTime())
                .plan(plan)
                .usuario(usuario)
                .fechaInicio(LocalDateTime.now().plusDays(1).toLocalDate())
                .fechaFin(LocalDateTime.now().plusDays(2).toLocalDate())
                .numeroPersonas(2)
                .montoTotal(new BigDecimal("200"))
                .montoFinal(new BigDecimal("200"))
                .estado(Reserva.EstadoReserva.CONFIRMADA)
                .fechaReserva(LocalDateTime.now())
                .build();

        plan.getReservas().add(reserva);
        planRepository.save(plan);

        List<PlanTuristico> list = planRepository.findMostPopular();
        assertEquals(1, list.size());
    }

    // 13
    @Test @Order(13)
    void testGuardarPlan() {
        assertNotNull(plan.getId());
    }

    // 14
    @Test @Order(14)
    void testActualizarPlan() {
        plan.setNombre("Nuevo Plan");
        planRepository.save(plan);

        PlanTuristico updated = planRepository.findById(plan.getId()).orElseThrow();
        assertEquals("Nuevo Plan", updated.getNombre());
    }

    // 15
    @Test @Order(15)
    void testListarTodos() {
        List<PlanTuristico> list = planRepository.findAll();
        assertEquals(1, list.size());
    }

    // 16
    @Test @Order(16)
    void testEliminarPlan() {
        planRepository.delete(plan);
        assertFalse(planRepository.findById(plan.getId()).isPresent());
    }

    // 17
    @Test @Order(17)
    void testNombreLikeNoMatch() {
        List<PlanTuristico> list = planRepository.findByNombreOrDescripcionContaining("xxx", "yyy");
        assertTrue(list.isEmpty());
    }

    // 18
    @Test @Order(18)
    void testDuracionOutsideRange() {
        List<PlanTuristico> list = planRepository.findByDuracionDiasBetween(10, 20);
        assertTrue(list.isEmpty());
    }
}
