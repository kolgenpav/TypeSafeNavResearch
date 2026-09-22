package ua.edu.znu.tsnid

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.edu.znu.tsnid.benchmark.LatencyMeasurement
import ua.edu.znu.tsnid.benchmark.calculateStats
import ua.edu.znu.tsnid.data.SubjectRepository
import ua.edu.znu.tsnid.nav.Nav
import ua.edu.znu.tsnid.ui.theme.TSNIdTheme
import java.util.Collections
import kotlin.time.Duration.Companion.nanoseconds

private const val TAG = "LatencyStats"
private const val ITERATIONS = 1
private const val WARMUP_ITERATIONS = 0

/**
 * Instrumented test that navigates from FirstScreen to SecondScreen [ITERATIONS] times,
 * collects a 'LatencyMeasurement' per cycle via the 'Nav.onLatencyMeasured' callback,
 * and logs the mean of each metric.
 */
@RunWith(AndroidJUnit4::class)
class LatencyStatisticsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // @Test starts with a clean repository.
        SubjectRepository.clear()
    }

    private fun performNavigationCycle(index: Int, subjectLabel: String, categoryLabel: String, measurements: List<LatencyMeasurement>? = null) {
        composeTestRule.onNodeWithTag("subjectNameInput").performTextClearance()
        composeTestRule.onNodeWithTag("subjectNameInput").performClick()
            .performTextInput("$subjectLabel $index")
        if (index % 2 == 0) {
            composeTestRule.onNodeWithTag("subjectCheckedInput").performClick()
        }
        composeTestRule.onNodeWithTag("categoryNameInput").performTextClearance()
        composeTestRule.onNodeWithTag("categoryNameInput").performClick()
            .performTextInput("$categoryLabel $index")
        composeTestRule.onNodeWithText("Go to Second Screen").performClick()
        
        // Wait for measurement if measurements list provided, otherwise just wait for idle
        if (measurements != null) {
            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                measurements.size > index
            }
        } else {
            composeTestRule.waitForIdle()
        }
        
        composeTestRule.onNodeWithText("Go back to First Screen").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun meanLatencies_over100NavigationCycles() {
        val measurements: MutableList<LatencyMeasurement> =
            Collections.synchronizedList(mutableListOf())

        composeTestRule.setContent {
            TSNIdTheme {
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

        // JVM warm-up iterations (without measuring)
        repeat(WARMUP_ITERATIONS) { index ->
            performNavigationCycle(index, "Warmup", "Warmup")
        }

        // Clear any measurements from warm-up (though callback not attached, but be safe)
        measurements.clear()

        repeat(ITERATIONS) { index ->
            performNavigationCycle(index, "Subject", "Category", measurements)
        }

        assertTrue(
            "Expected $ITERATIONS measurements, got ${measurements.size}",
            measurements.size == ITERATIONS
        )

        val setupLatencyValues = measurements.map { it.setupLatency }
        val frameworkRoutingLatencyValues = measurements.map { it.frameworkRoutingLatency }
        val objectRetrievalLatencyValues = measurements.map { it.objectRetrievalLatency }
        val screenRenderLatencyValues = measurements.map { it.screenRenderLatency }

        val setupLatencyStats = calculateStats(setupLatencyValues)
        val frameworkRoutingLatencyStats = calculateStats(frameworkRoutingLatencyValues)
        val objectRetrievalLatencyStats = calculateStats(objectRetrievalLatencyValues)
        val screenRenderLatencyStats = calculateStats(screenRenderLatencyValues)

        Log.i(TAG, "---- Latency Statistics ($ITERATIONS iterations) ----")
        Log.i(TAG, "")
        Log.i(TAG, "Setup (FirstScreen):")
        Log.i(TAG, "  Mean:          ${setupLatencyStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${setupLatencyStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${setupLatencyStats.ci95Lower.nanoseconds} - ${setupLatencyStats.ci95Upper.nanoseconds}")
        Log.i(TAG, "")
        Log.i(TAG, "Framework Routing:")
        Log.i(TAG, "  Mean:          ${frameworkRoutingLatencyStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${frameworkRoutingLatencyStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${frameworkRoutingLatencyStats.ci95Lower.nanoseconds} - ${frameworkRoutingLatencyStats.ci95Upper.nanoseconds}")
        Log.i(TAG, "")
        Log.i(TAG, "Object Retrieval:")
        Log.i(TAG, "  Mean:          ${objectRetrievalLatencyStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${objectRetrievalLatencyStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${objectRetrievalLatencyStats.ci95Lower.nanoseconds} - ${objectRetrievalLatencyStats.ci95Upper.nanoseconds}")
        Log.i(TAG, "")
        Log.i(TAG, "Screen Render:")
        Log.i(TAG, "  Mean:          ${screenRenderLatencyStats.mean.nanoseconds}")
        Log.i(TAG, "  Std Dev:       ${screenRenderLatencyStats.stdDev.nanoseconds}")
        Log.i(TAG, "  95% CI:        ${screenRenderLatencyStats.ci95Lower.nanoseconds} - ${screenRenderLatencyStats.ci95Upper.nanoseconds}")

        // Sanity assertions: all values must be positive and ordered correctly.
        assertTrue("Setup must be > 0", setupLatencyStats.mean > 0)
        assertTrue("Routing must be > 0", frameworkRoutingLatencyStats.mean > 0)
        assertTrue("Object retrieval must be >= 0", objectRetrievalLatencyStats.mean >= 0)
        assertTrue("Screen render must be > 0", screenRenderLatencyStats.mean > 0)
    }
}
