/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- API KEY
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS api_key(
    api_key_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id TEXT NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT FALSE,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    modified_utc TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);