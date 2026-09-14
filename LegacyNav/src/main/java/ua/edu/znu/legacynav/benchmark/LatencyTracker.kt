package ua.edu.znu.legacynav.benchmark

import java.util.concurrent.CountDownLatch

/**
 * Stores the intermediate [System.nanoTime] timestamps that span the boundary between
 * FirstFragment and SecondFragment and are therefore needed by both sides.
 *
 * Timing points:
 * - navigationInitiated (t0): Navigation starts (captured in FirstFragment)
 * - setupCompleted (t1): FirstFragment setup completes (captured in FirstFragment)
 * - destinationScreenEntered (t2): SecondFragment view creation starts (captured in SecondFragment)
 * - objectRetrieved (t3): Subject retrieved from repository (captured in SecondFragment)
 * - firstFrameDispatched (t4): First Choreographer frame (captured in frame callback)
 */
object LatencyTracker {
    var navigationInitiated: Long = 0L
    var setupCompleted: Long = 0L
    var destinationScreenEntered: Long = 0L
    var objectRetrieved: Long = 0L

    /** Accumulated [LatencyMeasurement] instances, one per completed navigation cycle. */
    val measurements: MutableList<LatencyMeasurement> = mutableListOf()

    /**
     * Counts down to zero when the Choreographer frame callback fires for the current cycle.
     * Reset to a fresh latch before each navigation in the test harness.
     */
    @Volatile
    var frameLatch: java.util.concurrent.CountDownLatch = java.util.concurrent.CountDownLatch(1)
}
