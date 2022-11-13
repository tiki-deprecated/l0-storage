/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.api_id;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApiIdRepository extends JpaRepository<ApiIdDO, UUID> {
    Page<ApiIdDO> findAllByUid(String uid, Pageable pageable);
}
