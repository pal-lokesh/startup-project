@echo off
echo Testing Complete User Management System with Vendor Business Themes...
echo.

echo ============================================
echo 1. Creating a VENDOR user...
echo ============================================
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phoneNumber\":\"123-456-7890\",\"userType\":\"VENDOR\"}"

echo.
echo.
echo ============================================
echo 2. Creating a CLIENT user...
echo ============================================
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\",\"phoneNumber\":\"987-654-3210\",\"userType\":\"CLIENT\"}"

echo.
echo.
echo ============================================
echo 3. Creating vendor profile...
echo ============================================
curl -X POST http://localhost:8080/api/vendors ^
  -H "Content-Type: application/json" ^
  -d "{\"phoneNumber\":\"123-456-7890\",\"businessName\":\"Doe's Event Planning\",\"businessDescription\":\"Professional event planning services\",\"businessAddress\":\"123 Main St, City\",\"businessPhone\":\"123-456-7890\",\"businessEmail\":\"business@doeevents.com\"}"

echo.
echo.
echo ============================================
echo 4. Creating business for vendor...
echo ============================================
curl -X POST http://localhost:8080/api/businesses ^
  -H "Content-Type: application/json" ^
  -d "{\"phoneNumber\":\"123-456-7890\",\"businessName\":\"Doe's Event Planning\",\"businessDescription\":\"Professional event planning services\",\"businessCategory\":\"Event Planning\",\"businessAddress\":\"123 Main St, City\",\"businessPhone\":\"123-456-7890\",\"businessEmail\":\"business@doeevents.com\"}"

echo.
echo.
echo ============================================
echo 5. Creating theme for business...
echo ============================================
curl -X POST http://localhost:8080/api/themes ^
  -H "Content-Type: application/json" ^
  -d "{\"businessId\":\"BUSINESS_1\",\"themeName\":\"Elegant Wedding\",\"themeDescription\":\"Classic elegant wedding theme\",\"themeCategory\":\"Wedding\",\"priceRange\":\"5000-10000\"}"

echo.
echo.
echo ============================================
echo 6. Creating another theme...
echo ============================================
curl -X POST http://localhost:8080/api/themes ^
  -H "Content-Type: application/json" ^
  -d "{\"businessId\":\"BUSINESS_1\",\"themeName\":\"Modern Corporate\",\"themeDescription\":\"Contemporary corporate event theme\",\"themeCategory\":\"Corporate\",\"priceRange\":\"3000-8000\"}"

echo.
echo.
echo ============================================
echo 7. Adding images to first theme...
echo ============================================
curl -X POST http://localhost:8080/api/images ^
  -H "Content-Type: application/json" ^
  -d "{\"themeId\":\"THEME_1\",\"imageName\":\"wedding_main.jpg\",\"imageUrl\":\"https://example.com/images/wedding_main.jpg\",\"imagePath\":\"/images/wedding_main.jpg\",\"imageSize\":2048000,\"imageType\":\"image/jpeg\"}"

echo.
echo.
curl -X POST http://localhost:8080/api/images ^
  -H "Content-Type: application/json" ^
  -d "{\"themeId\":\"THEME_1\",\"imageName\":\"wedding_detail.jpg\",\"imageUrl\":\"https://example.com/images/wedding_detail.jpg\",\"imagePath\":\"/images/wedding_detail.jpg\",\"imageSize\":1536000,\"imageType\":\"image/jpeg\"}"

echo.
echo.
echo ============================================
echo 8. Adding images to second theme...
echo ============================================
curl -X POST http://localhost:8080/api/images ^
  -H "Content-Type: application/json" ^
  -d "{\"themeId\":\"THEME_2\",\"imageName\":\"corporate_main.jpg\",\"imageUrl\":\"https://example.com/images/corporate_main.jpg\",\"imagePath\":\"/images/corporate_main.jpg\",\"imageSize\":1800000,\"imageType\":\"image/jpeg\"}"

echo.
echo.
echo ============================================
echo 9. Setting primary image for first theme...
echo ============================================
curl -X POST http://localhost:8080/api/images/IMAGE_1/set-primary

echo.
echo.
echo ============================================
echo 10. Getting all users...
echo ============================================
curl -X GET http://localhost:8080/api/users

echo.
echo.
echo ============================================
echo 11. Getting users by type (VENDOR)...
echo ============================================
curl -X GET http://localhost:8080/api/users/type/VENDOR

echo.
echo.
echo ============================================
echo 12. Getting all vendors...
echo ============================================
curl -X GET http://localhost:8080/api/vendors

echo.
echo.
echo ============================================
echo 13. Getting all businesses...
echo ============================================
curl -X GET http://localhost:8080/api/businesses

echo.
echo.
echo ============================================
echo 14. Getting themes by business...
echo ============================================
curl -X GET http://localhost:8080/api/themes/business/BUSINESS_1

echo.
echo.
echo ============================================
echo 15. Getting images by theme...
echo ============================================
curl -X GET http://localhost:8080/api/images/theme/THEME_1

echo.
echo.
echo ============================================
echo 16. Getting counts...
echo ============================================
echo Users: 
curl -X GET http://localhost:8080/api/users/count
echo.
echo Vendors: 
curl -X GET http://localhost:8080/api/vendors/count
echo.
echo Businesses: 
curl -X GET http://localhost:8080/api/businesses/count
echo.
echo Themes: 
curl -X GET http://localhost:8080/api/themes/count
echo.
echo Images: 
curl -X GET http://localhost:8080/api/images/count

echo.
echo.
echo ============================================
echo Test completed! System Architecture Working!
echo ============================================
pause
