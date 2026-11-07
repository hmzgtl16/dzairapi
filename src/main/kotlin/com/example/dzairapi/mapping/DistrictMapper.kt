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

import com.example.dzairapi.dto.DistrictNameResponse
import com.example.dzairapi.dto.DistrictResponse
import com.example.dzairapi.dto.DistrictStatsResponse
import com.example.dzairapi.model.District
import com.example.dzairapi.model.DistrictStats
import com.example.dzairapi.model.DistrictTranslation

fun District.toResponse(
    languages: List<String>,
    stats: DistrictStatsResponse?,
): DistrictResponse =
    DistrictResponse(
        code = String.format("%04d", code),
        slug = slug,
        names =
            translations
                .filter { languages.contains(it.language?.code) }
                .map { it.toResponse() },
        stats = stats,
    )

fun DistrictTranslation.toResponse(): DistrictNameResponse =
    DistrictNameResponse(
        name = name,
        language = language!!.toResponse(),
    )

fun DistrictStats?.toResponse(): DistrictStatsResponse? =
    this?.let {
        DistrictStatsResponse(
            municipalityCount = it.municipalityCount,
        )
    }
