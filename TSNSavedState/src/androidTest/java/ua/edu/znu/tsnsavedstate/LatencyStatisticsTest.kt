package ua.edu.znu.tsnsavedstate

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
import ua.edu.znu.tsnsavedstate.nav.LatencyMeasurement
import ua.edu.znu.tsnsavedstate.nav.Nav
import ua.edu.znu.tsnsavedstate.ui.theme.TSNSavedStateTheme
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
            TSNSavedStateTheme {
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

        val meanHandoffSetup = measurements.map { it.handoffSetupNs }.average().toLong()
        val meanHandoff      = measurements.map { it.handoffNs }.average().toLong()
        val meanComposition  = measurements.map { it.compositionNs }.average().toLong()
        val meanFirstFrame   = measurements.map { it.firstFrameNs }.average().toLong()

        Log.i(TAG, "---- Latency Statistics ($ITERATIONS iterations) ----")
        Log.i(TAG, "  Mean handoff setup:    ${meanHandoffSetup.nanoseconds}")
        Log.i(TAG, "  Mean handoff:          ${meanHandoff.nanoseconds}")
        Log.i(TAG, "  Mean composition:      ${meanComposition.nanoseconds}")
        Log.i(TAG, "  Mean first frame:      ${meanFirstFrame.nanoseconds}")

        // Sanity assertions: all values must be positive and ordered correctly.
        assertTrue("Handoff setup must be > 0", meanHandoffSetup > 0)
        assertTrue("Handoff must be >= handoff setup",  meanHandoff >= meanHandoffSetup)
        assertTrue("First frame must be >= handoff",    meanFirstFrame >= meanHandoff)
    }
}
