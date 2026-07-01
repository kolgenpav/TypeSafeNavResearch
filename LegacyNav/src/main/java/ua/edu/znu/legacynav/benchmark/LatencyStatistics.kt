package ua.edu.znu.legacynav.benchmark

import kotlin.math.sqrt

/**
 * Statistics (mean, std dev, 95% CI) computed over a collection of [LatencyMeasurement] runs.
 */
data class LatencyStatistics(
    val meanHandoffSetupNs: Long,
    val meanHandoffNs: Long,
    val meanFirstFrameNs: Long,
    val stdDevHandoffSetupNs: Long,
    val stdDevHandoffNs: Long,
    val stdDevFirstFrameNs: Long,
    val ci95LowerHandoffSetupNs: Long,
    val ci95UpperHandoffSetupNs: Long,
    val ci95LowerHandoffNs: Long,
    val ci95UpperHandoffNs: Long,
    val ci95LowerFirstFrameNs: Long,
    val ci95UpperFirstFrameNs: Long
) {
    companion object {
        fun from(measurements: List<LatencyMeasurement>): LatencyStatistics {
            require(measurements.isNotEmpty()) { "Cannot compute statistics from an empty list" }
            
            val handoffSetupValues = measurements.map { it.handoffSetupNs }
            val handoffValues = measurements.map { it.handoffNs }
            val firstFrameValues = measurements.map { it.firstFrameNs }
            
            val handoffSetupStats = computeStats(handoffSetupValues)
            val handoffStats = computeStats(handoffValues)
            val firstFrameStats = computeStats(firstFrameValues)
            
            return LatencyStatistics(
                meanHandoffSetupNs = handoffSetupStats.mean,
                meanHandoffNs = handoffStats.mean,
                meanFirstFrameNs = firstFrameStats.mean,
                stdDevHandoffSetupNs = handoffSetupStats.stdDev,
                stdDevHandoffNs = handoffStats.stdDev,
                stdDevFirstFrameNs = firstFrameStats.stdDev,
                ci95LowerHandoffSetupNs = handoffSetupStats.ci95Lower,
                ci95UpperHandoffSetupNs = handoffSetupStats.ci95Upper,
                ci95LowerHandoffNs = handoffStats.ci95Lower,
                ci95UpperHandoffNs = handoffStats.ci95Upper,
                ci95LowerFirstFrameNs = firstFrameStats.ci95Lower,
                ci95UpperFirstFrameNs = firstFrameStats.ci95Upper
            )
        }
        
        private fun computeStats(values: List<Long>): StatsResult {
            val mean = values.average().toLong()
            val variance = values.map { (it - mean) * (it - mean) }.average()
            val stdDev = sqrt(variance).toLong()
            
            // 95% CI using t-distribution approximation (t ≈ 1.984 for n=100)
            val n = values.size
            val marginOfError = (1.984 * stdDev / sqrt(n.toDouble())).toLong()
            
            return StatsResult(
                mean = mean,
                stdDev = stdDev,
                ci95Lower = mean - marginOfError,
                ci95Upper = mean + marginOfError
            )
        }
        
        private data class StatsResult(
            val mean: Long,
            val stdDev: Long,
            val ci95Lower: Long,
            val ci95Upper: Long
        )
    }
}