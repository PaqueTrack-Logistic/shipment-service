-- ══════════════════════════════════════════════
-- PaqueTrack — Shipment Service
-- V1: Creación de tabla shipments
-- ══════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS shipments (
    id                VARCHAR(36)     PRIMARY KEY,
    tracking_id       VARCHAR(30)     UNIQUE NOT NULL,  -- ← de 20 a 30, PQ-20240401-XXXXXX
    status            VARCHAR(30)     NOT NULL DEFAULT 'CREATED',

    sender_name       VARCHAR(200)    NOT NULL,
    sender_address    VARCHAR(300),
    sender_city       VARCHAR(100)    NOT NULL,

    recipient_name    VARCHAR(200)    NOT NULL,
    recipient_address VARCHAR(300),
    recipient_city    VARCHAR(100)    NOT NULL,

    weight_kg         DECIMAL(8,3),
    created_at        TIMESTAMP       NOT NULL,
    updated_at        TIMESTAMP       NOT NULL
);

-- Índices para rendimiento
CREATE INDEX IF NOT EXISTS idx_shipments_tracking_id
    ON shipments(tracking_id);

CREATE INDEX IF NOT EXISTS idx_shipments_status
    ON shipments(status);

CREATE INDEX IF NOT EXISTS idx_shipments_created_at
    ON shipments(created_at DESC);

-- Comentarios
COMMENT ON TABLE shipments IS 'Tabla de envíos - PaqueTrack';
COMMENT ON COLUMN shipments.tracking_id IS 'Número de guía formato PQ-YYYYMMDD-XXXXXX';
COMMENT ON COLUMN shipments.status IS 'Estado: CREATED, IN_TRANSIT, DELIVERED, CANCELLED';

/*Columnas para auditoría*/
ALTER TABLE shipment
ADD COLUMN created_by VARCHAR(255),
ADD COLUMN created_by_role VARCHAR(100);
