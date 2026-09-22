package ua.edu.znu.tsnsavedstate.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.edu.znu.tsnsavedstate.benchmark.LatencyMeasurement
import ua.edu.znu.tsnsavedstate.data.Subject
import ua.edu.znu.tsnsavedstate.ui.screens.FirstScreen
import ua.edu.znu.tsnsavedstate.ui.screens.SecondScreen

private const val SUBJECT_ARG_KEY = "subject_arg"

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
    val navigationInitiated = remember { mutableLongStateOf(0L) }  // T0: Navigation initiated
    val setupCompleted = remember { mutableLongStateOf(0L) }       // T1: Setup complete

    NavHost(
        navController = navController,
        startDestination = Routes.FirstScreen,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable<Routes.FirstScreen> {
            FirstScreen(
                onNavigateForward = { subject ->
                    // Store the Subject in the SavedStateHandle of the current back stack entry
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        SUBJECT_ARG_KEY,
                        subject
                    )
                    // T0: Capture start before setup work
                    val t0 = System.nanoTime()
                    navigationInitiated.longValue = t0
                    // Strategy D: SavedStateHandle (data passed via SavedStateHandle, no args in route)
                    navController.navigate(Routes.SecondScreenD)
                    // T1: Capture after navigate() call completes
                    val t1 = System.nanoTime()
                    setupCompleted.longValue = t1
                })
        }

        // Strategy D: SavedStateHandle (data passed via SavedStateHandle, no args in route)
        composable<Routes.SecondScreenD> {
            // T2: Capture when SecondScreen composable starts
            val t2 = System.nanoTime()
            val subject =
                navController.previousBackStackEntry?.savedStateHandle?.get<Subject>(
                    SUBJECT_ARG_KEY
                )
            // T3: Capture after data is retrieved
            val t3 = System.nanoTime()
            
            SecondScreen(
                subject = subject,
                onNavigateBack = { navController.popBackStack() }
            )
            // LaunchedEffect(Unit) runs exactly once after the first composition
            LaunchedEffect(Unit) {
                withFrameNanos {
                    // T4: Capture when first frame is dispatched
                    val t4 = System.nanoTime()
                    onLatencyMeasured?.invoke(
                        LatencyMeasurement(
                            navigationInitiated = navigationInitiated.longValue,
                            setupCompleted = setupCompleted.longValue,
                            destinationScreenEntered = t2,
                            objectRetrieved = t3,
                            firstFrameDispatched = t4
                        )
                    )
                }
            }
        }
    }
}
