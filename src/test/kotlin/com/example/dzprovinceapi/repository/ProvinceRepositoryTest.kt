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
package com.example.dzprovinceapi.repository

import com.example.dzprovinceapi.model.ProvinceTranslation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class ProvinceRepositoryTest {
    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    // ------------------------------------------------------------------
    // findProvinceByCode
    // ------------------------------------------------------------------

    @Test
    fun findProvinceByCode_returnsProvince_whenCodeExists() {
        val result = provinceRepository.findProvinceByCode(16)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(16.toShort())
    }

    @Test
    fun findProvinceByCode_returnsNull_whenCodeAbsent() {
        val result = provinceRepository.findProvinceByCode(99)

        assertThat(result).isNull()
    }

    @Test
    fun findProvinceByCode_returnsNull_whenCodeIsNegativeOrZero() {
        assertThat(provinceRepository.findProvinceByCode(0)).isNull()
        assertThat(provinceRepository.findProvinceByCode(-1)).isNull()
    }

    @Test
    fun findProvinceByCode_returnsExactMatch_whenMultipleExist() {
        val result = provinceRepository.findProvinceByCode(31)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(31.toShort())
    }

    @Test
    fun findProvinceByCode_loadsTranslationsAndLanguages() {
        val result = provinceRepository.findProvinceByCode(16)

        assertThat(result).isNotNull
        assertThat(result!!.translations)
            .isNotEmpty
            .extracting(ProvinceTranslation::name)
            .contains(Tuple("Alger"), Tuple("Algiers"), Tuple("الجزائر"))

        assertThat(result.translations)
            .allSatisfy { translation ->
                assertThat(translation.language).isNotNull
            }
    }

    // ------------------------------------------------------------------
    // findProvincesByCodeIn
    // ------------------------------------------------------------------

    @Test
    fun findProvincesByCodeIn_returnsMatchingProvinces_whenAllCodesExist() {
        val codes: List<Short> = listOf(16, 31)
        val result = provinceRepository.findProvincesByCodeIn(codes, Pageable.unpaged())

        assertThat(result.content).hasSize(2)
        assertThat(result.content)
            .extracting("code")
            .containsExactlyInAnyOrder(16.toShort(), 31.toShort())
    }

    @Test
    fun findProvincesByCodeIn_returnsOnlyExistingProvinces_whenSomeCodesAbsent() {
        val codes: List<Short> = listOf(16, 99, 100)
        val result = provinceRepository.findProvincesByCodeIn(codes, Pageable.unpaged())

        assertThat(result.content).hasSize(1)
        assertThat(result.content.first().code).isEqualTo(16.toShort())
    }

    @Test
    fun findProvincesByCodeIn_returnsEmptyPage_whenNoCodesExist() {
        val codes: List<Short> = listOf(99, 100)
        val result = provinceRepository.findProvincesByCodeIn(codes, Pageable.unpaged())

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isZero()
    }

    @Test
    fun findProvincesByCodeIn_returnsEmptyPage_whenCodeListIsEmpty() {
        val result = provinceRepository.findProvincesByCodeIn(emptyList(), Pageable.unpaged())

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isZero()
    }

    @Test
    fun findProvincesByCodeIn_deduplicatesResults_whenDuplicateCodesProvided() {
        val codes: List<Short> = listOf(16, 16, 31)
        val result = provinceRepository.findProvincesByCodeIn(codes, Pageable.unpaged())

        assertThat(result.content).hasSize(2)
        assertThat(result.content)
            .extracting("code")
            .containsExactlyInAnyOrder(16.toShort(), 31.toShort())
    }

    @Test
    fun findProvincesByCodeIn_appliesPaginationAndSorting() {
        val codes: List<Short> = listOf(1, 5, 16, 31)
        val pageable =
            PageRequest
                .of(
                    0,
                    2,
                    Sort.by(Sort.Direction.ASC, "code"),
                )

        val firstPage = provinceRepository.findProvincesByCodeIn(codes, pageable)

        assertThat(firstPage.content).hasSize(2)
        assertThat(firstPage.totalElements).isEqualTo(4)
        assertThat(firstPage.totalPages).isEqualTo(2)
        assertThat(firstPage.content)
            .extracting("code")
            .containsExactly(1.toShort(), 5.toShort())

        val secondPage = provinceRepository.findProvincesByCodeIn(codes, pageable.next())

        assertThat(secondPage.content).hasSize(2)
        assertThat(secondPage.content)
            .extracting("code")
            .containsExactly(16.toShort(), 31.toShort())
    }

    @Test
    fun findProvincesByCodeIn_loadsTranslationsAndLanguages() {
        val codes: List<Short> = listOf(16, 31)
        val result = provinceRepository.findProvincesByCodeIn(codes, Pageable.unpaged())

        assertThat(result.content).hasSize(2)
        assertThat(result.content).allSatisfy { province ->
            assertThat(province.translations).isNotEmpty
            assertThat(province.translations).allSatisfy { translation ->
                assertThat(translation.name).isNotBlank()
                assertThat(translation.language).isNotNull
            }
        }
    }

    // ------------------------------------------------------------------
    // existsProvinceByCode
    // ------------------------------------------------------------------

    @Test
    fun existsProvinceByCode_returnsTrue_whenCodeExists() {
        assertThat(provinceRepository.existsProvinceByCode(16)).isTrue()
        assertThat(provinceRepository.existsProvinceByCode(31)).isTrue()
    }

    @Test
    fun existsProvinceByCode_returnsFalse_whenCodeAbsent() {
        assertThat(provinceRepository.existsProvinceByCode(99)).isFalse()
    }

    @Test
    fun existsProvinceByCode_returnsFalse_whenCodeIsNegativeOrZero() {
        assertThat(provinceRepository.existsProvinceByCode(0)).isFalse()
        assertThat(provinceRepository.existsProvinceByCode(-1)).isFalse()
    }
}
