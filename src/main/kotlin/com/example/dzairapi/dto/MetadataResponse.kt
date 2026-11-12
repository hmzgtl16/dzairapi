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
import kotlin.time.Instant

@Serializable
@Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
data class MetadataResponse(
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5)
    @SerialName("api-version")
    val apiVersion: String,
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5)
    @SerialName("dataset-version")
    val datasetVersion: String,
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @SerialName("last-updated")
    val lastUpdated: Instant,
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    val sources: List<DataSourceResponse>,
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    val counts: CountsResponse,
)
