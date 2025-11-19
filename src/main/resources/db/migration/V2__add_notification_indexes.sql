-- Add indexes for better notification query performance
CREATE INDEX IF NOT EXISTS idx_client_notifications_client_phone ON client_notifications(client_phone);
CREATE INDEX IF NOT EXISTS idx_client_notifications_is_read ON client_notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_client_notifications_client_phone_is_read ON client_notifications(client_phone, is_read);
CREATE INDEX IF NOT EXISTS idx_client_notifications_created_at ON client_notifications(created_at DESC);

