#!/bin/bash

echo "🔧 Creating Order Test for Vendor"
echo "================================="
echo

# Step 1: Create fresh vendor
echo "1. Creating fresh vendor..."
VENDOR_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Order",
    "lastName": "Vendor",
    "email": "order@vendor.com",
    "phoneNumber": "123-456-7890",
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
    "phoneNumber": "123-456-7890",
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
    "businessName": "Order Test Services",
    "businessType": "Event Management",
    "description": "Business for order testing",
    "address": "123 Order St",
    "phoneNumber": "123-456-7890",
    "email": "order@vendor.com"
  }')

echo "Business: $BUSINESS_RESPONSE"
echo

# Extract business ID
BUSINESS_ID=$(echo $BUSINESS_RESPONSE | grep -o '"businessId":"[^"]*"' | cut -d'"' -f4)
echo "Business ID: $BUSINESS_ID"
echo

# Step 4: Create customer
echo "4. Creating customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Order",
    "lastName": "Customer",
    "email": "order@customer.com",
    "phoneNumber": "987-654-3210",
    "password": "password123",
    "userType": "CUSTOMER"
  }')

echo "Customer: $CUSTOMER_RESPONSE"
echo

# Step 5: Login customer
echo "5. Logging in customer..."
CUSTOMER_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "987-654-3210",
    "password": "password123"
  }')

CUSTOMER_TOKEN=$(echo $CUSTOMER_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Customer Token: $CUSTOMER_TOKEN"
echo

# Step 6: Create order
echo "6. Creating order..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "987-654-3210",
    "customerName": "Order Customer",
    "customerEmail": "order@customer.com",
    "customerPhone": "987-654-3210",
    "deliveryAddress": "123 Customer St",
    "deliveryDate": "2024-12-30",
    "specialNotes": "Test order for vendor",
    "items": [
      {
        "itemId": "THEME_ORDER",
        "itemName": "Order Test Theme",
        "itemPrice": 50000,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "'$BUSINESS_ID'"
      }
    ]
  }')

echo "Order: $ORDER_RESPONSE"
echo

# Step 7: Check vendor orders
echo "7. Checking vendor orders..."
VENDOR_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/$BUSINESS_ID")
echo "Vendor Orders: $VENDOR_ORDERS"
echo

# Step 8: Check notifications
echo "8. Checking notifications..."
NOTIFICATIONS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/notifications/vendor/123-456-7890")
echo "Notifications: $NOTIFICATIONS"
echo

echo "✅ Test completed!"
echo "=================="
echo "Now login to frontend with:"
echo "Phone: 123-456-7890"
echo "Password: password123"
echo "You should see the order in the Orders tab!"
