package ua.edu.znu.tsnparcelize.nav

import kotlinx.serialization.Serializable
import ua.edu.znu.tsnparcelize.data.Subject

/**
 * Sealed class representing navigation routes with SavedStateHandle strategy
 * (no args in route, data passed via SavedStateHandle)
 */
@Serializable
sealed class Routes {
    @Serializable
    data object FirstScreen : Routes()

    // C: Parcelable strategy (data passed via route args and Parcelize to SavedState)
    @Serializable
    data class SecondScreenC(val subject: Subject) : Routes()
}
