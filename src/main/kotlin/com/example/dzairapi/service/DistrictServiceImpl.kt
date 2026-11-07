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
package com.example.dzairapi.service

import com.example.dzairapi.dto.DistrictResponse
import com.example.dzairapi.dto.MunicipalityResponse
import com.example.dzairapi.dto.PageQuery
import com.example.dzairapi.dto.PageResponse
import com.example.dzairapi.exception.DistrictNotFoundException
import com.example.dzairapi.mapping.toResponse
import com.example.dzairapi.model.District
import com.example.dzairapi.repository.DistrictRepository
import com.example.dzairapi.repository.DistrictStatsRepository
import com.example.dzairapi.repository.MunicipalityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.dzairapi.mapping.toResponse as toMunicipalityResponse

@Service
class DistrictServiceImpl(
    private val districtRepository: DistrictRepository,
    private val districtStatsRepository: DistrictStatsRepository,
    private val municipalityRepository: MunicipalityRepository,
) : DistrictService {
    @Transactional(readOnly = true)
    override fun getDistricts(
        languages: List<String>,
        pageQuery: PageQuery,
    ): PageResponse<DistrictResponse> {
        val districts =
            districtRepository
                .findDistrictsBy(pageQuery.toPageable())

        val stats =
            districtStatsRepository
                .findDistrictStatsByCodeIn(districts.content.map(District::code))

        val response = PageResponse.from(
            districts,
            {
                it.toResponse(
                    languages.normalizeOrDefaults(),
                    stats.firstOrNull { s -> s.code == it.code }.toResponse(),
                )
            }
        )

        return response
    }

    @Transactional(readOnly = true)
    override fun getDistrictByCode(
        languages: List<String>,
        code: Short,
    ): DistrictResponse {
        val district =
            districtRepository.findDistrictByCode(code)
                ?: throw DistrictNotFoundException(code)
        val stats =
            districtStatsRepository.findDistrictStatsByCode(code)
        val response =
            district
                .toResponse(
                    languages.normalizeOrDefaults(),
                    stats.toResponse(),
                )

        return response
    }

    @Transactional(readOnly = true)
    override fun getDistrictMunicipalitiesByCode(
        languages: List<String>,
        code: Short,
        pageQuery: PageQuery,
    ): PageResponse<MunicipalityResponse> {
        if (!districtRepository.existsDistrictByCode(code)) {
            throw DistrictNotFoundException(code)
        }

        val municipalities = municipalityRepository
            .findMunicipalitiesByDistrictCode(code, pageQuery.toPageable())

        val response = PageResponse.from(
            municipalities,
            { it.toMunicipalityResponse(languages.normalizeOrDefaults()) }
        )

        return response
    }

    private fun List<String>.normalizeOrDefaults(): List<String> =
        asSequence()
            .flatMap { it.split(',') }
            .map { it.trim().lowercase() }
            .filter { it.length == 2 && it.all(Char::isLetter) }
            .distinct()
            .toList()
            .ifEmpty { DEFAULT_LANGUAGES }

    companion object {
        private val DEFAULT_LANGUAGES = listOf("ar", "en", "fr")
    }
}
