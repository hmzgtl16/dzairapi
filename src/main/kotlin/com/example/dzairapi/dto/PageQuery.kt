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
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kotlinx.serialization.Serializable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@Serializable
@Schema
data class PageQuery(
    @field:Min(0)
    @field:Schema(type = "integer", minimum = "0", defaultValue = "0")
    val page: Int = 0,
    @field:Min(1)
    @field:Max(100)
    @field:Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20")
    val size: Int = 20,
) {
    fun toPageable(): Pageable = PageRequest.of(page, size)
}
