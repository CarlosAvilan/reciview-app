package ar.edu.uade.capturarecibosapp.ui.viewmodel

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel()
    }

    // ── Estado inicial ──────────────────────────────────────────────────────

    @Test
    fun `initial uiState is Idle`() {
        assertTrue(viewModel.uiState is RegisterState.Idle)
    }

    @Test
    fun `initial fields are blank`() {
        assertEquals("", viewModel.nombreCompleto)
        assertEquals("", viewModel.correoElectronico)
        assertEquals("", viewModel.password)
        assertEquals("", viewModel.paisNacimiento)
        assertNull(viewModel.fechaNacimiento)
    }

    @Test
    fun `initial error flags are false`() {
        assertFalse(viewModel.emailError)
        assertFalse(viewModel.passwordError)
        assertFalse(viewModel.birthDateError)
    }

    @Test
    fun `initial terms flags are false`() {
        assertFalse(viewModel.terminosAceptados)
        assertFalse(viewModel.haLeidoTerminos)
        assertFalse(viewModel.permisosCamaraAceptados)
    }

    // ── Actualizaciones de campos ───────────────────────────────────────────

    @Test
    fun `onNombreChange updates nombreCompleto`() {
        viewModel.onNombreChange("Juan Pérez")
        assertEquals("Juan Pérez", viewModel.nombreCompleto)
    }

    @Test
    fun `onCorreoChange updates correoElectronico`() {
        viewModel.onCorreoChange("juan@gmail.com")
        assertEquals("juan@gmail.com", viewModel.correoElectronico)
    }

    @Test
    fun `onPasswordChange updates password`() {
        viewModel.onPasswordChange("pass1234")
        assertEquals("pass1234", viewModel.password)
    }

    @Test
    fun `onPaisChange updates paisNacimiento`() {
        viewModel.onPaisChange("Argentina")
        assertEquals("Argentina", viewModel.paisNacimiento)
    }

    @Test
    fun `onFechaNacimientoChange updates fechaNacimiento`() {
        val fecha = LocalDate.of(2000, 6, 15)
        viewModel.onFechaNacimientoChange(fecha)
        assertEquals(fecha, viewModel.fechaNacimiento)
    }

    // ── Limpieza de flags de error al corregir campos ───────────────────────

    @Test
    fun `onCorreoChange clears emailError`() {
        viewModel.emailError = true
        viewModel.onCorreoChange("correcto@mail.com")
        assertFalse(viewModel.emailError)
    }

    @Test
    fun `onPasswordChange clears passwordError`() {
        viewModel.passwordError = true
        viewModel.onPasswordChange("nuevapass")
        assertFalse(viewModel.passwordError)
    }

    @Test
    fun `onFechaNacimientoChange clears birthDateError`() {
        viewModel.birthDateError = true
        viewModel.onFechaNacimientoChange(LocalDate.of(1995, 1, 1))
        assertFalse(viewModel.birthDateError)
    }

    // ── Gestión de términos ─────────────────────────────────────────────────

    @Test
    fun `onTerminosAceptadosChange sets terminosAceptados to true`() {
        viewModel.onTerminosAceptadosChange(true)
        assertTrue(viewModel.terminosAceptados)
    }

    @Test
    fun `onTerminosAceptadosChange can toggle back to false`() {
        viewModel.onTerminosAceptadosChange(true)
        viewModel.onTerminosAceptadosChange(false)
        assertFalse(viewModel.terminosAceptados)
    }

    @Test
    fun `onPermisosCamaraAceptadosChange updates permisosCamaraAceptados`() {
        viewModel.onPermisosCamaraAceptadosChange(true)
        assertTrue(viewModel.permisosCamaraAceptados)
    }

    @Test
    fun `marcarTerminosComoLeidos sets haLeidoTerminos to true`() {
        viewModel.marcarTerminosComoLeidos()
        assertTrue(viewModel.haLeidoTerminos)
    }
}