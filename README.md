# RecordService - User Management System

A comprehensive Spring Boot application for managing users (vendors and clients) with vendor-specific business details, themes, and images.

## 🚀 Features

- **User Management**: Support for VENDOR and CLIENT user types
- **Vendor Profiles**: Extended business information for vendors
- **Business Management**: Complete business profiles with categories
- **Theme Management**: Organize services by themes
- **Image Management**: Multiple images per theme with primary selection
- **RESTful APIs**: 42+ endpoints for complete CRUD operations

## 🏗️ Architecture

- **Presentation Layer**: REST Controllers
- **Business Logic Layer**: Services
- **Data Access Layer**: Repositories
- **Data Layer**: Entities

## 📊 Entity Relationships

```
User (VENDOR/CLIENT)
├── phoneNumber (Primary Key)
└── If VENDOR → Vendor Profile
    └── Business Profile
        └── Themes (One-to-Many)
            └── Images (One-to-Many)
```

## 🚀 Quick Start

1. **Clone the repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/RecordService.git
   cd RecordService
   ```

2. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Test the system**:
   ```bash
   test-complete-system.bat
   ```

## 📡 API Endpoints

| **Module** | **Endpoints** | **Count** |
|------------|---------------|-----------|
| **Users** | `/api/users/*` | 8 endpoints |
| **Vendors** | `/api/vendors/*` | 8 endpoints |
| **Businesses** | `/api/businesses/*` | 9 endpoints |
| **Themes** | `/api/themes/*` | 8 endpoints |
| **Images** | `/api/images/*` | 9 endpoints |

### Example API Usage

**Create a Vendor User:**
```json
POST /api/users
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "123-456-7890",
  "userType": "VENDOR"
}
```

**Create Business:**
```json
POST /api/businesses
{
  "phoneNumber": "123-456-7890",
  "businessName": "Doe's Event Planning",
  "businessCategory": "Event Planning",
  "businessEmail": "business@doeevents.com"
}
```

## 📚 Documentation

- [System Architecture](SYSTEM_ARCHITECTURE.md) - Detailed architecture overview
- [Complete Documentation](COMPLETE_SYSTEM_DOCUMENTATION.md) - Comprehensive system guide
- [GitHub Setup Guide](GITHUB_SETUP_GUIDE.md) - How to push to GitHub

## 🛠️ Technologies Used

- **Java 17**
- **Spring Boot 3.5.5**
- **Maven**
- **REST APIs**
- **In-Memory Storage** (ConcurrentHashMap)

## 🧪 Testing

The project includes comprehensive test scripts:
- `test-api.bat` - Basic API testing
- `test-complete-system.bat` - Complete system testing

## 📋 Project Structure

```
src/
├── main/
│   ├── java/com/example/RecordService/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── repository/     # Data Access
│   │   └── model/          # Entities
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/RecordService/
```

## 🎯 Use Cases

### Event Planning Platform
- **Vendors** register and showcase their services
- **Clients** browse themes and book services
- **Businesses** organize services by categories
- **Images** showcase previous work

### Service Marketplace
- **Vendors** create detailed business profiles
- **Themes** categorize different service types
- **Images** provide visual examples
- **Clients** find and contact service providers

## 🔧 Development

### Prerequisites
- Java 17+
- Maven 3.6+
- Git

### Running Locally
```bash
# Clone repository
git clone https://github.com/YOUR_USERNAME/RecordService.git

# Navigate to project
cd RecordService

# Run application
mvn spring-boot:run

# Application will be available at http://localhost:8080
```

### Building
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Maven for dependency management
- GitHub for version control

---

⭐ **Star this repository if you found it helpful!**
