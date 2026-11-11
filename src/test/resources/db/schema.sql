-- dzairapi
-- Copyright 2026 Hamza Gattal
--
-- This database and its contents are licensed under a
-- Creative Commons Attribution 4.0 International License (CC BY 4.0).
--
-- You are free to:
--   - Share: copy and redistribute the material in any medium or format
--   - Adapt: remix, transform, and build upon the material for any purpose,
--     even commercially.
--
-- Under the following terms:
--   - Attribution: You must give appropriate credit, provide a link to the
--     license, and indicate if changes were made.
--
-- Full license text: https://creativecommons.org/licenses/by/4.0/
-- SPDX-License-Identifier: CC-BY-4.0
CREATE TABLE IF NOT EXISTS language (
  id UUID PRIMARY KEY,
  code VARCHAR(2) NOT NULL UNIQUE, -- ISO 639-1: ar, fr, en
  name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS province (
  id UUID PRIMARY KEY,
  code SMALLINT NOT NULL UNIQUE,
  slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS province_translation (
  language_id UUID NOT NULL REFERENCES language (id) ON DELETE RESTRICT,
  province_id UUID NOT NULL REFERENCES province (id) ON DELETE CASCADE,
  name VARCHAR(150) NOT NULL,
  PRIMARY KEY (province_id, language_id)
);

CREATE TABLE IF NOT EXISTS district (
  id UUID PRIMARY KEY,
  slug VARCHAR(100) NOT NULL UNIQUE,
  code SMALLINT NOT NULL UNIQUE,
  province_id UUID NOT NULL REFERENCES province (id) ON DELETE RESTRICT,
  CONSTRAINT uq_district_province_slug UNIQUE (province_id, slug)
);

CREATE TABLE IF NOT EXISTS district_translation (
  language_id UUID NOT NULL REFERENCES language (id) ON DELETE RESTRICT,
  district_id UUID NOT NULL REFERENCES district (id) ON DELETE CASCADE,
  name VARCHAR(150) NOT NULL,
  PRIMARY KEY (district_id, language_id)
);

CREATE TABLE IF NOT EXISTS municipality (
  id UUID PRIMARY KEY,
  slug VARCHAR(100) NOT NULL UNIQUE,
  code SMALLINT NOT NULL UNIQUE,
  district_id UUID NOT NULL REFERENCES district (id) ON DELETE RESTRICT,
  CONSTRAINT uq_municipality_district_slug UNIQUE (district_id, slug)
);

CREATE TABLE IF NOT EXISTS municipality_translation (
  language_id UUID NOT NULL REFERENCES language (id) ON DELETE RESTRICT,
  municipality_id UUID NOT NULL REFERENCES municipality (id) ON DELETE CASCADE,
  name VARCHAR(150) NOT NULL,
  PRIMARY KEY (municipality_id, language_id)
);
