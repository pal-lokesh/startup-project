@echo off
echo Testing Updated User API Endpoint (Phone Number as Primary Key)...
echo.

echo 1. Creating a new user...
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phoneNumber\":\"123-456-7890\"}"

echo.
echo.
echo 2. Creating another user...
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\",\"phoneNumber\":\"987-654-3210\"}"

echo.
echo.
echo 3. Getting all users...
curl -X GET http://localhost:8080/api/users

echo.
echo.
echo 4. Getting user by phone number...
curl -X GET http://localhost:8080/api/users/phone/123-456-7890

echo.
echo.
echo 5. Getting user by email...
curl -X GET http://localhost:8080/api/users/email/jane.smith@example.com

echo.
echo.
echo 6. Getting user count...
curl -X GET http://localhost:8080/api/users/count

echo.
echo.
echo 7. Testing duplicate phone number (should return 409 Conflict)...
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Bob\",\"lastName\":\"Wilson\",\"email\":\"bob.wilson@example.com\",\"phoneNumber\":\"123-456-7890\"}"

echo.
echo.
echo 8. Testing duplicate email (should return 409 Conflict)...
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Alice\",\"lastName\":\"Johnson\",\"email\":\"john.doe@example.com\",\"phoneNumber\":\"555-123-4567\"}"

echo.
echo.
echo Test completed!
pause
