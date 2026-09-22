package ua.edu.znu.tsnparcelize.nav

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ua.edu.znu.tsnparcelize.benchmark.LatencyMeasurement
import ua.edu.znu.tsnparcelize.data.Subject
import ua.edu.znu.tsnparcelize.ui.screens.FirstScreen
import ua.edu.znu.tsnparcelize.ui.screens.SecondScreen
import kotlin.reflect.typeOf

/**
 * Main navigation graph composable.
 *
 * Measures five timing points per forward-navigation cycle and optionally
 * reports them via [onLatencyMeasured] for testing and statistical analysis.
 *
 * @param innerPadding    Padding values provided by the Scaffold.
 * @param onLatencyMeasured Optional callback invoked once per cycle, inside the first
 *                          Choreographer frame of SecondScreen, with all five timing points.
 */
@Composable
fun Nav(
    innerPadding: PaddingValues,
    onLatencyMeasured: ((LatencyMeasurement) -> Unit)? = null,
) {
    val navController = rememberNavController()
    // Shared timestamps that persist across scope boundaries (FirstScreen → SecondScreen).
    val navigationInitiated = remember { mutableStateOf(0L) }  // T0: Navigation initiated
    val setupCompleted = remember { mutableStateOf(0L) }       // T1: Setup complete

    NavHost(
        navController = navController,
        startDestination = Routes.FirstScreen,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable<Routes.FirstScreen> {
            FirstScreen(
                onNavigateForward = { subject ->
                    // T0: Capture start before setup work
                    val t0 = System.nanoTime()
                    navigationInitiated.value = t0
                    Log.d("MResearch", "FirstScreen: Before navigate() call, subject id=${subject.id}, name=${subject.name}")
                    // C: Parcelable strategy (data passed via route args and Parcelize to SavedState)
                    navController.navigate(Routes.SecondScreenC(subject))
                    // T1: Capture after navigate() call completes
                    val t1 = System.nanoTime()
                    setupCompleted.value = t1
                    Log.d("MResearch", "FirstScreen: After navigate() call, subject id=${subject.id}, name=${subject.name}, navigate latency=${t1 - t0} ns")
                })
        }

        // C: Parcelable strategy (data passed via route args and Parcelize to SavedState)
        composable<Routes.SecondScreenC>(
            typeMap = mapOf(typeOf<Subject>() to SubjectParcelableNavType.subjectType)
        ) { backStackEntry ->
            // T2: Capture when SecondScreen composable starts
            val t2 = System.nanoTime()
            Log.d("MResearch", "SecondScreen: Before retrieving subject from backStackEntry, arguments=${backStackEntry.arguments}, framework rooting latency=${t2 - setupCompleted.value} ns")
            val subject = backStackEntry.toRoute<Routes.SecondScreenC>().subject
            // T3: Capture after data is retrieved
            val t3 = System.nanoTime()
            Log.d("MResearch", "SecondScreen: After retrieving subject, subject id=${subject.id}, name=${subject.name}, data retrieval latency=${t3 - t2} ns")
            SecondScreen(
                subject = subject,
                onNavigateBack = { navController.popBackStack() }
            )
            // LaunchedEffect(Unit) runs exactly once after the first composition
            LaunchedEffect(Unit) {
                withFrameNanos {
                    // T4: Capture when first frame is dispatched
                    val t4 = System.nanoTime()
                    Log.d("MResearch", "SecondScreen: Inside withFrameNanos, first frame dispatched, frame dispatch latency=${t4 - t3} ns")
                    onLatencyMeasured?.invoke(
                        LatencyMeasurement(
                            navigationInitiated = navigationInitiated.value,
                            setupCompleted = setupCompleted.value,
                            destinationScreenEntered = t2,
                            objectRetrieved = t3,
                            firstFrameDispatched = t4
                        )
                    )
                    val t5 = System.nanoTime()
                    Log.d("MResearch", "SecondScreen: After invoking onLatencyMeasured, callback latency=${t5 - t4} ns")
                }
            }
        }
    }
}
