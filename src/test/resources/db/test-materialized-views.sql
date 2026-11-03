-- Copyright 2026 Hamza Gattal
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
CREATE VIEW IF NOT EXISTS province_stats AS
SELECT
  p.code,
  COUNT(DISTINCT d.id) AS district_count,
  COUNT(m.id) AS municipality_count
FROM
  province p
  LEFT JOIN district d ON p.id = d.province_id
  LEFT JOIN municipality m ON d.id = m.district_id
GROUP BY
  p.code
ORDER BY
  p.code;

CREATE VIEW IF NOT EXISTS district_stats AS
SELECT
  d.code,
  COUNT(m.id) AS municipality_count
FROM
  district d
  LEFT JOIN municipality m ON d.id = m.district_id
GROUP BY
  d.code
ORDER BY
  d.code;
