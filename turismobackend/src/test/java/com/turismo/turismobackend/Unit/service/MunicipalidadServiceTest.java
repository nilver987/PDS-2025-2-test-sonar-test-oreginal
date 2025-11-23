package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
import com.turismo.turismobackend.dto.response.MunicipalidadResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.MunicipalidadRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import com.turismo.turismobackend.service.MunicipalidadService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MunicipalidadServiceTest {

    @Mock
    private MunicipalidadRepository municipalidadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MunicipalidadService municipalidadService;

    private Usuario usuario;
    private Municipalidad municipalidad;
    private MunicipalidadRequest request;

    @BeforeEach
    void setUp() {

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .username("juan")
                .email("juan@test.com")
                .password("123")
                .roles(Set.of(new Rol(1L, Rol.RolNombre.ROLE_MUNICIPALIDAD)))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(usuario, null)
        );

        municipalidad = Municipalidad.builder()
                .id(10L)
                .nombre("Muni Puno")
                .departamento("Puno")
                .provincia("Puno")
                .distrito("Centro")
                .descripcion("Municipalidad central")
                .usuario(usuario)
                .emprendedores(new ArrayList<>())
                .build();

        request = MunicipalidadRequest.builder()
                .nombre("Nueva Muni")
                .departamento("Arequipa")
                .provincia("Arequipa")
                .distrito("Cercado")
                .descripcion("Descripción nueva")
                .telefono("987654321")
                .direccion("Av. X")
                .sitioWeb("web.com")
                .build();


    }

    // =====================================================================
    // 1. LISTAR MUNICIPALIDADES
    // =====================================================================

    @Test @Order(1)
    void testGetAllMunicipalidades() {
        when(municipalidadRepository.findAll()).thenReturn(List.of(municipalidad));

        var lista = municipalidadService.getAllMunicipalidades();

        assertThat(lista).hasSize(1);
    }

    @Test @Order(2)
    void testGetMunicipalidadById() {
        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));

        var res = municipalidadService.getMunicipalidadById(10L);

        assertThat(res.getNombre()).isEqualTo("Muni Puno");
    }

    @Test @Order(3)
    void testGetMunicipalidadByIdNotFound() {
        when(municipalidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> municipalidadService.getMunicipalidadById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 2. CREAR MUNICIPALIDAD
    // =====================================================================

    @Test @Order(4)
    void testCreateMunicipalidad() {

        when(municipalidadRepository.findByUsuario(usuario)).thenReturn(Optional.empty());
        when(municipalidadRepository.save(any(Municipalidad.class))).thenReturn(municipalidad);

        var res = municipalidadService.createMunicipalidad(request);

        assertThat(res).isNotNull();
        assertThat(res.getNombre()).isEqualTo("Nueva Muni");
    }

    @Test @Order(5)
    void testCreateMunicipalidadUsuarioYaTiene() {

        when(municipalidadRepository.findByUsuario(usuario)).thenReturn(Optional.of(municipalidad));

        assertThatThrownBy(() -> municipalidadService.createMunicipalidad(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene una municipalidad");
    }

    // =====================================================================
    // 3. ACTUALIZAR MUNICIPALIDAD
    // =====================================================================

    @Test @Order(6)
    void testUpdateMunicipalidad() {

        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));
        when(municipalidadRepository.save(any(Municipalidad.class))).thenReturn(municipalidad);

        MunicipalidadResponse res = municipalidadService.updateMunicipalidad(10L, request);

        assertThat(res.getNombre()).isEqualTo("Nueva Muni");
    }

    @Test @Order(7)
    void testUpdateMunicipalidadNotFound() {
        when(municipalidadRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> municipalidadService.updateMunicipalidad(999L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 4. ELIMINAR MUNICIPALIDAD
    // =====================================================================

    @Test @Order(8)
    void testDeleteMunicipalidad() {

        usuario.setRoles(Set.of(new Rol(1L, Rol.RolNombre.ROLE_ADMIN)));

        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));

        municipalidadService.deleteMunicipalidad(10L);

        verify(municipalidadRepository, times(1)).delete(municipalidad);
    }

    @Test
    @Order(9)
    void testDeleteMunicipalidadSinPermiso() {

        // Usuario sin permisos
        Usuario noPermiso = Usuario.builder()
                .id(999L)
                .nombre("Juan")
                .apellido("Perez")
                .username("juan")
                .email("juan@test.com")
                .password("123")
                .roles(Set.of())  // NO ES ADMIN Y NO ES EL DUEÑO
                .build();

        // Inyectar usuario en contexto de seguridad
        var auth = new UsernamePasswordAuthenticationToken(noPermiso, null, noPermiso.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Mock municipalidad (dueño diferente)
        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));

        assertThatThrownBy(() -> municipalidadService.deleteMunicipalidad(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permiso");

        SecurityContextHolder.clearContext();
    }


    @Test @Order(10)
    void testDeleteMunicipalidadNotFound() {
        when(municipalidadRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> municipalidadService.deleteMunicipalidad(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 5. BUSCAR POR CAMPOS
    // =====================================================================

    @Test @Order(11)
    void testBuscarPorDepartamento() {
        when(municipalidadRepository.findByDepartamento("Puno")).thenReturn(List.of(municipalidad));
        var lista = municipalidadService.getMunicipalidadesByDepartamento("Puno");
        assertThat(lista).hasSize(1);
    }

    @Test @Order(12)
    void testBuscarPorProvincia() {
        when(municipalidadRepository.findByProvincia("Puno")).thenReturn(List.of(municipalidad));
        var lista = municipalidadService.getMunicipalidadesByProvincia("Puno");
        assertThat(lista).hasSize(1);
    }

    @Test @Order(13)
    void testBuscarPorDistrito() {
        when(municipalidadRepository.findByDistrito("Centro")).thenReturn(List.of(municipalidad));
        var lista = municipalidadService.getMunicipalidadesByDistrito("Centro");
        assertThat(lista).hasSize(1);
    }

    // =====================================================================
    // 6. GET MUNICIPALIDAD POR USUARIO
    // =====================================================================

    @Test @Order(14)
    void testGetMunicipalidadByUsuario() {

        when(municipalidadRepository.findByUsuario(usuario)).thenReturn(Optional.of(municipalidad));

        var res = municipalidadService.getMunicipalidadByUsuario();

        assertThat(res.getId()).isEqualTo(10L);
    }

    @Test @Order(15)
    void testGetMunicipalidadByUsuarioNotFound() {

        when(municipalidadRepository.findByUsuario(usuario)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> municipalidadService.getMunicipalidadByUsuario())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // =====================================================================
    // 7. MAPEO DE RESPUESTA
    // =====================================================================

    @Test @Order(16)
    void testMapeoConListaEmprendedoresNula() {

        municipalidad.setEmprendedores(null);

        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));

        var res = municipalidadService.getMunicipalidadById(10L);

        assertThat(res.getEmprendedores()).isEmpty();
    }

    @Test
    @Order(17)
    void testMapeoConEmprendedores() {

        Emprendedor e = new Emprendedor();
        e.setId(99L);
        e.setNombreEmpresa("DoomDistribuidores");
        e.setRubro("Zapatillas");

        municipalidad.setEmprendedores(List.of(e));

        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));

        var res = municipalidadService.getMunicipalidadById(10L);

        assertThat(res.getEmprendedores()).hasSize(1);
        assertThat(res.getEmprendedores().get(0).getNombreEmpresa())
                .isEqualTo("DoomDistribuidores");
    }


    @Test @Order(18)
    void testUsuarioAutenticadoNulo() {

        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> municipalidadService.getMunicipalidadByUsuario())
                .isInstanceOf(RuntimeException.class);
    }

    @Test @Order(19)
    void testCrearMunicipalidadCamposCorrectos() {

        when(municipalidadRepository.findByUsuario(usuario)).thenReturn(Optional.empty());
        when(municipalidadRepository.save(any())).thenReturn(municipalidad);

        MunicipalidadResponse res = municipalidadService.createMunicipalidad(request);

        assertThat(res.getDepartamento()).isEqualTo("Arequipa");
    }

    @Test @Order(20)
    void testUpdateMunicipalidadCambiaTelefono() {

        when(municipalidadRepository.findById(10L)).thenReturn(Optional.of(municipalidad));
        when(municipalidadRepository.save(any())).thenReturn(municipalidad);

        request.setTelefono("111222333");

        MunicipalidadResponse res = municipalidadService.updateMunicipalidad(10L, request);

        assertThat(res.getTelefono()).isEqualTo("111222333");
    }
}
