package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.repository.CategoryRepository
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository = DependencyProvider.provideCategoryRepository(application)
    private val userId = SessionManager.userId ?: ""

    // Lista cruda de Room para búsquedas síncronas rápidas (compatibilidad con UI actual)
    private val _rawCategories = MutableStateFlow<List<UserCategory>>(emptyList())

    // Observamos las categorías desde el repositorio y las mapeamos a la UI
    val categories: StateFlow<List<CategoryItem>> = repository.getCategories(userId)
        .onEach { _rawCategories.value = it }
        .map { list ->
            list.map { it.toItem() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Al iniciar, intentamos sincronizar cambios pendientes
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            repository.syncPendingCategories()
        }
    }

    /**
     * Busca una categoría por su nombre.
     */
    fun getCategoryByName(name: String?): UserCategory? {
        if (name == "new" || name == null) return null
        return _rawCategories.value.find { it.name == name }
    }

    /**
     * Guarda o actualiza una categoría.
     */
    fun saveCategory(nombre: String, limite: String, existingCategory: UserCategory? = null) {
        val budget = limite.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
        val category = existingCategory?.copy(
            name = nombre,
            budget = budget
        ) ?: UserCategory(
            name = nombre,
            budget = budget,
            userId = userId
        )

        viewModelScope.launch {
            repository.saveCategory(category)
        }
    }

    fun deleteCategory(category: UserCategory) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    private fun UserCategory.toItem() = CategoryItem(
        icon = "📁", 
        name = name,
        spent = 0.0,
        budget = budget
    )
}
