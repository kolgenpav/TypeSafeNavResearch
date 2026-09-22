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
     * Strategy A: ID strategy (data saved to a repository, ID passed via route arguments)
     */
    @Serializable
    data class SecondScreenA(val subjectId: Int) : Routes()
}
