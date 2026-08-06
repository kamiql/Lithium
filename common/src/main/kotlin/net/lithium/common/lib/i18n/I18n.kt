package net.lithium.common.lib.i18n

import net.lithium.common.LithiumApplication
import net.lithium.common.lib.config.ConfigService
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*

object I18n : KoinComponent {
    val app: LithiumApplication = get()

    private val locales: MutableMap<Locale, TranslationConfig> = mutableMapOf()

    fun init() {
        val dir = app
            .applicationDataFolder
            .resolve("locales")
            .also { it.mkdirs() }

        val locales = dir.listFiles { file ->
            file.isFile &&
                    file.extension == "yml" &&
                    runCatching {
                        Locale.Builder()
                            .setLanguageTag(file.nameWithoutExtension.replace('_', '-'))
                            .build()
                            .language
                            .isNotEmpty()
                    }.getOrDefault(false)
        }?.associate { file ->
            Locale.Builder()
                .setLanguageTag(file.nameWithoutExtension.replace('_', '-'))
                .build() to ConfigService.load<TranslationConfig>(file.name, dir)
        }.orEmpty().toMutableMap()

        if (locales.isEmpty()) {
            val default = ConfigService.load<TranslationConfig>(
                "${Locale.ENGLISH.language}.yml",
                dir
            )
            locales[Locale.ENGLISH] = default
            app.applicationLogger.warning("No locales registered, created default: ${Locale.ENGLISH.language}")
        }

        app.applicationLogger.info("Registered ${locales.size} locales: ${locales.keys.joinToString(", ") { it.displayName }}")

        locales.clear()
        locales.putAll(locales)
    }

    fun translation(locale: Locale, key: String, placeholders: Map<String, Any>): List<String> {
        val translations = locales[locale] ?: locales.values.firstOrNull() ?: return listOf(
            "No translations found"
        )

        val translation = translations.translations[key] ?: return listOf(
            "Translation $key not found"
        )

        return translation.map { text ->
            placeholders.entries.fold(text) { result, (key, value) ->
                result.replace(key, value.toString())
            }
        }
    }
}