-- ============================================================
-- V2__core_schema.sql
-- Manufacturing Excellence Portal - Core Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM TYPES
-- ─────────────────────────────────────────

CREATE TYPE portal.program_status AS ENUM (
    'DRAFT',
    'ACTIVE',
    'ON_HOLD',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE portal.health_status AS ENUM (
    'GREEN',
    'YELLOW',
    'RED'
);

CREATE TYPE portal.milestone_status AS ENUM (
    'NOT_STARTED',
    'IN_PROGRESS',
    'COMPLETED',
    'DELAYED',
    'CANCELLED'
);

CREATE TYPE portal.work_order_status AS ENUM (
    'PLANNED',
    'IN_PROGRESS',
    'ON_HOLD',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE portal.user_role AS ENUM (
    -- Customer roles
    'CUSTOMER_ENGINEERING',
    'CUSTOMER_SUPPLY_CHAIN',
    'CUSTOMER_QUALITY',
    'CUSTOMER_PROGRAM_MANAGER',
    'CUSTOMER_AFTER_SALES',
    -- Internal roles
    'INTERNAL_ACCOUNT_MANAGER',
    'INTERNAL_PRODUCTION',
    'INTERNAL_QUALITY',
    'INTERNAL_LOGISTICS',
    'INTERNAL_SERVICE',
    'ADMIN'
);

CREATE TYPE portal.notification_type AS ENUM (
    'MILESTONE_DELAY',
    'QUALITY_ALERT',
    'SHIPMENT_EXCEPTION',
    'CERT_EXPIRY',
    'ECO_CHANGE',
    'RMA_UPDATE',
    'GENERAL'
);

-- ─────────────────────────────────────────
-- CUSTOMERS
-- ─────────────────────────────────────────
CREATE TABLE portal.customers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(50)  NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    industry            VARCHAR(100),
    country             VARCHAR(100),
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.customers IS 'Customer accounts - each customer sees only their own data';

-- ─────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────
CREATE TABLE portal.users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id         VARCHAR(255) NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    full_name           VARCHAR(255) NOT NULL,
    role                portal.user_role NOT NULL,
    customer_id         UUID REFERENCES portal.customers(id) ON DELETE SET NULL,
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at       TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.users IS 'Portal users linked to Keycloak. customer_id is NULL for internal users';
CREATE INDEX idx_users_keycloak_id  ON portal.users(keycloak_id);
CREATE INDEX idx_users_customer_id  ON portal.users(customer_id);
CREATE INDEX idx_users_role         ON portal.users(role);

-- ─────────────────────────────────────────
-- SITES (Manufacturing Facilities)
-- ─────────────────────────────────────────
CREATE TABLE portal.sites (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(50)  NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    country             VARCHAR(100) NOT NULL,
    city                VARCHAR(100),
    timezone            VARCHAR(100) NOT NULL DEFAULT 'UTC',
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.sites IS 'Manufacturing sites and facilities (multi-site support)';

-- ─────────────────────────────────────────
-- PROGRAMS
-- ─────────────────────────────────────────
CREATE TABLE portal.programs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(50)  NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    customer_id         UUID         NOT NULL REFERENCES portal.customers(id) ON DELETE RESTRICT,
    site_id             UUID         REFERENCES portal.sites(id) ON DELETE SET NULL,
    owner_id            UUID         REFERENCES portal.users(id) ON DELETE SET NULL,
    status              portal.program_status NOT NULL DEFAULT 'DRAFT',
    health              portal.health_status  NOT NULL DEFAULT 'GREEN',
    planned_start_date  DATE,
    planned_end_date    DATE,
    actual_start_date   DATE,
    actual_end_date     DATE,
    jira_project_key    VARCHAR(50),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.programs IS 'Programs/Projects - top level entity linked to a customer';
CREATE INDEX idx_programs_customer_id    ON portal.programs(customer_id);
CREATE INDEX idx_programs_site_id        ON portal.programs(site_id);
CREATE INDEX idx_programs_status         ON portal.programs(status);
CREATE INDEX idx_programs_jira_key       ON portal.programs(jira_project_key);

-- ─────────────────────────────────────────
-- MILESTONES
-- ─────────────────────────────────────────
CREATE TABLE portal.milestones (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    program_id          UUID         NOT NULL REFERENCES portal.programs(id) ON DELETE CASCADE,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    status              portal.milestone_status NOT NULL DEFAULT 'NOT_STARTED',
    planned_date        DATE         NOT NULL,
    actual_date         DATE,
    delay_days          INTEGER,
    delay_reason        TEXT,
    owner_id            UUID         REFERENCES portal.users(id) ON DELETE SET NULL,
    jira_issue_key      VARCHAR(50),
    is_critical_path    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.milestones IS 'Program milestones with planned vs actual tracking and auto-calculated delay';
CREATE INDEX idx_milestones_program_id   ON portal.milestones(program_id);
CREATE INDEX idx_milestones_status       ON portal.milestones(status);
CREATE INDEX idx_milestones_planned_date ON portal.milestones(planned_date);
CREATE INDEX idx_milestones_jira_key     ON portal.milestones(jira_issue_key);

-- ─────────────────────────────────────────
-- WORK ORDERS
-- ─────────────────────────────────────────
CREATE TABLE portal.work_orders (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number        VARCHAR(100) NOT NULL UNIQUE,
    program_id          UUID         REFERENCES portal.programs(id) ON DELETE SET NULL,
    customer_id         UUID         NOT NULL REFERENCES portal.customers(id) ON DELETE RESTRICT,
    site_id             UUID         NOT NULL REFERENCES portal.sites(id) ON DELETE RESTRICT,
    description         TEXT,
    status              portal.work_order_status NOT NULL DEFAULT 'PLANNED',
    planned_qty         INTEGER      NOT NULL DEFAULT 0,
    completed_qty       INTEGER      NOT NULL DEFAULT 0,
    yield_percentage    NUMERIC(5,2),
    planned_start_date  DATE,
    planned_end_date    DATE,
    actual_start_date   DATE,
    actual_end_date     DATE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.work_orders IS 'Production work orders linked to programs and sites';
CREATE INDEX idx_work_orders_program_id     ON portal.work_orders(program_id);
CREATE INDEX idx_work_orders_customer_id    ON portal.work_orders(customer_id);
CREATE INDEX idx_work_orders_site_id        ON portal.work_orders(site_id);
CREATE INDEX idx_work_orders_status         ON portal.work_orders(status);
CREATE INDEX idx_work_orders_order_number   ON portal.work_orders(order_number);

-- ─────────────────────────────────────────
-- NOTIFICATIONS
-- ─────────────────────────────────────────
CREATE TABLE portal.notifications (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID         NOT NULL REFERENCES portal.users(id) ON DELETE CASCADE,
    type                portal.notification_type NOT NULL,
    title               VARCHAR(255) NOT NULL,
    message             TEXT         NOT NULL,
    reference_id        UUID,
    reference_type      VARCHAR(50),
    is_read             BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.notifications IS 'User notifications for delays, alerts, cert expiry, ECO changes etc.';
CREATE INDEX idx_notifications_user_id      ON portal.notifications(user_id);
CREATE INDEX idx_notifications_is_read      ON portal.notifications(is_read);
CREATE INDEX idx_notifications_type         ON portal.notifications(type);
CREATE INDEX idx_notifications_created_at   ON portal.notifications(created_at DESC);

-- ─────────────────────────────────────────
-- AUDIT LOG
-- ─────────────────────────────────────────
CREATE TABLE portal.audit_log (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID         REFERENCES portal.users(id) ON DELETE SET NULL,
    action              VARCHAR(100) NOT NULL,
    entity_type         VARCHAR(100) NOT NULL,
    entity_id           UUID,
    old_values          JSONB,
    new_values          JSONB,
    ip_address          VARCHAR(50),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE portal.audit_log IS 'Immutable audit trail for all changes across the portal';
CREATE INDEX idx_audit_log_user_id      ON portal.audit_log(user_id);
CREATE INDEX idx_audit_log_entity       ON portal.audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at   ON portal.audit_log(created_at DESC);

-- ─────────────────────────────────────────
-- AUTO UPDATE updated_at TRIGGER
-- ─────────────────────────────────────────
CREATE OR REPLACE FUNCTION portal.update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_customers_updated_at
    BEFORE UPDATE ON portal.customers
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON portal.users
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_sites_updated_at
    BEFORE UPDATE ON portal.sites
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_programs_updated_at
    BEFORE UPDATE ON portal.programs
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_milestones_updated_at
    BEFORE UPDATE ON portal.milestones
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

CREATE TRIGGER trg_work_orders_updated_at
    BEFORE UPDATE ON portal.work_orders
    FOR EACH ROW EXECUTE FUNCTION portal.update_updated_at();

-- ─────────────────────────────────────────
-- SEED DATA (Dev only)
-- ─────────────────────────────────────────

-- Sample site
INSERT INTO portal.sites (code, name, country, city, timezone)
VALUES ('SITE-HYD', 'Hyderabad Plant', 'India', 'Hyderabad', 'Asia/Kolkata');

-- Sample customer
INSERT INTO portal.customers (code, name, industry, country)
VALUES ('CUST-001', 'Acme Aerospace', 'Aerospace', 'USA');

-- Sample program
INSERT INTO portal.programs (code, name, customer_id, site_id, status, health, planned_start_date, planned_end_date)
VALUES (
    'PROG-001',
    'Acme Avionics NPI',
    (SELECT id FROM portal.customers WHERE code = 'CUST-001'),
    (SELECT id FROM portal.sites WHERE code = 'SITE-HYD'),
    'ACTIVE',
    'GREEN',
    '2026-01-01',
    '2026-12-31'
);