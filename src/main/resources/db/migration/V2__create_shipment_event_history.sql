-- ══════════════════════════════════════════════════════════════
-- PaqueTrack — Shipment Service
-- V2: Historial de cambios de estado
-- ══════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS shipment_event_history (
    id              VARCHAR(36)     PRIMARY KEY,
    shipment_id     VARCHAR(36)     NOT NULL,
    previous_status VARCHAR(30),
    new_status      VARCHAR(30)     NOT NULL,
    occurred_at     VARCHAR(50)     NOT NULL,
    recorded_at     TIMESTAMP       NOT NULL,

    CONSTRAINT fk_event_history_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_event_history_shipment_id
    ON shipment_event_history(shipment_id);

CREATE INDEX IF NOT EXISTS idx_event_history_recorded_at
    ON shipment_event_history(recorded_at DESC);

COMMENT ON TABLE shipment_event_history
    IS 'Historial de cambios de estado recibidos desde tracking';