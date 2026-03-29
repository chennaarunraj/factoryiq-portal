package com.portal.document.controller;

import com.portal.document.model.Document;
import com.portal.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Document Service is running!");
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Document>> getDocumentsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(documentService.getDocumentsByCustomer(customerId));
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<List<Document>> getDocumentsByProgram(@PathVariable UUID programId) {
        return ResponseEntity.ok(documentService.getDocumentsByProgram(programId));
    }

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "docType", required = false) String docType,
            @RequestParam(value = "customerId", required = false) UUID customerId,
            @RequestParam(value = "programId", required = false) UUID programId) {
        try {
            Document document = documentService.uploadDocument(file, title, docType, customerId, programId);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (Exception e) {
            log.error("Upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/download-url")
    public ResponseEntity<String> getDownloadUrl(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(documentService.getDownloadUrl(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Document> updateStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(documentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}