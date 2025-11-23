package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.CarritoItemRequest;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import com.turismo.turismobackend.service.CarritoService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private CarritoItemRepository carritoItemRepository;
    @Mock private ServicioTuristicoRepository servicioRepository;

    @InjectMocks
    private CarritoService carritoService;

    private Usuario usuario;
    private Carrito carrito;
    private CarritoItem item;
    private ServicioTuristico servicio;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder().id(1L).build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);
        var ctx = mock(org.springframework.security.core.context.SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        carrito = Carrito.builder()
                .id(10L)
                .usuario(usuario)
                .items(new ArrayList<>())
                .build();

        servicio = ServicioTuristico.builder()
                .id(50L)
                .nombre("Tour Lago")
                .precio(BigDecimal.valueOf(100))
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .emprendedor(new Emprendedor())
                .build();

        item = CarritoItem.builder()
                .id(200L)
                .carrito(carrito)
                .servicio(servicio)
                .cantidad(1)
                .precioUnitario(BigDecimal.valueOf(100))
                .fechaServicio(LocalDate.now())
                .build();
    }

    // ===============================================================
    // 1) Obtener carrito (se crea si no existe)
    // ===============================================================
    @Test @Order(1)
    void testObtenerCarritoSeCreaSiNoExiste() {
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.empty());
        when(carritoRepository.save(any())).thenReturn(carrito);

        var res = carritoService.obtenerCarrito();

        assertThat(res.getId()).isEqualTo(10L);
        verify(carritoRepository).save(any());
    }

    // ===============================================================
    // 2) Agregar item nuevo
    // ===============================================================
    @Test @Order(2)
    void testAgregarItemNuevo() {
        CarritoItemRequest req = new CarritoItemRequest(50L, 2, LocalDate.now(), null);

        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(servicioRepository.findById(50L)).thenReturn(Optional.of(servicio));
        when(carritoItemRepository.findByCarritoIdAndServicioIdAndFechaServicio(10L, 50L, req.getFechaServicio()))
                .thenReturn(Optional.empty());

        var res = carritoService.agregarItem(req);

        assertThat(res.getId()).isEqualTo(10L);
        verify(carritoItemRepository).save(any());
    }

    // ===============================================================
    // 3) Agregar item ya existente (incrementa cantidad)
    // ===============================================================
    @Test @Order(3)
    void testAgregarItemExistenteIncrementaCantidad() {
        CarritoItemRequest req = new CarritoItemRequest(50L, 3, LocalDate.now(), "nota");

        item.setCantidad(1);
        carrito.getItems().add(item);

        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(servicioRepository.findById(50L)).thenReturn(Optional.of(servicio));
        when(carritoItemRepository.findByCarritoIdAndServicioIdAndFechaServicio(10L, 50L, req.getFechaServicio()))
                .thenReturn(Optional.of(item));

        carritoService.agregarItem(req);

        assertThat(item.getCantidad()).isEqualTo(4);
        verify(carritoItemRepository).save(item);
    }

    // ===============================================================
    // 4) Agregar item con servicio inactivo
    // ===============================================================
    @Test @Order(4)
    void testAgregarItemServicioInactivo() {
        servicio.setEstado(ServicioTuristico.EstadoServicio.INACTIVO);

        CarritoItemRequest req = new CarritoItemRequest(50L, 1, LocalDate.now(), null);

        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(servicioRepository.findById(50L)).thenReturn(Optional.of(servicio));

        assertThatThrownBy(() -> carritoService.agregarItem(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no está disponible");
    }

    // ===============================================================
    // 5) Actualizar cantidad (cantidad > 0)
    // ===============================================================
    @Test @Order(5)
    void testActualizarCantidad() {
        when(carritoItemRepository.findById(200L)).thenReturn(Optional.of(item));

        carritoService.actualizarCantidad(200L, 5);

        assertThat(item.getCantidad()).isEqualTo(5);
        verify(carritoItemRepository).save(item);
    }

    // ===============================================================
    // 6) Actualizar cantidad a cero (elimina item)
    // ===============================================================
    @Test @Order(6)
    void testActualizarCantidadCeroElimina() {
        when(carritoItemRepository.findById(200L)).thenReturn(Optional.of(item));

        carritoService.actualizarCantidad(200L, 0);

        verify(carritoItemRepository).delete(item);
    }

    // ===============================================================
    // 7) Eliminar item sin permiso
    // ===============================================================
    @Test @Order(7)
    void testEliminarItemSinPermiso() {
        Usuario otro = Usuario.builder().id(999L).build();
        item.getCarrito().setUsuario(otro);

        when(carritoItemRepository.findById(200L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> carritoService.eliminarItem(200L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tiene permisos");
    }

    // ===============================================================
    // 8) Limpiar carrito elimina todos los items
    // ===============================================================
    @Test @Order(8)
    void testLimpiarCarrito() {
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));

        carritoService.limpiarCarrito();

        verify(carritoItemRepository).deleteByCarritoId(10L);
    }

    // ===============================================================
    // 9) Contar items del usuario
    // ===============================================================
    @Test @Order(9)
    void testContarItems() {
        when(carritoItemRepository.countByUsuarioId(1L)).thenReturn(5L);

        Long total = carritoService.contarItems();

        assertThat(total).isEqualTo(5L);
    }
}
