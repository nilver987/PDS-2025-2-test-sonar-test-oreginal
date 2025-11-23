package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.Categoria;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.CategoriaRepository;
import com.turismo.turismobackend.repository.EmprendedorRepository;
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
public class EmprendedorRepositoryTest {

    @Autowired
    private EmprendedorRepository emprendedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipalidadRepository municipalidadRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Emprendedor emp;
    private Usuario usuario;
    private Municipalidad muni;
    private Categoria categoria;

    @BeforeEach
    void setUp() {

        usuario = usuarioRepository.save(
                Usuario.builder()
                        .nombre("Juan")
                        .apellido("Tester")
                        .username("usert" + System.currentTimeMillis())
                        .email("t" + System.currentTimeMillis() + "@mail.com")
                        .password("123456")
                        .build()
        );

        muni = municipalidadRepository.save(
                Municipalidad.builder()
                        .nombre("Muni Test")
                        .departamento("Puno")
                        .provincia("San Roman")
                        .distrito("Juliaca")
                        .direccion("Av Siempre Viva")
                        .telefono("999888777")
                        .sitioWeb("www.muni.com")
                        .descripcion("Muni test desc")
                        .usuario(usuario)
                        .build()
        );

        categoria = categoriaRepository.save(
                Categoria.builder()
                        .nombre("Gastronomía")
                        .descripcion("Comidas")
                        .build()
        );

        emp = emprendedorRepository.save(
                Emprendedor.builder()
                        .nombreEmpresa("Empresa Test")
                        .rubro("Gastronomía")
                        .direccion("Jr Lima")
                        .telefono("944555222")
                        .email("empresa@mail.com")
                        .sitioWeb("www.emp.com")
                        .descripcion("Desc test")
                        .productos("P1, P2")
                        .servicios("S1, S2")
                        .municipalidad(muni)
                        .categoria(categoria)
                        .usuario(usuario)
                        .build()
        );
    }

    // ===================================
    // 1) GUARDAR EMPRENDEDOR
    // ===================================
    @Test @Order(1)
    void testGuardar() {
        assertNotNull(emp.getId());
        assertEquals("Empresa Test", emp.getNombreEmpresa());
    }

    // ===================================
    // 2) BUSCAR POR ID
    // ===================================
    @Test @Order(2)
    void testBuscarPorId() {
        Optional<Emprendedor> found = emprendedorRepository.findById(emp.getId());
        assertTrue(found.isPresent());
    }

    // ===================================
    // 3) BUSCAR POR USUARIO
    // ===================================
    @Test @Order(3)
    void testBuscarPorUsuario() {
        Optional<Emprendedor> found = emprendedorRepository.findByUsuario(usuario);
        assertTrue(found.isPresent());
    }

    // ===================================
    // 4) BUSCAR POR USUARIO ID
    // ===================================
    @Test @Order(4)
    void testBuscarPorUsuarioId() {
        Optional<Emprendedor> found = emprendedorRepository.findByUsuarioId(usuario.getId());
        assertTrue(found.isPresent());
    }

    // ===================================
    // 5) BUSCAR POR MUNICIPALIDAD
    // ===================================
    @Test @Order(5)
    void testBuscarPorMunicipalidad() {
        List<Emprendedor> lista = emprendedorRepository.findByMunicipalidad(muni);
        assertFalse(lista.isEmpty());
    }

    // ===================================
    // 6) BUSCAR POR MUNICIPALIDAD ID
    // ===================================
    @Test @Order(6)
    void testBuscarPorMunicipalidadId() {
        List<Emprendedor> lista = emprendedorRepository.findByMunicipalidadId(muni.getId());
        assertFalse(lista.isEmpty());
    }

    // ===================================
    // 7) BUSCAR POR RUBRO
    // ===================================
    @Test @Order(7)
    void testBuscarPorRubro() {
        List<Emprendedor> lista = emprendedorRepository.findByRubro("Gastronomía");
        assertFalse(lista.isEmpty());
    }

    // ===================================
    // 8) BUSCAR POR CATEGORIA
    // ===================================
    @Test @Order(8)
    void testBuscarPorCategoria() {
        List<Emprendedor> lista = emprendedorRepository.findByCategoria(categoria);
        assertFalse(lista.isEmpty());
    }

