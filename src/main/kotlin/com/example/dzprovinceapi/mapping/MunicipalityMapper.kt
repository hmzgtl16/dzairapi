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
package com.example.dzprovinceapi.mapping

import com.example.dzprovinceapi.dto.MunicipalityNameResponse
import com.example.dzprovinceapi.dto.MunicipalityResponse
import com.example.dzprovinceapi.model.Municipality
import com.example.dzprovinceapi.model.MunicipalityTranslation

fun Municipality?.toResponse(languages: List<String>): MunicipalityResponse =
    MunicipalityResponse(
        slug = this?.slug,
        code = this?.code?.let { String.format("%04d", it) },
        names =
            this
                ?.translations
                ?.filter { languages.contains(it.language?.code) }
                ?.map(MunicipalityTranslation::toResponse) ?: emptyList(),
    )

fun MunicipalityTranslation?.toResponse(): MunicipalityNameResponse =
    MunicipalityNameResponse(
        name = this?.name,
        language = this?.language?.toResponse(),
    )
