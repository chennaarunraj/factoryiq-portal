package com.portal.quality.repository;

import com.portal.quality.model.Capa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapaRepository extends JpaRepository<Capa, UUID> {
    Optional<Capa> findByCapaNumber(String capaNumber);
    List<Capa> findByNcrId(UUID ncrId);
    List<Capa> findByStatus(String status);
    List<Capa> findByOwnerId(UUID ownerId);
    boolean existsByCapaNumber(String capaNumber);
}