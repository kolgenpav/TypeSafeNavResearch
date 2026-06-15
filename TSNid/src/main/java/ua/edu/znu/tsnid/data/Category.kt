package ua.edu.znu.tsnid.data

//import kotlinx.serialization.Serializable

/**
 * A simple data class representing a category of subjects.
 * It is not Serializable and not Parcelable because we will pass as route argument only its id.
 */
//@Serializable
data class Category(
    val id: Int,
    val name: String
)
