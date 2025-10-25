#!/bin/bash

echo "🔧 Simple Test - Using Existing Data"
echo "===================================="
echo

# Step 1: Login as existing vendor (111-111-1111)
echo "1. Logging in as existing vendor..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "111-111-1111",
    "password": "password123"
  }')

VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 2: Check vendor's businesses
echo "2. Checking vendor businesses..."
BUSINESSES=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/businesses")
echo "Businesses: $BUSINESSES"
echo

# Step 3: If no businesses, create one
if [ -z "$BUSINESSES" ] || [ "$BUSINESSES" = "[]" ]; then
  echo "3. Creating business for vendor..."
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
fi

# Step 4: Check businesses again
echo "4. Checking businesses again..."
BUSINESSES=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/businesses")
echo "Businesses: $BUSINESSES"
echo

# Extract business ID
BUSINESS_ID=$(echo $BUSINESSES | grep -o '"businessId":"[^"]*"' | cut -d'"' -f4 | head -1)
echo "Business ID: $BUSINESS_ID"
echo

# Step 5: Create a customer
echo "5. Creating customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Customer",
    "email": "test@customer.com",
    "phoneNumber": "999-999-9999",
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
    "phoneNumber": "999-999-9999",
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
    "userId": "999-999-9999",
    "customerName": "Test Customer",
    "customerEmail": "test@customer.com",
    "customerPhone": "999-999-9999",
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

# Step 8: Check vendor orders
echo "8. Checking vendor orders..."
VENDOR_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/$BUSINESS_ID")
echo "Vendor Orders: $VENDOR_ORDERS"
echo

echo "✅ Test completed!"
echo "=================="
echo "Now login to frontend with:"
echo "Phone: 111-111-1111"
echo "Password: password123"
echo "You should see the order in the Orders tab!"
