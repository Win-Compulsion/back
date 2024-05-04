package com.lkkp.runwith.record.repository;

import com.lkkp.runwith.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
