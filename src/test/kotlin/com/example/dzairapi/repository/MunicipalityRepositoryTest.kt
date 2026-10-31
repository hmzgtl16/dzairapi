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
package com.example.dzairapi.repository

import com.example.dzairapi.model.MunicipalityTranslation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class MunicipalityRepositoryTest {
    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    // ------------------------------------------------------------------
    // findMunicipalityByCode
    // ------------------------------------------------------------------

    @Test
    fun findMunicipalityByCode_returnsMunicipality_whenCodeExists() {
        val result = municipalityRepository.findMunicipalityByCode(1601)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(1601.toShort())
    }

    @Test
    fun findMunicipalityByCode_returnsNull_whenCodeAbsent() {
        val result = municipalityRepository.findMunicipalityByCode(9999)

        assertThat(result).isNull()
    }

    @Test
    fun findMunicipalityByCode_returnsNull_whenCodeIsNegativeOrZero() {
        assertThat(municipalityRepository.findMunicipalityByCode(0)).isNull()
        assertThat(municipalityRepository.findMunicipalityByCode(-1)).isNull()
    }

    @Test
    fun findMunicipalityByCode_returnsExactMatch_whenMultipleExist() {
        val municipalities =
            municipalityRepository
                .findMunicipalitiesByDistrictProvinceCode(16, PageRequest.of(0, 2))
                .content
        val target = municipalities.last()

        val result = municipalityRepository.findMunicipalityByCode(target.code)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(target.code)
        assertThat(result.id).isEqualTo(target.id)
    }

    @Test
    fun findMunicipalityByCode_loadsTranslationsAndLanguages() {
        val result = municipalityRepository.findMunicipalityByCode(1601)

        assertThat(result).isNotNull
        assertThat(result!!.translations)
            .isNotEmpty
            .extracting(MunicipalityTranslation::name)
            .allSatisfy { name -> assertThat(name).isNotNull }

        assertThat(result.translations).allSatisfy { translation ->
            assertThat(translation.language).isNotNull
        }
    }

    // ------------------------------------------------------------------
    // findMunicipalitiesByDistrictProvinceCode
    // ------------------------------------------------------------------

    @Test
    fun findMunicipalitiesByDistrictProvinceCode_returnsMunicipalities_whenProvinceExists() {
        val result = municipalityRepository.findMunicipalitiesByDistrictProvinceCode(16, Pageable.unpaged())

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { municipality ->
            assertThat(municipality.district?.province?.code).isEqualTo(16.toShort())
        }
    }

    @Test
    fun findMunicipalitiesByDistrictProvinceCode_returnsEmptyPage_whenProvinceCodeAbsent() {
        val result = municipalityRepository.findMunicipalitiesByDistrictProvinceCode(99, Pageable.unpaged())

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isZero()
    }

    @Test
    fun findMunicipalitiesByDistrictProvinceCode_returnsEmptyPage_whenProvinceCodeIsNegativeOrZero() {
        assertThat(
            municipalityRepository.findMunicipalitiesByDistrictProvinceCode(0, Pageable.unpaged()).content,
        ).isEmpty()
        assertThat(
            municipalityRepository.findMunicipalitiesByDistrictProvinceCode(-1, Pageable.unpaged()).content,
        ).isEmpty()
    }

    @Test
    fun findMunicipalitiesByDistrictProvinceCode_loadsTranslationsAndLanguages() {
        val result = municipalityRepository.findMunicipalitiesByDistrictProvinceCode(16, PageRequest.of(0, 5))

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { municipality ->
            assertThat(municipality.translations).isNotEmpty
            assertThat(municipality.translations).allSatisfy { translation ->
                assertThat(translation.name).isNotBlank()
                assertThat(translation.language).isNotNull
            }
        }
    }

    // ------------------------------------------------------------------
    // findMunicipalitiesByDistrictCode
    // ------------------------------------------------------------------

    @Test
    fun findMunicipalitiesByDistrictCode_returnsMunicipalities_whenDistrictExists() {
        val existingDistrictCode =
            municipalityRepository
                .findMunicipalityByCode(1601)!!
                .district
                ?.code ?: 0

        val result =
            municipalityRepository.findMunicipalitiesByDistrictCode(
                existingDistrictCode,
                Pageable.unpaged(),
            )

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { municipality ->
            assertThat(municipality.district?.code).isEqualTo(existingDistrictCode)
        }
    }

    @Test
    fun findMunicipalitiesByDistrictCode_returnsEmptyPage_whenDistrictCodeAbsent() {
        val result = municipalityRepository.findMunicipalitiesByDistrictCode(Short.MAX_VALUE, Pageable.unpaged())

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isZero()
    }

    @Test
    fun findMunicipalitiesByDistrictCode_returnsEmptyPage_whenDistrictCodeIsNegativeOrZero() {
        assertThat(
            municipalityRepository.findMunicipalitiesByDistrictCode(0, Pageable.unpaged()).content,
        ).isEmpty()
        assertThat(
            municipalityRepository.findMunicipalitiesByDistrictCode(-1, Pageable.unpaged()).content,
        ).isEmpty()
    }

    @Test
    fun findMunicipalitiesByDistrictCode_appliesPaginationAndSorting() {
        val existingDistrictCode =
            municipalityRepository
                .findMunicipalityByCode(1601)!!
                .district
                ?.code ?: 0
        val pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "code"))

        val firstPage = municipalityRepository.findMunicipalitiesByDistrictCode(existingDistrictCode, pageable)

        assertThat(firstPage.content).hasSize(1)

        if (firstPage.totalElements > 1) {
            val secondPage =
                municipalityRepository.findMunicipalitiesByDistrictCode(
                    existingDistrictCode,
                    pageable.next(),
                )
            assertThat(secondPage.content).hasSize(1)
            assertThat(secondPage.content.first().code)
                .isGreaterThan(firstPage.content.first().code)
        }
    }

    @Test
    fun findMunicipalitiesByDistrictCode_loadsTranslationsAndLanguages() {
        val existingDistrictCode =
            municipalityRepository
                .findMunicipalityByCode(1601)!!
                .district
                ?.code ?: 0

        val result =
            municipalityRepository.findMunicipalitiesByDistrictCode(
                existingDistrictCode,
                PageRequest.of(0, 5),
            )

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { municipality ->
            assertThat(municipality.translations).isNotEmpty
            assertThat(municipality.translations).allSatisfy { translation ->
                assertThat(translation.name).isNotBlank()
                assertThat(translation.language).isNotNull
            }
        }
    }
}
