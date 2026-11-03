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

import com.example.dzairapi.exception.DistrictNotFoundException
import com.example.dzairapi.helpers.testDistrict
import com.example.dzairapi.helpers.testDistrictStats
import com.example.dzairapi.helpers.testDistrictTranslation
import com.example.dzairapi.helpers.testLanguage
import com.example.dzairapi.helpers.testMunicipality
import com.example.dzairapi.helpers.testMunicipalityTranslation
import com.example.dzairapi.model.District
import com.example.dzairapi.repository.DistrictRepository
import com.example.dzairapi.repository.DistrictStatsRepository
import com.example.dzairapi.repository.MunicipalityRepository
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
class DistrictServiceTest {
    @Mock
    private lateinit var districtRepository: DistrictRepository

    @Mock
    private lateinit var districtStatsRepository: DistrictStatsRepository

    @Mock
    private lateinit var municipalityRepository: MunicipalityRepository

    @InjectMocks
    private lateinit var districtService: DistrictServiceImpl

    // ------------------------------------------------------------------
    // getDistricts
    // ------------------------------------------------------------------

    @Test
    fun `getDistricts returns a page of mapped responses`() {
        val district = testDistrict(code = 1601, slug = "test-slug", translations = emptySet())
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(district), pageable, 1)
        `when`(districtRepository.findDistrictsBy(pageable)).thenReturn(page)
        `when`(
            districtStatsRepository.findDistrictStatsByCodeIn(page.content.map(District::code)),
        ).thenReturn(listOf(districtStats))

