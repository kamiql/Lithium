package net.lithium.common.lib.process

import kotlin.time.Duration

abstract class ProcessManager {
    abstract fun from(builder: Process.Builder): Process<*>

    fun async(): Process.Builder = Process.Builder(this).async()
    fun withDelay(delay: Duration): Process.Builder = Process.Builder(this).withDelay(delay)
    fun repeatEvery(repeat: Duration): Process.Builder = Process.Builder(this).repeatEvery(repeat)
    fun sync(): Process.Builder = Process.Builder(this).sync()
    fun run(block: (Process<*>) -> Unit): Process<*> = Process.Builder(this).run(block)
}