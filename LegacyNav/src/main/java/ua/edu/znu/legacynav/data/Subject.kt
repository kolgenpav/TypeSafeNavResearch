package ua.edu.znu.legacynav.data

/**
 * A simple data class representing a subject will be passed between screens.
 */
data class Subject(
    val id: Int,
    val name: String,
    val isChecked: Boolean,
    val category: Category
)
