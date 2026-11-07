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
package com.example.dzairapi.controller

import com.example.dzairapi.repository.DistrictRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
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
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(20) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistricts without lang should use empty list`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(20) }
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
                .param("size", "5")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(5) }
            .hasPathSatisfying("$.content.length()") { it.assertThat().isEqualTo(5) }
    }

    @Test
    fun `getDistricts without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }

    // ------------------------------------------------------------------
    // getDistrictByCode
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictByCode should return 200`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/101")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.code") { it.assertThat().isEqualTo("0101") }
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictByCode without lang should pass an empty list to the service`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/101")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.code") { it.assertThat().isEqualTo("0101") }
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictByCode when not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/999")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `getDistrictByCode with non-numeric code should return 400`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/invalid-code")

        assertThat(result)
            .hasStatus(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `getDistrictByCode without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts/101")

        assertThat(result)
            .hasStatus(404)
    }

    // ------------------------------------------------------------------
    // getDistrictMunicipalitiesByCode
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictMunicipalitiesByCode should return 200 with page`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/101/municipalities")
                .param("lang", "en", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(20) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictMunicipalitiesByCode without lang should use empty list`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/101/municipalities")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(20) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictMunicipalitiesByCode should respect custom pagination parameters`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/101/municipalities")
                .param("page", "0")
                .param("size", "2")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(2) }
    }

    @Test
    fun `getDistrictMunicipalitiesByCode when district not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/999/municipalities")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode with non-numeric code should return 400`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/invalid-code/municipalities")

        assertThat(result)
            .hasStatus(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `getDistrictMunicipalitiesByCode without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts/101/municipalities")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }
}
