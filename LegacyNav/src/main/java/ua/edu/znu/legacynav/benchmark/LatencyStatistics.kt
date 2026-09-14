package ua.edu.znu.legacynav.benchmark

import kotlin.math.sqrt

/**
 * Statistics (mean, std dev, 95% CI) computed over a collection of [LatencyMeasurement] runs.
 */
data class LatencyStatistics(
    val meanSetupLatency: Long,
    val meanFrameworkRoutingLatency: Long,
    val meanObjectRetrievalLatency: Long,
    val meanScreenRenderLatency: Long,
    val stdDevSetupLatency: Long,
    val stdDevFrameworkRoutingLatency: Long,
    val stdDevObjectRetrievalLatency: Long,
    val stdDevScreenRenderLatency: Long,
    val ci95LowerSetupLatency: Long,
    val ci95UpperSetupLatency: Long,
    val ci95LowerFrameworkRoutingLatency: Long,
    val ci95UpperFrameworkRoutingLatency: Long,
    val ci95LowerObjectRetrievalLatency: Long,
    val ci95UpperObjectRetrievalLatency: Long,
    val ci95LowerScreenRenderLatency: Long,
    val ci95UpperScreenRenderLatency: Long
) {
    companion object {
        fun from(measurements: List<LatencyMeasurement>): LatencyStatistics {
            require(measurements.isNotEmpty()) { "Cannot compute statistics from an empty list" }
            
            val setupLatencyValues = measurements.map { it.setupLatency }
            val frameworkRoutingLatencyValues = measurements.map { it.frameworkRoutingLatency }
            val objectRetrievalLatencyValues = measurements.map { it.objectRetrievalLatency }
            val screenRenderLatencyValues = measurements.map { it.screenRenderLatency }
            
            val setupLatencyStats = computeStats(setupLatencyValues)
            val frameworkRoutingLatencyStats = computeStats(frameworkRoutingLatencyValues)
            val objectRetrievalLatencyStats = computeStats(objectRetrievalLatencyValues)
            val screenRenderLatencyStats = computeStats(screenRenderLatencyValues)
            
            return LatencyStatistics(
                meanSetupLatency = setupLatencyStats.mean,
                meanFrameworkRoutingLatency = frameworkRoutingLatencyStats.mean,
                meanObjectRetrievalLatency = objectRetrievalLatencyStats.mean,
                meanScreenRenderLatency = screenRenderLatencyStats.mean,
                stdDevSetupLatency = setupLatencyStats.stdDev,
                stdDevFrameworkRoutingLatency = frameworkRoutingLatencyStats.stdDev,
                stdDevObjectRetrievalLatency = objectRetrievalLatencyStats.stdDev,
                stdDevScreenRenderLatency = screenRenderLatencyStats.stdDev,
                ci95LowerSetupLatency = setupLatencyStats.ci95Lower,
                ci95UpperSetupLatency = setupLatencyStats.ci95Upper,
                ci95LowerFrameworkRoutingLatency = frameworkRoutingLatencyStats.ci95Lower,
                ci95UpperFrameworkRoutingLatency = frameworkRoutingLatencyStats.ci95Upper,
                ci95LowerObjectRetrievalLatency = objectRetrievalLatencyStats.ci95Lower,
                ci95UpperObjectRetrievalLatency = objectRetrievalLatencyStats.ci95Upper,
                ci95LowerScreenRenderLatency = screenRenderLatencyStats.ci95Lower,
                ci95UpperScreenRenderLatency = screenRenderLatencyStats.ci95Upper
            )
        }
        
        private fun computeStats(values: List<Long>): StatsResult {
            val mean = values.average().toLong()
            val n = values.size
            
            // Sample variance: Σ(x - mean)² / (n - 1)
            val variance = if (n > 1) {
                values.map { (it - mean) * (it - mean) }.sum() / (n - 1).toDouble()
            } else {
                0.0
            }
            val stdDev = sqrt(variance).toLong()
            
            // 95% CI using t-distribution approximation (t ≈ 1.984 for n=100)
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