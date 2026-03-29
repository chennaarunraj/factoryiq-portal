-- ============================================================
-- V7__document_schema.sql
-- Manufacturing Excellence Portal - Document Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM TYPES
-- ─────────────────────────────────────────
CREATE TYPE portal.document_status AS ENUM (
    'DRAFT', 'UNDER_REVIEW', 'APPROVED', 'OBSOLETE'
);

CREATE TYPE portal.document_type AS ENUM (
    'DRAWING', 'SPECIFICATION', 'TEST_PLAN', 'TEST_RESULT',
    'CERTIFICATE', 'COMPLIANCE', 'PHOTO', 'VIDEO',
    'BOM', 'ECO', 'OTHER'
);

-- ─────────────────────────────────────────
-- DOCUMENTS
-- ─────────────────────────────────────────
CREATE TABLE portal.documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    doc_type        portal.document_type NOT NULL DEFAULT 'OTHER',
    status          portal.document_status NOT NULL DEFAULT 'DRAFT',
    customer_id     UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    program_id      UUID REFERENCES portal.programs(id) ON DELETE SET NULL,
    file_name       VARCHAR(255) NOT NULL,
    file_size       BIGINT,
    content_type    VARCHAR(100),
    minio_bucket    VARCHAR(100) NOT NULL,
    minio_key       VARCHAR(500) NOT NULL,
    version         INTEGER NOT NULL DEFAULT 1,
    uploaded_by_id  UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.documents IS 'Document metadata - files stored in MinIO';
CREATE INDEX idx_docs_customer   ON portal.documents(customer_id);
CREATE INDEX idx_docs_program    ON portal.documents(program_id);
CREATE INDEX idx_docs_type       ON portal.documents(doc_type);
CREATE INDEX idx_docs_status     ON portal.documents(status);

-- ─────────────────────────────────────────
-- TRIGGERS
-- ─────────────────────────────────────────
CREATE TRIGGER trg_documents_updated_at
    BEFORE UPDATE ON portal.documents
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();