        val result = districtService.getDistricts(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].names).isEmpty()

        verify(districtRepository).findDistrictsBy(pageable)
        verify(districtStatsRepository).findDistrictStatsByCodeIn(page.content.map(District::code))
    }

    @Test
    fun `getDistricts falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(district), pageable, 1)
        `when`(districtRepository.findDistrictsBy(pageable))
            .thenReturn(page)
        `when`(districtStatsRepository.findDistrictStatsByCodeIn(page.content.map(District::code)))
            .thenReturn(listOf(districtStats))

        val result = districtService.getDistricts(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].names).hasSize(district.translations.size)
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictsBy(pageable)
        verify(districtStatsRepository).findDistrictStatsByCodeIn(page.content.map(District::code))
    }

    @Test
    fun `getDistricts filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(district), pageable, 1)
        `when`(districtRepository.findDistrictsBy(pageable))
            .thenReturn(page)
        `when`(districtStatsRepository.findDistrictStatsByCodeIn(page.content.map(District::code)))
            .thenReturn(listOf(districtStats))

        val result = districtService.getDistricts(listOf("en"), pageable)

        assertThat(result.content[0].names).hasSize(1)
        assertThat(
            result.content[0]
                .names
                .single()
                .language
                ?.code,
        ).isEqualTo("en")

        verify(districtRepository).findDistrictsBy(pageable)
        verify(districtStatsRepository).findDistrictStatsByCodeIn(page.content.map(District::code))
    }

    @Test
    fun `getDistricts normalizes languages by splitting on comma, trimming, lowercasing and de-duplicating`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(district), pageable, 1)
        `when`(districtRepository.findDistrictsBy(pageable))
            .thenReturn(page)
        `when`(districtStatsRepository.findDistrictStatsByCodeIn(page.content.map(District::code)))
            .thenReturn(listOf(districtStats))

        val result =
            districtService.getDistricts(
                listOf(" EN ", "fr,ar", "en", "12", "e"),
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr", "ar")

        verify(districtRepository).findDistrictsBy(pageable)
        verify(districtStatsRepository).findDistrictStatsByCodeIn(page.content.map(District::code))
    }

    @Test
    fun `getDistricts falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(district), pageable, 1)
        `when`(districtRepository.findDistrictsBy(pageable))
            .thenReturn(page)
        `when`(districtStatsRepository.findDistrictStatsByCodeIn(page.content.map(District::code)))
            .thenReturn(listOf(districtStats))

        val result =
            districtService.getDistricts(listOf("1", "!!", "", "e", "eng"), pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictsBy(pageable)
        verify(districtStatsRepository).findDistrictStatsByCodeIn(page.content.map(District::code))
    }

    @Test
    fun `getDistricts returns an empty page when no districts exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.findDistrictsBy(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = districtService.getDistricts(emptyList(), pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(districtRepository).findDistrictsBy(pageable)
    }

    // ------------------------------------------------------------------
    // getDistrictByCode
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictByCode returns the mapped response`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        `when`(districtRepository.findDistrictByCode(1601)).thenReturn(district)
        `when`(districtStatsRepository.findDistrictStatsByCode(1601)).thenReturn(districtStats)

        val result = districtService.getDistrictByCode(emptyList(), 1601)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictByCode(1601)
        verify(districtStatsRepository).findDistrictStatsByCode(1601)
    }

    @Test
    fun `getDistrictByCode throws DistrictNotFoundException when no district matches`() {
        `when`(districtRepository.findDistrictByCode(9999)).thenReturn(null)

        assertThatThrownBy { districtService.getDistrictByCode(emptyList(), 9999) }
            .isInstanceOf(DistrictNotFoundException::class.java)

        verify(districtRepository).findDistrictByCode(9999)
    }

    @Test
    fun `getDistrictByCode normalizes the given languages`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "test-slug",
                code = 1601,
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)

        `when`(districtRepository.findDistrictByCode(1601)).thenReturn(district)
        `when`(districtStatsRepository.findDistrictStatsByCode(1601)).thenReturn(districtStats)

        val result =
            districtService.getDistrictByCode(listOf(" FR ", "en", "fr", "123"), 1601)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "en")

        verify(districtRepository).findDistrictByCode(1601)
        verify(districtStatsRepository).findDistrictStatsByCode(1601)
    }

    @Test
    fun `getDistrictByCode falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val districtStats = testDistrictStats(code = 1601, municipalityCount = 5)
        `when`(districtRepository.findDistrictByCode(1601)).thenReturn(district)
        `when`(districtStatsRepository.findDistrictStatsByCode(1601)).thenReturn(districtStats)

        val result =
            districtService.getDistrictByCode(listOf("1", "!!", "", "e"), 1601)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictByCode(1601)
        verify(districtStatsRepository).findDistrictStatsByCode(1601)
    }

    // ------------------------------------------------------------------
    // getDistrictMunicipalitiesByCode
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictMunicipalitiesByCode returns a page of mapped municipality responses`() {
        val municipalityId = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = municipalityId,
                code = 1601,
                slug = "test-municipality",
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
        `when`(districtRepository.existsDistrictByCode(1601)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictCode(1601, pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result = districtService.getDistrictMunicipalitiesByCode(emptyList(), 1601, pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).existsDistrictByCode(1601)
        verify(municipalityRepository).findMunicipalitiesByDistrictCode(1601, pageable)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode throws DistrictNotFoundException when district does not exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.existsDistrictByCode(9999)).thenReturn(false)

        assertThatThrownBy {
            districtService.getDistrictMunicipalitiesByCode(emptyList(), 9999, pageable)
        }.isInstanceOf(DistrictNotFoundException::class.java)

        verify(districtRepository).existsDistrictByCode(9999)
        verifyNoInteractions(municipalityRepository)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode filters and normalizes the given languages`() {
        val municipalityId = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = municipalityId,
                code = 1601,
                slug = "test-municipality",
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
        `when`(districtRepository.existsDistrictByCode(1601)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictCode(1601, pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            districtService.getDistrictMunicipalitiesByCode(
                listOf(" AR , en ", "12", "!"),
                1601,
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en")

        verify(districtRepository).existsDistrictByCode(1601)
        verify(municipalityRepository).findMunicipalitiesByDistrictCode(1601, pageable)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode falls back to default languages when every entry is invalid`() {
        val municipalityId = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = municipalityId,
                code = 1601,
                slug = "test-municipality",
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
        `when`(districtRepository.existsDistrictByCode(1601)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictCode(1601, pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            districtService.getDistrictMunicipalitiesByCode(
                listOf("1", "!!", "", "e"),
                1601,
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).existsDistrictByCode(1601)
        verify(municipalityRepository).findMunicipalitiesByDistrictCode(1601, pageable)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode returns an empty page when district has no municipalities`() {
        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.existsDistrictByCode(1601)).thenReturn(true)
        `when`(municipalityRepository.findMunicipalitiesByDistrictCode(1601, pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = districtService.getDistrictMunicipalitiesByCode(emptyList(), 1601, pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(districtRepository).existsDistrictByCode(1601)
        verify(municipalityRepository).findMunicipalitiesByDistrictCode(1601, pageable)
    }
}
