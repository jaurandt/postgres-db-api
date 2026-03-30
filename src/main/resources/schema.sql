-- Run this once to set up the database before starting the server.
--
-- Connect to PostgreSQL and run:
--   psql -U postgres -d postgresdb -f src/main/resources/schema.sql

CREATE TABLE IF NOT EXISTS records (
    id    SERIAL       PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    value TEXT         NOT NULL
);