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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicioTuristicoRepositoryTest {

    @Autowired
    private ServicioTuristicoRepository servicioRepo;

    @Autowired
    private EmprendedorRepository emprendedorRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private MunicipalidadRepository municipalidadRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    private static Long servicioId;
    private Emprendedor emprendedor;
    private Categoria categoria;
    private Municipalidad municipalidad;

    @BeforeEach
    void setUp() {

        // Crear roles
        Rol rolEmp = rolRepo.save(Rol.builder()
                .nombre(Rol.RolNombre.ROLE_EMPRENDEDOR)
                .build());

        // Crear usuario
        Usuario usu = usuarioRepo.save(
                Usuario.builder()
                        .nombre("Juan")
                        .apellido("Lopez")
                        .username("juan_emp")
                        .email("juan@test.com")
                        .password("123456")
                        .roles(Set.of(rolEmp))
                        .build()
        );

        // Crear municipalidad
        municipalidad = municipalidadRepo.save(
                Municipalidad.builder()
                        .nombre("Muni Lima")
                        .departamento("Lima")
                        .provincia("Lima")
                        .distrito("Lima")
                        .direccion("Av Test")
                        .telefono("1111")
                        .build()
        );

        // Crear categoría
        categoria = categoriaRepo.save(
                Categoria.builder()
                        .nombre("Turismo Cultural")
                        .descripcion("Descripcion")
                        .build()
        );

        // Crear emprendedor
        emprendedor = emprendedorRepo.save(
                Emprendedor.builder()
                        .nombreEmpresa("Empresa Test")
                        .rubro("Turismo")
                        .municipalidad(municipalidad)
                        .categoria(categoria)
                        .usuario(usu)
                        .build()
        );

        // Crear servicio turístico base
        ServicioTuristico s = ServicioTuristico.builder()
                .nombre("Tour Machu")
                .descripcion("Desc 1")
                .precio(new BigDecimal("50"))
                .duracionHoras(3)
                .capacidadMaxima(10)
                .tipo(ServicioTuristico.TipoServicio.TOUR)
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .latitud(-12.06)
                .longitud(-77.05)
                .emprendedor(emprendedor)
                .build();

        servicioId = servicioRepo.save(s).getId();
    }

    // ==========================================================
    // 1) Buscar por ID de emprendedor
    // ==========================================================
    @Test @Order(1)
    void testFindByEmprendedorId() {
        List<ServicioTuristico> servicios =
                servicioRepo.findByEmprendedorId(emprendedor.getId());

        assertFalse(servicios.isEmpty());
        assertEquals("Tour Machu", servicios.get(0).getNombre());
    }

    // ==========================================================
    // 2) Buscar por ID de municipalidad (a través del emprendedor)
    // ==========================================================
    @Test @Order(2)
    void testFindByEmprendedorMunicipalidadId() {

        List<ServicioTuristico> servicios =
                servicioRepo.findByEmprendedorMunicipalidadId(municipalidad.getId());

        assertFalse(servicios.isEmpty());
        assertEquals("Tour Machu", servicios.get(0).getNombre());
    }

    // ==========================================================
    // 3) Buscar por tipo
    // ==========================================================
    @Test @Order(3)
    void testFindByTipo() {
        List<ServicioTuristico> servicios =
                servicioRepo.findByTipo(ServicioTuristico.TipoServicio.TOUR);

        assertFalse(servicios.isEmpty());
    }

    // ==========================================================
    // 4) Buscar por estado
    // ==========================================================
    @Test @Order(4)
    void testFindByEstado() {
        List<ServicioTuristico> servicios =
                servicioRepo.findByEstado(ServicioTuristico.EstadoServicio.ACTIVO);

        assertFalse(servicios.isEmpty());
    }

    // ==========================================================
    // 5) Buscar precios entre rango
    // ==========================================================
    @Test @Order(5)
    void testFindByPrecioBetween() {
        List<ServicioTuristico> servicios =
                servicioRepo.findByPrecioBetween(new BigDecimal("40"), new BigDecimal("60"));

        assertFalse(servicios.isEmpty());
    }

    // ==========================================================
    // 6) Buscar por capacidad mínima
    // ==========================================================
    @Test @Order(6)
    void testFindByCapacidadMaximaGreaterThanEqual() {
        List<ServicioTuristico> servicios = servicioRepo.findByCapacidadMaximaGreaterThanEqual(5);

        assertFalse(servicios.isEmpty());
    }

    // ==========================================================
    // 7) Buscar por municipalidad + estado
    // ==========================================================
    @Test @Order(7)
    void testFindByMunicipalidadAndEstado() {

        List<ServicioTuristico> servicios =
                servicioRepo.findByMunicipalidadAndEstado(
                        municipalidad.getId(),
                        ServicioTuristico.EstadoServicio.ACTIVO
                );

        assertFalse(servicios.isEmpty());
    }

    // ==========================================================
    // 8) Buscar por nombre o descripción LIKE
    // ==========================================================
    @Test @Order(8)
    void testFindByNombreOrDescripcionContaining() {
        List<ServicioTuristico> servicios =
                servicioRepo.findByNombreOrDescripcionContaining("Mach", "Mach");

        assertFalse(servicios.isEmpty());
    }

    // ==========================================================
    // 9) Buscar por categoría del emprendedor
    // ==========================================================
    @Test @Order(9)
    void testFindByEmprendedorCategoriaId() {
        List<ServicioTuristico> servicios =
                servicioRepo.findByEmprendedorCategoriaId(categoria.getId());

        assertFalse(servicios.isEmpty());
    }
}
