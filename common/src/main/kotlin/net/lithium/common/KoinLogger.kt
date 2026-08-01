package net.lithium.common

import org.koin.core.logger.Level as KoinLevel
import org.koin.core.logger.Logger as KoinLogger
import org.koin.core.logger.MESSAGE
import java.util.logging.Level
import java.util.logging.Logger

class KoinLogger(
    private val logger: Logger,
    level: KoinLevel = KoinLevel.INFO
) : KoinLogger(level) {

    private fun level(level: KoinLevel): Level = when (level) {
        KoinLevel.DEBUG -> Level.FINE
        KoinLevel.INFO -> Level.INFO
        KoinLevel.WARNING -> Level.WARNING
        KoinLevel.ERROR -> Level.SEVERE
        KoinLevel.NONE -> Level.OFF
    }

    override fun display(level: KoinLevel, msg: MESSAGE) {
        logger.log(this.level(level), msg)
    }
}