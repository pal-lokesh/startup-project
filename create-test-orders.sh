#!/bin/bash

echo "🔧 Creating Test Orders for Existing System"
echo "==========================================="
echo

# Step 1: Login as existing vendor (0000000000)
echo "1. Logging in as vendor 0000000000..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "0000000000",
    "password": "M0nchi@123"
  }')

VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 2: Login as existing client (2222222222)
echo "2. Logging in as client 2222222222..."
CLIENT_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "2222222222",
    "password": "M0nchi@123"
  }')

CLIENT_TOKEN=$(echo $CLIENT_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Client Token: $CLIENT_TOKEN"
echo

# Step 3: Create order for BUSINESS_001
echo "3. Creating order for BUSINESS_001..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CLIENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "2222222222",
    "customerName": "Test Customer",
    "customerEmail": "test@customer.com",
    "customerPhone": "2222222222",
    "deliveryAddress": "123 Test Street, Test City",
    "deliveryDate": "2024-12-25",
    "specialNotes": "Test order for vendor dashboard",
    "items": [
      {
        "itemId": "THEME_001",
        "itemName": "Royal Wedding Theme",
        "itemPrice": 50000,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "BUSINESS_001",
        "businessName": "Elegant Events & Tents"
      }
    ]
  }')

echo "Order Response: $ORDER_RESPONSE"
echo

# Step 4: Create another order for BUSINESS_002
echo "4. Creating order for BUSINESS_002..."
ORDER_RESPONSE2=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CLIENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "2222222222",
    "customerName": "Test Customer 2",
    "customerEmail": "test2@customer.com",
    "customerPhone": "2222222222",
    "deliveryAddress": "456 Test Avenue, Test City",
    "deliveryDate": "2024-12-30",
    "specialNotes": "Second test order",
    "items": [
      {
        "itemId": "PLATE_001",
        "itemName": "Chicken Biryani",
        "itemPrice": 25000,
        "quantity": 2,
        "itemType": "plate",
        "businessId": "BUSINESS_002",
        "businessName": "Delicious Catering Services"
      }
    ]
  }')

echo "Order Response 2: $ORDER_RESPONSE2"
echo

# Step 5: Check vendor orders for BUSINESS_001
echo "5. Checking orders for BUSINESS_001..."
VENDOR_ORDERS_1=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/BUSINESS_001")
echo "Orders for BUSINESS_001: $VENDOR_ORDERS_1"
echo

# Step 6: Check vendor orders for BUSINESS_002
echo "6. Checking orders for BUSINESS_002..."
VENDOR_ORDERS_2=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/BUSINESS_002")
echo "Orders for BUSINESS_002: $VENDOR_ORDERS_2"
echo

# Step 7: Check notifications
echo "7. Checking notifications..."
NOTIFICATIONS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/notifications/vendor/0000000000")
echo "Notifications: $NOTIFICATIONS"
echo

echo "✅ Test completed!"
echo "=================="
echo "Now login to frontend with:"
echo "Vendor Phone: 0000000000"
echo "Password: M0nchi@123"
echo "You should see orders in the Orders tab!"
