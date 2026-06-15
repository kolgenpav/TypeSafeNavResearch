package ua.edu.znu.tsnserialize.data

import kotlinx.serialization.Serializable

/**
 * A simple data class representing a category of subjects.
 * It should be Serializable because it will be part of the Subject data class,
 * which is passed between screens using serialization.
 */
@Serializable
data class Category(
    val id: Int,
    val name: String
)
