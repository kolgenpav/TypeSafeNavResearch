package ua.edu.znu.legacynav

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.edu.znu.legacynav.benchmark.LatencyStatistics
import ua.edu.znu.legacynav.benchmark.LatencyTracker
import ua.edu.znu.legacynav.data.SubjectRepository
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class LatencyStatisticsTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        SubjectRepository.clear()
        LatencyTracker.measurements.clear()
    }

    @Test
    fun meanLatencies_over10Runs() {
        val runs = 100

        repeat(runs) { i ->
            // Arm the latch before navigation so the Choreographer callback can signal it.
            LatencyTracker.frameLatch = java.util.concurrent.CountDownLatch(1)

            onView(withId(R.id.txtSubjectName))
                .perform(replaceText("Subject $i"), closeSoftKeyboard())
            if(i % 2 == 0) {
                onView(withId(R.id.isSubjectChecked)).perform(click()) // toggle the checkbox to add some variability
            }
            onView(withId(R.id.txtCategoryName))
                .perform(replaceText("Category $i"), closeSoftKeyboard())
            onView(withId(R.id.btnNavigate)).perform(click())

            // Wait for the Choreographer frame callback to fire (max 2 s).
            assertTrue(
                "Choreographer frame not received within timeout on run $i",
                LatencyTracker.frameLatch.await(2, TimeUnit.SECONDS)
            )

            // Verify SecondFragment is visible, then go back.
            onView(withId(R.id.subjectId)).check(matches(isDisplayed()))
            onView(withId(R.id.btnBack)).perform(click())
        }

        assertEquals(runs, LatencyTracker.measurements.size)

        val stats = LatencyStatistics.from(LatencyTracker.measurements)
        Log.d("LatencyStats", stats.toString())
    }
}
