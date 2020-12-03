CREATE TABLE auth (
  api_key   VARCHAR(50)   PRIMARY KEY,
  user_id   VARCHAR(50)   NOT NULL
);

INSERT INTO auth(api_key, user_id) VALUES ('aec093c2-c981-44f9-9a4a-365ad1d2f05e','testuser@gmail.com');
