#!/bin/bash

echo "🔧 Testing Client Order Tracking System"
echo "======================================"

# Backend URL
BASE_URL="http://localhost:8080/api"

# Client credentials (from DataInitializationService)
CLIENT_PHONE="2222222222"
CLIENT_PASSWORD="M0nchi@123"

# Vendor credentials (from DataInitializationService)
VENDOR_PHONE="0000000000"
VENDOR_PASSWORD="M0nchi@123"

# 1. Log in as client
echo ""
echo "1. Logging in as client ${CLIENT_PHONE}..."
CLIENT_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "'"${CLIENT_PHONE}"'",
    "password": "'"${CLIENT_PASSWORD}"'"
  }')
CLIENT_TOKEN=$(echo "${CLIENT_LOGIN_RESPONSE}" | jq -r '.token')
echo "Client Token: ${CLIENT_TOKEN}"

# 2. Log in as vendor
echo ""
echo "2. Logging in as vendor ${VENDOR_PHONE}..."
VENDOR_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "'"${VENDOR_PHONE}"'",
    "password": "'"${VENDOR_PASSWORD}"'"
  }')
VENDOR_TOKEN=$(echo "${VENDOR_LOGIN_RESPONSE}" | jq -r '.token')
echo "Vendor Token: ${VENDOR_TOKEN}"

# Check if tokens are valid
if [ "${CLIENT_TOKEN}" == "null" ] || [ -z "${CLIENT_TOKEN}" ]; then
  echo "❌ Failed to get client token. Exiting."
  exit 1
fi

if [ "${VENDOR_TOKEN}" == "null" ] || [ -z "${VENDOR_TOKEN}" ]; then
  echo "❌ Failed to get vendor token. Exiting."
  exit 1
fi

# Headers for authenticated requests
AUTH_HEADER_CLIENT="Authorization: Bearer ${CLIENT_TOKEN}"
AUTH_HEADER_VENDOR="Authorization: Bearer ${VENDOR_TOKEN}"

# Business ID (from DataInitializationService)
BUSINESS_ID="BUSINESS_001"

# 3. Create an order as client
echo ""
echo "3. Creating order as client..."
ORDER_RESPONSE=$(curl -s -X POST "${BASE_URL}/orders" \
  -H "Content-Type: application/json" \
  -H "${AUTH_HEADER_CLIENT}" \
  -d '{
    "userId": "'"${CLIENT_PHONE}"'",
    "customerName": "Test Client",
    "customerEmail": "client@test.com",
    "customerPhone": "'"${CLIENT_PHONE}"'",
    "deliveryAddress": "123 Client Street, Client City",
    "deliveryDate": "2024-12-25",
    "specialNotes": "Test order for client tracking",
    "items": [
      {
        "itemId": "THEME_001",
        "itemName": "Royal Wedding Theme",
        "itemPrice": 50000.0,
        "quantity": 1,
        "itemType": "theme",
        "businessId": "'"${BUSINESS_ID}"'",
        "businessName": "Elegant Events & Tents"
      }
    ]
  }')
echo "Order Response: ${ORDER_RESPONSE}"

# Extract order ID
ORDER_ID=$(echo "${ORDER_RESPONSE}" | jq -r '.orderId')
echo "Created Order ID: ${ORDER_ID}"

# 4. Check client orders
echo ""
echo "4. Checking client orders..."
CLIENT_ORDERS=$(curl -s -X GET "${BASE_URL}/orders/user/${CLIENT_PHONE}" \
  -H "${AUTH_HEADER_CLIENT}")
echo "Client Orders: ${CLIENT_ORDERS}"

# 5. Check client notifications
echo ""
echo "5. Checking client notifications..."
CLIENT_NOTIFICATIONS=$(curl -s -X GET "${BASE_URL}/client-notifications/client/${CLIENT_PHONE}" \
  -H "${AUTH_HEADER_CLIENT}")
echo "Client Notifications: ${CLIENT_NOTIFICATIONS}"

# 6. Update order status as vendor
echo ""
echo "6. Updating order status to CONFIRMED..."
STATUS_UPDATE_RESPONSE=$(curl -s -X PUT "${BASE_URL}/orders/${ORDER_ID}/status?status=CONFIRMED" \
  -H "${AUTH_HEADER_VENDOR}")
echo "Status Update Response: ${STATUS_UPDATE_RESPONSE}"

# 7. Check client notifications after status update
echo ""
echo "7. Checking client notifications after status update..."
CLIENT_NOTIFICATIONS_AFTER=$(curl -s -X GET "${BASE_URL}/client-notifications/client/${CLIENT_PHONE}" \
  -H "${AUTH_HEADER_CLIENT}")
echo "Client Notifications After Update: ${CLIENT_NOTIFICATIONS_AFTER}"

# 8. Update order status to PREPARING
echo ""
echo "8. Updating order status to PREPARING..."
STATUS_UPDATE_RESPONSE2=$(curl -s -X PUT "${BASE_URL}/orders/${ORDER_ID}/status?status=PREPARING" \
  -H "${AUTH_HEADER_VENDOR}")
echo "Status Update Response 2: ${STATUS_UPDATE_RESPONSE2}"

# 9. Check final client notifications
echo ""
echo "9. Checking final client notifications..."
FINAL_NOTIFICATIONS=$(curl -s -X GET "${BASE_URL}/client-notifications/client/${CLIENT_PHONE}" \
  -H "${AUTH_HEADER_CLIENT}")
echo "Final Client Notifications: ${FINAL_NOTIFICATIONS}"

# 10. Check unread notification count
echo ""
echo "10. Checking unread notification count..."
UNREAD_COUNT=$(curl -s -X GET "${BASE_URL}/client-notifications/client/${CLIENT_PHONE}/unread-count" \
  -H "${AUTH_HEADER_CLIENT}")
echo "Unread Notification Count: ${UNREAD_COUNT}"

echo ""
echo "✅ Client Order Tracking Test Completed!"
echo "========================================"
echo "Now you can:"
echo "1. Login to frontend as client: ${CLIENT_PHONE} / ${CLIENT_PASSWORD}"
echo "2. Go to 'My Orders' to see order tracking"
echo "3. Check notifications for order updates"
echo "4. Login as vendor: ${VENDOR_PHONE} / ${VENDOR_PASSWORD}"
echo "5. Update order status to see client notifications"
