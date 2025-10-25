#!/bin/bash

echo "Creating Test Data for Order System"
echo "=================================="
echo

# Step 1: Create a vendor user
echo "1. Creating vendor user..."
VENDOR_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Vendor", 
    "email": "vendor@test.com",
    "phoneNumber": "111-111-1111",
    "password": "password123",
    "userType": "VENDOR"
  }')

echo "Vendor Response: $VENDOR_RESPONSE"
echo

# Step 2: Create a client user  
echo "2. Creating client user..."
CLIENT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Client",
    "email": "client@test.com", 
    "phoneNumber": "222-222-2222",
    "password": "password123",
    "userType": "CLIENT"
  }')

echo "Client Response: $CLIENT_RESPONSE"
echo

# Step 3: Login as vendor to get token
echo "3. Logging in as vendor..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "111-111-1111",
    "password": "password123"
  }')

echo "Vendor Login: $VENDOR_LOGIN"
echo

# Extract token (this is a simplified approach)
VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 4: Create business (requires authentication)
echo "4. Creating business..."
BUSINESS_RESPONSE=$(curl -s -X POST http://localhost:8080/api/businesses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -d '{
    "businessName": "Test Event Services",
    "businessDescription": "Professional event planning services",
    "businessCategory": "Event Planning", 
    "businessAddress": "123 Test St, Test City",
    "businessPhone": "111-111-1111",
    "businessEmail": "vendor@testevents.com"
  }')

echo "Business Response: $BUSINESS_RESPONSE"
echo

# Step 5: Create theme for business
echo "5. Creating theme..."
THEME_RESPONSE=$(curl -s -X POST http://localhost:8080/api/themes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -d '{
    "businessId": "BUSINESS_1",
    "themeName": "Test Wedding Theme",
    "themeDescription": "Beautiful wedding theme for testing",
    "themeCategory": "Wedding",
    "priceRange": "5000-10000"
  }')

echo "Theme Response: $THEME_RESPONSE"
echo

# Step 6: Login as client to get token
echo "6. Logging in as client..."
CLIENT_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "222-222-2222", 
    "password": "password123"
  }')

echo "Client Login: $CLIENT_LOGIN"
echo

CLIENT_TOKEN=$(echo $CLIENT_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Client Token: $CLIENT_TOKEN"
echo

# Step 7: Create order (requires authentication)
echo "7. Creating order..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CLIENT_TOKEN" \
  -d '{
    "userId": "222-222-2222",
    "customerName": "Test Customer",
    "customerEmail": "customer@test.com",
    "customerPhone": "333-333-3333",
    "deliveryAddress": "456 Customer St, Customer City",
    "deliveryDate": "2024-12-01",
    "specialNotes": "Test order for notification testing",
    "items": [
      {
        "itemId": "THEME_1",
        "itemName": "Test Wedding Theme", 
        "itemPrice": 7500,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "BUSINESS_1",
        "businessName": "Test Event Services",
        "imageUrl": ""
      }
    ]
  }')

echo "Order Response: $ORDER_RESPONSE"
echo

# Step 8: Check orders
echo "8. Checking orders..."
ORDERS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/test/orders)
echo "Orders: $ORDERS_RESPONSE"
echo

# Step 9: Check notifications
echo "9. Checking notifications..."
NOTIFICATIONS_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/test/notifications/vendor/111-111-1111")
echo "Notifications: $NOTIFICATIONS_RESPONSE"
echo

echo "Test data creation completed!"
echo "Now you can:"
echo "1. Open http://localhost:3000"
echo "2. Login as vendor (111-111-1111 / password123)"
echo "3. Check the Orders tab to see the order and notifications"
