package com.portal.document.repository;

import com.portal.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByCustomerId(UUID customerId);
    List<Document> findByProgramId(UUID programId);
    List<Document> findByDocType(String docType);
    List<Document> findByStatus(String status);
    List<Document> findByCustomerIdAndDocType(UUID customerId, String docType);
}