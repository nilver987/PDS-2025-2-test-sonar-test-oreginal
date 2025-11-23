package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicioPlanRepositoryTest {

    @Autowired
    private ServicioPlanRepository servicioPlanRepository;

    @Autowired
    private PlanTuristicoRepository planTuristicoRepository;

    @Autowired
    private ServicioTuristicoRepository servicioTuristicoRepository;

    @Autowired
    private EmprendedorRepository emprendedorRepository;

    @Autowired
    private MunicipalidadRepository municipalidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long plan1Id;
    private Long plan2Id;
    private Long servicio1Id;
    private Long servicio2Id;
    private Long servicio3Id;
    private Long emprendedorId;

    @BeforeEach
    void setUp() {

        // ===== Usuario creador =====
        Usuario usuario = Usuario.builder()
                .nombre("Admin")
                .apellido("Test")
                .username("admin_test")
                .email("admin@test.com")
                .password("123456")
                .build();
        usuario = usuarioRepository.save(usuario);

        // ===== Municipalidad =====
        Municipalidad muni = Municipalidad.builder()
                .nombre("Municipalidad Test")
                .departamento("Puno")
                .provincia("Puno")
                .distrito("Puno")
                .direccion("Av. Principal 123")
                .telefono("051-123456")
                .build();
        muni = municipalidadRepository.save(muni);

        // ===== Emprendedor =====
        Emprendedor emprendedor = Emprendedor.builder()
                .nombreEmpresa("Aventura Andina")
                .rubro("Turismo de aventura")
                .telefono("999999999")
                .email("emp@test.com")
                .municipalidad(muni)
                .usuario(usuario)
                .build();
        emprendedor = emprendedorRepository.save(emprendedor);
        emprendedorId = emprendedor.getId();

        // ===== Planes turísticos =====
        PlanTuristico plan1 = PlanTuristico.builder()
                .nombre("Plan Selva 3D2N")
                .descripcion("Tour de aventura en la selva")
                .precioTotal(new BigDecimal("500.00"))
                .duracionDias(3)
                .capacidadMaxima(10)
                .estado(PlanTuristico.EstadoPlan.ACTIVO)
                .nivelDificultad(PlanTuristico.NivelDificultad.MODERADO)
                .municipalidad(muni)
                .usuarioCreador(usuario)
                .build();

        PlanTuristico plan2 = PlanTuristico.builder()
                .nombre("Plan Sierra 2D1N")
                .descripcion("Tour cultural en la sierra")
                .precioTotal(new BigDecimal("300.00"))
                .duracionDias(2)
                .capacidadMaxima(15)
                .estado(PlanTuristico.EstadoPlan.ACTIVO)
                .nivelDificultad(PlanTuristico.NivelDificultad.FACIL)
                .municipalidad(muni)
                .usuarioCreador(usuario)
                .build();

        plan1 = planTuristicoRepository.save(plan1);
        plan2 = planTuristicoRepository.save(plan2);
        plan1Id = plan1.getId();
        plan2Id = plan2.getId();

        // ===== Servicios turísticos =====
        ServicioTuristico servicio1 = ServicioTuristico.builder()
                .nombre("Hospedaje ecológico")
                .descripcion("Alojamiento en eco-lodge")
                .precio(new BigDecimal("150.00"))
                .duracionHoras(24)
                .capacidadMaxima(5)
                .tipo(ServicioTuristico.TipoServicio.ALOJAMIENTO)
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .emprendedor(emprendedor)
                .build();

        ServicioTuristico servicio2 = ServicioTuristico.builder()
                .nombre("Tour en bote")
                .descripcion("Paseo en bote por el río")
                .precio(new BigDecimal("80.00"))
                .duracionHoras(4)
                .capacidadMaxima(10)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .emprendedor(emprendedor)
                .build();

        ServicioTuristico servicio3 = ServicioTuristico.builder()
                .nombre("Almuerzo típico")
                .descripcion("Comida regional")
                .precio(new BigDecimal("40.00"))
                .duracionHoras(2)
                .capacidadMaxima(20)
                .tipo(ServicioTuristico.TipoServicio.ALIMENTACION)
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .emprendedor(emprendedor)
                .build();

        servicio1 = servicioTuristicoRepository.save(servicio1);
        servicio2 = servicioTuristicoRepository.save(servicio2);
        servicio3 = servicioTuristicoRepository.save(servicio3);
        servicio1Id = servicio1.getId();
        servicio2Id = servicio2.getId();
        servicio3Id = servicio3.getId();

        // ===== ServicioPlan para plan1 =====
        ServicioPlan sp1 = ServicioPlan.builder()
                .plan(plan1)
                .servicio(servicio1)
                .diaDelPlan(1)
                .ordenEnElDia(1)
                .horaInicio("08:00")
                .horaFin("12:00")
                .precioEspecial(new BigDecimal("140.00"))
                .notas("Incluye desayuno")
                .esOpcional(false)
                .esPersonalizable(false)
                .build();

        ServicioPlan sp2 = ServicioPlan.builder()
                .plan(plan1)
                .servicio(servicio2)
                .diaDelPlan(1)
                .ordenEnElDia(2)
                .horaInicio("14:00")
                .horaFin("18:00")
                .precioEspecial(null)
                .notas("Uso de chalecos salvavidas")
                .esOpcional(true)
                .esPersonalizable(false)
                .build();

        ServicioPlan sp3 = ServicioPlan.builder()
                .plan(plan1)
                .servicio(servicio2)
                .diaDelPlan(2)
                .ordenEnElDia(1)
                .horaInicio("09:00")
                .horaFin("11:00")
                .precioEspecial(new BigDecimal("70.00"))
                .notas("Actividad adicional")
                .esOpcional(true)
                .esPersonalizable(true)
                .build();

        // ===== ServicioPlan para plan2 =====
        ServicioPlan sp4 = ServicioPlan.builder()
                .plan(plan2)
                .servicio(servicio3)
                .diaDelPlan(1)
                .ordenEnElDia(1)
                .horaInicio("13:00")
                .horaFin("15:00")
                .precioEspecial(new BigDecimal("35.00"))
                .notas("Incluye bebida")
                .esOpcional(false)
                .esPersonalizable(false)
                .build();

        servicioPlanRepository.saveAll(List.of(sp1, sp2, sp3, sp4));
    }

    // =====================================================
    // 1) Guardar ServicioPlan
    // =====================================================
    @Test
    @Order(1)
    void testGuardarServicioPlan() {
        long countInicial = servicioPlanRepository.count();

        PlanTuristico plan = planTuristicoRepository.findById(plan1Id).orElseThrow();
        ServicioTuristico servicio = servicioTuristicoRepository.findById(servicio1Id).orElseThrow();

        ServicioPlan nuevo = ServicioPlan.builder()
                .plan(plan)
                .servicio(servicio)
                .diaDelPlan(3)
                .ordenEnElDia(1)
                .esOpcional(false)
                .esPersonalizable(false)
                .build();

        ServicioPlan guardado = servicioPlanRepository.save(nuevo);

        assertNotNull(guardado.getId());
        assertEquals(countInicial + 1, servicioPlanRepository.count());
    }

    // =====================================================
    // 2) Buscar por planId
    // =====================================================
    @Test
    @Order(2)
    void testFindByPlanId() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanId(plan1Id);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(sp -> sp.getPlan().getId().equals(plan1Id)));
    }

    // =====================================================
    // 3) Buscar por servicioId
    // =====================================================
    @Test
    @Order(3)
    void testFindByServicioId() {
        List<ServicioPlan> lista = servicioPlanRepository.findByServicioId(servicio2Id);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(sp -> sp.getServicio().getId().equals(servicio2Id)));
    }

    // =====================================================
    // 4) Buscar ordenado por día y orden en el día
    // =====================================================
    @Test
    @Order(4)
    void testFindByPlanIdOrdered() {
        List<ServicioPlan> lista = servicioPlanRepository
                .findByPlanIdOrderByDiaDelPlanAscOrdenEnElDiaAsc(plan1Id);

        assertFalse(lista.isEmpty());
        // Verificamos que esté ordenado
        for (int i = 1; i < lista.size(); i++) {
            ServicioPlan prev = lista.get(i - 1);
            ServicioPlan curr = lista.get(i);
            if (prev.getDiaDelPlan().equals(curr.getDiaDelPlan())) {
                assertTrue(prev.getOrdenEnElDia() <= curr.getOrdenEnElDia());
            } else {
                assertTrue(prev.getDiaDelPlan() <= curr.getDiaDelPlan());
            }
        }
    }

    // =====================================================
    // 5) Buscar por planId y día
    // =====================================================
    @Test
    @Order(5)
    void testFindByPlanIdAndDia() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanIdAndDiaDelPlan(plan1Id, 1);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(sp ->
                sp.getPlan().getId().equals(plan1Id) && sp.getDiaDelPlan().equals(1)));
    }

    // =====================================================
    // 6) Buscar por esOpcional = true
    // =====================================================
    @Test
    @Order(6)
    void testFindByOpcional() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanIdAndEsOpcional(plan1Id, true);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(ServicioPlan::getEsOpcional));
    }

    // =====================================================
    // 7) Buscar por esPersonalizable = true
    // =====================================================
    @Test
    @Order(7)
    void testFindByPersonalizable() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanIdAndEsPersonalizable(plan1Id, true);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(ServicioPlan::getEsPersonalizable));
    }

    // =====================================================
    // 8) Buscar por planId y emprendedorId (query con @Query)
    // =====================================================
    @Test
    @Order(8)
    void testFindByEmprendedorId() {
        List<ServicioPlan> lista = servicioPlanRepository
                .findByPlanIdAndEmprendedorId(plan1Id, emprendedorId);

        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(sp ->
                sp.getPlan().getId().equals(plan1Id) &&
                        sp.getServicio().getEmprendedor().getId().equals(emprendedorId)));
    }

    // =====================================================
    // 9) Buscar por planId y tipo de servicio (query con @Query)
    // =====================================================
    @Test
    @Order(9)
    void testFindByTipoServicio() {
        List<ServicioPlan> lista = servicioPlanRepository
                .findByPlanIdAndTipoServicio(plan1Id, ServicioTuristico.TipoServicio.TOUR);

        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(sp ->
                sp.getPlan().getId().equals(plan1Id) &&
                        sp.getServicio().getTipo() == ServicioTuristico.TipoServicio.TOUR));
    }

    // =====================================================
    // 10) Eliminar por planId
    // =====================================================
    @Test
    @Order(10)
    void testDeleteByPlanId() {
        long antes = servicioPlanRepository.count();
        servicioPlanRepository.deleteByPlanId(plan2Id);
        long despues = servicioPlanRepository.count();

        assertTrue(despues < antes);

        List<ServicioPlan> lista = servicioPlanRepository.findByPlanId(plan2Id);
        assertTrue(lista.isEmpty());
    }

    // =====================================================
    // 11) Buscar por día inexistente
    // =====================================================
    @Test
    @Order(11)
    void testFindByPlanIdAndDiaSinResultados() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanIdAndDiaDelPlan(plan1Id, 99);
        assertTrue(lista.isEmpty());
    }

    // =====================================================
    // 12) Buscar por emprendedor distinto
    // =====================================================
    @Test
    @Order(12)
    void testFindByPlanIdAndEmprendedorIdSinResultados() {
        List<ServicioPlan> lista = servicioPlanRepository
                .findByPlanIdAndEmprendedorId(plan1Id, 9999L);
        assertTrue(lista.isEmpty());
    }

    // =====================================================
    // 13) Buscar por tipo de servicio que no está en el plan
    // =====================================================
    @Test
    @Order(13)
    void testFindByPlanIdAndTipoServicioSinResultados() {
        List<ServicioPlan> lista = servicioPlanRepository
                .findByPlanIdAndTipoServicio(plan1Id, ServicioTuristico.TipoServicio.ALIMENTACION);
        // En plan1 no usamos ALIMENTACION, solo en plan2
        assertTrue(lista.isEmpty());
    }

    // =====================================================
    // 14) Verificar que precioEspecial puede ser null
    // =====================================================
    @Test
    @Order(14)
    void testPrecioEspecialNullable() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanId(plan1Id);
        assertTrue(lista.stream().anyMatch(sp -> sp.getPrecioEspecial() == null));
    }

    // =====================================================
    // 15) Verificar relaciones @ManyToOne no son nulas
    // =====================================================
    @Test
    @Order(15)
    void testRelacionesPlanYServicioNoNulas() {
        List<ServicioPlan> lista = servicioPlanRepository.findByPlanId(plan1Id);
        assertFalse(lista.isEmpty());

        ServicioPlan sp = lista.get(0);
        assertNotNull(sp.getPlan());
        assertNotNull(sp.getServicio());
        assertNotNull(sp.getServicio().getEmprendedor());
    }
}
