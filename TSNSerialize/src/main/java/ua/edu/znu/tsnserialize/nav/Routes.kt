package ua.edu.znu.tsnserialize.nav

import kotlinx.serialization.Serializable
import ua.edu.znu.tsnserialize.data.Subject

/**
 * Sealed class representing navigation routes with Serialization strategy
 * (data passed via route arguments, serialized to JSON)
 */
@Serializable
sealed class Routes {
    @Serializable
    data object FirstScreen : Routes()

    // B: Serialization strategy (data passed via route arguments, serialized to JSON)
    @Serializable
    data class SecondScreenB(val subject: Subject) : Routes()
}
