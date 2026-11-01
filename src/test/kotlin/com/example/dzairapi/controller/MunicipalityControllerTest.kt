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
class MunicipalityControllerTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    // ------------------------------------------------------------------
    // getMunicipalities
    // ------------------------------------------------------------------

    @Test
    fun `getMunicipalities should return 200 with page`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities")
                .param("lang", "en", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("empty") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("last") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("number-of-elements") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getMunicipalities without lang should use empty list`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities")

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
    fun `getMunicipalities should respect custom pagination parameters`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities")
                .param("page", "0")
                .param("size", "5")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(5) }
            .hasPathSatisfying("number-of-elements") { it.assertThat().isEqualTo(5) }
    }

    @Test
    fun `getMunicipalities without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/municipalities")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }

    // ------------------------------------------------------------------
    // getMunicipalityByCode
    // ------------------------------------------------------------------

    @Test
    fun `getMunicipalityByCode should return 200`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities/101")
                .param("lang", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.code") { it.assertThat().isEqualTo("0101") }
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getMunicipalityByCode without lang should pass an empty list to the service`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities/101")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.code") { it.assertThat().isEqualTo("0101") }
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getMunicipalityByCode when not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities/999")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `getMunicipalityByCode with non-numeric code should return 400`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/municipalities/invalid-code")

        assertThat(result)
            .hasStatus(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `getMunicipalityByCode without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/municipalities/101")

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND)
    }
}
