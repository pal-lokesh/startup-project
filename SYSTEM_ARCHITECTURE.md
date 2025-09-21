# System Architecture: User Management with Vendor Business Themes

## Entity Relationship Diagram

```
User (Base Entity)
├── id (Primary Key)
├── firstName
├── lastName  
├── email (Unique)
├── phoneNumber (Primary Key)
├── userType (VENDOR/CLIENT)
├── createdAt
└── updatedAt

Vendor (Extends User)
├── vendorId (Primary Key)
├── userId (Foreign Key to User)
├── businessName
├── businessDescription
├── businessAddress
├── businessPhone
├── businessEmail
├── registrationNumber
├── taxId
├── isVerified
└── business (One-to-One with Business)

Business
├── businessId (Primary Key)
├── vendorId (Foreign Key to Vendor)
├── businessName
├── businessDescription
├── businessCategory
├── businessAddress
├── businessPhone
├── businessEmail
├── website
├── socialMediaLinks
├── operatingHours
├── isActive
└── themes (One-to-Many with Theme)

Theme
├── themeId (Primary Key)
├── businessId (Foreign Key to Business)
├── themeName
├── themeDescription
├── themeCategory
├── priceRange
├── isActive
├── createdAt
└── images (One-to-Many with Image)

Image
├── imageId (Primary Key)
├── themeId (Foreign Key to Theme)
├── imageName
├── imageUrl
├── imagePath
├── imageSize
├── imageType
├── isPrimary
├── uploadedAt
└── metadata
```

## System Architecture Layers

### 1. Presentation Layer (Controllers)
- UserController
- VendorController  
- BusinessController
- ThemeController
- ImageController

### 2. Business Logic Layer (Services)
- UserService
- VendorService
- BusinessService
- ThemeService
- ImageService

### 3. Data Access Layer (Repositories)
- UserRepository
- VendorRepository
- BusinessRepository
- ThemeRepository
- ImageRepository

### 4. Data Layer (Entities)
- User
- Vendor
- Business
- Theme
- Image

## API Endpoints Design

### User Management
- POST /api/users - Create user (vendor/client)
- GET /api/users - Get all users
- GET /api/users/{phoneNumber} - Get user by phone
- PUT /api/users/{phoneNumber} - Update user
- DELETE /api/users/{phoneNumber} - Delete user

### Vendor Management
- POST /api/vendors - Create vendor profile
- GET /api/vendors - Get all vendors
- GET /api/vendors/{phoneNumber} - Get vendor by phone
- PUT /api/vendors/{phoneNumber} - Update vendor
- GET /api/vendors/{phoneNumber}/business - Get vendor's business

### Business Management
- POST /api/businesses - Create business
- GET /api/businesses - Get all businesses
- GET /api/businesses/{businessId} - Get business by ID
- PUT /api/businesses/{businessId} - Update business
- GET /api/businesses/{businessId}/themes - Get business themes

### Theme Management
- POST /api/themes - Create theme
- GET /api/themes - Get all themes
- GET /api/themes/{themeId} - Get theme by ID
- PUT /api/themes/{themeId} - Update theme
- GET /api/themes/{themeId}/images - Get theme images

### Image Management
- POST /api/images - Upload image
- GET /api/images/{imageId} - Get image by ID
- DELETE /api/images/{imageId} - Delete image
- POST /api/images/{imageId}/set-primary - Set as primary image
