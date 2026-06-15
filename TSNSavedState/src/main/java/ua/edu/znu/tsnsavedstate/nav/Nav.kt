package ua.edu.znu.tsnsavedstate.nav

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.edu.znu.tsnsavedstate.data.Subject
import ua.edu.znu.tsnsavedstate.ui.screens.FirstScreen
import ua.edu.znu.tsnsavedstate.ui.screens.SecondScreen
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.measureTime

private const val TAG = "Nav"
private const val SUBJECT_ARG_KEY = "subject_arg"

/**
 * Main navigation graph composable.
 *
 * Measures four latency values per forward-navigation cycle and optionally
 * reports them via [onLatencyMeasured] for testing and statistical analysis.
 *
 * @param innerPadding    Padding values provided by the Scaffold.
 * @param onLatencyMeasured Optional callback invoked once per cycle, inside the first
 *                          Choreographer frame of SecondScreen, with all four measurements.
 */
@Composable
fun Nav(
    innerPadding: PaddingValues,
    onLatencyMeasured: ((LatencyMeasurement) -> Unit)? = null,
) {
    val navController = rememberNavController()
    // Shared monotonic timestamps (ns). LongArray(1) avoids recomposition
    // side-effects that mutableStateOf would cause on write.
    val navigationStartNs = remember { LongArray(1) }
    val handoffSetupNs = remember { LongArray(1) }

    NavHost(
        navController = navController,
        startDestination = Routes.FirstScreen,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable<Routes.FirstScreen> {
            FirstScreen(
                onNavigateForward = { subject ->
                    // Capture start BEFORE any strategy work so the handler's own overhead
                    // (data save to SavedState + navigate()) is cleanly isolated from the
                    // test-framework dispatch cost that precedes it on the main thread.
                    navigationStartNs[0] = System.nanoTime()
                    val handoffSetupLatency = measureTime {
                        // Strategy D: SavedStateHandle (data passed via SavedStateHandle, no args in route)
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            SUBJECT_ARG_KEY,
                            subject
                        )
                        navController.navigate(Routes.SecondScreenD)
                    }
                    handoffSetupNs[0] = handoffSetupLatency.inWholeNanoseconds
                    Log.d(TAG, "Subject handoff setup took $handoffSetupLatency")
                })
        }

        // Strategy D: SavedStateHandle (data passed via SavedStateHandle, no args in route)
        composable<Routes.SecondScreenD> {
            val subject =
                navController.previousBackStackEntry?.savedStateHandle?.get<Subject>(
                    SUBJECT_ARG_KEY
                )
            // Capture elapsed time after the Subject is retrieved: this is the true
            // end-to-end handoff latency - from navigation start to Subject in hand.
            val handoffLatencyNs = remember { System.nanoTime() - navigationStartNs[0] }
            // Capture composition start just before SecondScreen() runs.
            val compositionStartNs = remember { System.nanoTime() }
            SecondScreen(
                subject = subject,
                onNavigateBack = { navController.popBackStack() }
            )
            // Captured right after SecondScreen() completes its composition pass.
            val compositionLatencyNs = remember { System.nanoTime() - compositionStartNs }
            // LaunchedEffect(Unit) runs exactly once after the first composition -
            // the correct place for side effects such as logging and reporting.
            LaunchedEffect(Unit) {
                Log.d(TAG, "Subject handoff latency took ${handoffLatencyNs.nanoseconds}")
                Log.d(TAG, "SecondScreen composition took ${compositionLatencyNs.nanoseconds}")
                withFrameNanos {
                    val renderingLatencyNs = System.nanoTime() - navigationStartNs[0]
                    Log.d(TAG, "SecondScreen first frame dispatch took ${renderingLatencyNs.nanoseconds}")
                    onLatencyMeasured?.invoke(
                        LatencyMeasurement(
                            handoffSetupNs = handoffSetupNs[0],
                            handoffNs = handoffLatencyNs,
                            compositionNs = compositionLatencyNs,
                            firstFrameNs = renderingLatencyNs
                        )
                    )
                }
            }
        }
    }
}
