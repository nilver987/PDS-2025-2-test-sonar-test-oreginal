package com.capachica.turismokotlin

import com.capachica.turismokotlin.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ChatModelsTest {

 // TESTS PARA LOS ENUMS:

 // Test para EstadoConversacion enum
 @Test
 fun testEstadoConversacionFromString() {
  assertEquals(EstadoConversacion.ACTIVA, EstadoConversacion.fromString("ACTIVA"))
  assertEquals(EstadoConversacion.CERRADA, EstadoConversacion.fromString("CERRADA"))
  assertEquals(EstadoConversacion.PAUSADA, EstadoConversacion.fromString("PAUSADA"))
  assertEquals(EstadoConversacion.ACTIVA, EstadoConversacion.fromString("UNKNOWN"))  // Valor no reconocido
 }

 // Test para TipoMensaje enum
 @Test
 fun testTipoMensajeFromString() {
  assertEquals(TipoMensaje.TEXTO, TipoMensaje.fromString("TEXTO"))
  assertEquals(TipoMensaje.IMAGEN, TipoMensaje.fromString("IMAGEN"))
  assertEquals(TipoMensaje.ARCHIVO, TipoMensaje.fromString("ARCHIVO"))
  assertEquals(TipoMensaje.UBICACION, TipoMensaje.fromString("UBICACION"))
  assertEquals(TipoMensaje.SISTEMA, TipoMensaje.fromString("SISTEMA"))
  assertEquals(TipoMensaje.TEXTO, TipoMensaje.fromString("UNKNOWN"))  // Valor no reconocido
 }

 // TESTS PARA LOS MODELOS:

 // Test para Conversacion
 @Test
 fun testConversacionCreation() {
  val usuario = UsuarioBasico(id = 1, nombre = "Usuario1", apellido = "Apellido1", username = "usuario1", email = "usuario1@example.com")
  val municipio = MunicipalidadBasica(id = 1, nombre = "Municipalidad1", departamento = "Departamento", provincia = "Provincia", distrito = "Distrito")
  val emprendedor = EmprendedorBasico(id = 1, nombreEmpresa = "Emprendedor1", rubro = "Servicios", telefono = "123456789", email = "emprendedor1@example.com", municipalidad = municipio)

  val conversacion = Conversacion(
   id = 1,
   usuarioId = 1,
   emprendedorId = 1,
   reservaId = null,
   reservaCarritoId = null,
   codigoReservaAsociada = null,
   fechaCreacion = "2025-06-30T12:00:00",
   fechaUltimoMensaje = "2025-06-30T12:30:00",
   estado = EstadoConversacion.ACTIVA,
   usuario = usuario,
   emprendedor = emprendedor,
   ultimoMensaje = null,
   mensajesNoLeidos = 0,
   mensajesRecientes = emptyList()
  )

  assertEquals(1, conversacion.id)
  assertEquals("Usuario1", conversacion.usuario.nombre)
  assertEquals("Emprendedor1", conversacion.emprendedor.nombreEmpresa)
  assertEquals(EstadoConversacion.ACTIVA, conversacion.estado)
 }

 // Test para MensajeChat
 @Test
 fun testMensajeChatCreation() {
  val mensaje = MensajeChat(
   id = 1,
   conversacionId = 1,
   mensaje = "Hola, ¿cómo estás?",
   tipo = TipoMensaje.TEXTO,
   fechaEnvio = "2025-06-30T12:30:00",
   leido = false,
   esDeEmprendedor = true,
   remitenteId = 1,
   remitenteNombre = "Emprendedor1",
   archivoUrl = null,
   archivoNombre = null,
   archivoTipo = null
  )

  assertEquals(1, mensaje.id)
  assertEquals("Hola, ¿cómo estás?", mensaje.mensaje)
  assertEquals(TipoMensaje.TEXTO, mensaje.tipo)
  assertEquals(false, mensaje.leido)
  assertEquals(true, mensaje.esDeEmprendedor)
  assertEquals("Emprendedor1", mensaje.remitenteNombre)
 }

 // Test para EmprendedorBasico
 @Test
 fun testEmprendedorBasicoCreation() {
  val municipio = MunicipalidadBasica(id = 1, nombre = "Municipalidad1", departamento = "Departamento", provincia = "Provincia", distrito = "Distrito")
  val emprendedor = EmprendedorBasico(
   id = 1,
   nombreEmpresa = "Emprendedor1",
   rubro = "Servicios",
   telefono = "123456789",
   email = "emprendedor1@example.com",
   municipalidad = municipio
  )

  assertEquals(1, emprendedor.id)
  assertEquals("Emprendedor1", emprendedor.nombreEmpresa)
  assertEquals("Servicios", emprendedor.rubro)
  assertEquals("123456789", emprendedor.telefono)
  assertEquals("emprendedor1@example.com", emprendedor.email)
  assertEquals("Municipalidad1", emprendedor.municipalidad.nombre)
 }

 // TESTS PARA LOS REQUESTS:

 // Test para EnviarMensajeRequest con un mensaje vacío
 @Test
 fun testEnviarMensajeRequestWithEmptyMessage() {
  val request = EnviarMensajeRequest(
   conversacionId = 1,
   mensaje = "",  // Mensaje vacío
   tipo = TipoMensaje.TEXTO
  )

  assertEquals(1, request.conversacionId)
  assertEquals("", request.mensaje)  // Mensaje vacío
  assertEquals(TipoMensaje.TEXTO, request.tipo)
 }

 // Test para CrearConversacionRequest con todos los parámetros nulos
 @Test
 fun testCrearConversacionRequestWithNullValues() {
  val request = CrearConversacionRequest(
   emprendedorId = 1,
   mensaje = "Mensaje de prueba",
   reservaId = null,
   reservaCarritoId = null
  )

  assertEquals(1, request.emprendedorId)
  assertEquals("Mensaje de prueba", request.mensaje)
  assertNull(request.reservaId)
  assertNull(request.reservaCarritoId)
 }

 // Test para CrearConversacionRequest con todos los parámetros opcionales
 @Test
 fun testCrearConversacionRequestWithAllOptionalValues() {
  val request = CrearConversacionRequest(
   emprendedorId = 1,
   mensaje = "Mensaje con reserva",
   reservaId = 1001L,  // Cambio a Long
   reservaCarritoId = 5001L  // Cambio a Long
  )

  assertEquals(1L, request.emprendedorId)  // Cambiado a Long
  assertEquals("Mensaje con reserva", request.mensaje)
  assertEquals(1001L, request.reservaId)  // Cambiado a Long
  assertEquals(5001L, request.reservaCarritoId)  // Cambiado a Long
 }

 // Test para CrearConversacionRequest con un mensaje largo
 @Test
 fun testCrearConversacionRequestWithLongMessage() {
  val request = CrearConversacionRequest(
   emprendedorId = 1,
   mensaje = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam scelerisque."
  )

  assertEquals(1, request.emprendedorId)
  assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam scelerisque.", request.mensaje)
 }

 // Test para EnviarMensajeRequest con un tipo diferente (no TEXTO)
 @Test
 fun testEnviarMensajeRequestWithImage() {
  val request = EnviarMensajeRequest(
   conversacionId = 1,
   mensaje = "Aquí va una imagen",
   tipo = TipoMensaje.IMAGEN
  )

  assertEquals(1, request.conversacionId)
  assertEquals("Aquí va una imagen", request.mensaje)
  assertEquals(TipoMensaje.IMAGEN, request.tipo)
 }

 // Test para EstadoConversacion con valores no reconocidos
 @Test
 fun testEstadoConversacionWithUnknownValue() {
  assertEquals(EstadoConversacion.ACTIVA, EstadoConversacion.fromString("UNKNOWN"))
 }

 // Test para TipoMensaje con valor nulo
 @Test
 fun testTipoMensajeWithNullValue() {
  assertEquals(TipoMensaje.TEXTO, TipoMensaje.fromString(null))
 }

 // Test para Conversacion con mensajes no leídos
 @Test
 fun testConversacionWithUnreadMessages() {
  val usuario = UsuarioBasico(id = 1, nombre = "Usuario1", apellido = "Apellido1", username = "usuario1", email = "usuario1@example.com")
  val municipio = MunicipalidadBasica(id = 1, nombre = "Municipalidad1", departamento = "Departamento", provincia = "Provincia", distrito = "Distrito")
  val emprendedor = EmprendedorBasico(id = 1, nombreEmpresa = "Emprendedor1", rubro = "Servicios", telefono = "123456789", email = "emprendedor1@example.com", municipalidad = municipio)

  val conversacion = Conversacion(
   id = 1,
   usuarioId = 1,
   emprendedorId = 1,
   reservaId = null,
   reservaCarritoId = null,
   codigoReservaAsociada = null,
   fechaCreacion = "2025-06-30T12:00:00",
   fechaUltimoMensaje = "2025-06-30T12:30:00",
   estado = EstadoConversacion.ACTIVA,
   usuario = usuario,
   emprendedor = emprendedor,
   ultimoMensaje = null,
   mensajesNoLeidos = 5,  // Mensajes no leídos
   mensajesRecientes = emptyList()
  )

  assertEquals(5, conversacion.mensajesNoLeidos)  // Verificar los mensajes no leídos
 }

 // Test para MensajeChat con archivo adjunto
 @Test
 fun testMensajeChatWithAttachment() {
  val mensaje = MensajeChat(
   id = 1,
   conversacionId = 1,
   mensaje = "Este es un archivo",
   tipo = TipoMensaje.ARCHIVO,
   fechaEnvio = "2025-06-30T12:30:00",
   leido = false,
   esDeEmprendedor = true,
   remitenteId = 1,
   remitenteNombre = "Emprendedor1",
   archivoUrl = "http://example.com/archivo.pdf",
   archivoNombre = "archivo.pdf",
   archivoTipo = "application/pdf"
  )

  assertEquals("http://example.com/archivo.pdf", mensaje.archivoUrl)
  assertEquals("archivo.pdf", mensaje.archivoNombre)
  assertEquals("application/pdf", mensaje.archivoTipo)
 }
}
