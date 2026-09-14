package ua.edu.znu.legacynav.benchmark

/**
 * Holds timing points (in nanoseconds) captured during a single forward-navigation
 * cycle from FirstFragment to SecondFragment, with calculated latencies derived from them.
 *
 * Timing points:
 * - t0 (navigationInitiated): FirstFragment button click, navigation starts
 * - t1 (setupCompleted): FirstFragment setup + navigate() call completes
 * - t2 (destinationScreenEntered): SecondFragment view creation/retrieval starts
 * - t3 (objectRetrieved): Subject fully retrieved from repository
 * - t4 (firstFrameDispatched): First Choreographer frame dispatch
 */
data class LatencyMeasurement(
    val navigationInitiated: Long,   // T0: Navigation initiated on FirstFragment
    val setupCompleted: Long,        // T1: FirstFragment setup + navigate() completes
    val destinationScreenEntered: Long,   // T2: SecondFragment view creation starts
    val objectRetrieved: Long,       // T3: Subject fully retrieved from repository
    val firstFrameDispatched: Long   // T4: First Choreographer frame dispatch
) {
    // Derived latency metrics (calculated from timing points)
    val setupLatency: Long get() = setupCompleted - navigationInitiated
    val frameworkRoutingLatency: Long get() = destinationScreenEntered - setupCompleted
    val objectRetrievalLatency: Long get() = objectRetrieved - destinationScreenEntered
    val screenRenderLatency: Long get() = firstFrameDispatched - objectRetrieved
}
