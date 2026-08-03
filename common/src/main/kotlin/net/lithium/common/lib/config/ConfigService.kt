package net.lithium.common.lib.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.lithium.common.LithiumApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.reflect.full.createInstance

object ConfigService : KoinComponent {
    val app: LithiumApplication by inject()
    val yaml: Yaml by inject()

    inline fun <reified T: Any> load(name: String, directory: File = app.applicationDataFolder): T {
        val file = directory.resolve(name)

        app.applicationLogger.info("Loading config $file")

        if (!file.exists()) {
            file.parentFile?.mkdirs()

            val instance = T::class.createInstance()
            save(instance, name, directory)

            app.applicationLogger.warning("$file empty, created default config")

            return instance
        }

        return yaml.decodeFromString<T>(file.readText())
    }

    inline fun <reified T> save(config: T, name: String, directory: File = app.applicationDataFolder) {
        val file = directory.resolve(name)
        file.parentFile?.mkdirs()
        file.writeText(yaml.encodeToString(config))
    }
}