package ua.edu.znu.tsnserialize.nav

import android.net.Uri
//import android.util.Log
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.json.Json
import ua.edu.znu.tsnserialize.data.Subject

//private const val TAG = "JSONEncodedSubjectSize"
/**
 * NavType for Subject using kotlinx-serialization-json to convert to/from JSON string.
 * This is a pure string-based strategy, so it doesn't rely on Parcelable or Bundle's type system.
 */
object SubjectSerializableNavType {
    val subjectType: NavType<Subject> = object : NavType<Subject>(isNullableAllowed = false) {

        override fun put(bundle: SavedState, key: String, value: Subject) {
            // Stored form for SavedState
            val json = Json.encodeToString(value)
            // Use once for size logging, but in production you might want to remove this for performance reasons
//            Log.d(TAG, "The JSON string size for Subject with id=${value.id} is ${json.toByteArray().size} bytes")
            bundle.putString(key, json)
        }

        override fun get(bundle: SavedState, key: String): Subject? {
            val raw = bundle.getString(key) ?: return null
            return runCatching { Json.decodeFromString<Subject>(raw) }.getOrNull()
        }

        override fun parseValue(value: String): Subject {
            // Route arg comes URI-encoded
            // Only triggers when the app explicitly needs to turn a URL string back into your object
            // (like opening a deep link or restoring your app after Android kills it in the background).
            val decoded = Uri.decode(value)
            return Json.decodeFromString(decoded)
        }

        override fun serializeAsValue(value: Subject): String {
            // Route args must be string-safe
            // Only triggers when the app explicitly needs to turn a URL string back into your object
            // (like opening a deep link or restoring your app after Android kills it in the background).
            val json = Json.encodeToString(value)
            return Uri.encode(json)
        }
    }
}