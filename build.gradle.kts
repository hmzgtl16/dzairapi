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
import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kover)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependencyManagement)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "dzairapi"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.caffeine)
    implementation(libs.caffeine.jcache)
    implementation(libs.flyway.postgresql)
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.bucket4j)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.kotlinx.serialization.json)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    developmentOnly(libs.spring.boot.docker.compose)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.flyway.test)
    testImplementation(libs.spring.boot.starter.kotlinx.serialization.json.test)
    testImplementation(libs.spring.boot.starter.security.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.h2database)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

spotless {
    kotlin {
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.kt"))
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.kts"), "(^(?![\\/ ]\\*).*$)")
    }
    yaml {
        target("**/*.yaml")
        targetExclude("spotless/**")
        prettier(libs.versions.prettier.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.yaml"), "^[^#]")
    }
    sql {
        target("**/*.sql")
        targetExclude("spotless/**")
        prettier(
            mapOf(
                "prettier" to libs.versions.prettier.get(),
                "prettier-plugin-sql" to libs.versions.prettierPluginSql.get(),
            ),
        ).config(
            mapOf(
                "parser" to "sql",
                "plugins" to listOf("prettier-plugin-sql"),
            ),
        )
        licenseHeaderFile(rootDir.resolve("spotless/license-header.sql"), "^[^-]")
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "com.example.dzairapi.DzairAPIApplication",
                    "com.example.dzairapi.DzairAPIApplicationKt",
                    "com.example.dzairapi.config.*",
                    "com.example.dzairapi.dto.*",
                    "com.example.dzairapi.model.*",
                    "com.example.dzairapi.generated.*",
                    "com.example.dzairapi.utils.*",
                )
                annotatedBy(
                    "org.springframework.boot.autoconfigure.SpringBootApplication",
                    "*Generated*",
                )
            }
        }

        total {
            html { onCheck = true }
            xml { onCheck = true }

            log {
                aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                coverageUnits = CoverageUnit.LINE
            }
        }

        verify {
            rule("Minimum line coverage") {
                bound {
                    minValue = 80
                    coverageUnits = CoverageUnit.LINE
                    aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                }
            }
            rule("Minimum branch coverage") {
                bound {
                    minValue = 70
                    coverageUnits = CoverageUnit.BRANCH
                    aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}

tasks.withType<BootBuildImage> {
    imageName = "docker.io/library/${project.name}:${project.version}"
    builder = "paketobuildpacks/builder-noble-java-tiny:0.0.206"
    runImage = "paketobuildpacks/ubuntu-noble-run-tiny:0.0.153"
    environment.putAll(
        mapOf(
            "BP_NATIVE_IMAGE" to "true",
            "BP_JVM_VERSION" to "25",
            // "BP_JVM_CDS_ENABLED" to "true",
            // "BP_JVM_JLINK_ENABLED" to "true",
            // "BP_SPRING_AOT_ENABLED" to "true",
        ),
    )
    buildpacks.addAll(
        listOf(
            "paketobuildpacks/ca-certificates:3.14.4",
            "paketobuildpacks/bellsoft-liberica:11.10.5",
            "paketobuildpacks/syft:2.43.4",
            "paketobuildpacks/executable-jar:6.17.5",
            "paketobuildpacks/spring-boot:5.40.4",
            "paketobuildpacks/native-image:5.19.1",
        ),
    )
    cleanCache = true
}

graalvmNative {
    metadataRepository {
        enabled = true
        version = libs.versions.graalvmReachabilityMetadata.get()
    }
}
