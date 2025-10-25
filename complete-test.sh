#!/bin/bash

echo "🔧 Creating Complete Test Scenario"
echo "=================================="
echo

# Step 1: Create vendor
echo "1. Creating vendor..."
VENDOR_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Demo",
    "lastName": "Vendor",
    "email": "demo@vendor.com",
    "phoneNumber": "777-777-7777",
    "password": "password123",
    "userType": "VENDOR"
  }')

echo "Vendor: $VENDOR_RESPONSE"
echo

# Step 2: Login vendor
echo "2. Logging in vendor..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "777-777-7777",
    "password": "password123"
  }')

VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 3: Create business for vendor
echo "3. Creating business..."
BUSINESS_RESPONSE=$(curl -s -X POST http://localhost:8080/api/businesses \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Demo Event Services",
    "businessType": "Event Management",
    "description": "Demo business for testing",
    "address": "123 Demo St",
    "phoneNumber": "777-777-7777",
    "email": "demo@vendor.com"
  }')

echo "Business: $BUSINESS_RESPONSE"
echo

# Extract business ID from response
BUSINESS_ID=$(echo $BUSINESS_RESPONSE | grep -o '"businessId":"[^"]*"' | cut -d'"' -f4)
echo "Business ID: $BUSINESS_ID"
echo

# Step 4: Create theme for this business
echo "4. Creating theme..."
THEME_RESPONSE=$(curl -s -X POST http://localhost:8080/api/themes \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "themeName": "Demo Wedding Theme",
    "description": "Beautiful wedding theme for demo",
    "price": 25000,
    "category": "wedding"
  }')

echo "Theme: $THEME_RESPONSE"
echo

# Step 5: Create customer
echo "5. Creating customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Demo",
    "lastName": "Customer",
    "email": "demo@customer.com",
    "phoneNumber": "888-888-8888",
    "password": "password123",
    "userType": "CUSTOMER"
  }')

echo "Customer: $CUSTOMER_RESPONSE"
echo

# Step 6: Login customer
echo "6. Logging in customer..."
CUSTOMER_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "888-888-8888",
    "password": "password123"
  }')

CUSTOMER_TOKEN=$(echo $CUSTOMER_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Customer Token: $CUSTOMER_TOKEN"
echo

# Step 7: Create order for the vendor's business
echo "7. Creating order for vendor business..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "888-888-8888",
    "customerName": "Demo Customer",
    "customerEmail": "demo@customer.com",
    "customerPhone": "888-888-8888",
    "deliveryAddress": "123 Customer St, Demo City",
    "deliveryDate": "2024-12-20",
    "specialNotes": "Demo order for testing",
    "items": [
      {
        "itemId": "THEME_DEMO",
        "itemName": "Demo Wedding Theme",
        "itemPrice": 25000,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "'$BUSINESS_ID'"
      }
    ]
  }')

echo "Order: $ORDER_RESPONSE"
echo

# Step 8: Check vendor's orders by business ID
echo "8. Checking vendor orders by business ID..."
VENDOR_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/$BUSINESS_ID")
echo "Vendor Orders: $VENDOR_ORDERS"
echo

# Step 9: Check notifications
echo "9. Checking notifications..."
NOTIFICATIONS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/notifications/vendor/777-777-7777")
echo "Notifications: $NOTIFICATIONS"
echo

echo "✅ Test completed!"
echo "=================="
echo "Now login to frontend with:"
echo "Phone: 777-777-7777"
echo "Password: password123"
echo "You should see the order in the Orders tab!"
