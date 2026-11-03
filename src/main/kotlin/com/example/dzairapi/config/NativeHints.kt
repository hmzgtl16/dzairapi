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
package com.example.dzairapi.config

import com.giffing.bucket4j.spring.boot.starter.context.constraintvalidations.Bucket4JConfigurationPredicateNameValidator
import com.giffing.bucket4j.spring.boot.starter.context.constraintvalidations.DurationChronoUnitValidator
import com.giffing.bucket4j.spring.boot.starter.context.constraintvalidations.RateLimitBandWidthIdsValidator
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.registerType
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

@Configuration
@ImportRuntimeHints(NativeHints::class)
class NativeHintsConfig

class NativeHints : RuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        classLoader: ClassLoader?,
    ) {
        val members =
            arrayOf(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.ACCESS_DECLARED_FIELDS,
            )
        hints
            .reflection()
            .registerType<Bucket4JConfigurationPredicateNameValidator>(*members)
            .registerType<RateLimitBandWidthIdsValidator>(*members)
            .registerType<DurationChronoUnitValidator>(*members)

        hints
            .reflection()
            .registerType(java.util.UUID::class.java.arrayType())
    }
}
