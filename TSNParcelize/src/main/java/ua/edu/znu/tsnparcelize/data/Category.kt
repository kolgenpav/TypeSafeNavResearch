package ua.edu.znu.tsnparcelize.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A simple data class representing a category of subjects.
 * It should be Serializable because it will be part of the Subject data class,
 *  which is passed between screens using serialization.
 *  It should be Parcelable because it will be part of the Subject data class,
 *  which is passed between screens using Parcelable.
 */
@Serializable
@Parcelize
data class Category(
    val id: Int,
    val name: String
) : Parcelable
