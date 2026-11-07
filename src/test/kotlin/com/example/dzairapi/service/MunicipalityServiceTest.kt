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

import com.example.dzairapi.dto.PageQuery
import com.example.dzairapi.exception.MunicipalityNotFoundException
import com.example.dzairapi.helpers.testLanguage
import com.example.dzairapi.helpers.testMunicipality
import com.example.dzairapi.helpers.testMunicipalityTranslation
import com.example.dzairapi.repository.MunicipalityRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class MunicipalityServiceTest {
    @Mock
    private lateinit var municipalityRepository: MunicipalityRepository

    @InjectMocks
    private lateinit var municipalityService: MunicipalityServiceImpl

    // ------------------------------------------------------------------
    // getMunicipalities
    // ------------------------------------------------------------------

    @Test
    fun `getMunicipalities returns a page of mapped responses`() {
        val municipality = testMunicipality(code = 1601, slug = "test-slug", translations = emptySet())

        val pageQuery = PageQuery(0, 10)
        val pageable = pageQuery.toPageable()
        val municipalityPage = PageImpl(listOf(municipality), pageable, 1)
        `when`(municipalityRepository.findAll(pageable)).thenReturn(municipalityPage)

        val result = municipalityService.getMunicipalities(emptyList(), pageQuery)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("1601")
        assertThat(result.content[0].names).isEmpty()

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getMunicipalities falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageQuery = PageQuery(0, 10)
        val pageable = pageQuery.toPageable()
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result = municipalityService.getMunicipalities(emptyList(), pageQuery)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("1601")
        assertThat(result.content[0].names).hasSize(municipality.translations.size)
        assertThat(result.content[0].names.map { it.language.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getMunicipalities filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageQuery = PageQuery(0, 10)
        val pageable = pageQuery.toPageable()
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result = municipalityService.getMunicipalities(listOf("en"), pageQuery)

        assertThat(result.content[0].names).hasSize(1)
        assertThat(
            result.content[0]
                .names
                .single()
                .language
                .code,
        ).isEqualTo("en")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getMunicipalities normalizes languages by splitting on comma, trimming, lowercasing and de-duplicating`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageQuery = PageQuery(0, 10)
        val pageable = pageQuery.toPageable()
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            municipalityService.getMunicipalities(
                listOf(" EN ", "fr,ar", "en", "12", "e"),
                pageQuery,
            )

        assertThat(result.content[0].names.map { it.language.code })
            .containsExactlyInAnyOrder("en", "fr", "ar")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getMunicipalities falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageQuery = PageQuery(0, 10)
        val pageable = pageQuery.toPageable()
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            municipalityService.getMunicipalities(listOf("1", "!!", "", "e", "ara"), pageQuery)

        assertThat(result.content[0].names.map { it.language.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getMunicipalities returns an empty page when no municipalities exist`() {
        val pageQuery = PageQuery(0, 10)
        val pageable = pageQuery.toPageable()
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = municipalityService.getMunicipalities(emptyList(), pageQuery)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(municipalityRepository).findAll(pageable)
    }

    // ------------------------------------------------------------------
    // getMunicipalityByCode
    // ------------------------------------------------------------------

    @Test
    fun `getMunicipalityByCode returns the mapped response`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityByCode(1601)).thenReturn(municipality)

        val result = municipalityService.getMunicipalityByCode(emptyList(), 1601)

        assertThat(result.code).isEqualTo("1601")
        assertThat(result.names.map { it.language.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findMunicipalityByCode(1601)
    }

    @Test
    fun `getMunicipalityByCode throws MunicipalityCodeNotFoundException when no municipality matches`() {
        `when`(municipalityRepository.findMunicipalityByCode(9999)).thenReturn(null)

        assertThatThrownBy { municipalityService.getMunicipalityByCode(emptyList(), 9999) }
            .isInstanceOf(MunicipalityNotFoundException::class.java)

        verify(municipalityRepository).findMunicipalityByCode(9999)
    }

    @Test
    fun `getMunicipalityByCode normalizes the given languages`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityByCode(1601)).thenReturn(municipality)

        val result =
            municipalityService.getMunicipalityByCode(listOf("EN, fr", "en", "!"), 1601)

        assertThat(result.names.map { it.language.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(municipalityRepository).findMunicipalityByCode(1601)
    }

    @Test
    fun `getMunicipalityByCode falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "test-slug",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityByCode(1601)).thenReturn(municipality)

        val result =
            municipalityService.getMunicipalityByCode(listOf("1", "!!", "", "e"), 1601)

        assertThat(result.names.map { it.language.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findMunicipalityByCode(1601)
    }
}
