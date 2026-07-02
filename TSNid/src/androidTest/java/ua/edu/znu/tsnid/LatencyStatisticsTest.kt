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
private const val ITERATIONS = 100
private const val WARMUP_ITERATIONS = 10

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

    private fun performNavigationCycle(index: Int, label: String, measurements: List<LatencyMeasurement>? = null) {
        composeTestRule.onNodeWithTag("subjectNameInput").performTextClearance()
        composeTestRule.onNodeWithTag("subjectNameInput").performClick()
            .performTextInput("$label $index")
        if (index % 2 == 0) {
            composeTestRule.onNodeWithTag("subjectCheckedInput").performClick()
        }
        composeTestRule.onNodeWithTag("categoryNameInput").performTextClearance()
        composeTestRule.onNodeWithTag("categoryNameInput").performClick()
            .performTextInput("$label $index")
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
            performNavigationCycle(index, "Warmup")
        }

        // Clear any measurements from warm-up (though callback not attached, but be safe)
        measurements.clear()

        repeat(ITERATIONS) { index ->
            performNavigationCycle(index, "Subject", measurements)
        }

        assertTrue(
            "Expected $ITERATIONS measurements, got ${measurements.size}",
            measurements.size == ITERATIONS
        )

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
        assertTrue("Handoff must be >= handoff setup", handoffStats.mean >= handoffSetupStats.mean)
        assertTrue("First frame must be >= handoff", firstFrameStats.mean >= handoffStats.mean)
    }
}
