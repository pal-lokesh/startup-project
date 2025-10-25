#!/bin/bash

echo "🔧 Final Test - Complete Flow"
echo "============================="
echo

# Step 1: Login vendor
echo "1. Logging in vendor..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "111-111-1111",
    "password": "password123"
  }')

VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 2: Create business
echo "2. Creating business..."
BUSINESS_RESPONSE=$(curl -s -X POST http://localhost:8080/api/businesses \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Test Event Services",
    "businessType": "Event Management",
    "description": "Test business for orders",
    "address": "123 Test St",
    "phoneNumber": "111-111-1111",
    "email": "vendor@test.com"
  }')

echo "Business Response: $BUSINESS_RESPONSE"
echo

# Step 3: Check businesses
echo "3. Checking businesses..."
BUSINESSES=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/businesses")
echo "Businesses: $BUSINESSES"
echo

# Extract business ID
BUSINESS_ID=$(echo $BUSINESSES | grep -o '"businessId":"[^"]*"' | cut -d'"' -f4 | head -1)
echo "Business ID: $BUSINESS_ID"
echo

# Step 4: Create customer
echo "4. Creating customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Customer",
    "email": "customer@test.com",
    "phoneNumber": "222-222-2222",
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
    "phoneNumber": "222-222-2222",
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
    "userId": "222-222-2222",
    "customerName": "Test Customer",
    "customerEmail": "customer@test.com",
    "customerPhone": "222-222-2222",
    "deliveryAddress": "123 Customer St",
    "deliveryDate": "2024-12-25",
    "specialNotes": "Test order for vendor",
    "items": [
      {
        "itemId": "THEME_TEST",
        "itemName": "Test Wedding Theme",
        "itemPrice": 30000,
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
NOTIFICATIONS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/notifications/vendor/111-111-1111")
echo "Notifications: $NOTIFICATIONS"
echo

echo "✅ Test completed!"
echo "=================="
echo "Now login to frontend with:"
echo "Phone: 111-111-1111"
echo "Password: password123"
echo "You should see the order in the Orders tab!"
