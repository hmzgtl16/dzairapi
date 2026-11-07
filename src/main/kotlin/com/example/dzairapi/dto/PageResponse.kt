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
package com.example.dzairapi.dto

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.data.domain.Page

@Serializable
@Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
data class PageResponse<T>(
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    val content: List<T>,
    @field:Schema(
        type = "integer",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val number: Int,
    @field:Schema(
        type = "integer",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @SerialName("number-of-elements")
    val numberOfElements: Int,
    @field:Schema(
        type = "integer",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val size: Int,
    @field:Schema(
        type = "integer",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @SerialName("total-pages")
    val totalPages: Int,
    @field:Schema(
        type = "long",
        minimum = "0",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @SerialName("total-elements")
    val totalElements: Long,
) {
    companion object {
        fun <S : Any, T> from(
            page: Page<S>,
            transform: (S) -> T,
        ) = PageResponse(
            content = page.content.map(transform),
            number = page.number,
            numberOfElements = page.numberOfElements,
            size = page.size,
            totalPages = page.totalPages,
            totalElements = page.totalElements,
        )
    }
}
