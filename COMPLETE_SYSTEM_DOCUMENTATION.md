# Complete User Management System with Vendor Business Themes

## 🏗️ System Architecture Overview

This is a comprehensive Spring Boot application that manages users (vendors and clients) with vendor-specific business details, themes, and images. The system uses a layered architecture with clear separation of concerns.

## 📊 Entity Relationship Diagram

```
User (Base Entity)
├── phoneNumber (Primary Key)
├── firstName, lastName, email
├── userType (VENDOR/CLIENT)
├── createdAt, updatedAt
└── ──────────────────────────
    │
    ├── Vendor (if userType = VENDOR)
    │   ├── vendorId (Primary Key)
    │   ├── phoneNumber (FK to User)
    │   ├── businessName, businessDescription
    │   ├── businessAddress, businessPhone, businessEmail
    │   ├── registrationNumber, taxId
    │   ├── isVerified
    │   └── ──────────────────────────
    │       │
    │       └── Business
    │           ├── businessId (Primary Key)
    │           ├── phoneNumber (FK to Vendor)
    │           ├── businessName, businessDescription
    │           ├── businessCategory, businessAddress
    │           ├── businessPhone, businessEmail
    │           ├── website, socialMediaLinks
    │           ├── operatingHours, isActive
    │           └── ──────────────────────────
    │               │
    │               └── Theme (One-to-Many)
    │                   ├── themeId (Primary Key)
    │                   ├── businessId (FK to Business)
    │                   ├── themeName, themeDescription
    │                   ├── themeCategory, priceRange
    │                   ├── isActive
    │                   └── ──────────────────────────
    │                       │
    │                       └── Image (One-to-Many)
    │                           ├── imageId (Primary Key)
    │                           ├── themeId (FK to Theme)
    │                           ├── imageName, imageUrl, imagePath
    │                           ├── imageSize, imageType
    │                           ├── isPrimary, uploadedAt
    │                           └── metadata
```

## 🎯 Key Features

### ✅ User Management
- **User Types**: VENDOR and CLIENT
- **Primary Key**: Phone Number (unique identifier)
- **Validation**: Email and phone number uniqueness
- **CRUD Operations**: Create, Read, Update, Delete

### ✅ Vendor Management
- **Vendor Profiles**: Extended user information for vendors
- **Business Details**: Business name, description, address, contact info
- **Verification Status**: Track vendor verification
- **Registration Info**: Registration number, tax ID

### ✅ Business Management
- **Business Profiles**: Detailed business information
- **Categories**: Business categorization
- **Contact Information**: Multiple contact methods
- **Social Media**: Website and social media links
- **Operating Hours**: Business schedule

### ✅ Theme Management
- **Theme Creation**: Themes for each business
- **Categories**: Theme categorization
- **Pricing**: Price range information
- **Status Management**: Active/inactive themes

### ✅ Image Management
- **Image Upload**: Multiple images per theme
- **Primary Image**: Set primary image per theme
- **Metadata**: Image size, type, upload date
- **URL Management**: Image URL and path storage

## 🚀 API Endpoints

### User Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users` | Create user (vendor/client) |
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/phone/{phoneNumber}` | Get user by phone |
| `GET` | `/api/users/email/{email}` | Get user by email |
| `GET` | `/api/users/type/{userType}` | Get users by type |
| `PUT` | `/api/users/phone/{phoneNumber}` | Update user |
| `DELETE` | `/api/users/phone/{phoneNumber}` | Delete user |
| `GET` | `/api/users/count` | Get user count |

### Vendor Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/vendors` | Create vendor profile |
| `GET` | `/api/vendors` | Get all vendors |
| `GET` | `/api/vendors/phone/{phoneNumber}` | Get vendor by phone |
| `GET` | `/api/vendors/{vendorId}` | Get vendor by ID |
| `GET` | `/api/vendors/verified/{verified}` | Get vendors by verification |
| `PUT` | `/api/vendors/phone/{phoneNumber}` | Update vendor |
| `DELETE` | `/api/vendors/phone/{phoneNumber}` | Delete vendor |
| `GET` | `/api/vendors/count` | Get vendor count |

### Business Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/businesses` | Create business |
| `GET` | `/api/businesses` | Get all businesses |
| `GET` | `/api/businesses/{businessId}` | Get business by ID |
| `GET` | `/api/businesses/phone/{phoneNumber}` | Get business by phone |
| `GET` | `/api/businesses/category/{category}` | Get businesses by category |
| `GET` | `/api/businesses/active/{active}` | Get businesses by status |
| `PUT` | `/api/businesses/{businessId}` | Update business |
| `DELETE` | `/api/businesses/{businessId}` | Delete business |
| `GET` | `/api/businesses/count` | Get business count |

### Theme Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/themes` | Create theme |
| `GET` | `/api/themes` | Get all themes |
| `GET` | `/api/themes/{themeId}` | Get theme by ID |
| `GET` | `/api/themes/business/{businessId}` | Get themes by business |
| `GET` | `/api/themes/category/{category}` | Get themes by category |
| `GET` | `/api/themes/active/{active}` | Get themes by status |
| `PUT` | `/api/themes/{themeId}` | Update theme |
| `DELETE` | `/api/themes/{themeId}` | Delete theme |
| `GET` | `/api/themes/count` | Get theme count |

