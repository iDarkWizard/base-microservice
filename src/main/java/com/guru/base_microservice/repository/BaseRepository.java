package com.guru.base_microservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BaseRepository<ENTITY, ID> extends JpaRepository<ENTITY, ID>, JpaSpecificationExecutor<ENTITY> {
	
	Optional<ENTITY> findByUuidAndDeactivatedAtIsNull (ID id);

	Page<ENTITY> findByDeactivatedAtIsNull(Pageable pageable);
}
