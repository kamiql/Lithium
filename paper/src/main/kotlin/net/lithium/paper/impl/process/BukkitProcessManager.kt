package net.lithium.paper.impl.process

import net.lithium.common.lib.process.Process
import net.lithium.common.lib.process.ProcessManager
import net.lithium.paper.impl.toBukkitTicks
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

class BukkitProcessManager(val plugin: JavaPlugin): ProcessManager() {
    override fun from(builder: Process.Builder): Process<*> {

        val process = BukkitProcess()

        val (block, isAsync, delay, repeat) = builder.data()

        val repeatTicks = repeat?.toBukkitTicks()
        val delayTicks = delay?.toBukkitTicks()

        val runnable = Runnable {
            block?.invoke(process) ?: throw IllegalStateException("Process block cannot be null")
        }

        val scheduler = Bukkit.getScheduler()
        val bukkitTask = when {
            repeatTicks != null -> {
                scheduleTimer(scheduler, runnable, delayTicks ?: 0, repeatTicks, isAsync)
            }
            delayTicks != null -> {
                scheduleLater(scheduler, runnable, delayTicks, isAsync)
            }
            else -> {
                scheduleNow(scheduler, runnable, isAsync)
            }
        }

        process.task = bukkitTask
        return process
    }

    private fun scheduleTimer(
        scheduler: BukkitScheduler,
        runnable: Runnable,
        delayTicks: Long,
        repeatTicks: Long,
        isAsync: Boolean
    ): BukkitTask {
        if (!isAsync) return scheduler.runTaskTimer(plugin, runnable, delayTicks, repeatTicks)

        return try {
            scheduler.runTaskTimerAsynchronously(plugin, runnable, delayTicks, repeatTicks)
        } catch (_: UnsupportedOperationException) {
            plugin.logger.warning("Async repeating tasks are not supported on this server; falling back to sync scheduler.")
            scheduler.runTaskTimer(plugin, runnable, delayTicks, repeatTicks)
        }
    }

    private fun scheduleLater(
        scheduler: BukkitScheduler,
        runnable: Runnable,
        delayTicks: Long,
        isAsync: Boolean
    ): BukkitTask {
        if (!isAsync) return scheduler.runTaskLater(plugin, runnable, delayTicks)

        return try {
            scheduler.runTaskLaterAsynchronously(plugin, runnable, delayTicks)
        } catch (_: UnsupportedOperationException) {
            plugin.logger.warning("Async delayed tasks are not supported on this server; falling back to sync scheduler.")
            scheduler.runTaskLater(plugin, runnable, delayTicks)
        }
    }

    private fun scheduleNow(
        scheduler: BukkitScheduler,
        runnable: Runnable,
        isAsync: Boolean
    ): BukkitTask {
        if (!isAsync) return scheduler.runTask(plugin, runnable)

        return try {
            scheduler.runTaskAsynchronously(plugin, runnable)
        } catch (_: UnsupportedOperationException) {
            plugin.logger.warning("Async tasks are not supported on this server; falling back to sync scheduler.")
            scheduler.runTask(plugin, runnable)
        }
    }
}