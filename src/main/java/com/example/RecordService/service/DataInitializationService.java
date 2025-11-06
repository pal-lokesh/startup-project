package com.example.RecordService.service;

import com.example.RecordService.entity.Inventory;
import com.example.RecordService.entity.InventoryImage;
import com.example.RecordService.entity.Plate;
import com.example.RecordService.model.Business;
import com.example.RecordService.model.Image;
import com.example.RecordService.model.Theme;
import com.example.RecordService.model.User;
import com.example.RecordService.repository.BusinessRepository;
import com.example.RecordService.repository.ImageRepository;
import com.example.RecordService.repository.InventoryImageRepository;
import com.example.RecordService.repository.InventoryRepository;
import com.example.RecordService.repository.PlateRepository;
import com.example.RecordService.repository.ThemeRepository;
import com.example.RecordService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PlateRepository plateRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private InventoryImageRepository inventoryImageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        System.out.println("🚀 Initializing application data...");

        // Create database tables
        createTables();

        // Create users
        User vendor1 = createVendor1();
        User vendor2 = createVendor2();
        createClient1();

        // Create businesses
        Business business1 = createBusiness1(vendor1);
        Business business2 = createBusiness2(vendor2);

        // Create themes for Business 1 (Tent & Events)
        createThemes(business1);

        // Create inventory for Business 1 (Tent & Events)
        createInventory(business1);

        // Create plates for Business 2 (Catering & Food)
        createPlates(business2);

        // Create images for themes
        createThemeImages();

        // Create images for inventory
        createInventoryImages();

        // Create images for plates
        createPlateImages();

        System.out.println("✅ Data initialization completed successfully!");
    }

    private void createTables() {
        try {
            // Create ratings table
            String createRatingsTable = """
                CREATE TABLE IF NOT EXISTS ratings (
                    rating_id VARCHAR(255) PRIMARY KEY,
                    client_phone VARCHAR(20) NOT NULL,
                    item_id VARCHAR(255) NOT NULL,
                    item_type VARCHAR(50) NOT NULL,
                    business_id VARCHAR(255) NOT NULL,
                    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
                    comment TEXT,
                    order_id VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_active BOOLEAN DEFAULT TRUE
                )
                """;
            
            jdbcTemplate.execute(createRatingsTable);
            System.out.println("✅ Created ratings table");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    private User createVendor1() {
        if (userRepository.findByPhoneNumber("0000000000").isEmpty()) {
            User vendor = new User();
            vendor.setPhoneNumber("0000000000");
            vendor.setFirstName("vendor");
            vendor.setLastName("one");
            vendor.setEmail("abc@gmail.com");
            vendor.setPassword(passwordEncoder.encode("M0nchi@123"));
            vendor.setUserType(User.UserType.VENDOR);
            vendor.setRole(User.Role.VENDOR_ADMIN);
            vendor.setEnabled(true);
            vendor.setAccountNonExpired(true);
            vendor.setAccountNonLocked(true);
            vendor.setCredentialsNonExpired(true);
            vendor.setCreatedAt(LocalDateTime.now());
            vendor.setUpdatedAt(LocalDateTime.now());
            userRepository.save(vendor);
            System.out.println("✅ Created Vendor 1: " + vendor.getFirstName());
            return vendor;
        }
        return userRepository.findByPhoneNumber("0000000000").get();
    }

    private User createVendor2() {
        if (userRepository.findByPhoneNumber("1111111111").isEmpty()) {
            User vendor = new User();
            vendor.setPhoneNumber("1111111111");
            vendor.setFirstName("vendor");
            vendor.setLastName("two");
            vendor.setEmail("abcdef@gmail.com");
            vendor.setPassword(passwordEncoder.encode("M0nchi@123"));
            vendor.setUserType(User.UserType.VENDOR);
            vendor.setRole(User.Role.VENDOR_ADMIN);
            vendor.setEnabled(true);
            vendor.setAccountNonExpired(true);
            vendor.setAccountNonLocked(true);
            vendor.setCredentialsNonExpired(true);
            vendor.setCreatedAt(LocalDateTime.now());
            vendor.setUpdatedAt(LocalDateTime.now());
            userRepository.save(vendor);
            System.out.println("✅ Created Vendor 2: " + vendor.getFirstName());
            return vendor;
        }
        return userRepository.findByPhoneNumber("1111111111").get();
    }

    private User createClient1() {
        if (userRepository.findByPhoneNumber("2222222222").isEmpty()) {
            User client = new User();
            client.setPhoneNumber("2222222222");
            client.setFirstName("client");
            client.setLastName("one");
            client.setEmail("abcef@gmail.com");
            client.setPassword(passwordEncoder.encode("M0nchi@123"));
            client.setUserType(User.UserType.CLIENT);
            client.setRole(User.Role.USER);
            client.setEnabled(true);
            client.setAccountNonExpired(true);
            client.setAccountNonLocked(true);
            client.setCredentialsNonExpired(true);
            client.setCreatedAt(LocalDateTime.now());
            client.setUpdatedAt(LocalDateTime.now());
            userRepository.save(client);
            System.out.println("✅ Created Client: " + client.getFirstName());
            return client;
        }
        return userRepository.findByPhoneNumber("2222222222").get();
    }

    private Business createBusiness1(User vendor) {
        if (businessRepository.findByBusinessId("BUSINESS_001") == null) {
            Business business = new Business();
            business.setBusinessId("BUSINESS_001");
            business.setBusinessName("Elegant Events & Tents");
            business.setBusinessCategory("tent");
            business.setBusinessDescription("Premium tent and event decoration services");
            business.setBusinessAddress("123 Event Street, City");
            business.setBusinessPhone("0000000000");
            business.setBusinessEmail("abc@gmail.com");
            business.setPhoneNumber(vendor.getPhoneNumber());
            business.setCreatedAt(LocalDateTime.now());
            business.setUpdatedAt(LocalDateTime.now());

            businessRepository.save(business);
            System.out.println("✅ Created Business 1: " + business.getBusinessName());
            return business;
        }
        return businessRepository.findByBusinessId("BUSINESS_001");
    }

    private Business createBusiness2(User vendor) {
        if (businessRepository.findByBusinessId("BUSINESS_002") == null) {
            Business business = new Business();
            business.setBusinessId("BUSINESS_002");
            business.setBusinessName("Delicious Catering Services");
            business.setBusinessCategory("caters");
            business.setBusinessDescription("Professional catering and food services");
            business.setBusinessAddress("456 Food Avenue, City");
            business.setBusinessPhone("1111111111");
            business.setBusinessEmail("abcdef@gmail.com");
            business.setPhoneNumber(vendor.getPhoneNumber());
            business.setCreatedAt(LocalDateTime.now());
            business.setUpdatedAt(LocalDateTime.now());

            businessRepository.save(business);
            System.out.println("✅ Created Business 2: " + business.getBusinessName());
            return business;
        }
        return businessRepository.findByBusinessId("BUSINESS_002");
    }

    private void createThemes(Business business) {
        if (themeRepository.findByThemeId("THEME_001") == null) {
            // Create Theme 1: Royal Wedding
            Theme theme1 = new Theme();
            theme1.setThemeId("THEME_001");
            theme1.setThemeName("Royal Wedding");
            theme1.setThemeCategory("wedding");
            theme1.setThemeDescription("Elegant royal wedding theme with gold and white decorations");
            theme1.setPriceRange("50000");
            theme1.setBusinessId(business.getBusinessId());
            theme1.setCreatedAt(LocalDateTime.now());
            theme1.setUpdatedAt(LocalDateTime.now());

            themeRepository.save(theme1);
            System.out.println("✅ Created Theme 1: " + theme1.getThemeName());
        }

        if (themeRepository.findByThemeId("THEME_002") == null) {
            // Create Theme 2: Garden Party
            Theme theme2 = new Theme();
            theme2.setThemeId("THEME_002");
            theme2.setThemeName("Garden Party");
            theme2.setThemeCategory("party");
            theme2.setThemeDescription("Beautiful garden party theme with floral decorations");
            theme2.setPriceRange("35000");
            theme2.setBusinessId(business.getBusinessId());
            theme2.setCreatedAt(LocalDateTime.now());
            theme2.setUpdatedAt(LocalDateTime.now());

            themeRepository.save(theme2);
            System.out.println("✅ Created Theme 2: " + theme2.getThemeName());
        }
    }

    private void createInventory(Business business) {
        if (inventoryRepository.findByInventoryId("INV_001") == null) {
            // Create Inventory 1: Premium Chairs
            Inventory inventory1 = new Inventory();
            inventory1.setInventoryId("INV_001");
            inventory1.setInventoryName("Premium Chairs");
            inventory1.setInventoryCategory("furniture");
            inventory1.setInventoryDescription("High-quality premium chairs for events");
            inventory1.setPrice(500.0);
            inventory1.setBusinessId(business.getBusinessId());
            inventory1.setCreatedAt(LocalDateTime.now());
            inventory1.setUpdatedAt(LocalDateTime.now());

            inventoryRepository.save(inventory1);
            System.out.println("✅ Created Inventory 1: " + inventory1.getInventoryName());
        }

        if (inventoryRepository.findByInventoryId("INV_002") == null) {
            // Create Inventory 2: Decorative Lights
            Inventory inventory2 = new Inventory();
            inventory2.setInventoryId("INV_002");
            inventory2.setInventoryName("Decorative Lights");
            inventory2.setInventoryCategory("decoration");
            inventory2.setPrice(2000.0);
            inventory2.setBusinessId(business.getBusinessId());
            inventory2.setCreatedAt(LocalDateTime.now());
            inventory2.setUpdatedAt(LocalDateTime.now());

            inventoryRepository.save(inventory2);
            System.out.println("✅ Created Inventory 2: " + inventory2.getInventoryName());
        }
    }

    private void createPlates(Business business) {
        if (plateRepository.findById("PLATE_001").isEmpty()) {
            // Create Plate 1: Biryani
            Plate plate1 = new Plate();
            plate1.setPlateId("PLATE_001");
            plate1.setDishName("Chicken Biryani");
            plate1.setDishDescription("Authentic chicken biryani with aromatic spices");
            plate1.setPlateImage("http://localhost:8080/uploads/plates/butterChicken.jpg");
            plate1.setPrice(250.0);
            plate1.setDishType("non-veg");
            plate1.setBusinessId(business.getBusinessId());
            plate1.setCreatedAt(LocalDateTime.now());
            plate1.setUpdatedAt(LocalDateTime.now());

            plateRepository.save(plate1);
            System.out.println("✅ Created Plate 1: " + plate1.getDishName());
        }

        if (plateRepository.findById("PLATE_002").isEmpty()) {
            // Create Plate 2: Paneer Curry
            Plate plate2 = new Plate();
            plate2.setPlateId("PLATE_002");
            plate2.setDishName("Paneer Curry");
            plate2.setDishDescription("Delicious paneer curry with rich gravy");
            plate2.setPlateImage("http://localhost:8080/uploads/plates/malaichapp.jpg");
            plate2.setPrice(200.0);
            plate2.setDishType("veg");
            plate2.setBusinessId(business.getBusinessId());
            plate2.setCreatedAt(LocalDateTime.now());
            plate2.setUpdatedAt(LocalDateTime.now());

            plateRepository.save(plate2);
            System.out.println("✅ Created Plate 2: " + plate2.getDishName());
        }
    }

    private void createThemeImages() {
        // Create images for Theme 1 (Royal Wedding)
        if (imageRepository.findByImageId("IMG_THEME_001_1") == null) {
            Image theme1Image1 = new Image();
            theme1Image1.setImageId("IMG_THEME_001_1");
            theme1Image1.setThemeId("THEME_001");
            theme1Image1.setImageName("tent1.jpg");
            theme1Image1.setImageUrl("http://localhost:8080/uploads/themes/tent1.jpg");
            theme1Image1.setImagePath("/uploads/themes/tent1.jpg");
            theme1Image1.setImageSize(1024000);
            theme1Image1.setImageType("image/jpeg");
            theme1Image1.setPrimary(true);
            theme1Image1.setUploadedAt(LocalDateTime.now());
            imageRepository.save(theme1Image1);
            System.out.println("✅ Created Theme 1 Image 1: " + theme1Image1.getImageName());
        }

        if (imageRepository.findByImageId("IMG_THEME_001_2") == null) {
            Image theme1Image2 = new Image();
            theme1Image2.setImageId("IMG_THEME_001_2");
            theme1Image2.setThemeId("THEME_001");
            theme1Image2.setImageName("tent2.jpg");
            theme1Image2.setImageUrl("http://localhost:8080/uploads/themes/tent2.jpg");
            theme1Image2.setImagePath("/uploads/themes/tent2.jpg");
            theme1Image2.setImageSize(1200000);
            theme1Image2.setImageType("image/jpeg");
            theme1Image2.setPrimary(false);
            theme1Image2.setUploadedAt(LocalDateTime.now());
            imageRepository.save(theme1Image2);
            System.out.println("✅ Created Theme 1 Image 2: " + theme1Image2.getImageName());
        }

        // Create images for Theme 2 (Garden Party)
        if (imageRepository.findByImageId("IMG_THEME_002_1") == null) {
            Image theme2Image1 = new Image();
            theme2Image1.setImageId("IMG_THEME_002_1");
            theme2Image1.setThemeId("THEME_002");
            theme2Image1.setImageName("tent3.jpeg");
            theme2Image1.setImageUrl("http://localhost:8080/uploads/themes/tent3.jpeg");
            theme2Image1.setImagePath("/uploads/themes/tent3.jpeg");
            theme2Image1.setImageSize(950000);
            theme2Image1.setImageType("image/jpeg");
            theme2Image1.setPrimary(true);
            theme2Image1.setUploadedAt(LocalDateTime.now());
            imageRepository.save(theme2Image1);
            System.out.println("✅ Created Theme 2 Image 1: " + theme2Image1.getImageName());
        }

        if (imageRepository.findByImageId("IMG_THEME_002_2") == null) {
            Image theme2Image2 = new Image();
            theme2Image2.setImageId("IMG_THEME_002_2");
            theme2Image2.setThemeId("THEME_002");
            theme2Image2.setImageName("theme4.jpg");
            theme2Image2.setImageUrl("http://localhost:8080/uploads/themes/theme4.jpg");
            theme2Image2.setImagePath("/uploads/themes/theme4.jpg");
            theme2Image2.setImageSize(1100000);
            theme2Image2.setImageType("image/jpeg");
            theme2Image2.setPrimary(false);
            theme2Image2.setUploadedAt(LocalDateTime.now());
            imageRepository.save(theme2Image2);
            System.out.println("✅ Created Theme 2 Image 2: " + theme2Image2.getImageName());
        }
    }

    private void createInventoryImages() {
        // Create images for Inventory 1 (Premium Chairs)
        if (inventoryImageRepository.findById("IMG_INV_001_1").isEmpty()) {
            InventoryImage inv1Image1 = new InventoryImage();
            inv1Image1.setImageId("IMG_INV_001_1");
            inv1Image1.setInventoryId("INV_001");
            inv1Image1.setImageName("woddenChair.jpeg");
            inv1Image1.setImageUrl("http://localhost:8080/uploads/inventory/woddenChair.jpeg");
            inv1Image1.setImagePath("/uploads/inventory/woddenChair.jpeg");
            inv1Image1.setImageSize(800000);
            inv1Image1.setImageType("image/jpeg");
            inv1Image1.setPrimary(true);
            inv1Image1.setUploadedAt(LocalDateTime.now());
            inventoryImageRepository.save(inv1Image1);
            System.out.println("✅ Created Inventory 1 Image 1: " + inv1Image1.getImageName());
        }

        // Create images for Inventory 2 (Decorative Lights)
        if (inventoryImageRepository.findById("IMG_INV_002_1").isEmpty()) {
            InventoryImage inv2Image1 = new InventoryImage();
            inv2Image1.setImageId("IMG_INV_002_1");
            inv2Image1.setInventoryId("INV_002");
            inv2Image1.setImageName("roundTable.jpg");
            inv2Image1.setImageUrl("http://localhost:8080/uploads/inventory/roundTable.jpg");
            inv2Image1.setImagePath("/uploads/inventory/roundTable.jpg");
            inv2Image1.setImageSize(900000);
            inv2Image1.setImageType("image/jpeg");
            inv2Image1.setPrimary(true);
            inv2Image1.setUploadedAt(LocalDateTime.now());
            inventoryImageRepository.save(inv2Image1);
            System.out.println("✅ Created Inventory 2 Image 1: " + inv2Image1.getImageName());
        }
    }

    private void createPlateImages() {
        // Create images for Plate 1 (Chicken Biryani)
        if (imageRepository.findByImageId("IMG_PLATE_001_1") == null) {
            Image plate1Image1 = new Image();
            plate1Image1.setImageId("IMG_PLATE_001_1");
            plate1Image1.setThemeId("PLATE_001"); // Using themeId field for plate images
            plate1Image1.setImageName("butterChicken.jpg");
            plate1Image1.setImageUrl("http://localhost:8080/uploads/plates/butterChicken.jpg");
            plate1Image1.setImagePath("/uploads/plates/butterChicken.jpg");
            plate1Image1.setImageSize(750000);
            plate1Image1.setImageType("image/jpeg");
            plate1Image1.setPrimary(true);
            plate1Image1.setUploadedAt(LocalDateTime.now());
            imageRepository.save(plate1Image1);
            System.out.println("✅ Created Plate 1 Image 1: " + plate1Image1.getImageName());
        }

        // Create images for Plate 2 (Paneer Curry)
        if (imageRepository.findByImageId("IMG_PLATE_002_1") == null) {
            Image plate2Image1 = new Image();
            plate2Image1.setImageId("IMG_PLATE_002_1");
            plate2Image1.setThemeId("PLATE_002"); // Using themeId field for plate images
            plate2Image1.setImageName("malaichapp.jpg");
            plate2Image1.setImageUrl("http://localhost:8080/uploads/plates/malaichapp.jpg");
            plate2Image1.setImagePath("/uploads/plates/malaichapp.jpg");
            plate2Image1.setImageSize(650000);
            plate2Image1.setImageType("image/jpeg");
            plate2Image1.setPrimary(true);
            plate2Image1.setUploadedAt(LocalDateTime.now());
            imageRepository.save(plate2Image1);
            System.out.println("✅ Created Plate 2 Image 1: " + plate2Image1.getImageName());
        }
    }
}