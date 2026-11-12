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
package com.example.dzairapi.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
@Table(name = "metadata", schema = "public")
class Metadata(
    @Column(name = "api_version")
    var apiVersion: String,
    @Column(name = "dataset_version")
    var datasetVersion: String,
    @Column(name = "last_updated")
    var lastUpdated: Instant,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sources", columnDefinition = "jsonb", nullable = false)
    var sources: List<DataSource>,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "counts", columnDefinition = "jsonb", nullable = false)
    var counts: Counts,
) {
    @Id
    var id: Int? = null
}
