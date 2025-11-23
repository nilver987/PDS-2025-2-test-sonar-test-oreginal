package com.turismo.turismobackend.Unit.service;

import com.turismo.turismobackend.dto.request.LoginRequest;
import com.turismo.turismobackend.dto.request.RegisterRequest;
import com.turismo.turismobackend.dto.response.AuthResponse;
import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.RolRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import com.turismo.turismobackend.service.AuthService;
import com.turismo.turismobackend.service.JwtService;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private Rol rolUser, rolAdmin, rolMuni, rolEmprendedor;
    private Usuario usuarioBase;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        rolUser = new Rol(1L, Rol.RolNombre.ROLE_USER);
        rolAdmin = new Rol(2L, Rol.RolNombre.ROLE_ADMIN);
        rolMuni = new Rol(3L, Rol.RolNombre.ROLE_MUNICIPALIDAD);
        rolEmprendedor = new Rol(4L, Rol.RolNombre.ROLE_EMPRENDEDOR);

        usuarioBase = Usuario.builder()
                .id(10L)
                .username("nayder")
                .email("na@na.com")
                .roles(Set.of(rolUser))
                .build();
    }

    // --------------------------------------------------------------------
    // 1) Registro básico asignando ROLE_USER por defecto
    // --------------------------------------------------------------------
    @Test @Order(1)
    void testRegisterAsignaRoleUserPorDefecto() {
        RegisterRequest request = new RegisterRequest("Nayder", "Arce", "na", "na@correo.com", "123", null);

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)).thenReturn(Optional.of(rolUser));
        when(passwordEncoder.encode("123")).thenReturn("ENC123");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("TOKEN123");

        AuthResponse res = authService.register(request);

        assertThat(res.getRoles()).containsExactly("ROLE_USER");
        assertThat(res.getToken()).isEqualTo("TOKEN123");
    }

    // --------------------------------------------------------------------
    @Test @Order(2)
    void testRegisterConRolAdmin() {
        RegisterRequest request = new RegisterRequest("Nay", "Ar", "admin", "adm@mail", "123",
                Set.of("admin"));

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("TK");

        AuthResponse res = authService.register(request);

        assertThat(res.getRoles()).containsExactly("ROLE_ADMIN");
    }

    // --------------------------------------------------------------------
    @Test @Order(3)
    void testRegisterConRolMunicipalidad() {
        RegisterRequest request = new RegisterRequest("A", "B", "u", "u@mail", "123",
                Set.of("municipalidad"));

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_MUNICIPALIDAD)).thenReturn(Optional.of(rolMuni));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("TK");

        AuthResponse res = authService.register(request);

        assertThat(res.getRoles()).containsExactly("ROLE_MUNICIPALIDAD");
    }

    // --------------------------------------------------------------------
    @Test @Order(4)
    void testRegisterConRolEmprendedor() {
        RegisterRequest request = new RegisterRequest("A", "B", "x", "x@mail", "123",
                Set.of("emprendedor"));

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_EMPRENDEDOR)).thenReturn(Optional.of(rolEmprendedor));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("TK");

        AuthResponse res = authService.register(request);

        assertThat(res.getRoles()).containsExactly("ROLE_EMPRENDEDOR");
    }

    // --------------------------------------------------------------------
    @Test @Order(5)
    void testRegisterRolDesconocidoAsignaUser() {
        RegisterRequest request = new RegisterRequest("A", "B", "t", "t@mail", "123",
                Set.of("raro"));

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)).thenReturn(Optional.of(rolUser));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthResponse res = authService.register(request);

        assertThat(res.getRoles()).containsExactly("ROLE_USER");
    }

    // --------------------------------------------------------------------
    @Test @Order(6)
    void testRegisterLanzaErrorSiNoEncuentraRole() {
        RegisterRequest request = new RegisterRequest("A", "B", "z", "z@z", "123",
                Set.of("admin"));

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rol no encontrado");
    }

    // --------------------------------------------------------------------
    @Test @Order(7)
    void testRegisterGuardaUsuarioCorrectamente() {
        RegisterRequest request = new RegisterRequest("A", "B", "u", "u@u", "abc",
                null);

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)).thenReturn(Optional.of(rolUser));
        when(passwordEncoder.encode("abc")).thenReturn("ENCODED");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthResponse res = authService.register(request);

        assertThat(res.getEmail()).isEqualTo("u@u");
    }

    // --------------------------------------------------------------------
    // LOGIN
    // --------------------------------------------------------------------
    @Test @Order(8)
    void testLoginCorrecto() {
        LoginRequest req = new LoginRequest("nayder", "123");

        Authentication authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(usuarioBase);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);

        when(jwtService.generateToken(usuarioBase)).thenReturn("TOKEN123");

        AuthResponse res = authService.login(req);

        assertThat(res.getUsername()).isEqualTo("nayder");
        assertThat(res.getToken()).isEqualTo("TOKEN123");
    }

    // --------------------------------------------------------------------
    @Test @Order(9)
    void testLoginFallaAutenticacion() {
        LoginRequest req = new LoginRequest("x", "y");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Error"));

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class);
    }

    // --------------------------------------------------------------------
    @Test @Order(10)
    void testLoginRetornaRolesCorrectos() {
        LoginRequest req = new LoginRequest("na", "123");

        Authentication auth = mock(Authentication.class);
        usuarioBase.setRoles(Set.of(rolAdmin, rolUser));

        when(auth.getPrincipal()).thenReturn(usuarioBase);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(usuarioBase)).thenReturn("TKN");

        AuthResponse res = authService.login(req);

        assertThat(res.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    // --------------------------------------------------------------------
    @Test @Order(11)
    void testInitRolesCreaRolesFaltantes() {
        when(rolRepository.existsByNombre(any())).thenReturn(false);
        when(rolRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        authService.initRoles();

        verify(rolRepository, times(4)).save(any());
    }

    // --------------------------------------------------------------------
    @Test @Order(12)
    void testInitRolesNoCreaRolesSiExisten() {
        when(rolRepository.existsByNombre(any())).thenReturn(true);

        authService.initRoles();

        verify(rolRepository, never()).save(any());
    }

    // --------------------------------------------------------------------
    @Test @Order(13)
    void testRegisterGeneraToken() {
        RegisterRequest r = new RegisterRequest("A", "B", "u", "e", "123", null);

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)).thenReturn(Optional.of(rolUser));
        when(passwordEncoder.encode(any())).thenReturn("ENC");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("TOK123");

        AuthResponse res = authService.register(r);

        assertThat(res.getToken()).isEqualTo("TOK123");
    }

    // --------------------------------------------------------------------
    @Test @Order(14)
    void testRegisterGuardaPasswordEncriptado() {
        RegisterRequest r = new RegisterRequest("a","b","c","d","clave",null);

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)).thenReturn(Optional.of(rolUser));
        when(passwordEncoder.encode("clave")).thenReturn("ENC_PASS");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("TKN");

        AuthResponse res = authService.register(r);

        assertThat(res.getToken()).isEqualTo("TKN");
        verify(passwordEncoder).encode("clave");
    }

    // --------------------------------------------------------------------
    @Test @Order(15)
    void testRegisterAsignaMultiplesRoles() {
        RegisterRequest r = new RegisterRequest("a","b","c","d","123",
                Set.of("admin","municipalidad"));

        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN)).thenReturn(Optional.of(rolAdmin));
        when(rolRepository.findByNombre(Rol.RolNombre.ROLE_MUNICIPALIDAD)).thenReturn(Optional.of(rolMuni));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("T");

        AuthResponse res = authService.register(r);

        assertThat(res.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_MUNICIPALIDAD");
    }

    // --------------------------------------------------------------------
    @Test @Order(16)
    void testLoginRetornaEmailCorrecto() {
        LoginRequest req = new LoginRequest("na", "123");

        Authentication auth = mock(Authentication.class);
        usuarioBase.setEmail("correo@test.com");

        when(auth.getPrincipal()).thenReturn(usuarioBase);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(any())).thenReturn("TK");

        AuthResponse res = authService.login(req);

        assertThat(res.getEmail()).isEqualTo("correo@test.com");
    }
}
