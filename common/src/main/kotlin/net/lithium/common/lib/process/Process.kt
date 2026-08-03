package net.lithium.common.lib.process

import kotlin.time.Duration

abstract class Process<Task: Any> {

    lateinit var task: Task

    abstract fun cancel()
    abstract fun isCancelled(): Boolean

    class Builder(val processManager: ProcessManager) {
        var block: ((Process<*>) -> Unit)? = null
        var isAsync: Boolean = true
        var delay: Duration? = null
        var repeat: Duration? = null

        fun data(): ProcessBuilderData = ProcessBuilderData(block, isAsync, delay, repeat)

        fun run(block: (Process<*>) -> Unit): Process<*> {
            apply { this.block = block }
            return processManager.from(this)
        }
        fun async() = apply { this.isAsync = true }
        fun sync() = apply { this.isAsync = false }
        fun withDelay(delay: Duration) = apply { this.delay = delay }
        fun repeatEvery(repeat: Duration) = apply { this.repeat = repeat }

        data class ProcessBuilderData(
            val block: ((Process<*>) -> Unit)? = null,
            val isAsync: Boolean = true,
            val delay: Duration? = null,
            val repeat: Duration? = null,
        )
    }
}