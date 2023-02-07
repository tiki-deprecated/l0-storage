/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- TOKEN
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS token(
    token_id UUID PRIMARY KEY,
    app_id UUID NOT NULL,
    urn_prefix TEXT NOT NULL,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL
);

-- -----------------------------------------------------------------------
-- REPORT
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS report(
    report_id BIGSERIAL PRIMARY KEY,
    urn_prefix TEXT NOT NULL,
    size_bytes BIGINT,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_usage_log_urn_created ON report (urn_prefix, created_utc);