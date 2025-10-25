#!/bin/bash

echo "Testing Notification System - Order Alerts for Vendors"
echo

echo "============================================"
echo "1. Creating a VENDOR user..."
echo "============================================"
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Vendor","lastName":"Owner","email":"vendor@example.com","phoneNumber":"111-111-1111","userType":"VENDOR"}'

echo
echo
echo "============================================"
echo "2. Creating a CLIENT user..."
echo "============================================"
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Client","lastName":"Customer","email":"client@example.com","phoneNumber":"222-222-2222","userType":"CLIENT"}'

echo
echo
echo "============================================"
echo "3. Creating vendor profile..."
echo "============================================"
curl -X POST http://localhost:8080/api/vendors \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"111-111-1111","businessName":"Test Event Services","businessDescription":"Professional event planning services","businessAddress":"123 Test St, Test City","businessPhone":"111-111-1111","businessEmail":"vendor@testevents.com"}'

echo
echo
echo "============================================"
echo "4. Creating business for vendor..."
echo "============================================"
curl -X POST http://localhost:8080/api/businesses \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"111-111-1111","businessName":"Test Event Services","businessDescription":"Professional event planning services","businessCategory":"Event Planning","businessAddress":"123 Test St, Test City","businessPhone":"111-111-1111","businessEmail":"vendor@testevents.com"}'

echo
echo
echo "============================================"
echo "5. Creating theme for business..."
echo "============================================"
curl -X POST http://localhost:8080/api/themes \
  -H "Content-Type: application/json" \
  -d '{"businessId":"BUSINESS_1","themeName":"Test Wedding Theme","themeDescription":"Beautiful wedding theme for testing","themeCategory":"Wedding","priceRange":"5000-10000"}'

echo
echo
echo "============================================"
echo "6. Creating inventory item for business..."
echo "============================================"
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"businessId":"BUSINESS_1","inventoryName":"Test Chair","inventoryDescription":"Elegant chair for events","inventoryCategory":"Furniture","price":500,"quantity":10}'

echo
echo
echo "============================================"
echo "7. Creating a test order (this should trigger notifications)..."
echo "============================================"
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"222-222-2222","customerName":"Test Customer","customerEmail":"customer@test.com","customerPhone":"333-333-3333","deliveryAddress":"456 Customer St, Customer City","deliveryDate":"2024-12-01","specialNotes":"Test order for notification testing","items":[{"itemId":"THEME_1","itemName":"Test Wedding Theme","itemPrice":7500,"quantity":1,"itemType":"theme","businessId":"BUSINESS_1","businessName":"Test Event Services","imageUrl":""},{"itemId":"INVENTORY_1","itemName":"Test Chair","itemPrice":500,"quantity":2,"itemType":"inventory","businessId":"BUSINESS_1","businessName":"Test Event Services","imageUrl":""}]}'

echo
echo
echo "============================================"
echo "8. Checking notifications for vendor..."
echo "============================================"
curl -X GET http://localhost:8080/api/notifications/vendor/111-111-1111

echo
echo
echo "============================================"
echo "9. Checking unread notification count..."
echo "============================================"
curl -X GET http://localhost:8080/api/notifications/vendor/111-111-1111/count

echo
echo
echo "============================================"
echo "10. Checking unread notifications..."
echo "============================================"
curl -X GET http://localhost:8080/api/notifications/vendor/111-111-1111/unread

echo
echo
echo "============================================"
echo "11. Updating order status (should trigger another notification)..."
echo "============================================"
curl -X PUT "http://localhost:8080/api/orders/1/status?status=CONFIRMED"

echo
echo
echo "============================================"
echo "12. Checking notifications again..."
echo "============================================"
curl -X GET http://localhost:8080/api/notifications/vendor/111-111-1111

echo
echo
echo "============================================"
echo "Notification system test completed!"
echo "Check the vendor dashboard to see notifications."
echo "============================================"
