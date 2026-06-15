package ua.edu.znu.legacynav.data

/**
 * A simple in-memory repository for subjects.
 * Uses a Map for O(1) lookup so that Strategy A's retrieval cost stays
 * constant regardless of how many entries accumulate during benchmark runs.
 */
object SubjectRepository {
    private val subjects = mutableMapOf<Int, Subject>()
    private var nextId = 1

    /**
     * Adds a subject to the repository, assigning it a unique ID.
     */
    fun add(subject: Subject): Subject {
        val saved = subject.copy(id = nextId++)
        subjects[saved.id] = saved
        return saved
    }

    fun getById(id: Int): Subject? = subjects[id]

    /** Clears all stored subjects and resets the ID counter.
     *  Called from the benchmark test harness (@Before) so that Strategy A's
     *  repository lookup cost stays constant across parameter sets.
     */
    @Suppress("unused") // called from androidTest
    fun clear() {
        subjects.clear()
        nextId = 1
    }
}
