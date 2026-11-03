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

import com.example.dzairapi.exception.ProvinceNotFoundException
import com.example.dzairapi.helpers.testDistrict
import com.example.dzairapi.helpers.testDistrictTranslation
import com.example.dzairapi.helpers.testLanguage
import com.example.dzairapi.helpers.testMunicipality
import com.example.dzairapi.helpers.testMunicipalityTranslation
import com.example.dzairapi.helpers.testProvince
import com.example.dzairapi.helpers.testProvinceTranslation
import com.example.dzairapi.repository.DistrictRepository
import com.example.dzairapi.repository.MunicipalityRepository
import com.example.dzairapi.repository.ProvinceRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class ProvinceServiceTest {
    @Mock
    private lateinit var districtRepository: DistrictRepository

    @Mock
    private lateinit var municipalityRepository: MunicipalityRepository

    @Mock
    private lateinit var provinceRepository: ProvinceRepository

    @InjectMocks
    private lateinit var provinceService: ProvinceServiceImpl

    // ------------------------------------------------------------------
    // getProvinces
    // ------------------------------------------------------------------

    @Test
    fun `getProvinces returns a page of mapped responses for codes 1 to 69`() {
        val province = testProvince(code = 16, slug = "alger", translations = emptySet())

        val pageable = PageRequest.of(0, 10)
        val provincePage = PageImpl(listOf(province), pageable, 1)
        `when`(provinceRepository.findProvincesBy(pageable))
            .thenReturn(provincePage)

        val result = provinceService.getProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("16")
        assertThat(result.content[0].names).isEmpty()

        verify(provinceRepository).findProvincesBy(pageable)
    }

    @Test
    fun `getProvinces falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvincesBy(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result = provinceService.getProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("16")
        assertThat(result.content[0].names).hasSize(province.translations.size)
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvincesBy(pageable)
    }

    @Test
    fun `getProvinces filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvincesBy(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result = provinceService.getProvinces(listOf("en"), pageable)

        assertThat(result.content[0].names).hasSize(1)
        assertThat(
            result.content[0]
                .names
                .single()
                .language
                ?.code,
        ).isEqualTo("en")

        verify(provinceRepository).findProvincesBy(pageable)
    }

    @Test
    fun `getProvinces normalizes languages by splitting on comma, trimming, lowercasing and de-duplicating`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvincesBy(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result =
            provinceService.getProvinces(
                listOf(" EN ", "fr,ar", "en", "12", "e"),
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr", "ar")

        verify(provinceRepository).findProvincesBy(pageable)
    }

    @Test
    fun `getProvinces falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvincesBy(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result =
            provinceService.getProvinces(listOf("1", "!!", "", "e", "eng"), pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvincesBy(pageable)
    }

    @Test
    fun `getProvinces returns an empty page when no provinces exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvincesBy(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = provinceService.getProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).findProvincesBy(pageable)
    }

    // ------------------------------------------------------------------
    // getProvinceByCode
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceByCode returns the mapped response`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceByCode(emptyList(), 16)

        assertThat(result.code).isEqualTo("16")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getProvinceByCode throws ProvinceNotFoundException when no province matches`() {
        `when`(provinceRepository.findProvinceByCode(99)).thenReturn(null)

        assertThatThrownBy { provinceService.getProvinceByCode(emptyList(), 99) }
            .isInstanceOf(ProvinceNotFoundException::class.java)

        verify(provinceRepository).findProvinceByCode(99)
    }

    @Test
    fun `getProvinceByCode normalizes the given languages`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result =
            provinceService.getProvinceByCode(listOf("EN, fr", "en", "!"), 16)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getProvinceByCode falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "alger",
                translations =
                    setOf(
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Alger",
                            language = testLanguage(code = "fr"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result =
            provinceService.getProvinceByCode(listOf("1", "!!", "", "e"), 16)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    // ------------------------------------------------------------------
    // getProvinceDistrictsByCode
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceDistrictsByCode returns a page of mapped district responses`() {
        val districtId = UUID.randomUUID()
        val district =
            testDistrict(
                id = districtId,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(districtRepository.findDistrictsByProvinceCode(16, pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        val result = provinceService.getProvinceDistrictsByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).existsProvinceByCode(16)
        verify(districtRepository).findDistrictsByProvinceCode(16, pageable)
    }

    @Test
    fun `getProvinceDistrictsByCode throws ProvinceNotFoundException when province does not exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(99)).thenReturn(false)

        assertThatThrownBy { provinceService.getProvinceDistrictsByCode(emptyList(), 99, pageable) }
            .isInstanceOf(ProvinceNotFoundException::class.java)

        verify(provinceRepository).existsProvinceByCode(99)
        verifyNoInteractions(districtRepository)
    }

    @Test
    fun `getProvinceDistrictsByCode filters and normalizes the given languages`() {
        val districtId = UUID.randomUUID()
        val district =
            testDistrict(
                id = districtId,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(districtRepository.findDistrictsByProvinceCode(16, pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        val result =
            provinceService.getProvinceDistrictsByCode(listOf("EN, fr", "en", "!"), 16, pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(provinceRepository).existsProvinceByCode(16)
        verify(districtRepository).findDistrictsByProvinceCode(16, pageable)
    }

    @Test
    fun `getProvinceDistrictsByCode falls back to default languages when every entry is invalid`() {
        val districtId = UUID.randomUUID()
        val district =
            testDistrict(
                id = districtId,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(districtRepository.findDistrictsByProvinceCode(16, pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        val result =
            provinceService.getProvinceDistrictsByCode(listOf("1", "!!", "", "e"), 16, pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).existsProvinceByCode(16)
        verify(districtRepository).findDistrictsByProvinceCode(16, pageable)
    }

    @Test
    fun `getProvinceDistrictsByCode returns an empty page when province has no districts`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(districtRepository.findDistrictsByProvinceCode(16, pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = provinceService.getProvinceDistrictsByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).existsProvinceByCode(16)
        verify(districtRepository).findDistrictsByProvinceCode(16, pageable)
    }

    // ------------------------------------------------------------------
    // getProvinceMunicipalitiesByCode
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceMunicipalitiesByCode returns a page of mapped municipality responses`() {
        val municipality1Id = UUID.randomUUID()
        val municipality1 =
            testMunicipality(
                id = municipality1Id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipality1Id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality1Id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality1Id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val municipality2Id = UUID.randomUUID()
        val municipality2 =
            testMunicipality(
                id = municipality2Id,
                code = 1602,
                slug = "test-slug2",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipality2Id,
                            name = "Casbah",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality2Id,
                            name = "Casbah",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality2Id,
                            name = "القصبة",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictProvinceCode(16, pageable))
            .thenReturn(PageImpl(listOf(municipality1, municipality2), pageable, 2))

        val result = provinceService.getProvinceMunicipalitiesByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.content).hasSize(2)
        assertThat(result.content.map { it.code })
            .containsExactlyInAnyOrder("1601", "1602")
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).existsProvinceByCode(16)
        verify(municipalityRepository).findMunicipalitiesByDistrictProvinceCode(16, pageable)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode throws ProvinceNotFoundException when province does not exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(99)).thenReturn(false)

        assertThatThrownBy { provinceService.getProvinceMunicipalitiesByCode(emptyList(), 99, pageable) }
            .isInstanceOf(ProvinceNotFoundException::class.java)

        verify(provinceRepository).existsProvinceByCode(99)
        verifyNoInteractions(municipalityRepository)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode filters and normalizes the given languages`() {
        val municipalityId = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = municipalityId,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictProvinceCode(16, pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            provinceService.getProvinceMunicipalitiesByCode(listOf(" AR , en ", "12", "!"), 16, pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en")

        verify(provinceRepository).existsProvinceByCode(16)
        verify(municipalityRepository).findMunicipalitiesByDistrictProvinceCode(16, pageable)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode falls back to default languages when every entry is invalid`() {
        val municipalityId = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = municipalityId,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictProvinceCode(16, pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            provinceService.getProvinceMunicipalitiesByCode(listOf("1", "!!", "", "e"), 16, pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).existsProvinceByCode(16)
        verify(municipalityRepository).findMunicipalitiesByDistrictProvinceCode(16, pageable)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode returns an empty page when province has no municipalities`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.existsProvinceByCode(16)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictProvinceCode(16, pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = provinceService.getProvinceMunicipalitiesByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).existsProvinceByCode(16)
        verify(municipalityRepository).findMunicipalitiesByDistrictProvinceCode(16, pageable)
    }
}
