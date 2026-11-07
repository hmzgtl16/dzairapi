package com.example.dzairapi.utils.openapi

import io.swagger.v3.oas.models.media.Schema

private val CAMEL_BOUNDARY = Regex("([a-z0-9])([A-Z])")

private fun String.toKebabCase(): String =
    replace(CAMEL_BOUNDARY, "$1-$2").lowercase()

internal fun Schema<*>.renamePropertiesToKebabCase() {
    properties = properties?.mapKeys { it.key.toKebabCase() }
    required = required?.map(String::toKebabCase)

    items?.renamePropertiesToKebabCase()
    (additionalProperties as? Schema<*>)?.renamePropertiesToKebabCase()
    allOf?.forEach { it.renamePropertiesToKebabCase() }
    anyOf?.forEach { it.renamePropertiesToKebabCase() }
    oneOf?.forEach { it.renamePropertiesToKebabCase() }
    not?.renamePropertiesToKebabCase()
}
