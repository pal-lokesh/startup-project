#!/bin/bash

# PostgreSQL Setup Script for RecordService
# This script helps you set up PostgreSQL database quickly

echo "=========================================="
echo "PostgreSQL Setup for RecordService"
echo "=========================================="
echo ""

# Check if PostgreSQL is installed
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL is not installed!"
    echo ""
    echo "Installation instructions:"
    echo "  macOS:   brew install postgresql@15"
    echo "  Linux:   sudo apt-get install postgresql postgresql-contrib"
    echo "  Windows: Download from https://www.postgresql.org/download/windows/"
    exit 1
fi

echo "✅ PostgreSQL is installed"
echo ""

# Check if PostgreSQL is running
if ! pg_isready -q; then
    echo "⚠️  PostgreSQL is not running. Starting it..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        brew services start postgresql@15 2>/dev/null || brew services start postgresql 2>/dev/null
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        sudo systemctl start postgresql
    fi
    sleep 2
fi

if pg_isready -q; then
    echo "✅ PostgreSQL is running"
else
    echo "❌ Failed to start PostgreSQL. Please start it manually."
    exit 1
fi

echo ""
echo "Creating database and user..."
echo ""

# Default values
DB_NAME="recordservice"
DB_USER="postgres"
DB_PASSWORD="postgres"

# Prompt for database name
read -p "Database name [$DB_NAME]: " input_db_name
DB_NAME=${input_db_name:-$DB_NAME}

# Prompt for username
read -p "Database username [$DB_USER]: " input_db_user
DB_USER=${input_db_user:-$DB_USER}

# Prompt for password (if not using default postgres user)
if [ "$DB_USER" != "postgres" ]; then
    read -sp "Database password: " DB_PASSWORD
    echo ""
else
    read -sp "PostgreSQL password for user 'postgres' [$DB_PASSWORD]: " input_password
    DB_PASSWORD=${input_password:-$DB_PASSWORD}
    echo ""
fi

# Create database
echo ""
echo "Creating database '$DB_NAME'..."
psql -U postgres -c "CREATE DATABASE $DB_NAME;" 2>/dev/null || echo "⚠️  Database might already exist (this is okay)"

# Create user if not using postgres
if [ "$DB_USER" != "postgres" ]; then
    echo "Creating user '$DB_USER'..."
    psql -U postgres -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';" 2>/dev/null || echo "⚠️  User might already exist (this is okay)"
    echo "Granting privileges..."
    psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;" 2>/dev/null
    psql -U postgres -d $DB_NAME -c "GRANT ALL ON SCHEMA public TO $DB_USER;" 2>/dev/null
fi

echo ""
echo "=========================================="
echo "✅ Setup Complete!"
echo "=========================================="
echo ""
echo "Database Configuration:"
echo "  Database: $DB_NAME"
echo "  Username: $DB_USER"
echo "  Password: [hidden]"
echo ""
echo "Update your application.properties with:"
echo "  spring.datasource.url=jdbc:postgresql://localhost:5432/$DB_NAME"
echo "  spring.datasource.username=$DB_USER"
echo "  spring.datasource.password=$DB_PASSWORD"
echo ""
echo "Then restart your Spring Boot application."
echo ""

