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
package com.example.dzairapi.helpers

import com.example.dzairapi.model.District
import com.example.dzairapi.model.DistrictStats
import com.example.dzairapi.model.DistrictTranslation
import com.example.dzairapi.model.DistrictTranslationId
import com.example.dzairapi.model.Language
import com.example.dzairapi.model.Municipality
import com.example.dzairapi.model.MunicipalityTranslation
import com.example.dzairapi.model.MunicipalityTranslationId
import com.example.dzairapi.model.Province
import com.example.dzairapi.model.ProvinceStats
import com.example.dzairapi.model.ProvinceTranslation
import com.example.dzairapi.model.ProvinceTranslationId
import java.util.UUID

fun testProvince(
    id: UUID = UUID.randomUUID(),
    code: Short,
    slug: String,
    translations: Set<ProvinceTranslation> = emptySet(),
    districts: Set<District> = emptySet(),
): Province =
    Province().apply {
        this.id = id
        this.code = code
        this.slug = slug
        this.translations = translations.toMutableSet()
        this.districts = districts.toMutableSet()
    }

fun testProvinceTranslation(
    provinceId: UUID,
    name: String,
    language: Language,
): ProvinceTranslation =
    ProvinceTranslation().apply {
        id =
            ProvinceTranslationId().apply {
                this.provinceId = provinceId
                this.languageId = language.id
            }
        this.language = language
        this.name = name
    }

fun testProvinceStats(
    code: Short,
    districtCount: Short,
    municipalityCount: Short,
): ProvinceStats =
    ProvinceStats(
        code = code,
        districtCount = districtCount,
        municipalityCount = municipalityCount,
    )

fun testLanguage(
    id: UUID = UUID.randomUUID(),
    code: String,
    name: String? = null,
): Language =
    Language().apply {
        this.id = id
        this.code = code
        this.name =
            when (code) {
                "ar" -> "العربية"
                "fr" -> "Français"
                "en" -> "English"
                else -> name ?: "Unknown"
            }
    }

fun testDistrict(
    id: UUID = UUID.randomUUID(),
    code: Short,
    slug: String,
    translations: Set<DistrictTranslation> = emptySet(),
    municipalities: Set<Municipality> = emptySet(),
): District =
    District().apply {
        this.id = id
        this.code = code
        this.slug = slug
        this.translations = translations.toMutableSet()
        this.municipalities = municipalities.toMutableSet()
    }

fun testDistrictTranslation(
    districtId: UUID,
    name: String,
    language: Language,
): DistrictTranslation =
    DistrictTranslation().apply {
        id =
            DistrictTranslationId().apply {
                this.districtId = districtId
                this.languageId = language.id
            }
        this.language = language
        this.name = name
    }

fun testDistrictStats(
    code: Short,
    municipalityCount: Short,
): DistrictStats =
    DistrictStats(
        code = code,
        municipalityCount = municipalityCount,
    )

fun testMunicipality(
    id: UUID = UUID.randomUUID(),
    code: Short,
    slug: String,
    translations: Set<MunicipalityTranslation> = emptySet(),
): Municipality =
    Municipality().apply {
        this.id = id
        this.code = code
        this.slug = slug
        this.translations = translations.toMutableSet()
    }

fun testMunicipalityTranslation(
    municipalityId: UUID,
    name: String,
    language: Language,
): MunicipalityTranslation =
    MunicipalityTranslation().apply {
        id =
            MunicipalityTranslationId().apply {
                this.municipalityId = municipalityId
                this.languageId = language.id
            }
        this.language = language
        this.name = name
    }
