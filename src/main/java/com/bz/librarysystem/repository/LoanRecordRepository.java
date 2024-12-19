package com.bz.librarysystem.repository;

import com.bz.librarysystem.entity.LoanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRecordRepository extends JpaRepository<LoanRecord, UUID> {
    Optional<LoanRecord> findByBookIdAndReturnedAtIsNull(UUID bookId);
}

