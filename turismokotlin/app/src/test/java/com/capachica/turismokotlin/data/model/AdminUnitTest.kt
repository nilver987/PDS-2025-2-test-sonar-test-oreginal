package com.capachica.turismokotlin

import com.capachica.turismokotlin.data.model.*
import org.junit.Assert.*
import org.junit.Test

class AdminUnitTest {

 // Test para la clase CreateEmprendedorRequest
 @Test
 fun testCreateEmprendedorRequest() {
  val request = CreateEmprendedorRequest(
   nombreEmpresa = "Tienda A",
   rubro = "Retail",
   direccion = "Calle Ficticia 123",
   latitud = 12.3456,  // Valor Double no nulo
   longitud = 78.9101, // Valor Double no nulo
   telefono = "987654321",
   email = "contacto@tiendaa.com",
   sitioWeb = "www.tiendaa.com",
   descripcion = "Una tienda para todo",
   productos = "Ropa, Calzado",
   servicios = "Entrega a domicilio",
   municipalidadId = 1,
   categoriaId = 1
  )

  // Verificar que los datos se asignaron correctamente
  assertEquals("Tienda A", request.nombreEmpresa)
  assertEquals("Retail", request.rubro)
  assertEquals("Calle Ficticia 123", request.direccion)
  request.latitud?.let { assertEquals(12.3456, it, 0.0001) } // Comparación con tolerancia para Double
  request.longitud?.let { assertEquals(78.9101, it, 0.0001) } // Comparación con tolerancia para Double
  assertEquals("987654321", request.telefono)
  assertEquals("contacto@tiendaa.com", request.email)
  assertEquals("www.tiendaa.com", request.sitioWeb)
  assertEquals("Una tienda para todo", request.descripcion)
  assertEquals("Ropa, Calzado", request.productos)
  assertEquals("Entrega a domicilio", request.servicios)
  assertEquals(1, request.municipalidadId)
  assertEquals(1, request.categoriaId)
 }

 // Test para la clase UpdateEmprendedorRequest con valores nulos
 @Test
 fun testUpdateEmprendedorRequestWithNullValues() {
  val request = UpdateEmprendedorRequest(
   nombreEmpresa = "Tienda B",
   rubro = "Electrónica",
   direccion = null, // Dirección puede ser nula
   latitud = null,   // Latitud puede ser nula
   longitud = null,  // Longitud puede ser nula
   telefono = "1122334455",
   email = "contacto@tiendab.com",
   sitioWeb = null,  // Sitio web puede ser nulo
   descripcion = null, // Descripción puede ser nula
   productos = null,  // Productos pueden ser nulos
   servicios = null,  // Servicios pueden ser nulos
   municipalidadId = 2,
   categoriaId = 3
  )

  // Verificar que los valores opcionales sean null
  assertNull(request.direccion)
  assertNull(request.sitioWeb)
  assertNull(request.descripcion)
  assertNull(request.productos)
  assertNull(request.servicios)

  // Usar el operador Elvis para manejar valores nulos en latitud y longitud
  val latitudFinal = request.latitud ?: 0.0 // Si es null, usamos 0.0
  val longitudFinal = request.longitud ?: 0.0 // Si es null, usamos 0.0

  // Verificar que las variables latitud y longitud no sean null
  assertEquals(0.0, latitudFinal, 0.0001)
  assertEquals(0.0, longitudFinal, 0.0001)
 }

 // Test para la clase CreateCategoriaRequest
 @Test
 fun testCreateCategoriaRequest() {
  val request = CreateCategoriaRequest(
   nombre = "Categoria A",
   descripcion = "Descripción de la categoría"
  )

  // Verificar que los datos se asignaron correctamente
  assertEquals("Categoria A", request.nombre)
  assertEquals("Descripción de la categoría", request.descripcion)
 }

 // Test para la clase UpdateCategoriaRequest
 @Test
 fun testUpdateCategoriaRequest() {
  val request = UpdateCategoriaRequest(
   nombre = "Categoria B",
   descripcion = null  // Descripción puede ser nula
  )

  // Verificar que los datos se asignaron correctamente
  assertEquals("Categoria B", request.nombre)
  assertNull(request.descripcion)
 }
}
