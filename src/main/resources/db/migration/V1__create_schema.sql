--
-- Copyright (C) 2020  Greg Whitaker
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published
-- by the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.
--

CREATE TABLE auth (
  api_key   VARCHAR(50)   PRIMARY KEY,
  user_id   VARCHAR(50)   NOT NULL
);

INSERT INTO auth(api_key, user_id) VALUES ('aec093c2-c981-44f9-9a4a-365ad1d2f05e','testuser@gmail.com');
