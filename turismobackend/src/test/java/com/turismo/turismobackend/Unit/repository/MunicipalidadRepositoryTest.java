package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.MunicipalidadRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MunicipalidadRepositoryTest {

    @Autowired
    private MunicipalidadRepository municipalidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Municipalidad muni;

    @BeforeEach
    void setUp() {

        usuario = usuarioRepository.save(
                Usuario.builder()
                        .nombre("Juan")
                        .apellido("Tester")
                        .username("jtester" + System.currentTimeMillis())
                        .email("jt" + System.currentTimeMillis() + "@mail.com")
                        .password("123456")
                        .build()
        );

        muni = municipalidadRepository.save(
                Municipalidad.builder()
                        .nombre("Municipalidad Test")
                        .departamento("Puno")
                        .provincia("San Roman")
                        .distrito("Juliaca")
                        .usuario(usuario)
                        .direccion("Av Siempre Viva")
                        .telefono("987654321")
                        .sitioWeb("www.test.com")
                        .descripcion("Descripcion Test")
                        .build()
        );
    }


    // =======================
    // 1) GUARDAR MUNICIPALIDAD
    // =======================
    @Test
    @Order(1)
    void testGuardar() {
        assertNotNull(muni.getId());
        assertEquals("Municipalidad Test", muni.getNombre());
    }

    // ========================
    // 2) BUSCAR POR ID
    // ========================
    @Test
    @Order(2)
    void testBuscarPorId() {
        Optional<Municipalidad> found = municipalidadRepository.findById(muni.getId());
        assertTrue(found.isPresent());
        assertEquals("Municipalidad Test", found.get().getNombre());
    }

    // =========================
    // 3) BUSCAR POR USUARIO
    // =========================
    @Test
    @Order(3)
    void testBuscarPorUsuario() {
        Optional<Municipalidad> found = municipalidadRepository.findByUsuario(usuario);
        assertTrue(found.isPresent());
    }

    // ==============================
    // 4) BUSCAR POR USUARIO ID
    // ==============================
    @Test
    @Order(4)
    void testBuscarPorUsuarioId() {
        Optional<Municipalidad> found = municipalidadRepository.findByUsuarioId(usuario.getId());
        assertTrue(found.isPresent());
    }

    // ==============================
    // 5) BUSCAR POR DEPARTAMENTO
    // ==============================
    @Test
    @Order(5)
    void testBuscarPorDepartamento() {
        List<Municipalidad> lista = municipalidadRepository.findByDepartamento("Puno");
        assertFalse(lista.isEmpty());
    }

    // ==============================
    // 6) BUSCAR POR PROVINCIA
    // ==============================
    @Test
    @Order(6)
    void testBuscarPorProvincia() {
        List<Municipalidad> lista = municipalidadRepository.findByProvincia("San Roman");
        assertFalse(lista.isEmpty());
    }

    // ==============================
    // 7) BUSCAR POR DISTRITO
    // ==============================
    @Test
    @Order(7)
    void testBuscarPorDistrito() {
        List<Municipalidad> lista = municipalidadRepository.findByDistrito("Juliaca");
        assertFalse(lista.isEmpty());
    }

    // ==============================
    // 8) EXISTS BY NOMBRE
    // ==============================
    @Test
    @Order(8)
    void testExistsByNombre() {
        assertTrue(municipalidadRepository.existsByNombre("Municipalidad Test"));
        assertFalse(municipalidadRepository.existsByNombre("NoExiste"));
    }

    // ==============================
    // 9) UPDATE MUNICIPALIDAD
    // ==============================
    @Test
    @Order(9)
    void testActualizar() {
        muni.setTelefono("111222333");
        municipalidadRepository.save(muni);

        Municipalidad updated = municipalidadRepository.findById(muni.getId()).orElseThrow();
        assertEquals("111222333", updated.getTelefono());
    }

    // ==============================
    // 10) LISTAR TODAS
    // ==============================
    @Test
    @Order(10)
    void testListarTodas() {
        List<Municipalidad> lista = municipalidadRepository.findAll();
        assertFalse(lista.isEmpty());
    }

    // ==============================
    // 11) ELIMINAR MUNICIPALIDAD
    // ==============================
    @Test
    @Order(11)
    void testEliminar() {
        municipalidadRepository.delete(muni);

        Optional<Municipalidad> deleted = municipalidadRepository.findById(muni.getId());
        assertFalse(deleted.isPresent());
    }

    // ==============================
    // 12) MULTIPLES REGISTROS
    // ==============================
    @Test
    @Order(12)
    void testMultiplesRegistros() {
        municipalidadRepository.save(
                Municipalidad.builder()
                        .nombre("Otra Muni")
                        .departamento("Puno")
                        .provincia("San Roman")
                        .distrito("Juliaca")
                        .build()
        );

        List<Municipalidad> lista = municipalidadRepository.findByDepartamento("Puno");
        assertEquals(2, lista.size());
    }
}
