package com.turismo.turismobackend.Unit.controller;

import com.turismo.turismobackend.controller.UsuarioController;
import com.turismo.turismobackend.dto.response.UsuarioResponse;
import com.turismo.turismobackend.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    private UsuarioResponse usuario;
    private List<UsuarioResponse> usuarios;

    @BeforeEach
    void setUp() {
        usuario = UsuarioResponse.builder()
                .id(1L)
                .nombre("Nayder")
                .apellido("Arce")
                .email("nayder@example.com")
                .username("nayder")
                .build();

        usuarios = List.of(usuario);
    }

    // =============================================================
    // GET /api/usuarios
    // =============================================================
    @Test
    void testGetAllUsuarios_ReturnsOK() {
        BDDMockito.given(usuarioService.getAllUsuarios()).willReturn(usuarios);

        ResponseEntity<List<UsuarioResponse>> res = controller.getAllUsuarios();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetAllUsuarios_EmptyList() {
        BDDMockito.given(usuarioService.getAllUsuarios()).willReturn(List.of());

        ResponseEntity<List<UsuarioResponse>> res = controller.getAllUsuarios();

        assertTrue(res.getBody().isEmpty());
    }

    // =============================================================
    // GET /{id}
    // =============================================================
    @Test
    void testGetUsuarioById_ReturnsOK() {
        BDDMockito.given(usuarioService.getUsuarioById(1L)).willReturn(usuario);

        ResponseEntity<UsuarioResponse> res = controller.getUsuarioById(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("Nayder", res.getBody().getNombre());
    }

    @Test
    void testGetUsuarioById_ReturnsNull() {
        BDDMockito.given(usuarioService.getUsuarioById(99L)).willReturn(null);

        ResponseEntity<UsuarioResponse> res = controller.getUsuarioById(99L);

        assertNull(res.getBody());
    }

    // =============================================================
    // GET /sin-emprendedor
    // =============================================================
    @Test
    void testGetUsuariosSinEmprendedor_ReturnsOK() {
        BDDMockito.given(usuarioService.getUsuariosSinEmprendedor()).willReturn(usuarios);

        ResponseEntity<List<UsuarioResponse>> res = controller.getUsuariosSinEmprendedor();

        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetUsuariosSinEmprendedor_Empty() {
        BDDMockito.given(usuarioService.getUsuariosSinEmprendedor()).willReturn(List.of());

        ResponseEntity<List<UsuarioResponse>> res = controller.getUsuariosSinEmprendedor();

        assertTrue(res.getBody().isEmpty());
    }

    // =============================================================
    // GET /con-rol/{rol}
    // =============================================================
    @Test
    void testGetUsuariosPorRol_ReturnsOK() {
        BDDMockito.given(usuarioService.getUsuariosPorRol("ADMIN")).willReturn(usuarios);

        ResponseEntity<List<UsuarioResponse>> res = controller.getUsuariosPorRol("ADMIN");

        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetUsuariosPorRol_Empty() {
        BDDMockito.given(usuarioService.getUsuariosPorRol("USER")).willReturn(List.of());

        ResponseEntity<List<UsuarioResponse>> res = controller.getUsuariosPorRol("USER");

        assertTrue(res.getBody().isEmpty());
    }

    // =============================================================
    // PUT asignar emprendedor
    // =============================================================
    @Test
    void testAsignarUsuarioAEmprendedor_ReturnsOK() {
        BDDMockito.willDoNothing().given(usuarioService)
                .asignarUsuarioAEmprendedor(1L, 10L);

        ResponseEntity<Map<String, String>> res =
                controller.asignarUsuarioAEmprendedor(1L, 10L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("Usuario asignado al emprendedor correctamente",
                res.getBody().get("message"));
    }

    // =============================================================
    // PUT cambiar emprendedor
    // =============================================================
    @Test
    void testCambiarUsuarioDeEmprendedor_ReturnsOK() {
        BDDMockito.willDoNothing().given(usuarioService)
                .cambiarUsuarioDeEmprendedor(1L, 20L);

        ResponseEntity<Map<String, String>> res =
                controller.cambiarUsuarioDeEmprendedor(1L, 20L);

        assertEquals("Usuario cambiado de emprendedor correctamente",
                res.getBody().get("message"));
    }

    // =============================================================
    // DELETE desasignar emprendedor
    // =============================================================
    @Test
    void testDesasignarUsuarioDeEmprendedor_ReturnsOK() {
        BDDMockito.willDoNothing().given(usuarioService)
                .desasignarUsuarioDeEmprendedor(1L);

        ResponseEntity<Map<String, String>> res =
                controller.desasignarUsuarioDeEmprendedor(1L);

        assertEquals("Usuario desasignado del emprendedor correctamente",
                res.getBody().get("message"));
    }

    // =============================================================
    // PUT asignar rol
    // =============================================================
    @Test
    void testAsignarRolAUsuario_ReturnsOK() {
        BDDMockito.willDoNothing().given(usuarioService)
                .asignarRolAUsuario(1L, "ADMIN");

        ResponseEntity<Map<String, String>> res =
                controller.asignarRolAUsuario(1L, "ADMIN");

        assertEquals("Rol asignado al usuario correctamente",
                res.getBody().get("message"));
    }

    // =============================================================
    // PUT quitar rol
    // =============================================================
    @Test
    void testQuitarRolAUsuario_ReturnsOK() {
        BDDMockito.willDoNothing().given(usuarioService)
                .quitarRolAUsuario(1L, "USER");

        ResponseEntity<Map<String, String>> res =
                controller.quitarRolAUsuario(1L, "USER");

        assertEquals("Rol quitado al usuario correctamente",
                res.getBody().get("message"));
    }

    // =============================================================
    // PUT resetear roles
    // =============================================================
    @Test
    void testResetearRolesUsuario_ReturnsOK() {
        BDDMockito.willDoNothing().given(usuarioService)
                .resetearRolesAUsuario(1L);

        ResponseEntity<Map<String, String>> res =
                controller.resetearRolesUsuario(1L);

        assertEquals("Roles del usuario reseteados a ROLE_USER",
                res.getBody().get("message"));
    }
}
