package ua.edu.znu.legacynav.benchmark

import java.util.concurrent.CountDownLatch

/**
 * Stores the intermediate [System.nanoTime] timestamps that span the boundary between
 * FirstFragment and SecondFragment and are therefore needed by both sides.
 */
object LatencyTracker {
    /** Captured in FirstFragment just before the repository save + navigate calls. */
    var navigationStartNs: Long = 0L

    /** Captured in FirstFragment immediately after [androidx.navigation.NavController.navigate] returns. */
    var handoffSetupEndNs: Long = 0L

    /** Accumulated [LatencyMeasurement] instances, one per completed navigation cycle. */
    val measurements: MutableList<LatencyMeasurement> = mutableListOf()

    /**
     * Counts down to zero when the Choreographer frame callback fires for the current cycle.
     * Reset to a fresh latch before each navigation in the test harness.
     */
    @Volatile
    var frameLatch: CountDownLatch = CountDownLatch(1)
}
