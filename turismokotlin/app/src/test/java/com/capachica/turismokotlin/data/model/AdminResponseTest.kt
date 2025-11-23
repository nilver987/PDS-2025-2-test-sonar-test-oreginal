package com.capachica.turismokotlin.data.model



import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.model.AdminResponse  // Asegúrate de importar AdminResponse
import org.junit.Assert.*
import org.junit.Test

class AdminResponseTest {

 // Test para AdminResponse
 @Test
 fun testAdminResponseCreation() {
  val response = AdminResponse(
   message = "Operation successful"
  )

  // Verificar que el mensaje se asignó correctamente
  assertEquals("Operation successful", response.message)
 }
}