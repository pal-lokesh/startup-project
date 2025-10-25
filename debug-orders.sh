#!/bin/bash

echo "🔍 Debugging Orders Display Issue"
echo "================================="
echo

# Step 1: Login as vendor to get fresh token
echo "1. Logging in as vendor..."
VENDOR_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "111-111-1111",
    "password": "password123"
  }')

echo "Vendor Login Response: $VENDOR_LOGIN"
echo

# Extract token
VENDOR_TOKEN=$(echo $VENDOR_LOGIN | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Vendor Token: $VENDOR_TOKEN"
echo

# Step 2: Check vendor's businesses
echo "2. Checking vendor's businesses..."
BUSINESSES=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/businesses")
echo "Vendor Businesses: $BUSINESSES"
echo

# Step 3: Check all orders
echo "3. Checking all orders..."
ALL_ORDERS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders")
echo "All Orders: $ALL_ORDERS"
echo

# Step 4: Check orders by specific business IDs
echo "4. Checking orders by business ID BUSINESS_1..."
ORDERS_BUSINESS_1=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/BUSINESS_1")
echo "Orders for BUSINESS_1: $ORDERS_BUSINESS_1"
echo

echo "5. Checking orders by business ID BUSINESS_001..."
ORDERS_BUSINESS_001=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/orders/business/BUSINESS_001")
echo "Orders for BUSINESS_001: $ORDERS_BUSINESS_001"
echo

# Step 6: Check notifications
echo "6. Checking notifications..."
NOTIFICATIONS=$(curl -s -H "Authorization: Bearer $VENDOR_TOKEN" "http://localhost:8080/api/notifications/vendor/111-111-1111")
echo "Notifications: $NOTIFICATIONS"
echo

echo "🔍 Debug completed!"
echo "=================="
echo "If you see orders in 'All Orders' but not in specific business orders,"
echo "then the business ID mapping is the issue."
