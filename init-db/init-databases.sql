-- Creates one database per service inside a single local Postgres container.
-- Each service only ever connects to its own database and never queries another
-- service's tables directly -- this preserves the database-per-service principle
-- while keeping local resource usage low. In a cloud deployment, these would
-- typically be three separate managed database instances instead.

CREATE DATABASE orders_db;
CREATE DATABASE inventory_db;
CREATE DATABASE notifications_db;
