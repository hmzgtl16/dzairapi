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
package com.example.dzairapi.mapping

import com.example.dzairapi.dto.CountsResponse
import com.example.dzairapi.dto.DataSourceResponse
import com.example.dzairapi.dto.MetadataResponse
import com.example.dzairapi.model.Counts
import com.example.dzairapi.model.DataSource
import com.example.dzairapi.model.Metadata
import kotlin.time.toKotlinInstant

fun Metadata.toResponse(): MetadataResponse =
    MetadataResponse(
        apiVersion = apiVersion,
        datasetVersion = datasetVersion,
        lastUpdated = lastUpdated.toKotlinInstant(),
        sources = sources.map(DataSource::toResponse),
        counts = counts.toResponse(),
    )

fun DataSource.toResponse(): DataSourceResponse =
    DataSourceResponse(
        name = name,
        url = url,
        description = description,
    )

fun Counts.toResponse(): CountsResponse =
    CountsResponse(
        provinces = provinces,
        districts = districts,
        municipalities = municipalities,
    )
