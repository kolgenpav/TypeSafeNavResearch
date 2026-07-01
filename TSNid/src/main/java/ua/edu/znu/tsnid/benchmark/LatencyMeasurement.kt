package ua.edu.znu.tsnid.benchmark

/**
 * Holds all four latency values (in nanoseconds) captured during a single
 * forward-navigation cycle from FirstScreen to SecondScreen.
 *
 * All values share 'navigationStartNs' as their zero point so they can be
 * compared arithmetically.
 *
 * @param handoffSetupNs  Duration of 'save to SubjectRepository' + 'navigate' on the main thread.
 * @param handoffNs       End-to-end handoff: navigation start → Subject in hand on SecondScreen.
 * @param compositionNs   Duration of SecondScreen's composable body execution.
 * @param firstFrameNs    Navigation start → first Choreographer vsync (frame dispatch).
 */
data class LatencyMeasurement(
    val handoffSetupNs: Long,   // T1
    val handoffNs: Long,        // T2
    val compositionNs: Long,
    val firstFrameNs: Long      // T3
)
