package ua.edu.znu.tsnsavedstate.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
//import kotlinx.serialization.Serializable

/**
 * A simple data class representing a category of subjects.
 */
//@Serializable
@Parcelize
data class Category(
    val id: Int,
    val name: String
) : Parcelable
