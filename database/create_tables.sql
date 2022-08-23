/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- API ID
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS api_id(
    api_id UUID PRIMARY KEY,
    customer_id TEXT NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT FALSE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    modified_utc TIMESTAMP WITH TIME ZONE NOT NULL
);

-- -----------------------------------------------------------------------
-- POLICY
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS policy(
    policy_id BIGSERIAL NOT NULL,
    api_id uuid NOT NULL,
    urn_prefix TEXT NOT NULL,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY(api_id) REFERENCES api_id(api_id)
);

-- -----------------------------------------------------------------------
-- USAGE
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usage(
    usage_id BIGSERIAL NOT NULL,
    urn_prefix TEXT NOT NULL,
    block_count BIGINT,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_usage_log_urn_created ON usage (urn_prefix, created_utc);