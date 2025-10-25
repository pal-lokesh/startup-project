#!/bin/bash

echo "🔧 Fixing Orders Display Issue"
echo "============================="
echo

# Step 1: Create a fresh vendor
echo "1. Creating fresh vendor..."
VENDOR_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Debug",
    "lastName": "Vendor", 
    "email": "debug@test.com",
    "phoneNumber": "999-999-9999",
    "password": "password123",
    "userType": "VENDOR"
  }')

echo "Vendor created: $VENDOR_RESPONSE"
echo

# Step 2: Login as vendor
echo "2. Logging in vendor..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "999-999-9999",
    "password": "password123"
  }')

echo "Vendor login: $VENDOR_LOGIN"
echo

# Extract token
VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 3: Create business for this vendor
echo "3. Creating business for vendor..."
BUSINESS_RESPONSE=$(curl -s -X POST http://localhost:8080/api/businesses \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Debug Event Services",
    "businessType": "Event Management", 
    "description": "Debug business for testing",
    "address": "123 Debug St",
    "phoneNumber": "999-999-9999",
    "email": "debug@test.com"
  }')

echo "Business created: $BUSINESS_RESPONSE"
echo

# Step 4: Create a theme for this business
echo "4. Creating theme for business..."
THEME_RESPONSE=$(curl -s -X POST http://localhost:8080/api/themes \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "themeName": "Debug Wedding Theme",
    "description": "Beautiful wedding theme for testing",
    "price": 15000,
    "category": "wedding",
    "businessId": "BUSINESS_DEBUG"
  }')

echo "Theme created: $THEME_RESPONSE"
echo

# Step 5: Create a customer
echo "5. Creating customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Debug",
    "lastName": "Customer",
    "email": "customer@debug.com", 
    "phoneNumber": "888-888-8888",
    "password": "password123",
    "userType": "CUSTOMER"
  }')

echo "Customer created: $CUSTOMER_RESPONSE"
echo

# Step 6: Login as customer
echo "6. Logging in customer..."
CUSTOMER_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "888-888-8888",
    "password": "password123"
  }')

echo "Customer login: $CUSTOMER_LOGIN"
echo

# Extract customer token
CUSTOMER_TOKEN=$(echo $CUSTOMER_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Customer Token: $CUSTOMER_TOKEN"
echo

# Step 7: Create order for the vendor's business
echo "7. Creating order for vendor business..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Debug Customer",
    "customerEmail": "customer@debug.com",
    "customerPhone": "888-888-8888", 
    "deliveryAddress": "123 Customer St, Debug City",
    "deliveryDate": "2024-12-15",
    "specialNotes": "Debug order for testing",
    "orderItems": [
      {
        "itemId": "THEME_DEBUG",
        "itemName": "Debug Wedding Theme",
        "itemPrice": 15000,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "BUSINESS_DEBUG"
      }
    ]
  }')

echo "Order created: $ORDER_RESPONSE"
echo

# Step 8: Check vendor's orders
echo "8. Checking vendor orders..."
VENDOR_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders")
echo "Vendor Orders: $VENDOR_ORDERS"
echo

# Step 9: Check notifications
echo "9. Checking notifications..."
NOTIFICATIONS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/notifications/vendor/999-999-9999")
echo "Notifications: $NOTIFICATIONS"
echo

echo "✅ Test completed!"
echo "=================="
echo "Now login to frontend with:"
echo "Phone: 999-999-9999"
echo "Password: password123"
echo "You should see the order in the Orders tab!"
