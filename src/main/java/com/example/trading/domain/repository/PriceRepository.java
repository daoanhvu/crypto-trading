package com.example.trading.domain.repository;

import com.example.trading.domain.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, String>, JpaSpecificationExecutor<PriceEntity> {
}
