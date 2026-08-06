package net.lithium.common

import net.lithium.common.lib.config.ConfigService
import net.lithium.common.lib.i18n.I18n
import net.lithium.common.modules.MiniMessageModule
import net.lithium.common.modules.SerializationModule
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

object LithiumBootstrap : KoinComponent {
    inline fun <reified T : LithiumApplication, reified C: ApplicationConfig> bootstrap(app: T) {
        val meta = app.meta

        ascii()
            .replace("%version%", meta.version)
            .replace("%name%", meta.name)
            .replace("%authors%", meta.authors.joinToString(", "))
            .lines().forEach {
                app.applicationLogger.info(it)
            }

        startKoin {
            modules(
                module {
                    single {
                        app
                    } bind LithiumApplication::class

                    single(createdAtStart = true) {
                        ConfigService.load<C>("config.yml")
                    }
                },
                *app.modules.toTypedArray(),
                SerializationModule,
                MiniMessageModule
            )

            logger(KoinLogger(app.applicationLogger))
        }

        I18n.init()
    }

    /**
     * The ASCII banner displayed during bootstrap.
     *
     * Available placeholders:
     * - `%version%` – The application version.
     * - `%name%` – The application name.
     * - `%authors%` – The application authors.
     */
    fun ascii(): String {
        val RESET = "\u001B[0m"
        val CYAN = "\u001B[36m"
        val BLUE = "\u001B[34m"
        val WHITE = "\u001B[37m"

        return """
            $CYAN-------------------------------------------------------
            
            $BLUE  ██╗     ██╗████████╗██╗  ██╗██╗██╗   ██╗███╗   ███╗
            $BLUE  ██║     ██║╚══██╔══╝██║  ██║██║██║   ██║████╗ ████║
            $BLUE  ██║     ██║   ██║   ███████║██║██║   ██║██╔████╔██║
            $BLUE  ██║     ██║   ██║   ██╔══██║██║╚██╗ ██╔╝██║╚██╔╝██║
            $BLUE  ███████╗██║   ██║   ██║  ██║██║ ╚████╔╝ ██║ ╚═╝ ██║
            $BLUE  ╚══════╝╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝  ╚═╝     ╚═╝
            $WHITE  v%version% by %authors%
            
            $CYAN-------------------------------------------------------$RESET
        """.trimIndent()
    }
}