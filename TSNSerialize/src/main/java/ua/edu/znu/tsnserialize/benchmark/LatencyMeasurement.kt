package ua.edu.znu.tsnserialize.benchmark

/**
 * Holds all four latency values (in nanoseconds) captured during a single
 * forward-navigation cycle from FirstScreen to SecondScreen.
 *
 * All values share 'navigationStartNs' as their zero point so they can be
 * compared arithmetically.
 *
 * @param handoffSetupNs  Duration of serialization + 'navigate' on the main thread (includes JSON serialization and route argument setup).
 * @param handoffNs       End-to-end handoff: navigation start → Subject in hand on SecondScreen.
 * @param compositionNs   Duration of SecondScreen's composable body execution.
 * @param firstFrameNs    Navigation start → first Choreographer vsync (frame dispatch).
 */
data class LatencyMeasurement(
    val handoffSetupNs: Long,
    val handoffNs: Long,
    val compositionNs: Long,
    val firstFrameNs: Long
)
