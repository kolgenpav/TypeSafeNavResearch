package ua.edu.znu.legacynav.benchmark

/**
 * Holds all four latency values (in nanoseconds) captured during a single
 * forward-navigation cycle from FirstScreen to SecondScreen.
 *
 * All values share 'navigationStartNs' as their zero point so they can be
 * compared arithmetically.
 *
 * @param handoffSetupNs  Duration of 'savedStateHandle.set' + 'navigate' on the main thread.
 * @param handoffNs       End-to-end handoff: navigation start → Subject in hand on SecondScreen.
 * @param firstFrameNs    Navigation start → first Choreographer vsync (frame dispatch).
 */
data class LatencyMeasurement(
    val handoffSetupNs: Long,
    val handoffNs: Long,
    val firstFrameNs: Long
)
