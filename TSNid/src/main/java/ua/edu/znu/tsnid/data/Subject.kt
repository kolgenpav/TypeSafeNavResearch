package ua.edu.znu.tsnid.data

//import kotlinx.serialization.Serializable

/**
 * A simple data class representing a subject will be passed between screens.
 * It is not Serializable and not Parcelable because we will pass as route argument only its id.
 */
//@Serializable
data class Subject(
    val id: Int,
    val name: String,
    val isChecked: Boolean,
    val data: ByteArray,
    val category: Category
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subject

        if (id != other.id) return false
        if (isChecked != other.isChecked) return false
        if (name != other.name) return false
        if (!data.contentEquals(other.data)) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + isChecked.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + category.hashCode()
        return result
    }
}
