package ua.edu.znu.tsnserialize.nav

import android.net.Uri
import android.util.Log
//import android.util.Log
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.json.Json
import ua.edu.znu.tsnserialize.data.Subject

private const val TAG = "MResearch"
/**
 * NavType for Subject using kotlinx-serialization-json to convert to/from JSON string.
 * This is a pure string-based strategy, so it doesn't rely on Parcelable or Bundle's type system.
 */
object SubjectSerializableNavType {
    val subjectType: NavType<Subject> = object : NavType<Subject>(isNullableAllowed = false) {

        override fun put(bundle: SavedState, key: String, value: Subject) {
            val t0 = System.nanoTime()
            // Stored form for SavedState
            val json = Json.encodeToString(value)
            // Use once for size logging, but in production you might want to remove this for performance reasons
//            Log.d(TAG, "The JSON string size for Subject with id=${value.id} is ${json.toByteArray().size} bytes")
            bundle.putString(key, json)
            val t1 = System.nanoTime()
            Log.d(TAG, "put: Stored Subject with id=${value.id} into bundle for time=${t1 - t0} ns")
        }

        override fun get(bundle: SavedState, key: String): Subject? {
            val t0 = System.nanoTime()
            val raw = bundle.getString(key) ?: return null
            val subject = Json.decodeFromString<Subject>(raw)
            val t1 = System.nanoTime()
            Log.d(TAG, "get: Retrieved Subject with id=${subject.id} from bundle for time=${t1 - t0} ns")
            return subject
        }

        override fun parseValue(value: String): Subject {
            val t0 = System.nanoTime()
            // Route arg comes URI-encoded
            // Only triggers when the app explicitly needs to turn a URL string back into your object
            // (like opening a deep link or restoring your app after Android kills it in the background).
            val decoded = Uri.decode(value)
            val subject: Subject = Json.decodeFromString(decoded)
            val t1 = System.nanoTime()
            Log.d(TAG, "parseValue: Parsed Subject with id=${subject.id} from route argument for time=${t1 - t0} ns")
            return subject
        }

        /**
         * Serializes the Subject to a string for use in route arguments.
         * This is called when navigating to a destination that requires a Subject argument.
         */
        override fun serializeAsValue(value: Subject): String {
            val t0 = System.nanoTime()
            // Route args must be string-safe
            // Only triggers when the app explicitly needs to turn a URL string back into your object
            // (like opening a deep link or restoring your app after Android kills it in the background).
            val json = Json.encodeToString(value)
            val resultString = Uri.encode(json)
            val t1 = System.nanoTime()
            Log.d(TAG, "serializeAsValue: Serialized Subject with id=${value.id} to route argument for time=${t1 - t0} ns")
            return resultString
        }
    }
}