    // ===================================
    // 9) EXISTS BY NOMBRE EMPRESA
    // ===================================
    @Test @Order(9)
    void testExistsByNombreEmpresa() {
        assertTrue(emprendedorRepository.existsByNombreEmpresa("Empresa Test"));
        assertFalse(emprendedorRepository.existsByNombreEmpresa("Inexistente S.A."));
    }

    // ===================================
    // 10) ACTUALIZAR EMPRENDEDOR
    // ===================================
    @Test @Order(10)
    void testActualizar() {
        emp.setTelefono("111222333");
        emprendedorRepository.save(emp);

        Emprendedor updated = emprendedorRepository.findById(emp.getId()).orElseThrow();
        assertEquals("111222333", updated.getTelefono());
    }

    // ===================================
    // 11) ELIMINAR
    // ===================================
    @Test @Order(11)
    void testEliminar() {
        emprendedorRepository.delete(emp);
        Optional<Emprendedor> deleted = emprendedorRepository.findById(emp.getId());
        assertFalse(deleted.isPresent());
    }

    // ===================================
    // 12) LISTAR TODOS
    // ===================================
    @Test @Order(12)
    void testListarTodos() {
        List<Emprendedor> lista = emprendedorRepository.findAll();
        assertFalse(lista.isEmpty());
    }

    // ===================================
    // 13) MULTIPLES REGISTROS
    // ===================================
    @Test @Order(13)
    void testMultiplesRegistros() {
        emprendedorRepository.save(
                Emprendedor.builder()
                        .nombreEmpresa("Empresa 2")
                        .municipalidad(muni)
                        .rubro("Gastronomía")
                        .categoria(categoria)
                        .usuario(
                                usuarioRepository.save(
                                        Usuario.builder()
                                                .nombre("Otro")
                                                .apellido("Usuario")
                                                .username("nuevo" + System.currentTimeMillis())
                                                .email("nuevo@mail.com")
                                                .password("123")
                                                .build()
                                )
                        )
                        .build()
        );

        List<Emprendedor> lista = emprendedorRepository.findByMunicipalidadId(muni.getId());
        assertEquals(2, lista.size());
    }

    // ===================================
    // 14) BUSCAR POR RUBRO INEXISTENTE
    // ===================================
    @Test @Order(14)
    void testBuscarRubroInexistente() {
        List<Emprendedor> lista = emprendedorRepository.findByRubro("NoExiste");
        assertTrue(lista.isEmpty());
    }

    // ===================================
    // 15) BUSCAR POR CATEGORIA INEXISTENTE
    // ===================================
    @Test @Order(15)
    void testBuscarCategoriaInexistente() {
        Categoria catFake = categoriaRepository.save(
                Categoria.builder().nombre("Fake").descripcion("X").build()
        );
        List<Emprendedor> lista = emprendedorRepository.findByCategoria(catFake);
        assertTrue(lista.isEmpty());
    }

    // ===================================
    // 16) BUSCAR POR MUNICIPALIDAD SIN REGISTROS
    // ===================================
    @Test @Order(16)
    void testBuscarMunicipalidadSinRegistros() {
        Municipalidad nueva = municipalidadRepository.save(
                Municipalidad.builder()
                        .nombre("Otra")
                        .departamento("Cusco")
                        .provincia("Cusco")
                        .distrito("Cusco")
                        .build()
        );

        List<Emprendedor> lista = emprendedorRepository.findByMunicipalidad(nueva);
        assertTrue(lista.isEmpty());
    }

    // ===================================
    // 17) VALIDAR CAMPOS OBLIGATORIOS
    // ===================================
    @Test @Order(17)
    void testCamposObligatorios() {
        Emprendedor e = new Emprendedor();
        assertThrows(Exception.class, () -> emprendedorRepository.save(e));
    }

    // ===================================
    // 18) TEST FIND ALL AFTER DELETE
    // ===================================
    @Test @Order(18)
    void testListarLuegoEliminar() {
        emprendedorRepository.delete(emp);
        List<Emprendedor> lista = emprendedorRepository.findAll();
        assertTrue(lista.isEmpty());
    }

    // ===================================
    // 19) TEST EXISTENCIA INDEPENDIENTE
    // ===================================
    @Test @Order(19)
    void testExistenciaIndependiente() {
        boolean existe = emprendedorRepository.existsByNombreEmpresa("Empresa Test");
        assertTrue(existe);
    }
}