### Image Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/images` | Upload image |
| `GET` | `/api/images` | Get all images |
| `GET` | `/api/images/{imageId}` | Get image by ID |
| `GET` | `/api/images/theme/{themeId}` | Get images by theme |
| `GET` | `/api/images/primary/{isPrimary}` | Get images by primary status |
| `PUT` | `/api/images/{imageId}` | Update image |
| `POST` | `/api/images/{imageId}/set-primary` | Set as primary image |
| `DELETE` | `/api/images/{imageId}` | Delete image |
| `GET` | `/api/images/count` | Get image count |

## 📝 Example Usage

### 1. Create a Vendor User
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

### 2. Create Vendor Profile
```json
POST /api/vendors
{
  "phoneNumber": "123-456-7890",
  "businessName": "Doe's Event Planning",
  "businessDescription": "Professional event planning services",
  "businessAddress": "123 Main St, City",
  "businessPhone": "123-456-7890",
  "businessEmail": "business@doeevents.com"
}
```

### 3. Create Business
```json
POST /api/businesses
{
  "phoneNumber": "123-456-7890",
  "businessName": "Doe's Event Planning",
  "businessDescription": "Professional event planning services",
  "businessCategory": "Event Planning",
  "businessAddress": "123 Main St, City",
  "businessPhone": "123-456-7890",
  "businessEmail": "business@doeevents.com"
}
```

### 4. Create Theme
```json
POST /api/themes
{
  "businessId": "BUSINESS_1",
  "themeName": "Elegant Wedding",
  "themeDescription": "Classic elegant wedding theme",
  "themeCategory": "Wedding",
  "priceRange": "5000-10000"
}
```

### 5. Upload Image
```json
POST /api/images
{
  "themeId": "THEME_1",
  "imageName": "wedding_main.jpg",
  "imageUrl": "https://example.com/images/wedding_main.jpg",
  "imagePath": "/images/wedding_main.jpg",
  "imageSize": 2048000,
  "imageType": "image/jpeg"
}
```

## 🏛️ Architecture Layers

### 1. Presentation Layer (Controllers)
- **UserController**: Handles user CRUD operations
- **VendorController**: Manages vendor profiles
- **BusinessController**: Business management
- **ThemeController**: Theme operations
- **ImageController**: Image handling

### 2. Business Logic Layer (Services)
- **UserService**: User business logic
- **VendorService**: Vendor operations
- **BusinessService**: Business logic
- **ThemeService**: Theme management
- **ImageService**: Image operations

### 3. Data Access Layer (Repositories)
- **UserRepository**: User data access
- **VendorRepository**: Vendor data access
- **BusinessRepository**: Business data access
- **ThemeRepository**: Theme data access
- **ImageRepository**: Image data access

### 4. Data Layer (Entities)
- **User**: Base user entity
- **Vendor**: Vendor-specific data
- **Business**: Business information
- **Theme**: Theme details
- **Image**: Image metadata

## 🔧 Technical Implementation

### Data Storage
- **In-Memory Storage**: Uses ConcurrentHashMap for thread-safe operations
- **Static Collections**: Data persists during application runtime
- **Auto-Generated IDs**: Sequential ID generation for entities

### Validation
- **Required Fields**: All mandatory fields validated
- **Uniqueness**: Phone numbers and emails must be unique
- **Conflict Detection**: Returns 409 status for duplicates

### Error Handling
- **HTTP Status Codes**: Proper status codes for different scenarios
- **Exception Handling**: Graceful error handling with try-catch blocks
- **Validation Errors**: 400 Bad Request for validation failures

## 🧪 Testing

### Test Script
Run `test-complete-system.bat` to test the entire system:

1. **User Creation**: Creates vendor and client users
2. **Vendor Profile**: Sets up vendor business details
3. **Business Setup**: Creates business profiles
4. **Theme Creation**: Adds themes to businesses
5. **Image Upload**: Uploads images to themes
6. **Primary Image**: Sets primary images
7. **Data Retrieval**: Tests all GET endpoints
8. **Count Verification**: Verifies entity counts

### Test Coverage
- ✅ User management (VENDOR/CLIENT)
- ✅ Vendor profile creation
- ✅ Business management
- ✅ Theme creation and management
- ✅ Image upload and management
- ✅ Primary image setting
- ✅ Data retrieval by various criteria
- ✅ Count operations

## 🚀 Getting Started

1. **Start Application**: `mvn spring-boot:run`
2. **Run Tests**: Execute `test-complete-system.bat`
3. **Access APIs**: Use any REST client (Postman, curl, etc.)
4. **Monitor**: Check application logs for operations

## 📈 System Benefits

- **Scalable Architecture**: Clean separation of concerns
- **Flexible User Types**: Support for vendors and clients
- **Rich Business Data**: Comprehensive business information
- **Theme Management**: Organized theme categorization
- **Image Handling**: Multiple images per theme with primary selection
- **RESTful APIs**: Standard HTTP methods and status codes
- **Data Validation**: Comprehensive input validation
- **Error Handling**: Graceful error management

This system provides a complete solution for managing users with vendor-specific business details, themes, and images, following Spring Boot best practices and RESTful API design principles.
