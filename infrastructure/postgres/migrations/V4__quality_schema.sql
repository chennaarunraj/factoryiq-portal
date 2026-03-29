-- ============================================================
-- V4__quality_schema.sql
-- Manufacturing Excellence Portal - Quality Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM TYPES
-- ─────────────────────────────────────────
CREATE TYPE portal.ncr_status AS ENUM (
    'OPEN', 'IN_REVIEW', 'CONTAINED', 'ROOT_CAUSE_ANALYSIS',
    'CAPA_IN_PROGRESS', 'CLOSED', 'CANCELLED'
);

CREATE TYPE portal.severity_level AS ENUM (
    'CRITICAL', 'MAJOR', 'MINOR'
);

CREATE TYPE portal.capa_status AS ENUM (
    'OPEN', 'IN_PROGRESS', 'PENDING_VERIFICATION', 'CLOSED', 'CANCELLED'
);

CREATE TYPE portal.cert_status AS ENUM (
    'ACTIVE', 'EXPIRING_SOON', 'EXPIRED', 'RENEWAL_IN_PROGRESS'
);

-- ─────────────────────────────────────────
-- NCR (Non-Conformance Reports)
-- ─────────────────────────────────────────
CREATE TABLE portal.ncrs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ncr_number          VARCHAR(50) NOT NULL UNIQUE,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    customer_id         UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    program_id          UUID REFERENCES portal.programs(id) ON DELETE SET NULL,
    site_id             UUID REFERENCES portal.sites(id) ON DELETE SET NULL,
    status              portal.ncr_status NOT NULL DEFAULT 'OPEN',
    severity            portal.severity_level NOT NULL DEFAULT 'MINOR',
    detected_date       DATE NOT NULL DEFAULT CURRENT_DATE,
    containment_action  TEXT,
    reported_by_id      UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    assigned_to_id      UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    closed_date         DATE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.ncrs IS 'Non-Conformance Reports';
CREATE INDEX idx_ncrs_customer ON portal.ncrs(customer_id);
CREATE INDEX idx_ncrs_program  ON portal.ncrs(program_id);
CREATE INDEX idx_ncrs_status   ON portal.ncrs(status);
CREATE INDEX idx_ncrs_severity ON portal.ncrs(severity);

-- ─────────────────────────────────────────
-- CAPA (Corrective and Preventive Actions)
-- ─────────────────────────────────────────
CREATE TABLE portal.capas (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    capa_number         VARCHAR(50) NOT NULL UNIQUE,
    ncr_id              UUID REFERENCES portal.ncrs(id) ON DELETE SET NULL,
    title               VARCHAR(255) NOT NULL,
    root_cause          TEXT,
    corrective_action   TEXT,
    preventive_action   TEXT,
    status              portal.capa_status NOT NULL DEFAULT 'OPEN',
    due_date            DATE,
    closed_date         DATE,
    owner_id            UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.capas IS 'Corrective and Preventive Actions linked to NCRs';
CREATE INDEX idx_capas_ncr_id ON portal.capas(ncr_id);
CREATE INDEX idx_capas_status ON portal.capas(status);

-- ─────────────────────────────────────────
-- CERTIFICATIONS
-- ─────────────────────────────────────────
CREATE TABLE portal.certifications (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cert_number         VARCHAR(100) NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    standard            VARCHAR(100),
    issuing_body        VARCHAR(255),
    site_id             UUID REFERENCES portal.sites(id) ON DELETE SET NULL,
    status              portal.cert_status NOT NULL DEFAULT 'ACTIVE',
    issue_date          DATE,
    expiry_date         DATE,
    document_url        VARCHAR(500),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.certifications IS 'Quality certifications and compliance documents';
CREATE INDEX idx_certs_site      ON portal.certifications(site_id);
CREATE INDEX idx_certs_status    ON portal.certifications(status);
CREATE INDEX idx_certs_expiry    ON portal.certifications(expiry_date);

-- ─────────────────────────────────────────
-- TRIGGERS
-- ─────────────────────────────────────────
CREATE TRIGGER trg_ncrs_updated_at
    BEFORE UPDATE ON portal.ncrs
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_capas_updated_at
    BEFORE UPDATE ON portal.capas
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_certifications_updated_at
    BEFORE UPDATE ON portal.certifications
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

-- ─────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────
INSERT INTO portal.ncrs (ncr_number, title, customer_id, program_id, site_id, status, severity)
VALUES (
    'NCR-001',
    'Solder joint defect on PCB assembly',
    (SELECT id FROM portal.customers WHERE code = 'CUST-001'),
    (SELECT id FROM portal.programs WHERE code = 'PROG-001'),
    (SELECT id FROM portal.sites WHERE code = 'SITE-HYD'),
    'OPEN',
    'MAJOR'
);

INSERT INTO portal.certifications (cert_number, name, standard, issuing_body, site_id, status, issue_date, expiry_date)
VALUES (
    'CERT-ISO-001',
    'ISO 9001:2015 Quality Management',
    'ISO 9001',
    'Bureau Veritas',
    (SELECT id FROM portal.sites WHERE code = 'SITE-HYD'),
    'ACTIVE',
    '2024-01-01',
    '2027-01-01'
);