package ua.edu.znu.tsnid.nav

import kotlinx.serialization.Serializable

/**
 * Sealed class representing navigation routes with SavedStateHandle strategy.
 */
@Serializable
sealed class Routes {
    @Serializable
    data object FirstScreen : Routes()

    // A: ID strategy (ID passed in route, data retrieved from repository in destination)
    @Serializable
    data class SecondScreenA(val subjectId: Int) : Routes()
}
