package ua.edu.znu.tsnid.benchmark

import kotlin.math.sqrt

/**
 * Statistics holder for latency measurements.
 */
data class LatencyStats(
    val mean: Long,
    val stdDev: Long,
    val ci95Lower: Long,
    val ci95Upper: Long
)

/**
 * Calculates mean, standard deviation, and 95% confidence interval for a list of values.
 */
fun calculateStats(values: List<Long>): LatencyStats {
    val mean = values.average().toLong()
    val variance = values.map { (it - mean) * (it - mean) }.average()
    val stdDev = sqrt(variance).toLong()
    
    // 95% CI using t-distribution approximation (t ≈ 1.984 for n=100)
    val n = values.size
    val marginOfError = (1.984 * stdDev / sqrt(n.toDouble())).toLong()
    
    return LatencyStats(
        mean = mean,
        stdDev = stdDev,
        ci95Lower = mean - marginOfError,
        ci95Upper = mean + marginOfError
    )
}
