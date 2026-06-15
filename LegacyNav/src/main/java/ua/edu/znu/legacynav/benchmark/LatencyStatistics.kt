package ua.edu.znu.legacynav.benchmark

/**
 * Mean latencies (in nanoseconds) computed over a collection of [LatencyMeasurement] runs.
 */
data class LatencyStatistics(
    val meanHandoffSetupNs: Long,
    val meanHandoffNs: Long,
    val meanFirstFrameNs: Long
) {
    companion object {
        fun from(measurements: List<LatencyMeasurement>): LatencyStatistics {
            require(measurements.isNotEmpty()) { "Cannot compute statistics from an empty list" }
            return LatencyStatistics(
                meanHandoffSetupNs = measurements.map { it.handoffSetupNs }.average().toLong(),
                meanHandoffNs      = measurements.map { it.handoffNs }.average().toLong(),
                meanFirstFrameNs   = measurements.map { it.firstFrameNs }.average().toLong()
            )
        }
    }
}