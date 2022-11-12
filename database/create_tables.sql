/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- API ID
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS api_id(
    aid UUID PRIMARY KEY,
    uid TEXT NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT FALSE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    modified_utc TIMESTAMP WITH TIME ZONE NOT NULL
);

-- -----------------------------------------------------------------------
-- TOKEN
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS token(
    tid UUID PRIMARY KEY,
    aid UUID NOT NULL,
    urn_prefix TEXT NOT NULL,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY(aid) REFERENCES api_id(aid)
);

-- -----------------------------------------------------------------------
-- REPORT
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS report(
    rid BIGSERIAL PRIMARY KEY,
    urn_prefix TEXT NOT NULL,
    size_bytes BIGINT,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_usage_log_urn_created ON report (urn_prefix, created_utc);