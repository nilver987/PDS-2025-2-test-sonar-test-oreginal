package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.repository.RolRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RolRepositoryTest {

    @Autowired
    private RolRepository rolRepository;

    private Long rolAdminId;

    @BeforeEach
    void setUp() {
        Rol rolAdmin = Rol.builder()
                .nombre(Rol.RolNombre.ROLE_ADMIN)
                .build();
        rolAdmin = rolRepository.save(rolAdmin);
        rolAdminId = rolAdmin.getId();
    }

    // =============================================================
    // 1) Guardar nuevo rol ROLE_USER
    // =============================================================
    @Test
    @Order(1)
    void testGuardarRol() {
        Rol rol = Rol.builder()
                .nombre(Rol.RolNombre.ROLE_USER)
                .build();

        Rol guardado = rolRepository.save(rol);

        assertNotNull(guardado.getId());
        assertEquals(Rol.RolNombre.ROLE_USER, guardado.getNombre());
    }

    // =============================================================
    // 2) Buscar rol por ID
    // =============================================================
    @Test
    @Order(2)
    void testBuscarPorId() {
        Optional<Rol> rol = rolRepository.findById(rolAdminId);

        assertTrue(rol.isPresent());
        assertEquals(Rol.RolNombre.ROLE_ADMIN, rol.get().getNombre());
    }

    // =============================================================
    // 3) Buscar rol por nombre
    // =============================================================
    @Test
    @Order(3)
    void testBuscarPorNombre() {
        Optional<Rol> rol = rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN);

        assertTrue(rol.isPresent());
        assertEquals(Rol.RolNombre.ROLE_ADMIN, rol.get().getNombre());
    }

    // =============================================================
    // 4) Verificar existencia por nombre
    // =============================================================
    @Test
    @Order(4)
    void testExistsByNombre() {
        boolean existe = rolRepository.existsByNombre(Rol.RolNombre.ROLE_ADMIN);

        assertTrue(existe);
    }

    // =============================================================
    // 5) Listar todos los roles
    // =============================================================
    @Test
    @Order(5)
    void testListarRoles() {
        Rol rolUser = Rol.builder()
                .nombre(Rol.RolNombre.ROLE_USER)
                .build();
        rolRepository.save(rolUser);

        List<Rol> roles = rolRepository.findAll();
        assertFalse(roles.isEmpty());
        assertTrue(roles.size() >= 2);
    }

    // =============================================================
    // 6) Actualizar nombre (prácticamente no se cambia ENUM, pero se prueba persistencia)
    // =============================================================
    @Test
    @Order(6)
    void testActualizarRol() {
        Rol rol = rolRepository.findById(rolAdminId).orElseThrow();
        rol.setNombre(Rol.RolNombre.ROLE_MUNICIPALIDAD);

        Rol actualizado = rolRepository.save(rol);

        assertEquals(Rol.RolNombre.ROLE_MUNICIPALIDAD, actualizado.getNombre());
    }

    // =============================================================
    // 7) Eliminar rol
    // =============================================================
    @Test
    @Order(7)
    void testEliminarRol() {
        rolRepository.deleteById(rolAdminId);

        Optional<Rol> eliminado = rolRepository.findById(rolAdminId);
        assertTrue(eliminado.isEmpty());
    }

    // =============================================================
    // 8) Guardar rol duplicado (ENUM mismo nombre) debería permitirse SOLO si ID es distinto
    // =============================================================
    @Test
    @Order(8)
    void testGuardarRolDuplicado() {
        Rol rol = Rol.builder()
                .nombre(Rol.RolNombre.ROLE_ADMIN)
                .build();

        Rol guardado = rolRepository.save(rol);

        assertNotNull(guardado.getId());
        assertNotEquals(rolAdminId, guardado.getId()); // Son entidades distintas
    }

    // =============================================================
    // 9) Buscar por nombre inexistente
    // =============================================================
    @Test
    @Order(9)
    void testBuscarPorNombreInexistente() {
        Optional<Rol> rol = rolRepository.findByNombre(Rol.RolNombre.ROLE_EMPRENDEDOR);

        assertTrue(rol.isEmpty());
    }

    // =============================================================
    // 10) existsByNombre para rol inexistente
    // =============================================================
    @Test
    @Order(10)
    void testExistsByNombreFalse() {
        boolean existe = rolRepository.existsByNombre(Rol.RolNombre.ROLE_EMPRENDEDOR);

        assertFalse(existe);
    }

    // =============================================================
    // 11) Guardar múltiples roles
    // =============================================================
    @Test
    @Order(11)
    void testGuardarMultiplesRoles() {
        rolRepository.save(Rol.builder().nombre(Rol.RolNombre.ROLE_USER).build());
        rolRepository.save(Rol.builder().nombre(Rol.RolNombre.ROLE_MUNICIPALIDAD).build());

        List<Rol> list = rolRepository.findAll();

        assertTrue(list.size() >= 3);
    }

    // =============================================================
    // 12) findAll vacío si se eliminan todos
    // =============================================================
    @Test
    @Order(12)
    void testFindAllVacio() {
        rolRepository.deleteAll();

        List<Rol> roles = rolRepository.findAll();

        assertTrue(roles.isEmpty());
    }

    // =============================================================
    // 13) Guardar rol con ID null (normal)
    // =============================================================
    @Test
    @Order(13)
    void testGuardarRolIdNull() {
        Rol rol = new Rol();
        rol.setNombre(Rol.RolNombre.ROLE_USER);

        Rol guardado = rolRepository.save(rol);

        assertNotNull(guardado.getId());
    }

    // =============================================================
    // 14) Validar que @Enumerated funciona
    // =============================================================
    @Test
    @Order(14)
    void testEnumAlmacenadoCorrectamente() {
        Rol rol = rolRepository.findById(rolAdminId).orElseThrow();

        assertNotNull(rol.getNombre());
        assertTrue(rol.getNombre() instanceof Rol.RolNombre);
    }

    // =============================================================
    // 15) Guardar y recuperar rol ROLE_EMPRENDEDOR
    // =============================================================
    @Test
    @Order(15)
    void testGuardarYBuscarNuevoRol() {
        Rol rol = Rol.builder()
                .nombre(Rol.RolNombre.ROLE_EMPRENDEDOR)
                .build();

        rolRepository.save(rol);

        Optional<Rol> encontrado = rolRepository.findByNombre(Rol.RolNombre.ROLE_EMPRENDEDOR);

        assertTrue(encontrado.isPresent());
        assertEquals(Rol.RolNombre.ROLE_EMPRENDEDOR, encontrado.get().getNombre());
    }
}
