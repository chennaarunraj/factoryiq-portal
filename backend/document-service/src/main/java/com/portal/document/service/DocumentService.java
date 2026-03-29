package com.portal.document.service;

import com.portal.document.exception.ResourceNotFoundException;
import com.portal.document.model.Document;
import com.portal.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MinioService minioService;

    // ── GET ALL ───────────────────────────────
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> getDocumentsByCustomer(UUID customerId) {
        return documentRepository.findByCustomerId(customerId);
    }

    public List<Document> getDocumentsByProgram(UUID programId) {
        return documentRepository.findByProgramId(programId);
    }

    public Document getDocumentById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
    }

    // ── UPLOAD ────────────────────────────────
    @Transactional
    public Document uploadDocument(MultipartFile file, String title,
                                   String docType, UUID customerId,
                                   UUID programId) throws Exception {
        String folder = customerId != null ? customerId.toString() : "general";
        String minioKey = minioService.uploadFile(file, folder);

        Document document = Document.builder()
                .title(title)
                .docType(docType != null ? docType : "OTHER")
                .status("DRAFT")
                .customerId(customerId)
                .programId(programId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .minioBucket(minioService.getBucketName())
                .minioKey(minioKey)
                .version(1)
                .build();

        Document saved = documentRepository.save(document);
        log.info("Document saved: {}", saved.getId());
        return saved;
    }

    // ── GET DOWNLOAD URL ──────────────────────
    public String getDownloadUrl(UUID id) throws Exception {
        Document document = getDocumentById(id);
        return minioService.getPresignedUrl(document.getMinioKey());
    }

    // ── DELETE ────────────────────────────────
    @Transactional
    public void deleteDocument(UUID id) throws Exception {
        Document document = getDocumentById(id);
        minioService.deleteFile(document.getMinioKey());
        documentRepository.deleteById(id);
        log.info("Document deleted: {}", id);
    }

    // ── UPDATE STATUS ─────────────────────────
    @Transactional
    public Document updateStatus(UUID id, String status) {
        Document document = getDocumentById(id);
        document.setStatus(status);
        return documentRepository.save(document);
    }
}