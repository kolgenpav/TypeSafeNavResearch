package ua.edu.znu.tsnparcelize.benchmark

import kotlin.math.sqrt

/**
 * Statistics holder for latency measurements.
 */
data class LatencyStats(
    val mean: Double,
    val stdDev: Double,
    val ci95Lower: Double,
    val ci95Upper: Double
)

/**
 * Calculates mean, standard deviation, and 95% confidence interval for a list of values.
 */
fun calculateStats(values: List<Long>): LatencyStats {
    val mean = values.average()
    val n = values.size
    
    // Sample variance: Σ(x - mean)² / (n - 1)
    val variance = if (n > 1) {
        values.sumOf { (it - mean) * (it - mean) } / (n - 1)
    } else {
        0.0
    }
    val stdDev = sqrt(variance)
    
    // 95% CI using t-distribution approximation (t ≈ 1.984 for n=100)
    val marginOfError = 1.984 * stdDev / sqrt(n.toDouble())
    
    return LatencyStats(
        mean = mean,
        stdDev = stdDev,
        ci95Lower = mean - marginOfError,
        ci95Upper = mean + marginOfError
    )
}
