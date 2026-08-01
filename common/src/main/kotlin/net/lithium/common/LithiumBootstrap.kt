package net.lithium.common

import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

object LithiumBootstrap : KoinComponent {
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
            
            $WHITE  v%version%
            $WHITE  %authors%
            
            $CYAN-------------------------------------------------------$RESET
        """.trimIndent()
    }

    inline fun <reified T : LithiumApplication> bootstrap(app: T) {
        val meta = app.meta

        startKoin {
            modules(
                module {
                    single {
                        app
                    } bind LithiumApplication::class
                },
                *app.modules.toTypedArray()
            )

            logger(KoinLogger(app.logger()))
        }

        ascii()
            .replace("%version%", meta.version)
            .replace("%name%", meta.name)
            .replace("%authors%", meta.authors.joinToString(", "))
            .lines().forEach {
                app.logger().info(it)
            }
    }
}