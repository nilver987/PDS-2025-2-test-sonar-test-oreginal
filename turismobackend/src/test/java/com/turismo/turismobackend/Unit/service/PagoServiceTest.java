package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.PagoRequest;
import com.turismo.turismobackend.dto.response.PagoResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.PagoRepository;
import com.turismo.turismobackend.repository.ReservaRepository;
import com.turismo.turismobackend.service.PagoService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private PagoService pagoService;

    private Usuario usuario;
    private Reserva reserva;
    private Pago pago;

    @BeforeEach
    void setUp() {

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .username("juan")
                .email("juan@test.com")
                .password("123")
                .roles(Set.of(new Rol(1L, Rol.RolNombre.ROLE_USER)))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(usuario, null)
        );

        reserva = Reserva.builder()
                .id(10L)
                .codigoReserva("RES-123")
                .usuario(usuario)
                .plan(PlanTuristico.builder()
                        .id(5L)
                        .usuarioCreador(usuario)
                        .municipalidad(Municipalidad.builder().id(1L).usuario(usuario).build())
                        .build())
                .fechaInicio(LocalDate.now().plusDays(1))
                .fechaFin(LocalDate.now().plusDays(2))
                .montoFinal(BigDecimal.valueOf(200))
                .estado(Reserva.EstadoReserva.PENDIENTE)
                .build();

        pago = Pago.builder()
                .id(50L)
                .reserva(reserva)
                .monto(BigDecimal.valueOf(100))
                .estado(Pago.EstadoPago.PENDIENTE)
                .build();
    }

    // =====================================================================
    // 1. OBTENER PAGO POR ID
    // =====================================================================

    @Test
    @Order(1)
    void testGetPagoById() {
        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));
        PagoResponse res = pagoService.getPagoById(50L);
        assertThat(res).isNotNull();
        assertThat(res.getMonto()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    @Order(2)
    void testGetPagoByIdNotFound() {
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pagoService.getPagoById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 2. REGISTRAR PAGO
    // =====================================================================

    @Test
    @Order(3)
    void testRegistrarPago() {

        PagoRequest req = PagoRequest.builder()
                .reservaId(10L)
                .monto(BigDecimal.valueOf(50))
                .tipo(Pago.TipoPago.PAGO_COMPLETO)
                .metodoPago(Pago.MetodoPago.PAYPAL)
                .build();

        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(pagoRepository.getTotalPagadoByReserva(10L)).thenReturn(100L);

        PagoResponse res = pagoService.registrarPago(req);

        assertThat(res).isNotNull();
        assertThat(res.getMonto()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    @Order(4)
    void testRegistrarPagoReservaNotFound() {

        PagoRequest req = PagoRequest.builder()
                .reservaId(999L)
                .build();

        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.registrarPago(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 3. CONFIRMAR PAGO
    // =====================================================================

    @Test
    @Order(5)
    void testConfirmarPago() {

        pago.setEstado(Pago.EstadoPago.PENDIENTE);

        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(pagoRepository.getTotalPagadoByReserva(10L)).thenReturn(200L);

        PagoResponse res = pagoService.confirmarPago(50L);

        assertThat(res.getEstado()).isEqualTo(Pago.EstadoPago.CONFIRMADO);
    }

    @Test
    @Order(6)
    void testConfirmarPagoNotFound() {
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pagoService.confirmarPago(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 4. RECHAZAR PAGO
    // =====================================================================

    @Test
    @Order(7)
    void testRechazarPago() {

        pago.setEstado(Pago.EstadoPago.PENDIENTE);

        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponse res = pagoService.rechazarPago(50L, "fraude");

        assertThat(res.getEstado()).isEqualTo(Pago.EstadoPago.FALLIDO);
        assertThat(res.getObservaciones()).contains("Motivo rechazo");
    }

    @Test
    @Order(8)
    void testRechazarPagoNotFound() {

        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.rechazarPago(999L, "error"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 5. LISTAR PAGOS POR RESERVA
    // =====================================================================

    @Test
    @Order(9)
    void testGetPagosByReserva() {

        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));
        when(pagoRepository.findByReservaId(10L)).thenReturn(List.of(pago));

        var lista = pagoService.getPagosByReserva(10L);

        assertThat(lista).hasSize(1);
    }

    // =====================================================================
    // NUEVAS 20 PRUEBAS AQUI
    // =====================================================================

    @Test @Order(10)
    void testGetAllPagosAdmin() {
        usuario.setRoles(Set.of(new Rol(9L, Rol.RolNombre.ROLE_ADMIN)));
        when(pagoRepository.findAll()).thenReturn(List.of(pago));
        var lista = pagoService.getAllPagos();
        assertThat(lista).hasSize(1);
    }

    @Test @Order(11)
    void testGetAllPagosNoAdmin() {
        assertThatThrownBy(() -> pagoService.getAllPagos())
                .isInstanceOf(RuntimeException.class);
    }

    @Test @Order(12)
    void testGetPagoByCodigo() {
        when(pagoRepository.findByCodigoPago("ABC")).thenReturn(Optional.of(pago));
        assertThat(pagoService.getPagoByCodigo("ABC")).isNotNull();
    }

    @Test @Order(13)
    void testGetPagoByCodigoNotFound() {
        when(pagoRepository.findByCodigoPago("XXX")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pagoService.getPagoByCodigo("XXX"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @Order(14)
    void testGetMisPagos() {
        when(pagoRepository.findByUsuarioId(1L)).thenReturn(List.of(pago));
        assertThat(pagoService.getMisPagos()).hasSize(1);
    }

    @Test @Order(15)
    void testGetPagosByMunicipalidadAdmin() {
        usuario.setRoles(Set.of(new Rol(9L, Rol.RolNombre.ROLE_ADMIN)));
        when(pagoRepository.findByMunicipalidadId(1L)).thenReturn(List.of(pago));
        assertThat(pagoService.getPagosByMunicipalidad(1L)).hasSize(1);
    }

    @Test @Order(16)
    void testGetPagosByMunicipalidadNoPermiso() {
        assertThatThrownBy(() -> pagoService.getPagosByMunicipalidad(1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test @Order(17)
    void testRegistrarPagoEnReservaCancelada() {

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);

        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));

        PagoRequest req = PagoRequest.builder()
                .reservaId(10L)
                .monto(BigDecimal.TEN)
                .build();

        assertThatThrownBy(() -> pagoService.registrarPago(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cancelada");
    }

    @Test @Order(18)
    void testConfirmarPagoEstadoIncorrecto() {

        pago.setEstado(Pago.EstadoPago.CONFIRMADO);

        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> pagoService.confirmarPago(50L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test @Order(19)
    void testRechazarPagoEstadoIncorrecto() {

        pago.setEstado(Pago.EstadoPago.FALLIDO);

        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> pagoService.rechazarPago(50L, "error"))
                .isInstanceOf(RuntimeException.class);
    }



}
