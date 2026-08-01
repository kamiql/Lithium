package net.lithium.common

/**
 * Metadata describing a lithium based application.
 *
 * This information is typically displayed during bootstrap and used to
 * determine compatibility and update availability.
 *
 * @property version The Application version, needed to lookup updates and determine compatibility
 * @property name The Application name
 * @property authors The project authors
 */
data class ApplicationMeta(
    val version: String,
    val name: String,
    val authors: List<String>
)