-- ============================================================
-- V6__after_sales_schema.sql
-- Manufacturing Excellence Portal - After Sales Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM TYPES
-- ─────────────────────────────────────────
CREATE TYPE portal.rma_status AS ENUM (
    'REQUESTED', 'APPROVED', 'SHIPPED_TO_US', 'RECEIVED',
    'IN_TRIAGE', 'IN_REPAIR', 'REPAIRED', 'SHIPPED_BACK',
    'CLOSED', 'REJECTED', 'CANCELLED'
);

CREATE TYPE portal.warranty_status AS ENUM (
    'SUBMITTED', 'UNDER_REVIEW', 'APPROVED',
    'REJECTED', 'PAID', 'CLOSED'
);

-- ─────────────────────────────────────────
-- RMA (Return Merchandise Authorization)
-- ─────────────────────────────────────────
CREATE TABLE portal.rmas (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rma_number          VARCHAR(50) NOT NULL UNIQUE,
    customer_id         UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    program_id          UUID REFERENCES portal.programs(id) ON DELETE SET NULL,
    status              portal.rma_status NOT NULL DEFAULT 'REQUESTED',
    reason_code         VARCHAR(100),
    description         TEXT,
    requested_by_id     UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    assigned_to_id      UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    tracking_inbound    VARCHAR(100),
    tracking_outbound   VARCHAR(100),
    received_date       DATE,
    closed_date         DATE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.rmas IS 'Return Merchandise Authorization requests';
CREATE INDEX idx_rmas_customer ON portal.rmas(customer_id);
CREATE INDEX idx_rmas_status   ON portal.rmas(status);

-- ─────────────────────────────────────────
-- REPAIR CASES
-- ─────────────────────────────────────────
CREATE TABLE portal.repair_cases (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_number         VARCHAR(50) NOT NULL UNIQUE,
    rma_id              UUID REFERENCES portal.rmas(id) ON DELETE SET NULL,
    customer_id         UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    diagnosis           TEXT,
    repair_action       TEXT,
    repair_cost         NUMERIC(10,2),
    status              VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    technician_id       UUID REFERENCES portal.users(id) ON DELETE SET NULL,
    started_at          TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.repair_cases IS 'Repair cases linked to RMAs';
CREATE INDEX idx_repair_rma     ON portal.repair_cases(rma_id);
CREATE INDEX idx_repair_status  ON portal.repair_cases(status);

-- ─────────────────────────────────────────
-- WARRANTY CLAIMS
-- ─────────────────────────────────────────
CREATE TABLE portal.warranty_claims (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number        VARCHAR(50) NOT NULL UNIQUE,
    customer_id         UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    rma_id              UUID REFERENCES portal.rmas(id) ON DELETE SET NULL,
    status              portal.warranty_status NOT NULL DEFAULT 'SUBMITTED',
    failure_description TEXT,
    claim_amount        NUMERIC(10,2),
    approved_amount     NUMERIC(10,2),
    submitted_date      DATE DEFAULT CURRENT_DATE,
    resolved_date       DATE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.warranty_claims IS 'Warranty claims submitted by customers';
CREATE INDEX idx_warranty_customer ON portal.warranty_claims(customer_id);
CREATE INDEX idx_warranty_status   ON portal.warranty_claims(status);

-- ─────────────────────────────────────────
-- TRIGGERS
-- ─────────────────────────────────────────
CREATE TRIGGER trg_rmas_updated_at
    BEFORE UPDATE ON portal.rmas
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_repair_cases_updated_at
    BEFORE UPDATE ON portal.repair_cases
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_warranty_claims_updated_at
    BEFORE UPDATE ON portal.warranty_claims
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

-- ─────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────
INSERT INTO portal.rmas (rma_number, customer_id, status, reason_code, description)
VALUES (
    'RMA-2026-001',
    (SELECT id FROM portal.customers WHERE code = 'CUST-001'),
    'REQUESTED',
    'DEFECTIVE',
    'Unit not powering on after delivery'
);