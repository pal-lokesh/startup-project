-- Create plates table for JPA persistence
CREATE TABLE IF NOT EXISTS plates (
    plate_id VARCHAR(100) PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    dish_name VARCHAR(255) NOT NULL,
    dish_description TEXT,
    plate_image TEXT,
    price DOUBLE PRECISION NOT NULL,
    dish_type VARCHAR(50),
    quantity INTEGER DEFAULT 0,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index on business_id for faster queries
CREATE INDEX IF NOT EXISTS idx_plates_business_id ON plates(business_id);

-- Create index on dish_type for filtering
CREATE INDEX IF NOT EXISTS idx_plates_dish_type ON plates(dish_type);

