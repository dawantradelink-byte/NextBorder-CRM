import kotlin.system.measureTimeMillis

fun main() {
    val itemsToSync = 1000

    // Baseline simulation (N+1 inserts)
    val baselineTime = measureTimeMillis {
        for (i in 0 until itemsToSync) {
            // simulate individual insert + update overhead
            Thread.sleep(1) // 1ms per insert+update
        }
    }

    // Optimized simulation (batch insert)
    val optimizedTime = measureTimeMillis {
        // simulate a single batch operation for all items
        Thread.sleep(10) // 10ms for one bulk transaction
    }

    println("Baseline time (simulated): \${baselineTime}ms")
    println("Optimized time (simulated): \${optimizedTime}ms")
    println("Improvement: \${baselineTime - optimizedTime}ms")
}
