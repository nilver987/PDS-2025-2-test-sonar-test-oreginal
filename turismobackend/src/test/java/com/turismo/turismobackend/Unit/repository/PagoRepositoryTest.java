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
public class PagoRepositoryTest {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipalidadRepository municipalidadRepository;

    @Autowired
    private PlanTuristicoRepository planRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private Usuario usuario;
    private Municipalidad muni;
    private PlanTuristico plan;
    private Reserva reserva;
    private Pago pago1;
    private Pago pago2;

    @BeforeEach
    void setUp() {

        usuario = usuarioRepository.save(
                Usuario.builder()
                        .nombre("Juan")
                        .apellido("Tester")
                        .username("user" + System.nanoTime())
                        .email("mail" + System.nanoTime() + "@test.com")
                        .password("123456")
                        .build()
        );

        muni = municipalidadRepository.save(
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
                        .nombre("Plan Lago")
                        .descripcion("Tour turístico")
                        .precioTotal(new BigDecimal("500"))
                        .duracionDias(2)
                        .capacidadMaxima(30)
                        .estado(PlanTuristico.EstadoPlan.ACTIVO)
                        .nivelDificultad(PlanTuristico.NivelDificultad.FACIL)
                        .municipalidad(muni)
                        .usuarioCreador(usuario)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        reserva = reservaRepository.save(
                Reserva.builder()
                        .codigoReserva("RES-" + System.nanoTime())
                        .plan(plan)
                        .usuario(usuario)
                        .fechaInicio(LocalDate.now().plusDays(1))
                        .fechaFin(LocalDate.now().plusDays(2))
                        .numeroPersonas(2)
                        .montoTotal(new BigDecimal("500"))
                        .montoFinal(new BigDecimal("500"))
                        .estado(Reserva.EstadoReserva.PAGADA)
                        .fechaReserva(LocalDateTime.now())
                        .build()
        );

        pago1 = pagoRepository.save(
                Pago.builder()
                        .codigoPago("PAGO-" + System.nanoTime())
                        .reserva(reserva)
                        .monto(new BigDecimal("200"))
                        .estado(Pago.EstadoPago.CONFIRMADO)
                        .tipo(Pago.TipoPago.PAGO_PARCIAL)
                        .metodoPago(Pago.MetodoPago.TARJETA_CREDITO)
                        .fechaPago(LocalDateTime.now())
                        .build()
        );

        pago2 = pagoRepository.save(
                Pago.builder()
                        .codigoPago("PAGO-" + (System.nanoTime() + 1))
                        .reserva(reserva)
                        .monto(new BigDecimal("300"))
                        .estado(Pago.EstadoPago.PENDIENTE)
                        .tipo(Pago.TipoPago.SALDO_PENDIENTE)
                        .metodoPago(Pago.MetodoPago.TRANSFERENCIA)
                        .fechaPago(LocalDateTime.now())
                        .build()
        );
    }

    // 1
    @Test @Order(1)
    void testFindByCodigoPago() {
        assertTrue(pagoRepository.findByCodigoPago(pago1.getCodigoPago()).isPresent());
    }

    // 2
    @Test @Order(2)
    void testFindByReservaId() {
        List<Pago> list = pagoRepository.findByReservaId(reserva.getId());
        assertEquals(2, list.size());
    }

    // 3
    @Test @Order(3)
    void testFindByEstado() {
        List<Pago> list = pagoRepository.findByEstado(Pago.EstadoPago.PENDIENTE);
        assertEquals(1, list.size());
    }

    // 4
    @Test @Order(4)
    void testFindByTipo() {
        List<Pago> list = pagoRepository.findByTipo(Pago.TipoPago.PAGO_PARCIAL);
        assertEquals(1, list.size());
    }

    // 5
    @Test @Order(5)
    void testFindByMetodoPago() {
        List<Pago> list = pagoRepository.findByMetodoPago(Pago.MetodoPago.TARJETA_CREDITO);
        assertEquals(1, list.size());
    }

    // 6
    @Test @Order(6)
    void testFindByReservaIdAndEstado() {
        List<Pago> list = pagoRepository.findByReservaIdAndEstado(reserva.getId(), Pago.EstadoPago.CONFIRMADO);
        assertEquals(1, list.size());
    }

    // 7
    @Test @Order(7)
    void testFindByFechaPagoBetween() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);

        List<Pago> list = pagoRepository.findByFechaPagoBetween(inicio, fin);
        assertEquals(2, list.size());
    }

    // 8 - Query por usuario
    @Test @Order(8)
    void testFindByUsuarioId() {
        List<Pago> list = pagoRepository.findByUsuarioId(usuario.getId());
        assertEquals(2, list.size());
    }

    // 9 - Query por municipalidad
    @Test @Order(9)
    void testFindByMunicipalidadId() {
        List<Pago> list = pagoRepository.findByMunicipalidadId(muni.getId());
        assertEquals(2, list.size());
    }

    // 10 - pagos confirmados
    @Test @Order(10)
    void testFindPagosConfirmadosByReserva() {
        List<Pago> list = pagoRepository.findPagosConfirmadosByReserva(reserva.getId());
        assertEquals(1, list.size());
    }

    // 11 - total pagado confirmado
    @Test @Order(11)
    void testGetTotalPagadoByReserva() {
        Long total = pagoRepository.getTotalPagadoByReserva(reserva.getId());
        assertEquals(200L, total);
    }

    // 12
    @Test @Order(12)
    void testGuardarPago() {
        assertNotNull(pago1.getId());
    }

    // 13
    @Test @Order(13)
    void testActualizarPago() {
        pago1.setEstado(Pago.EstadoPago.REEMBOLSADO);
        pagoRepository.save(pago1);

        Pago updated = pagoRepository.findById(pago1.getId()).orElseThrow();
        assertEquals(Pago.EstadoPago.REEMBOLSADO, updated.getEstado());
    }

    // 14
    @Test @Order(14)
    void testEliminarPago() {
        pagoRepository.delete(pago2);
        assertFalse(pagoRepository.findById(pago2.getId()).isPresent());
    }
}
