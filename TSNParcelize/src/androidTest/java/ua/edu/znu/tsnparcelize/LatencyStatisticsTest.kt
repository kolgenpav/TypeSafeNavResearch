package ua.edu.znu.tsnparcelize

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.edu.znu.tsnparcelize.benchmark.LatencyMeasurement
import ua.edu.znu.tsnparcelize.benchmark.calculateStats
import ua.edu.znu.tsnparcelize.nav.Nav
import ua.edu.znu.tsnparcelize.ui.theme.TSNParcelizeTheme
import java.util.Collections
import kotlin.time.Duration.Companion.nanoseconds

private const val TAG = "LatencyStats"
private const val ITERATIONS = 100

/**
 * Instrumented test that navigates from FirstScreen to SecondScreen [ITERATIONS] times,
 * collects a 'LatencyMeasurement' per cycle via the 'Nav.onLatencyMeasured' callback,
 * and logs the mean of each metric.
 */
@RunWith(AndroidJUnit4::class)
class LatencyStatisticsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun meanLatencies_over10NavigationCycles() {
        val measurements: MutableList<LatencyMeasurement> =
            Collections.synchronizedList(mutableListOf())

        composeTestRule.setContent {
            TSNParcelizeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Nav(
                        innerPadding = innerPadding,
                        onLatencyMeasured = { measurement ->
                            measurements.add(measurement)
                        }
                    )
                }
            }
        }

        repeat(ITERATIONS) { index ->
            composeTestRule.onNodeWithTag("subjectNameInput").performTextClearance() // reset input fields
            composeTestRule.onNodeWithTag("subjectNameInput").performClick()
                .performTextInput("Subject $index")
            if (index % 2 == 0) {
                composeTestRule.onNodeWithTag("subjectCheckedInput")
                    .performClick() // toggle the checkbox to add some variability
            }
            composeTestRule.onNodeWithTag("categoryNameInput").performTextClearance()
            composeTestRule.onNodeWithTag("categoryNameInput").performClick()
                .performTextInput("Category $index")
            composeTestRule.onNodeWithText("Go to Second Screen").performClick()
            // Wait until the current cycle's measurement has been reported
            // (withFrameNanos fires after the frame is dispatched).
            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                measurements.size > index
            }
            composeTestRule.onNodeWithText("Go back to First Screen").performClick()
            composeTestRule.waitForIdle()
        }

        assertTrue("Expected $ITERATIONS measurements, got ${measurements.size}",
            measurements.size == ITERATIONS)

        val handoffSetupValues = measurements.map { it.handoffSetupNs }
        val handoffValues = measurements.map { it.handoffNs }
        val compositionValues = measurements.map { it.compositionNs }
        val firstFrameValues = measurements.map { it.firstFrameNs }

        val handoffSetupStats = calculateStats(handoffSetupValues)
        val handoffStats = calculateStats(handoffValues)
        val compositionStats = calculateStats(compositionValues)
        val firstFrameStats = calculateStats(firstFrameValues)

        Log.i(TAG, "---- Latency Statistics ($ITERATIONS iterations) ----")
        Log.i(TAG, "")
        Log.i(TAG, "Handoff Setup:")
        Log.i(TAG, "  Mean:          ${handoffSetupStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${handoffSetupStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${handoffSetupStats.ci95Lower.nanoseconds} - ${handoffSetupStats.ci95Upper.nanoseconds}")
        Log.i(TAG, "")
        Log.i(TAG, "Handoff:")
        Log.i(TAG, "  Mean:          ${handoffStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${handoffStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${handoffStats.ci95Lower.nanoseconds} - ${handoffStats.ci95Upper.nanoseconds}")
        Log.i(TAG, "")
        Log.i(TAG, "Composition:")
        Log.i(TAG, "  Mean:          ${compositionStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${compositionStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${compositionStats.ci95Lower.nanoseconds} - ${compositionStats.ci95Upper.nanoseconds}")
        Log.i(TAG, "")
        Log.i(TAG, "First Frame:")
        Log.i(TAG, "  Mean:          ${firstFrameStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${firstFrameStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${firstFrameStats.ci95Lower.nanoseconds} - ${firstFrameStats.ci95Upper.nanoseconds}")

        // Sanity assertions: all values must be positive and ordered correctly.
        assertTrue("Handoff setup must be > 0", handoffSetupStats.mean > 0)
        assertTrue("Handoff must be >= handoff setup",  handoffStats.mean >= handoffSetupStats.mean)
        assertTrue("First frame must be >= handoff",    firstFrameStats.mean >= handoffStats.mean)
    }
}
