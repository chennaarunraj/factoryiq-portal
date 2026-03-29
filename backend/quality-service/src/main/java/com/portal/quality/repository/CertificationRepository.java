package com.portal.quality.repository;

import com.portal.quality.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, UUID> {
    Optional<Certification> findByCertNumber(String certNumber);
    List<Certification> findBySiteId(UUID siteId);
    List<Certification> findByStatus(String status);

    @Query("SELECT c FROM Certification c WHERE c.expiryDate <= :date AND c.status = 'ACTIVE'")
    List<Certification> findExpiringSoon(LocalDate date);
}