-- ============================================================
-- V3__production_schema.sql
-- Manufacturing Excellence Portal - Production Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM TYPES
-- ─────────────────────────────────────────
CREATE TYPE portal.work_order_operation_status AS ENUM (
    'PENDING',
    'IN_PROGRESS',
    'COMPLETED',
    'ON_HOLD'
);

-- ─────────────────────────────────────────
-- PRODUCTION LINES
-- ─────────────────────────────────────────
CREATE TABLE portal.production_lines (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id             UUID NOT NULL REFERENCES portal.sites(id) ON DELETE RESTRICT,
    code                VARCHAR(50) NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    capacity_per_shift  INTEGER,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.production_lines IS 'Manufacturing production lines per site';
CREATE INDEX idx_prod_lines_site ON portal.production_lines(site_id);
CREATE INDEX idx_prod_lines_code ON portal.production_lines(code);

-- ─────────────────────────────────────────
-- WORK ORDER OPERATIONS
-- ─────────────────────────────────────────
CREATE TABLE portal.work_order_operations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_order_id   UUID NOT NULL REFERENCES portal.work_orders(id) ON DELETE CASCADE,
    line_id         UUID REFERENCES portal.production_lines(id) ON DELETE SET NULL,
    operation_name  VARCHAR(255) NOT NULL,
    sequence_no     INTEGER NOT NULL,
    status          portal.work_order_operation_status NOT NULL DEFAULT 'PENDING',
    planned_qty     INTEGER NOT NULL DEFAULT 0,
    completed_qty   INTEGER NOT NULL DEFAULT 0,
    rejected_qty    INTEGER NOT NULL DEFAULT 0,
    yield_pct       NUMERIC(5,2),
    started_at      TIMESTAMPTZ,
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.work_order_operations IS 'Individual operations within a work order';
CREATE INDEX idx_wo_ops_work_order ON portal.work_order_operations(work_order_id);
CREATE INDEX idx_wo_ops_line       ON portal.work_order_operations(line_id);
CREATE INDEX idx_wo_ops_status     ON portal.work_order_operations(status);
CREATE INDEX idx_wo_ops_sequence   ON portal.work_order_operations(work_order_id, sequence_no);

-- ─────────────────────────────────────────
-- TRIGGERS
-- ─────────────────────────────────────────
CREATE TRIGGER trg_production_lines_updated_at
    BEFORE UPDATE ON portal.production_lines
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_work_order_operations_updated_at
    BEFORE UPDATE ON portal.work_order_operations
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

-- ─────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────
INSERT INTO portal.production_lines (site_id, code, name, capacity_per_shift)
VALUES (
    (SELECT id FROM portal.sites WHERE code = 'SITE-HYD'),
    'LINE-01', 'Assembly Line 1', 100
);

INSERT INTO portal.production_lines (site_id, code, name, capacity_per_shift)
VALUES (
    (SELECT id FROM portal.sites WHERE code = 'SITE-HYD'),
    'LINE-02', 'Assembly Line 2', 150
);