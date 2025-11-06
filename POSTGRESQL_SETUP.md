# PostgreSQL Setup Guide

This guide will help you set up PostgreSQL for the RecordService application.

## Prerequisites

- PostgreSQL installed on your system
- Basic knowledge of PostgreSQL commands

## Installation

### macOS (using Homebrew)
```bash
brew install postgresql@15
brew services start postgresql@15
```

### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### Windows
Download and install from: https://www.postgresql.org/download/windows/

## Database Setup

### 1. Connect to PostgreSQL

```bash
# macOS/Linux
psql postgres

# Windows (if installed with default settings)
psql -U postgres
```

### 2. Create Database and User

```sql
-- Create database
CREATE DATABASE recordservice;

-- Create user (optional, you can use default 'postgres' user)
CREATE USER recordservice_user WITH PASSWORD 'your_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE recordservice TO recordservice_user;

-- Connect to the new database
\c recordservice

-- Grant schema privileges (if using separate user)
GRANT ALL ON SCHEMA public TO recordservice_user;
```

### 3. Update application.properties

Edit `src/main/resources/application.properties`:

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/recordservice
spring.datasource.username=postgres  # or recordservice_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update  # Use 'update' for production, 'create-drop' for development
```

## Configuration Options

### ddl-auto Options:
- `create-drop`: Drop and create schema on startup/shutdown (development only)
- `update`: Update schema if changes detected (recommended for production)
- `validate`: Validate schema without making changes
- `none`: Do nothing

### For Production:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### For Development:
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Switching Between H2 and PostgreSQL

### Use PostgreSQL (Production):
1. Comment out H2 configuration in `application.properties`
2. Uncomment PostgreSQL configuration
3. Ensure PostgreSQL is running
4. Restart the application

### Use H2 (Development/Testing):
1. Comment out PostgreSQL configuration
2. Uncomment H2 configuration
3. Restart the application

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL service is running:
  ```bash
  # macOS/Linux
  brew services list  # or systemctl status postgresql
  
  # Start if not running
  brew services start postgresql@15  # or sudo systemctl start postgresql
  ```

### Authentication Failed
- Check username and password in `application.properties`
- Verify user exists and has proper permissions:
  ```sql
  \du  -- List all users
  ```

### Database Does Not Exist
- Create the database:
  ```sql
  CREATE DATABASE recordservice;
  ```

### Port Already in Use
- Check if PostgreSQL is using the default port (5432):
  ```bash
  lsof -i :5432  # macOS/Linux
  netstat -ano | findstr :5432  # Windows
  ```

## Useful PostgreSQL Commands

```sql
-- List all databases
\l

-- Connect to a database
\c database_name

-- List all tables
\dt

-- Describe a table
\d table_name

-- List all users
\du

-- Exit psql
\q
```

## Docker Alternative (Optional)

If you prefer using Docker:

```bash
# Run PostgreSQL in Docker
docker run --name postgres-recordservice \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=recordservice \
  -p 5432:5432 \
  -d postgres:15

# Connect to it
docker exec -it postgres-recordservice psql -U postgres -d recordservice
```

Then use the same connection string:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recordservice
spring.datasource.username=postgres
spring.datasource.password=postgres
```

