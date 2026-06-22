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

    // Estado inicial

    @Test
    fun RegisterViewModelTest_EstadoInicial_EsIdle() {
        assertTrue(viewModel.uiState is RegisterState.Idle)
    }

    @Test
    fun RegisterViewModelTest_CamposIniciales_EstanVacios() {
        assertEquals("", viewModel.nombreCompleto)
        assertEquals("", viewModel.correoElectronico)
        assertEquals("", viewModel.password)
        assertEquals("", viewModel.paisNacimiento)
        assertNull(viewModel.fechaNacimiento)
    }

    @Test
    fun RegisterViewModelTest_ErroresIniciales_SonFalsos() {
        assertFalse(viewModel.emailError)
        assertFalse(viewModel.passwordError)
        assertFalse(viewModel.birthDateError)
    }

    @Test
    fun RegisterViewModelTest_TerminosIniciales_SonFalsos() {
        assertFalse(viewModel.terminosAceptados)
        assertFalse(viewModel.haLeidoTerminos)
        assertFalse(viewModel.permisosCamaraAceptados)
    }

    // Actualizaciones de campos

    @Test
    fun RegisterViewModelTest_OnNombreChange_ActualizaNombre() {
        viewModel.onNombreChange("Juan Pérez")
        assertEquals("Juan Pérez", viewModel.nombreCompleto)
    }

    @Test
    fun RegisterViewModelTest_OnCorreoChange_ActualizaCorreo() {
        viewModel.onCorreoChange("juan@gmail.com")
        assertEquals("juan@gmail.com", viewModel.correoElectronico)
    }

    @Test
    fun RegisterViewModelTest_OnPasswordChange_ActualizaPassword() {
        viewModel.onPasswordChange("pass1234")
        assertEquals("pass1234", viewModel.password)
    }

    @Test
    fun RegisterViewModelTest_OnPaisChange_ActualizaPais() {
        viewModel.onPaisChange("Argentina")
        assertEquals("Argentina", viewModel.paisNacimiento)
    }

    @Test
    fun RegisterViewModelTest_OnFechaNacimientoChange_ActualizaFecha() {
        val fecha = LocalDate.of(2000, 6, 15)
        viewModel.onFechaNacimientoChange(fecha)
        assertEquals(fecha, viewModel.fechaNacimiento)
    }

    // Limpieza de flags de error al corregir campos

    @Test
    fun RegisterViewModelTest_OnCorreoChange_LimpiaError() {
        viewModel.emailError = true
        viewModel.onCorreoChange("correcto@mail.com")
        assertFalse(viewModel.emailError)
    }

    @Test
    fun RegisterViewModelTest_OnPasswordChange_LimpiaError() {
        viewModel.passwordError = true
        viewModel.onPasswordChange("nuevapass")
        assertFalse(viewModel.passwordError)
    }

    @Test
    fun RegisterViewModelTest_OnFechaNacimientoChange_LimpiaError() {
        viewModel.birthDateError = true
        viewModel.onFechaNacimientoChange(LocalDate.of(1995, 1, 1))
        assertFalse(viewModel.birthDateError)
    }

    // Gestión de términos

    @Test
    fun RegisterViewModelTest_OnTerminosAceptadosChange_ActualizaEstado() {
        viewModel.onTerminosAceptadosChange(true)
        assertTrue(viewModel.terminosAceptados)
    }

    @Test
    fun RegisterViewModelTest_OnTerminosAceptadosChange_PuedeAlternar() {
        viewModel.onTerminosAceptadosChange(true)
        viewModel.onTerminosAceptadosChange(false)
        assertFalse(viewModel.terminosAceptados)
    }

    @Test
    fun RegisterViewModelTest_OnPermisosCamaraChange_ActualizaEstado() {
        viewModel.onPermisosCamaraAceptadosChange(true)
        assertTrue(viewModel.permisosCamaraAceptados)
    }

    @Test
    fun RegisterViewModelTest_MarcarTerminosLeidos_ActualizaEstado() {
        viewModel.marcarTerminosComoLeidos()
        assertTrue(viewModel.haLeidoTerminos)
    }
}