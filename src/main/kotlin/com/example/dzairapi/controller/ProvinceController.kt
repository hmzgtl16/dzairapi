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

import com.example.dzairapi.dto.DistrictResponse
import com.example.dzairapi.dto.MunicipalityResponse
import com.example.dzairapi.dto.PageQuery
import com.example.dzairapi.dto.PageResponse
import com.example.dzairapi.dto.ProvinceResponse
import com.example.dzairapi.service.ProvinceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{version}/provinces", version = "1.0")
@Tag(name = "Provinces", description = "Algerian provinces (wilayas)")
class ProvinceController(
    private val provinceService: ProvinceService,
) {
    @Operation(
        summary = "List os provinces",
        description = "Returns a list of provinces with their names in the requested languages.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of provinces",
        ),
        ApiResponse(
            responseCode = "500",
            description = "Unexpected error",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
    )
    @GetMapping
    fun getProvinces(
        @Parameter(description = "Language codes to return names for.")
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @Valid @ParameterObject query: PageQuery,
    ): ResponseEntity<PageResponse<ProvinceResponse>> {
        val response = provinceService
            .getProvinces(languages, query)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @Operation(
        summary = "Get a province",
        description = "Returns a single province by its numeric code.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Province found",
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid code",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "Province not found",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
        ApiResponse(
            responseCode = "500",
            description = "Unexpected error",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
    )
    @GetMapping("/{code}")
    fun getProvinceByCode(
        @Parameter(description = "Language codes to return names for.")
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @Parameter(description = "Province code")
        @PathVariable code: Short,
    ): ResponseEntity<ProvinceResponse> {
        val response = provinceService.getProvinceByCode(languages, code)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @Operation(
        summary = "List districts of a province",
        description = "Returns a list of the districts belonging to the given province.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of districts",
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid code",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "Province not found",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
        ApiResponse(
            responseCode = "500",
            description = "Unexpected error",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
    )
    @GetMapping("/{code}/districts")
    fun getProvinceDistrictsByCode(
        @Parameter(description = "Language codes to return names for.")
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @Parameter(description = "Province code")
        @PathVariable code: Short,
        @Valid @ParameterObject query: PageQuery,
    ): ResponseEntity<PageResponse<DistrictResponse>> {
        val response = provinceService
            .getProvinceDistrictsByCode(languages, code, query)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @Operation(
        summary = "List municipalities of a province",
        description = "Returns a list of the municipalities belonging to the given province.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of municipalities",
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid code",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "Province not found",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
        ApiResponse(
            responseCode = "500",
            description = "Unexpected error",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class),
                ),
            ],
        ),
    )
    @GetMapping("/{code}/municipalities")
    fun getProvinceMunicipalitiesByCode(
        @Parameter(description = "Language codes to return names for.")
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @Parameter(description = "Province code")
        @PathVariable code: Short,
        @Valid @ParameterObject query: PageQuery,
    ): ResponseEntity<PageResponse<MunicipalityResponse>> {
        val response = provinceService
            .getProvinceMunicipalitiesByCode(languages, code, query)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
