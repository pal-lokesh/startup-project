@echo off
echo ============================================
echo GitHub Setup Script for RecordService
echo ============================================
echo.

echo Step 1: Check if Git is installed...
git --version
if %errorlevel% neq 0 (
    echo ERROR: Git is not installed!
    echo Please install Git from: https://git-scm.com/download/win
    echo Then run this script again.
    pause
    exit /b 1
)

echo.
echo Step 2: Initialize Git repository...
git init

echo.
echo Step 3: Add all files...
git add .

echo.
echo Step 4: Create initial commit...
git commit -m "Initial commit: Complete User Management System with Vendor Business Themes"

echo.
echo ============================================
echo Next Steps:
echo ============================================
echo 1. Create a repository on GitHub
echo 2. Run these commands:
echo.
echo    git remote add origin https://github.com/YOUR_USERNAME/RecordService.git
echo    git push -u origin main
echo.
echo Replace YOUR_USERNAME with your actual GitHub username
echo ============================================
echo.
pause
