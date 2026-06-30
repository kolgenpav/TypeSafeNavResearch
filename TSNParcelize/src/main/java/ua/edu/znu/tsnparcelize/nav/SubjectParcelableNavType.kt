package ua.edu.znu.tsnparcelize.nav

import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.util.Base64
//import android.util.Log
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import ua.edu.znu.tsnparcelize.data.Subject

//private const val TAG = "ParcelizeSubjectSize"

/**
 * NavType for Subject using kotlin.plugin.parcelize to convert to/from Parcelable.
 */
object SubjectParcelableNavType {
    val subjectType: NavType<Subject> = object : NavType<Subject>(isNullableAllowed = false) {

        override fun put(bundle: SavedState, key: String, value: Subject) {
            // For debugging: calculate the size of the Parcelable in bytes
            // You should comment out this logging in production for performance reasons
//            val parcel = Parcel.obtain()
//            try {
//                value.writeToParcel(parcel, 0)
//                val sizeInBytes = parcel.dataSize()
//                Log.d(
//                    TAG,
//                    "The Parcelable size for Subject with id=${value.id} is $sizeInBytes bytes"
//                )
//
//            } finally {
//                parcel.recycle()
//            }
            // Use putParcelable instead of putString
            bundle.putParcelable(key, value)
        }

        override fun get(bundle: SavedState, key: String): Subject? {
            // Use getParcelable instead of getString
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, Subject::class.java)
            } else {
                @Suppress("DEPRECATION") // for backwards compatibility
                bundle.getParcelable(key)
            }
        }

        override fun parseValue(value: String): Subject {
            // This is still called for URI parsing, but for Parcelable strategy
            // the data flows through SavedState, not the route string
            // Only triggers when the app explicitly needs to turn a URL string back into your object
            // (like opening a deep link or restoring your app after Android kills it in the background).
            val decoded = Uri.decode(value)
            val bytes = Base64.decode(decoded, Base64.DEFAULT)
            val parcel = Parcel.obtain()
            return try {
                parcel.unmarshall(bytes, 0, bytes.size)
                parcel.setDataPosition(0)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                    parcel.readParcelable(
                        Subject::class.java.classLoader,
                        Subject::class.java
                    ) ?: error("Failed to decode Subject from parcel")
                } else {
                    @Suppress("DEPRECATION")
                    parcel.readParcelable(Subject::class.java.classLoader)
                        ?: error("Failed to decode Subject from parcel")
                }
            } finally {
                parcel.recycle()
            }
        }

        override fun serializeAsValue(value: Subject): String {
            // For route serialization, we can still use JSON as fallback
            // but the Parcelable strategy primarily uses SavedState
            // Only triggers when the app explicitly needs to turn a URL string back into your object
            // (like opening a deep link or restoring your app after Android kills it in the background).
            val parcel = Parcel.obtain()
            return try {
                parcel.writeParcelable(value, 0)
                val bytes = parcel.marshall()
                val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                Uri.encode(b64)
            } finally {
                parcel.recycle()
            }
        }
    }
}
