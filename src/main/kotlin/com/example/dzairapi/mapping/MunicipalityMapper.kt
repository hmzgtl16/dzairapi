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

import com.example.dzairapi.dto.MunicipalityNameResponse
import com.example.dzairapi.dto.MunicipalityResponse
import com.example.dzairapi.model.Municipality
import com.example.dzairapi.model.MunicipalityTranslation

fun Municipality.toResponse(languages: List<String>): MunicipalityResponse =
    MunicipalityResponse(
        code = String.format("%04d", code),
        slug = slug,
        names =
            translations
                .filter { languages.contains(it.language?.code) }
                .map(MunicipalityTranslation::toResponse),
    )

fun MunicipalityTranslation.toResponse(): MunicipalityNameResponse =
    MunicipalityNameResponse(
        name = name,
        language = language!!.toResponse(),
    )
