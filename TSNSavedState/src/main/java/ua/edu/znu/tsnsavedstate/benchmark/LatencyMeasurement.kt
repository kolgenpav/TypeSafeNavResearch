package ua.edu.znu.tsnsavedstate.benchmark

/**
 * Holds timing points (in nanoseconds) captured during a single forward-navigation
 * cycle from FirstScreen to SecondScreen, with calculated latencies derived from them.
 *
 * Timing points:
 * - t0 (navigationInitiated): FirstScreen callback triggered, navigation starts
 * - t1 (setupCompleted): FirstScreen setup + navigate() call completes
 * - t2 (destinationScreenEntered): SecondScreen composable execution starts
 * - t3 (objectRetrieved): Data fully retrieved and available
 * - t4 (firstFrameDispatched): First Choreographer frame dispatch
 */
data class LatencyMeasurement(
    val navigationInitiated: Long,   // T0: Navigation initiated on FirstScreen
    val setupCompleted: Long,        // T1: FirstScreen setup + navigate() completes
    val destinationScreenEntered: Long,   // T2: SecondScreen composable execution starts
    val objectRetrieved: Long,       // T3: Data fully retrieved/deserialized
    val firstFrameDispatched: Long   // T4: First Choreographer frame dispatch
) {
    // Derived latency metrics (calculated from timing points)
    val setupLatency: Long get() = setupCompleted - navigationInitiated
    val frameworkRoutingLatency: Long get() = destinationScreenEntered - setupCompleted
    val objectRetrievalLatency: Long get() = objectRetrieved - destinationScreenEntered
    val screenRenderLatency: Long get() = firstFrameDispatched - objectRetrieved
}
