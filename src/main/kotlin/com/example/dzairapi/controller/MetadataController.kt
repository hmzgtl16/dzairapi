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

import com.example.dzairapi.dto.MetadataResponse
import com.example.dzairapi.service.MetadataService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{version}/metadata", version = "1.0")
@Tag(name = "Metadata", description = "API and dataset metadata")
class MetadataController(
    private val metadataService: MetadataService,
) {
    @GetMapping
    @Operation(
        summary = "Get dataset metadata",
        description = "Returns current API and dataset metadata",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Metadata found",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Metadata not found",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        schema = Schema(implementation = ProblemDetail::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "429",
                description = "Rate limit exceeded.",
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
        ],
    )
    fun getMetadata(): ResponseEntity<MetadataResponse> {
        val response = metadataService.getDatasetMetadata()
        return ResponseEntity.ok(response)
    }
}
