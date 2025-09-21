# Record Service Backend

A Spring Boot REST API for the Record Service Management System with JWT authentication and comprehensive security features.

## Features

- **JWT Authentication**: Secure token-based authentication
- **User Management**: Complete CRUD operations with role-based access
- **Vendor Management**: Vendor registration and verification
- **Business Management**: Business profile management
- **Theme Management**: Theme and category management
- **Image Management**: Image upload and management
- **Security**: Spring Security with CORS support
- **Database**: H2 in-memory database for development
- **Validation**: Input validation with Bean Validation
- **Documentation**: Comprehensive API documentation

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Installation

1. Navigate to the backend directory:
   ```bash
   cd RecordService
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The API will be available at http://localhost:8080

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `GET /api/auth/check-phone/{phoneNumber}` - Check phone number availability
- `GET /api/auth/check-email/{email}` - Check email availability

### User Management
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/phone/{phoneNumber}` - Get user by phone number
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/type/{userType}` - Get users by type
- `POST /api/users` - Create new user
- `PUT /api/users/phone/{phoneNumber}` - Update user
- `DELETE /api/users/phone/{phoneNumber}` - Delete user
- `GET /api/users/count` - Get user count

### Vendor Management
- `GET /api/vendors` - Get all vendors
- `GET /api/vendors/{vendorId}` - Get vendor by ID
- `GET /api/vendors/phone/{phoneNumber}` - Get vendor by phone number
- `POST /api/vendors` - Create new vendor
- `PUT /api/vendors/phone/{phoneNumber}` - Update vendor
- `DELETE /api/vendors/phone/{phoneNumber}` - Delete vendor

### Business Management
- `GET /api/businesses` - Get all businesses
- `GET /api/businesses/{businessId}` - Get business by ID
- `GET /api/businesses/phone/{phoneNumber}` - Get business by phone number
- `POST /api/businesses` - Create new business
- `PUT /api/businesses/{businessId}` - Update business
- `DELETE /api/businesses/{businessId}` - Delete business

### Theme Management
- `GET /api/themes` - Get all themes
- `GET /api/themes/{themeId}` - Get theme by ID
- `GET /api/themes/business/{businessId}` - Get themes by business
- `POST /api/themes` - Create new theme
- `PUT /api/themes/{themeId}` - Update theme
- `DELETE /api/themes/{themeId}` - Delete theme

### Image Management
- `GET /api/images` - Get all images
- `GET /api/images/{imageId}` - Get image by ID
- `GET /api/images/theme/{themeId}` - Get images by theme
- `POST /api/images` - Create new image
- `PUT /api/images/{imageId}` - Update image
- `DELETE /api/images/{imageId}` - Delete image

## Security Configuration

### JWT Configuration
- Secret key: Configurable via `jwt.secret` property
- Token expiration: 24 hours (configurable via `jwt.expiration`)
- Algorithm: HS256

### Role-Based Access Control
- **USER**: Basic user access
- **ADMIN**: Full administrative access
- **VENDOR_ADMIN**: Vendor-specific administrative access

### CORS Configuration
- Allowed origins: All (*)
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed headers: All
- Credentials: Enabled

## Database

### H2 Database (Development)
- URL: `jdbc:h2:mem:testdb`
- Console: http://localhost:8080/h2-console
- Username: `sa`
- Password: `password`

### Database Schema
- **users**: User accounts with authentication
- **vendors**: Vendor information
- **businesses**: Business profiles
- **themes**: Theme and category data
- **images**: Image metadata

## Configuration

### Application Properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# JWT
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000

# CORS
spring.web.cors.allowed-origins=*
```

## Project Structure

```
src/main/java/com/example/RecordService/
├── config/                 # Configuration classes
│   └── SecurityConfig.java # Security configuration
├── controller/             # REST controllers
│   └── AuthController.java # Authentication endpoints
├── model/                  # Entity models
│   ├── User.java          # User entity
│   ├── Vendor.java        # Vendor entity
│   ├── Business.java      # Business entity
│   ├── Theme.java         # Theme entity
│   ├── Image.java         # Image entity
│   └── dto/               # Data Transfer Objects
├── repository/             # Data repositories
│   └── UserRepository.java # User data access
├── security/               # Security components
│   ├── JwtUtil.java       # JWT utilities
│   └── JwtAuthenticationFilter.java # JWT filter
├── service/                # Business logic
│   ├── AuthService.java   # Authentication service
│   └── CustomUserDetailsService.java # User details service
└── RecordServiceApplication.java # Main application class
```

## Testing

### Running Tests
```bash
mvn test
```

### Test Coverage
- Unit tests for services
- Integration tests for controllers
- Security tests for authentication

## Development

### Adding New Features
1. Create entity model
2. Create repository interface
3. Create service class
4. Create controller
5. Add security configuration if needed
6. Write tests

### Code Style
- Follow Spring Boot conventions
- Use proper Java naming conventions
- Add comprehensive JavaDoc comments
- Implement proper error handling

## Deployment

### Production Configuration
1. Update database configuration for production database
2. Set secure JWT secret key
3. Configure CORS for production domains
4. Set up proper logging
5. Configure SSL/TLS

### Docker Support
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/RecordService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Monitoring

### Health Checks
- Actuator endpoints available at `/actuator`
- Health check at `/actuator/health`
- Metrics at `/actuator/metrics`

### Logging
- Logback configuration
- Structured logging with JSON format
- Log levels configurable via properties

## Troubleshooting

### Common Issues
1. **Port conflicts**: Change server.port in application.properties
2. **Database connection**: Check H2 console access
3. **CORS errors**: Verify CORS configuration
4. **JWT issues**: Check secret key and expiration settings

### Debug Mode
Enable debug logging:
```properties
logging.level.com.example.RecordService=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.