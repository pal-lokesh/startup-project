@echo off
echo ============================================
echo Record Service Frontend Setup
echo ============================================
echo.

echo Step 1: Installing dependencies...
npm install

echo.
echo Step 2: Starting development server...
echo.
echo The React app will start on http://localhost:3000
echo Make sure your Spring Boot backend is running on http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

npm start
