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
package com.example.dzprovinceapi.service

import com.example.dzprovinceapi.dto.DistrictResponse
import com.example.dzprovinceapi.dto.MunicipalityResponse
import com.example.dzprovinceapi.dto.ProvinceResponse
import com.example.dzprovinceapi.exception.ProvinceNotFoundException
import com.example.dzprovinceapi.mapping.toResponse
import com.example.dzprovinceapi.repository.DistrictRepository
import com.example.dzprovinceapi.repository.MunicipalityRepository
import com.example.dzprovinceapi.repository.ProvinceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.dzprovinceapi.mapping.toResponse as toDistrictResponse
import com.example.dzprovinceapi.mapping.toResponse as toMunicipalityResponse

@Service
class ProvinceServiceImpl(
    private val districtRepository: DistrictRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val provinceRepository: ProvinceRepository,
) : ProvinceService {
    @Transactional(readOnly = true)
    override fun getAllProvinces(
        languages: List<String>,
        pageable: Pageable,
    ): Page<ProvinceResponse> {
        val codes =
            IntRange(1, 69)
                .map(Int::toShort)
                .toList()

        val provinces =
            provinceRepository
                .findProvincesByCodeIn(codes, pageable)
        val response =
            provinces.map {
                it.toResponse(languages.normalizeOrDefaults())
            }

        return response
    }

    @Transactional(readOnly = true)
    override fun getProvinceByCode(
        languages: List<String>,
        code: Short,
    ): ProvinceResponse {
        val province =
            provinceRepository
                .findProvinceByCode(code)
                ?: throw ProvinceNotFoundException(code)
        val response =
            province
                .toResponse(languages.normalizeOrDefaults())

        return response
    }

    @Transactional(readOnly = true)
    override fun getProvinceDistrictsByCode(
        languages: List<String>,
        code: Short,
        pageable: Pageable,
    ): Page<DistrictResponse> {
        if (!provinceRepository.existsProvinceByCode(code)) {
            throw ProvinceNotFoundException(code)
        }

        val response =
            districtRepository
                .findDistrictsByProvinceCode(code, pageable)
                .map { it.toDistrictResponse(languages.normalizeOrDefaults()) }

        return response
    }

    @Transactional(readOnly = true)
    override fun getProvinceMunicipalitiesByCode(
        languages: List<String>,
        code: Short,
        pageable: Pageable,
    ): Page<MunicipalityResponse> {
        if (!provinceRepository.existsProvinceByCode(code)) {
            throw ProvinceNotFoundException(code)
        }

        val response =
            municipalityRepository
                .findMunicipalitiesByDistrictProvinceCode(code, pageable)
                .map { it.toMunicipalityResponse(languages.normalizeOrDefaults()) }

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
