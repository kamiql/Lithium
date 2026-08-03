package net.lithium.paper.impl.process

import net.lithium.common.lib.process.Process
import org.bukkit.scheduler.BukkitTask

class BukkitProcess: Process<BukkitTask>() {
    override fun cancel() {
        task.cancel()
    }
    override fun isCancelled(): Boolean = task.isCancelled
}