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
package com.example.dzprovinceapi.controller

import com.example.dzprovinceapi.repository.DistrictRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.assertj.MockMvcTester

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DistrictControllerTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    private fun existingDistrictCode(): Short =
        districtRepository
            .findDistrictsBy(PageRequest.of(0, 1))
            .content
            .first()
            .code

    // ------------------------------------------------------------------
    // getDistricts
    // ------------------------------------------------------------------

    @Test
    fun `getDistricts should return 200 with page`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts")
                .param("lang", "en", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("empty") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistricts without lang should use empty list and return default languages`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("empty") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
            .hasPathSatisfying("$.content[0].names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistricts should respect custom pagination parameters`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts")
                .param("page", "0")
                .param("size", "2")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(2) }
            .hasPathSatisfying("number-of-elements") { it.assertThat().isEqualTo(2) }
    }

    @Test
    fun `getDistricts without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts")

        assertThat(result)
            .hasStatus(404)
    }

    // ------------------------------------------------------------------
    // getDistrictByCode
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictByCode should return 200`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/$code")
                .param("lang", "en")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictByCode without lang should pass an empty list to the service`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/$code")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictByCode when not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/${Short.MAX_VALUE}")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(404)
    }

    @Test
    fun `getDistrictByCode with non-numeric code should return 400`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/invalid-code")

        assertThat(result)
            .hasStatus(400)
    }

    @Test
    fun `getDistrictByCode without version segment should return 404`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts/$code")

        assertThat(result)
            .hasStatus(404)
    }

    // ------------------------------------------------------------------
    // getDistrictMunicipalitiesByCode
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictMunicipalitiesByCode should return 200 with page`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/$code/municipalities")
                .param("lang", "en", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictMunicipalitiesByCode without lang should use empty list`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/$code/municipalities")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictMunicipalitiesByCode should respect custom pagination parameters`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/$code/municipalities")
                .param("page", "0")
                .param("size", "1")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(1) }
    }

    @Test
    fun `getDistrictMunicipalitiesByCode when district not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/${Short.MAX_VALUE}/municipalities")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(404)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode with non-numeric code should return 400`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/invalid-code/municipalities")

        assertThat(result)
            .hasStatus(400)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode without version segment should return 404`() {
        val code = existingDistrictCode()
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts/$code/municipalities")

        assertThat(result)
            .hasStatus(404)
    }
}
