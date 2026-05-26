package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem

class CategoriesViewModel : ViewModel() {

    // Lista observable de categorías. En el futuro esto vendrá de un Repository/DB.
    private val _categories = mutableStateListOf(
        CategoryItem("🍔", "Comida y Bebida", 18500.0, 25000.0),
        CategoryItem("🚗", "Transporte", 12200.0, 15000.0),
        CategoryItem("💡", "Servicios y Hogar", 9800.0, 8000.0)
    )
    val categories: List<CategoryItem> = _categories

    /**
     * Busca una categoría por su nombre (ID temporal).
     */
    fun getCategoryByName(name: String?): CategoryItem? {
        if (name == "new" || name == null) return null
        return _categories.find { it.name == name }
    }

    /**
     * Simula el guardado de una categoría.
     */
    fun saveCategory(nombre: String, limite: String) {
        // Aquí iría la lógica para persistir los datos.
        // Por ahora es solo un placeholder para cumplir con el flujo de la UI.
    }
}
