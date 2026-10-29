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

import com.example.dzprovinceapi.model.DistrictTranslation
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
class DistrictRepositoryTest {
    @Autowired
    private lateinit var districtRepository: DistrictRepository

    // ------------------------------------------------------------------
    // findDistrictsBy
    // ------------------------------------------------------------------

    @Test
    fun findDistrictsBy_returnsAllDistricts_whenUnpaged() {
        val result = districtRepository.findDistrictsBy(Pageable.unpaged())

        assertThat(result.content).isNotEmpty
        assertThat(result.totalElements).isEqualTo(result.content.size.toLong())
    }

    @Test
    fun findDistrictsBy_appliesPaginationAndSorting() {
        val pageable =
            PageRequest
                .of(
                    0,
                    5,
                    Sort.by(Sort.Direction.ASC, "code"),
                )

        val firstPage = districtRepository.findDistrictsBy(pageable)

        assertThat(firstPage.content).hasSize(5)
        assertThat(firstPage.content)
            .extracting("code")
            .isSorted

        val secondPage = districtRepository.findDistrictsBy(pageable.next())

        assertThat(secondPage.content).isNotEmpty
        assertThat(secondPage.content.first().code)
            .isGreaterThan(firstPage.content.last().code)
    }

    @Test
    fun findDistrictsBy_loadsTranslationsAndLanguages() {
        val pageable = PageRequest.of(0, 5)
        val result = districtRepository.findDistrictsBy(pageable)

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { district ->
            assertThat(district.translations).isNotEmpty
            assertThat(district.translations).allSatisfy { translation ->
                assertThat(translation.name).isNotBlank()
                assertThat(translation.language).isNotNull
            }
        }
    }

    // ------------------------------------------------------------------
    // findDistrictByCode
    // ------------------------------------------------------------------

    @Test
    fun findDistrictByCode_returnsDistrict_whenCodeExists() {
        val existingCode =
            districtRepository
                .findDistrictsBy(PageRequest.of(0, 1))
                .content
                .first()
                .code

        val result = districtRepository.findDistrictByCode(existingCode)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(existingCode)
    }

    @Test
    fun findDistrictByCode_returnsNull_whenCodeAbsent() {
        val result = districtRepository.findDistrictByCode(Short.MAX_VALUE)

        assertThat(result).isNull()
    }

    @Test
    fun findDistrictByCode_returnsNull_whenCodeIsNegativeOrZero() {
        assertThat(districtRepository.findDistrictByCode(0)).isNull()
        assertThat(districtRepository.findDistrictByCode(-1)).isNull()
    }

    @Test
    fun findDistrictByCode_returnsExactMatch_whenMultipleExist() {
        val districts = districtRepository.findDistrictsBy(PageRequest.of(0, 2)).content
        val targetCode = districts.last().code

        val result = districtRepository.findDistrictByCode(targetCode)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(targetCode)
        assertThat(result.id).isEqualTo(districts.last().id)
    }

    @Test
    fun findDistrictByCode_loadsTranslationsAndLanguages() {
        val existingCode =
            districtRepository
                .findDistrictsBy(PageRequest.of(0, 1))
                .content
                .first()
                .code

        val result = districtRepository.findDistrictByCode(existingCode)

        assertThat(result).isNotNull
        assertThat(result!!.translations)
            .isNotEmpty
            .extracting(DistrictTranslation::name)
            .allSatisfy { name -> assertThat(name).isNotNull }

        assertThat(result.translations).allSatisfy { translation ->
            assertThat(translation.language).isNotNull
        }
    }

    // ------------------------------------------------------------------
    // findDistrictsByProvinceCode
    // ------------------------------------------------------------------

    @Test
    fun findDistrictsByProvinceCode_returnsDistricts_whenProvinceExists() {
        val result = districtRepository.findDistrictsByProvinceCode(16, Pageable.unpaged())

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { district ->
            assertThat(district.province?.code).isEqualTo(16.toShort())
        }
    }

    @Test
    fun findDistrictsByProvinceCode_returnsEmptyPage_whenProvinceCodeAbsent() {
        val result = districtRepository.findDistrictsByProvinceCode(99, Pageable.unpaged())

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isZero()
    }

    @Test
    fun findDistrictsByProvinceCode_returnsEmptyPage_whenProvinceCodeIsNegativeOrZero() {
        assertThat(districtRepository.findDistrictsByProvinceCode(0, Pageable.unpaged()).content).isEmpty()
        assertThat(districtRepository.findDistrictsByProvinceCode(-1, Pageable.unpaged()).content).isEmpty()
    }

    @Test
    fun findDistrictsByProvinceCode_appliesPaginationAndSorting() {
        val pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "code"))

        val firstPage = districtRepository.findDistrictsByProvinceCode(16, pageable)

        assertThat(firstPage.content).hasSize(2)
        assertThat(firstPage.content)
            .extracting("code")
            .isSorted

        val secondPage = districtRepository.findDistrictsByProvinceCode(16, pageable.next())

        assertThat(secondPage.content).isNotEmpty
        assertThat(secondPage.content.first().code)
            .isGreaterThan(firstPage.content.last().code)
    }

    @Test
    fun findDistrictsByProvinceCode_loadsTranslationsAndLanguages() {
        val result = districtRepository.findDistrictsByProvinceCode(16, PageRequest.of(0, 5))

        assertThat(result.content).isNotEmpty
        assertThat(result.content).allSatisfy { district ->
            assertThat(district.translations).isNotEmpty
            assertThat(district.translations).allSatisfy { translation ->
                assertThat(translation.name).isNotBlank()
                assertThat(translation.language).isNotNull
            }
        }
    }

    // ------------------------------------------------------------------
    // existsDistrictByCode
    // ------------------------------------------------------------------

    @Test
    fun existsDistrictByCode_returnsTrue_whenCodeExists() {
        val existingCode =
            districtRepository
                .findDistrictsBy(PageRequest.of(0, 1))
                .content
                .first()
                .code

        assertThat(districtRepository.existsDistrictByCode(existingCode)).isTrue()
    }

    @Test
    fun existsDistrictByCode_returnsFalse_whenCodeAbsent() {
        assertThat(districtRepository.existsDistrictByCode(Short.MAX_VALUE)).isFalse()
    }

    @Test
    fun existsDistrictByCode_returnsFalse_whenCodeIsNegativeOrZero() {
        assertThat(districtRepository.existsDistrictByCode(0)).isFalse()
        assertThat(districtRepository.existsDistrictByCode(-1)).isFalse()
    }
}
