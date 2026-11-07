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

import com.example.dzairapi.dto.ProvinceNameResponse
import com.example.dzairapi.dto.ProvinceResponse
import com.example.dzairapi.dto.ProvinceStatsResponse
import com.example.dzairapi.model.Province
import com.example.dzairapi.model.ProvinceStats
import com.example.dzairapi.model.ProvinceTranslation

fun Province.toResponse(
    languages: List<String>,
    stats: ProvinceStatsResponse?,
): ProvinceResponse =
    ProvinceResponse(
        code = String.format("%02d", code),
        slug = slug,
        names =
            translations
                .filter { languages.contains(it.language?.code) }
                .map(ProvinceTranslation::toResponse),
        stats = stats,
    )

fun ProvinceTranslation.toResponse(): ProvinceNameResponse =
    ProvinceNameResponse(
        name = name,
        language = language!!.toResponse(),
    )

fun ProvinceStats?.toResponse(): ProvinceStatsResponse? =
    this?.let {
        ProvinceStatsResponse(
            districtCount = it.districtCount,
            municipalityCount = it.municipalityCount,
        )
    }
