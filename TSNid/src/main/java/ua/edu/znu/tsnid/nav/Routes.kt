package ua.edu.znu.tsnid.nav

import kotlinx.serialization.Serializable

/**
 * Sealed class representing navigation routes with Repository strategy.
 * It is not safe-type navigation, but it is a common pattern in Jetpack Compose Navigation.
 */
@Serializable
sealed class Routes {
    @Serializable
    data object FirstScreen : Routes()

    /**
     * A: ID strategy (ID passed in route, data retrieved from repository in destination)
     */
    @Serializable
    data class SecondScreenA(val subjectId: Int) : Routes()
}
