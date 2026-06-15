package ua.edu.znu.tsnsavedstate.nav

import kotlinx.serialization.Serializable

/**
 * Sealed class representing navigation routes with SavedStateHandle strategy
 * (no args in route, data passed via SavedStateHandle)
 */
@Serializable
sealed class Routes {
    @Serializable
    data object FirstScreen : Routes()

    // D: SavedStateHandle strategy (no args in route)
    @Serializable
    data object SecondScreenD : Routes()
}
