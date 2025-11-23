package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.UsuarioRepository;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.RolRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EmprendedorRepository emprendedorRepository;

    private static Long usuarioId;
    private Rol rolUser;
    private Rol rolEmp;

    @BeforeEach
    void setUp() {
        // Crear roles para cada test
        rolUser = rolRepository.save(
                Rol.builder().nombre(Rol.RolNombre.ROLE_USER).build()
        );
        rolEmp = rolRepository.save(
                Rol.builder().nombre(Rol.RolNombre.ROLE_EMPRENDEDOR).build()
        );

        // Crear usuario inicial
        Usuario u = Usuario.builder()
                .nombre("Carlos")
                .apellido("Lopez")
                .username("carlos_test")
                .email("carlos@test.com")
                .password("123456")
                .roles(Set.of(rolUser))
                .build();

        usuarioId = usuarioRepository.save(u).getId();
    }

    // ==========================================================
    // 1) Guardar usuario
    // ==========================================================
    @Test @Order(1)
    void testGuardarUsuario() {
        Usuario u = Usuario.builder()
                .nombre("Ana")
                .apellido("Gomez")
                .username("ana_test")
                .email("ana@test.com")
                .password("123456")
                .roles(Set.of(rolUser))
                .build();

        Usuario guardado = usuarioRepository.save(u);

        assertNotNull(guardado.getId());
        assertEquals("ana_test", guardado.getUsername());
    }

    // ==========================================================
    // 2) Buscar por ID
    // ==========================================================
    @Test @Order(2)
    void testBuscarPorId() {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        assertTrue(usuario.isPresent());
        assertEquals("carlos_test", usuario.get().getUsername());
    }

    // ==========================================================
    // 3) Buscar por username
    // ==========================================================
    @Test @Order(3)
    void testFindByUsername() {
        Optional<Usuario> user = usuarioRepository.findByUsername("carlos_test");

        assertTrue(user.isPresent());
        assertEquals("carlos@test.com", user.get().getEmail());
    }

    // ==========================================================
    // 4) Buscar por email
    // ==========================================================
    @Test @Order(4)
    void testFindByEmail() {
        Optional<Usuario> user = usuarioRepository.findByEmail("carlos@test.com");

        assertTrue(user.isPresent());
        assertEquals("carlos_test", user.get().getUsername());
    }

    // ==========================================================
    // 5) existsByUsername
    // ==========================================================
    @Test @Order(5)
    void testExistsByUsername() {
        boolean exists = usuarioRepository.existsByUsername("carlos_test");

        assertTrue(exists);
    }

    // ==========================================================
    // 6) existsByEmail
    // ==========================================================
    @Test @Order(6)
    void testExistsByEmail() {
        boolean exists = usuarioRepository.existsByEmail("carlos@test.com");

        assertTrue(exists);
    }

    // ==========================================================
    // 7) findUsuariosSinEmprendedor
    // ==========================================================
    @Test @Order(7)
    void testUsuariosSinEmprendedor() {

        // Crear un usuario que SÍ tiene emprendedor
        Usuario u = Usuario.builder()
                .nombre("Mario")
                .apellido("Ramos")
                .username("mario_emp")
                .email("mario@test.com")
                .password("123456")
                .roles(Set.of(rolEmp))
                .build();

        Usuario saved = usuarioRepository.save(u);

        Emprendedor emp = Emprendedor.builder()
                .nombreEmpresa("EmpresaX")
                .rubro("Turismo")
                .usuario(saved)
                .build();

        emprendedorRepository.save(emp); // lo enlaza con el usuario

        // Ejecutar consulta personalizada
        List<Usuario> sinEmp = usuarioRepository.findUsuariosSinEmprendedor();

        // "carlos_test" no es emprendedor → debe aparecer
        assertTrue(
                sinEmp.stream().anyMatch(us -> us.getUsername().equals("carlos_test"))
        );

        // "mario_emp" SÍ es emprendedor → NO debe aparecer
        assertFalse(
                sinEmp.stream().anyMatch(us -> us.getUsername().equals("mario_emp"))
        );
    }

    // ==========================================================
    // 8) findByRolesContaining
    // ==========================================================
    @Test @Order(8)
    void testFindByRolesContaining() {

        List<Usuario> usuarios = usuarioRepository.findByRolesContaining(rolUser);

        assertFalse(usuarios.isEmpty());
        assertTrue(
                usuarios.stream().anyMatch(u -> u.getUsername().equals("carlos_test"))
        );
    }
}
