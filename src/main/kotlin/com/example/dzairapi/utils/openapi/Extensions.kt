/*
 * Copyright 2026 Hamza Gattal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dzairapi.utils.openapi

import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springframework.http.MediaType
import io.swagger.v3.oas.models.media.MediaType as OasMediaType

private val CAMEL_BOUNDARY = Regex("([a-z0-9])([A-Z])")

private fun String.toKebabCase(): String = replace(CAMEL_BOUNDARY, "$1-$2").lowercase()

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

fun jsonResponse(
    description: String,
    schemaName: String,
): ApiResponse =
    ApiResponse()
        .description(description)
        .content(
            Content().addMediaType(
                MediaType.APPLICATION_JSON_VALUE,
                OasMediaType().schema(Schema<Any>().`$ref`("#/components/schemas/$schemaName")),
            ),
        )
