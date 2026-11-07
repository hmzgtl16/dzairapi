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

import com.example.dzairapi.dto.MunicipalityResponse
import com.example.dzairapi.dto.PageQuery
import com.example.dzairapi.dto.PageResponse
import com.example.dzairapi.service.MunicipalityService
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
@RequestMapping("api/{version}/municipalities", version = "1.0")
@Tag(name = "Municipalities", description = "Algerian municipalities (communes)")
class MunicipalityController(
    private val municipalityService: MunicipalityService,
) {
    @Operation(
        summary = "List of municipalities",
        description = "Returns a list of municipalities with their names in the requested languages.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of municipalities",
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
    fun getMunicipalities(
        @Parameter(description = "Language codes to return names for.")
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @Valid @ParameterObject query: PageQuery,
    ): ResponseEntity<PageResponse<MunicipalityResponse>> {
        val response = municipalityService
            .getMunicipalities(languages, query)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @Operation(
        summary = "Get a municipality",
        description = "Returns a single municipality by its numeric code.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Municipality found",
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
            description = "Municipality not found",
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
    fun getMunicipalityByCode(
        @Parameter(description = "Language codes to return names for.")
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @Parameter(description = "Municipality code")
        @PathVariable code: Short,
    ): ResponseEntity<MunicipalityResponse> {
        val response = municipalityService.getMunicipalityByCode(languages, code)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
