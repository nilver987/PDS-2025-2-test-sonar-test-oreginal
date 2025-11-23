package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.response.UsuarioResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.RolRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import com.turismo.turismobackend.service.UsuarioService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmprendedorRepository emprendedorRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Rol rolAdmin;
    private Rol rolUser;
    private Emprendedor emprendedor;

    @BeforeEach
    void setUp() {

        rolAdmin = Rol.builder()
                .id(1L)
                .nombre(Rol.RolNombre.ROLE_ADMIN)
                .build();

        rolUser = Rol.builder()
                .id(2L)
                .nombre(Rol.RolNombre.ROLE_USER)
                .build();

        usuario = Usuario.builder()
                .id(10L)
                .nombre("Juan")
                .apellido("Perez")
                .username("juanp")
                .email("juan@test.com")
                .password("123")
                .roles(new HashSet<>(List.of(rolUser)))
                .build();

        emprendedor = Emprendedor.builder()
                .id(100L)
                .nombreEmpresa("Turismo SAC")
                .rubro("Tours")
                .usuario(null)
                .build();
    }

    // ============================================================
    @Test @Order(1)
    @DisplayName("Listar todos los usuarios")
    void testGetAllUsuarios() {

        given(usuarioRepository.findAll()).willReturn(List.of(usuario));

        List<UsuarioResponse> lista = usuarioService.getAllUsuarios();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getUsername()).isEqualTo("juanp");
    }

    // ============================================================
    @Test @Order(2)
    @DisplayName("Buscar usuario por ID existente")
    void testGetUsuarioById() {

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));

        UsuarioResponse res = usuarioService.getUsuarioById(10L);

        assertThat(res.getEmail()).isEqualTo("juan@test.com");
    }

    // ============================================================
    @Test @Order(3)
    @DisplayName("Buscar usuario por ID no existente")
    void testGetUsuarioByIdNotFound() {

        given(usuarioRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.getUsuarioById(99L));
    }

    // ============================================================
    @Test @Order(4)
    @DisplayName("Usuarios sin emprendedor")
    void testGetUsuariosSinEmprendedor() {

        given(usuarioRepository.findUsuariosSinEmprendedor()).willReturn(List.of(usuario));

        List<UsuarioResponse> lista = usuarioService.getUsuariosSinEmprendedor();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getId()).isEqualTo(10L);
    }

    // ============================================================
    @Test @Order(5)
    @DisplayName("Buscar usuarios por rol válido")
    void testGetUsuariosPorRol() {

        given(rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN))
                .willReturn(Optional.of(rolAdmin));

        given(usuarioRepository.findByRolesContaining(rolAdmin))
                .willReturn(List.of(usuario));

        List<UsuarioResponse> lista = usuarioService.getUsuariosPorRol("ROLE_ADMIN");

        assertThat(lista).hasSize(1);
    }

    // ============================================================
    @Test @Order(6)
    @DisplayName("Buscar usuarios por rol inválido")
    void testGetUsuariosPorRolInvalido() {

        assertThrows(RuntimeException.class,
                () -> usuarioService.getUsuariosPorRol("INVALIDO"));
    }

    // ============================================================
    @Test @Order(7)
    @DisplayName("Asignar usuario a emprendedor correctamente")
    void testAsignarUsuarioAEmprendedor() {

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(emprendedorRepository.findById(100L)).willReturn(Optional.of(emprendedor));
        given(emprendedorRepository.findByUsuario(usuario)).willReturn(Optional.empty());

        usuarioService.asignarUsuarioAEmprendedor(10L, 100L);

        assertThat(emprendedor.getUsuario()).isEqualTo(usuario);
    }

    // ============================================================
    @Test @Order(8)
    @DisplayName("Asignar usuario a emprendedor que ya tiene usuario asignado")
    void testAsignarUsuarioAEmprendedor_EmprendedorYaAsignado() {

        emprendedor.setUsuario(usuario);

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(emprendedorRepository.findById(100L)).willReturn(Optional.of(emprendedor));

        assertThrows(RuntimeException.class,
                () -> usuarioService.asignarUsuarioAEmprendedor(10L, 100L));
    }

    // ============================================================
    @Test @Order(9)
    @DisplayName("Asignar usuario que ya pertenece a un emprendedor")
    void testAsignarUsuarioAEmprendedor_UsuarioYaAsignado() {

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(emprendedorRepository.findById(100L)).willReturn(Optional.of(emprendedor));
        given(emprendedorRepository.findByUsuario(usuario)).willReturn(Optional.of(emprendedor));

        assertThrows(RuntimeException.class,
                () -> usuarioService.asignarUsuarioAEmprendedor(10L, 100L));
    }

    // ============================================================
    @Test @Order(10)
    @DisplayName("Cambiar usuario de un emprendedor a otro correctamente")
    void testCambiarUsuarioDeEmprendedor() {

        Emprendedor nuevo = Emprendedor.builder()
                .id(200L)
                .nombreEmpresa("Tours Pro")
                .build();

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(emprendedorRepository.findById(200L)).willReturn(Optional.of(nuevo));
        given(emprendedorRepository.findByUsuario(usuario)).willReturn(Optional.of(emprendedor));

        usuarioService.cambiarUsuarioDeEmprendedor(10L, 200L);

        assertNull(emprendedor.getUsuario());
        assertThat(nuevo.getUsuario()).isEqualTo(usuario);
    }

    // ============================================================
    @Test @Order(11)
    @DisplayName("Cambiar usuario a emprendedor que ya tiene otro usuario asignado")
    void testCambiarUsuarioDeEmprendedor_EmprendedorOcupado() {

        Emprendedor nuevo = Emprendedor.builder()
                .id(200L)
                .usuario(Usuario.builder().id(999L).build())
                .build();

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(emprendedorRepository.findById(200L)).willReturn(Optional.of(nuevo));

        assertThrows(RuntimeException.class,
                () -> usuarioService.cambiarUsuarioDeEmprendedor(10L, 200L));
    }

    // ============================================================
    @Test @Order(12)
    @DisplayName("Desasignar usuario de emprendedor correctamente")
    void testDesasignarUsuarioDeEmprendedor() {

        emprendedor.setUsuario(usuario);

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(emprendedorRepository.findByUsuario(usuario)).willReturn(Optional.of(emprendedor));

        usuarioService.desasignarUsuarioDeEmprendedor(10L);

        assertNull(emprendedor.getUsuario());
    }


    // ============================================================
    @Test @Order(14)
    @DisplayName("Quitar rol que el usuario NO tiene")
    void testQuitarRolAUsuario_NoTieneRol() {

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN))
                .willReturn(Optional.of(rolAdmin));

        assertThrows(RuntimeException.class,
                () -> usuarioService.quitarRolAUsuario(10L, "ROLE_ADMIN"));
    }

    // ============================================================
    @Test @Order(15)
    @DisplayName("Resetear roles del usuario")
    void testResetearRolesAUsuario() {

        usuario.getRoles().clear();
        usuario.getRoles().add(rolAdmin);

        given(usuarioRepository.findById(10L)).willReturn(Optional.of(usuario));
        given(rolRepository.findByNombre(Rol.RolNombre.ROLE_USER))
                .willReturn(Optional.of(rolUser));

        usuarioService.resetearRolesAUsuario(10L);

        assertThat(usuario.getRoles()).containsOnly(rolUser);
    }
}
