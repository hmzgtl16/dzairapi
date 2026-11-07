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
package com.example.dzairapi.config

import com.example.dzairapi.utils.openapi.renamePropertiesToKebabCase
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("DzairAPI")
                    .version("1.0")
                    .description("REST API for Algerian provinces, districts and municipalities.")
                    .contact(Contact().name("Hamza Gattal").url("https://github.com/hmzgtl16"))
                    .license(License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")),
            ).tags(
                listOf(
                    Tag().name("Provinces").description("Algerian provinces (wilayas)"),
                    Tag().name("Districts").description("Algerian districts (dairas)"),
                    Tag().name("Municipalities").description("Algerian municipalities (communes)"),
                ),
            ).servers(
                listOf(
                    Server().url("http://localhost:8081").description("Local development"),
                ),
            )

    @Bean
    fun jsonMediaTypeCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.paths?.values?.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    operation.responses?.forEach { (status, response) ->
                        if (!status.startsWith("2")) return@forEach
                        val content = response.content ?: return@forEach
                        content.remove("*/*")?.let {
                            content.addMediaType(MediaType.APPLICATION_JSON_VALUE, it)
                        }
                    }
                }
            }
        }

    @Bean
    fun schemaCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            val components =
                openApi.components
                    ?.schemas
                    ?.takeIf { it.isNotEmpty() } ?: return@OpenApiCustomizer

            components.values.forEach(Schema<*>::renamePropertiesToKebabCase)
        }
}
