package com.example.dzairapi.utils.aothint

import com.giffing.bucket4j.spring.boot.starter.context.constraintvalidations.Bucket4JConfigurationPredicateNameValidator
import com.giffing.bucket4j.spring.boot.starter.context.constraintvalidations.DurationChronoUnitValidator
import com.giffing.bucket4j.spring.boot.starter.context.constraintvalidations.RateLimitBandWidthIdsValidator
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.registerType
import java.util.UUID

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
            .registerType(UUID::class.java.arrayType())
    }
}
