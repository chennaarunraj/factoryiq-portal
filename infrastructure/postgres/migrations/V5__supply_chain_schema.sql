-- ============================================================
-- V5__supply_chain_schema.sql
-- Manufacturing Excellence Portal - Supply Chain Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM TYPES
-- ─────────────────────────────────────────
CREATE TYPE portal.po_status AS ENUM (
    'DRAFT', 'SUBMITTED', 'CONFIRMED', 'IN_TRANSIT',
    'PARTIALLY_RECEIVED', 'RECEIVED', 'CANCELLED'
);

CREATE TYPE portal.shipment_status AS ENUM (
    'PENDING', 'PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY',
    'DELIVERED', 'EXCEPTION', 'RETURNED'
);

-- ─────────────────────────────────────────
-- SUPPLIERS
-- ─────────────────────────────────────────
CREATE TABLE portal.suppliers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    country         VARCHAR(100),
    contact_email   VARCHAR(255),
    contact_phone   VARCHAR(50),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.suppliers IS 'Supplier master data';
CREATE INDEX idx_suppliers_code ON portal.suppliers(code);

-- ─────────────────────────────────────────
-- PURCHASE ORDERS
-- ─────────────────────────────────────────
CREATE TABLE portal.purchase_orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    po_number       VARCHAR(100) NOT NULL UNIQUE,
    supplier_id     UUID REFERENCES portal.suppliers(id) ON DELETE SET NULL,
    customer_id     UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    program_id      UUID REFERENCES portal.programs(id) ON DELETE SET NULL,
    status          portal.po_status NOT NULL DEFAULT 'DRAFT',
    total_amount    NUMERIC(15,2),
    currency        VARCHAR(10) DEFAULT 'USD',
    order_date      DATE,
    expected_date   DATE,
    received_date   DATE,
    dynamics_po_id  VARCHAR(100),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.purchase_orders IS 'Purchase orders synced from Microsoft Dynamics 365';
CREATE INDEX idx_po_supplier    ON portal.purchase_orders(supplier_id);
CREATE INDEX idx_po_customer    ON portal.purchase_orders(customer_id);
CREATE INDEX idx_po_status      ON portal.purchase_orders(status);
CREATE INDEX idx_po_dynamics    ON portal.purchase_orders(dynamics_po_id);

-- ─────────────────────────────────────────
-- INVENTORY
-- ─────────────────────────────────────────
CREATE TABLE portal.inventory (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_code       VARCHAR(100) NOT NULL,
    description     VARCHAR(255),
    site_id         UUID REFERENCES portal.sites(id) ON DELETE SET NULL,
    quantity        NUMERIC(15,3) NOT NULL DEFAULT 0,
    allocated_qty   NUMERIC(15,3) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(20),
    location_bin    VARCHAR(50),
    min_threshold   NUMERIC(15,3),
    max_threshold   NUMERIC(15,3),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(item_code, site_id)
);

COMMENT ON TABLE portal.inventory IS 'Inventory levels per site and item';
CREATE INDEX idx_inventory_item    ON portal.inventory(item_code);
CREATE INDEX idx_inventory_site    ON portal.inventory(site_id);

-- ─────────────────────────────────────────
-- SHIPMENTS
-- ─────────────────────────────────────────
CREATE TABLE portal.shipments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tracking_number VARCHAR(100) NOT NULL UNIQUE,
    po_id           UUID REFERENCES portal.purchase_orders(id) ON DELETE SET NULL,
    carrier         VARCHAR(100),
    status          portal.shipment_status NOT NULL DEFAULT 'PENDING',
    origin          VARCHAR(255),
    destination     VARCHAR(255),
    shipped_date    DATE,
    estimated_date  DATE,
    delivered_date  DATE,
    aftership_id    VARCHAR(100),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.shipments IS 'Shipment tracking via AfterShip';
CREATE INDEX idx_shipments_po       ON portal.shipments(po_id);
CREATE INDEX idx_shipments_status   ON portal.shipments(status);
CREATE INDEX idx_shipments_tracking ON portal.shipments(tracking_number);

-- ─────────────────────────────────────────
-- TRIGGERS
-- ─────────────────────────────────────────
CREATE TRIGGER trg_suppliers_updated_at
    BEFORE UPDATE ON portal.suppliers
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_purchase_orders_updated_at
    BEFORE UPDATE ON portal.purchase_orders
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_inventory_updated_at
    BEFORE UPDATE ON portal.inventory
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_shipments_updated_at
    BEFORE UPDATE ON portal.shipments
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

-- ─────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────
INSERT INTO portal.suppliers (code, name, country, contact_email)
VALUES ('SUP-001', 'Acme Electronics', 'USA', 'supply@acme.com');

INSERT INTO portal.purchase_orders (po_number, supplier_id, customer_id, status, order_date, expected_date)
VALUES (
    'PO-2026-001',
    (SELECT id FROM portal.suppliers WHERE code = 'SUP-001'),
    (SELECT id FROM portal.customers WHERE code = 'CUST-001'),
    'CONFIRMED',
    '2026-01-15',
    '2026-02-15'
);

INSERT INTO portal.inventory (item_code, description, site_id, quantity, unit_of_measure)
VALUES (
    'PCB-001',
    'Main PCB Assembly',
    (SELECT id FROM portal.sites WHERE code = 'SITE-HYD'),
    500,
    'PCS'
);