-- Add requested_date column to stock_notifications table for date-specific subscriptions
ALTER TABLE stock_notifications 
ADD COLUMN IF NOT EXISTS requested_date VARCHAR(255);

-- Add index for faster lookups on requested_date
CREATE INDEX IF NOT EXISTS idx_stock_notifications_requested_date 
ON stock_notifications(requested_date);

-- Add composite index for user, item, and date lookups
CREATE INDEX IF NOT EXISTS idx_stock_notifications_user_item_date 
ON stock_notifications(user_id, item_id, item_type, requested_date);

