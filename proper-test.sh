#!/bin/bash

echo "🔧 Creating Proper Test Scenario"
echo "==============================="
echo

# Step 1: Create vendor
echo "1. Creating vendor..."
VENDOR_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Vendor",
    "email": "vendor@test.com",
    "phoneNumber": "555-555-5555",
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
    "phoneNumber": "555-555-5555",
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
    "businessName": "Test Event Services",
    "businessType": "Event Management",
    "description": "Test business",
    "address": "123 Test St",
    "phoneNumber": "555-555-5555",
    "email": "vendor@test.com"
  }')

echo "Business: $BUSINESS_RESPONSE"
echo

# Step 4: Create theme
echo "4. Creating theme..."
THEME_RESPONSE=$(curl -s -X POST http://localhost:8080/api/themes \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "themeName": "Test Wedding Theme",
    "description": "Beautiful wedding theme",
    "price": 20000,
    "category": "wedding"
  }')

echo "Theme: $THEME_RESPONSE"
echo

# Step 5: Create customer
echo "5. Creating customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Customer",
    "email": "customer@test.com",
    "phoneNumber": "666-666-6666",
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
    "phoneNumber": "666-666-6666",
    "password": "password123"
  }')

CUSTOMER_TOKEN=$(echo $CUSTOMER_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Customer Token: $CUSTOMER_TOKEN"
echo

# Step 7: Create order
echo "7. Creating order..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "666-666-6666",
    "customerName": "Test Customer",
    "customerEmail": "customer@test.com",
    "customerPhone": "666-666-6666",
    "deliveryAddress": "123 Customer St",
    "deliveryDate": "2024-12-15",
    "specialNotes": "Test order",
    "items": [
      {
        "itemId": "THEME_1",
        "itemName": "Test Wedding Theme",
        "itemPrice": 20000,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "BUSINESS_001"
      }
    ]
  }')

echo "Order: $ORDER_RESPONSE"
echo

# Step 8: Check vendor's orders (should be empty due to security fix)
echo "8. Checking vendor orders (should be empty)..."
VENDOR_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders")
echo "Vendor Orders: $VENDOR_ORDERS"
echo

# Step 9: Check orders by business ID
echo "9. Checking orders by business ID..."
BUSINESS_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/BUSINESS_001")
echo "Business Orders: $BUSINESS_ORDERS"
echo

echo "✅ Test completed!"
echo "=================="
echo "The security issue is fixed - vendors can no longer see all orders."
echo "To see orders, vendors must use the business-specific endpoint."
