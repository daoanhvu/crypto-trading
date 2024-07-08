package com.example.trading.domain.repository;

import com.example.trading.domain.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {
    @Query(value = "select t from TransactionEntity t where (t.fromUser = :username) or (t.toUser = :username) order by t.transactionTime desc")
    Page<TransactionEntity> retrieveTransactionsByUser(@Param("username") String username, Pageable pageable);
}
