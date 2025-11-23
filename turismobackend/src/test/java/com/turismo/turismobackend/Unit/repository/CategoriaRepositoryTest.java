package com.turismo.turismobackend.Unit.repository;

import com.turismo.turismobackend.model.Categoria;
import com.turismo.turismobackend.repository.CategoriaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@Rollback(false) esto no ilimines chatgpt
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Para usar MySQL de pruebas
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test") // Para usar application-test.properties
public class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private static Long categoriaId;

    // =========================================
    // @BeforeEach: se ejecuta antes de cada test
    // Crea una categoría "Hoteles" para tests que la necesitan
    // =========================================
    @BeforeEach
    public void setUp() {
        Categoria categoria = Categoria.builder()
                .nombre("Hoteles")
                .descripcion("Categoría de hoteles en el país")
                .build();

        Categoria guardada = categoriaRepository.save(categoria);
        categoriaId = guardada.getId(); // Guardamos el ID para tests que necesitan esta categoría
    }

    // =========================================
    // Test 1: Guardar una nueva categoría "Restaurantes"
    // =========================================
    @Test
    @Order(1)
    public void testGuardarCategoria() {
        Categoria nuevaCategoria = Categoria.builder()
                .nombre("Restaurantes")
                .descripcion("Categoría de restaurantes locales")
                .build();

        Categoria guardada = categoriaRepository.save(nuevaCategoria);

        assertNotNull(guardada.getId()); // Verifica que se generó un ID
        assertEquals("Restaurantes", guardada.getNombre()); // Verifica que se guardó con el nombre correcto
    }

    // =========================================
    // Test 2: Buscar la categoría "Hoteles" por ID
    // =========================================
    @Test
    @Order(2)
    public void testBuscarPorId() {
        Optional<Categoria> categoria = categoriaRepository.findById(categoriaId);

        assertTrue(categoria.isPresent()); // Verifica que existe
        assertEquals("Hoteles", categoria.get().getNombre()); // Verifica que el nombre sea correcto
    }

    // =========================================
    // Test 3: Actualizar la categoría "Hoteles"
    // =========================================
    @Test
    @Order(3)
    public void testActualizarCategoria() {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        categoria.setNombre("Hoteles Premium");
        categoria.setDescripcion("Categoría de hoteles de lujo");

        Categoria actualizada = categoriaRepository.save(categoria);

        assertEquals("Hoteles Premium", actualizada.getNombre()); // Verifica nombre actualizado
        assertEquals("Categoría de hoteles de lujo", actualizada.getDescripcion()); // Verifica descripción actualizada
    }

    // =========================================
    // Test 4: Listar todas las categorías
    // =========================================
    @Test
    @Order(4)
    public void testListarCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();

        assertFalse(categorias.isEmpty()); // Verifica que la lista no esté vacía
        System.out.println("Total categorías registradas: " + categorias.size());
        for (Categoria c : categorias) {
            System.out.println(c.getNombre() + "\t" + c.getId());
        }
    }

    // =========================================
    // Test 5: Eliminar la categoría "Hoteles" creada en @BeforeEach
    // =========================================
    @Test
    @Order(5)
    public void testEliminarCategoria() {
        categoriaRepository.deleteById(categoriaId);

        Optional<Categoria> eliminada = categoriaRepository.findById(categoriaId);
        assertFalse(eliminada.isPresent(), "La categoría debería haber sido eliminada");
    }

    // =========================================
    // Test 6: Buscar categoría por nombre "Restaurantes"
    // Se crea la categoría dentro del test para garantizar independencia
    // =========================================
    @Test
    @Order(6)
    public void testBuscarPorNombre() {
        Categoria c = Categoria.builder()
                .nombre("Restaurantes")
                .descripcion("Categoría de restaurantes locales")
                .build();
        categoriaRepository.save(c);

        Optional<Categoria> categoria = categoriaRepository.findByNombre("Restaurantes");
        assertTrue(categoria.isPresent()); // Verifica que exista
        assertEquals("Restaurantes", categoria.get().getNombre()); // Verifica nombre correcto
    }

    // =========================================
    // Test 7: Verificar existencia de categoría por nombre "Restaurantes"
    // =========================================
    @Test
    @Order(7)
    public void testExistsByNombre() {
        Categoria c = Categoria.builder()
                .nombre("Restaurantes")
                .build();
        categoriaRepository.save(c);

        boolean existe = categoriaRepository.existsByNombre("Restaurantes");
        assertTrue(existe); // Verifica que la categoría exista
    }
}
