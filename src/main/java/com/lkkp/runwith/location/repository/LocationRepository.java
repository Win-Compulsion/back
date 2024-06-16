package com.lkkp.runwith.location.repository;

import com.lkkp.runwith.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
