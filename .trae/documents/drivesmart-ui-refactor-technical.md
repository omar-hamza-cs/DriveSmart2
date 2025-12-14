## 1. Architecture Design

```mermaid
graph TD
    A[User Browser] --> B[Spring Boot Backend]
    B --> C[Thymeleaf Templates]
    C --> D[CSS/JS Assets]
    B --> E[Supabase Database]
    B --> F[Spring Security]
    
    subgraph "Frontend Layer"
        C
        D
    end
    
    subgraph "Backend Layer"
        B
        F
    end
    
    subgraph "Data Layer"
        E
    end
```

## 2. Technology Description

**Backend Stack:**
- Spring Boot 3.x with Spring Security
- Thymeleaf templating engine
- Spring Data JPA with Hibernate
- Maven build system

**Frontend Stack:**
- HTML5 with Thymeleaf templating
- CSS3 with custom properties and modern features
- Vanilla JavaScript (ES6+)
- No external frameworks to maintain simplicity

**Database:**
- Supabase (PostgreSQL) with Spring Data JPA
- Connection pooling with HikariCP
- Database migrations with Flyway

**Development Tools:**
- Spring Boot DevTools for hot reload
- Maven for dependency management
- IntelliJ IDEA recommended IDE

## 3. Route Definitions

| Route | Purpose | Template |
|-------|---------|----------|
| `/` | Home dashboard | `home.html` |
| `/login` | User authentication | `auth/login.html` |
| `/register` | User registration | `auth/register.html` |
| `/cars` | Fleet listing | `cars/list.html` |
| `/cars/add` | Add new vehicle | `cars/add.html` |
| `/cars/{id}` | Vehicle details | `cars/details.html` |
| `/cars/search` | Vehicle search | `cars/search.html` |
| `/bookings` | Booking management | `bookings/list.html` |
| `/bookings/new` | Create booking | `bookings/new.html` |
| `/bookings/{id}` | Booking details | `bookings/confirmation.html` |
| `/customers` | Customer list | `customers/list.html` |
| `/user/profile` | User profile | `user/profile.html` |
| `/admin/dashboard` | Admin overview | `admin/dashboard.html` |
| `/admin/users` | User management | `admin/users.html` |
| `/admin/reports` | System reports | `admin/reports.html` |
| `/admin/settings` | System settings | `admin/settings.html` |

## 4. CSS Architecture

### 4.1 Design System Variables
```css
:root {
  /* Exact Color Palette */
  --light-lavender: #A7A5C6;
  --slate-blue: #8797B2;
  --steel-teal: #6D8A96;
  --dark-slate: #5D707F;
  --bright-cyan: #66CED6;
  
  /* Semantic Mapping */
  --primary: var(--light-lavender);
  --secondary: var(--slate-blue);
  --accent: var(--steel-teal);
  --text-primary: var(--dark-slate);
  --cta: var(--bright-cyan);
  
  /* Typography */
  --font-primary: 'Inter', system-ui, sans-serif;
  --font-secondary: 'Poppins', system-ui, sans-serif;
  
  /* Spacing System */
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  --spacing-xl: 2rem;
  
  /* Breakpoints */
  --mobile-breakpoint: 768px;
  --tablet-breakpoint: 1024px;
}
```

### 4.2 Component Structure
```css
/* Base Styles */
.app-styles.css {
  /* Reset and normalize */
  /* Typography system */
  /* Color utilities */
  /* Spacing utilities */
}

/* Component Classes */
.btn { /* Button variations */ }
.card { /* Card components */ }
.navbar { /* Navigation */ }
.form-input { /* Form controls */ }
.table { /* Data tables */ }
.modal { /* Modal dialogs */ }

/* Responsive Utilities */
@media (max-width: 768px) { /* Mobile styles */ }
@media (min-width: 769px) and (max-width: 1024px) { /* Tablet styles */ }
@media (min-width: 1025px) { /* Desktop styles */ }
```

## 5. JavaScript Architecture

### 5.1 Core Modules
```javascript
// main.js - Application initialization
const App = {
  init() {
    this.setupEventListeners();
    this.initializeComponents();
    this.setupRouting();
  },
  
  setupEventListeners() {
    // Global event handlers
    document.addEventListener('click', this.handleGlobalClicks);
    window.addEventListener('resize', this.handleResize);
  },
  
  initializeComponents() {
    // Initialize UI components
    this.initNavigation();
    this.initForms();
    this.initModals();
  }
};

// ui.js - UI interactions
const UI = {
  showToast(message, type = 'info') {
    // Toast notification system
  },
  
  showModal(title, content) {
    // Modal display system
  },
  
  toggleSidebar() {
    // Mobile sidebar navigation
  }
};

// api.js - API communication
const API = {
  async request(endpoint, options = {}) {
    // Centralized API calls
    const response = await fetch(endpoint, {
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': this.getCsrfToken()
      },
      ...options
    });
    return response.json();
  }
};
```

### 5.2 Component Initialization
```javascript
// Component-specific initialization
document.addEventListener('DOMContentLoaded', () => {
  // Initialize all components
  initNavigation();
  initForms();
  initTables();
  initAnimations();
  
  // Mobile-specific features
  if (window.innerWidth < 768) {
    initMobileFeatures();
  }
});

function initNavigation() {
  // Mobile menu toggle
  const menuButton = document.getElementById('mobileMenuButton');
  const sidebar = document.getElementById('mobileSidebar');
  
  menuButton?.addEventListener('click', () => {
    sidebar.classList.toggle('active');
  });
}

function initForms() {
  // Form validation
  const forms = document.querySelectorAll('form[data-validate]');
  forms.forEach(form => {
    form.addEventListener('submit', validateForm);
  });
  
  // Real-time validation
  const inputs = document.querySelectorAll('input[data-validate]');
  inputs.forEach(input => {
    input.addEventListener('blur', validateField);
    input.addEventListener('input', clearError);
  });
}
```

## 6. Data Model

### 6.1 Entity Relationships
```mermaid
erDiagram
    USER ||--o{ BOOKING : creates
    USER ||--o{ CAR : manages
    BOOKING ||--|| CAR : references
    USER {
        UUID id PK
        String email UK
        String password
        String fullName
        String role
        DateTime createdAt
        DateTime updatedAt
    }
    CAR {
        UUID id PK
        String make
        String model
        String year
        String licensePlate UK
        String status
        BigDecimal dailyRate
        UUID ownerId FK
        DateTime createdAt
    }
    BOOKING {
        UUID id PK
        UUID customerId FK
        UUID carId FK
        UUID createdBy FK
        DateTime startDate
        DateTime endDate
        String status
        BigDecimal totalAmount
        DateTime createdAt
    }
```

### 6.2 Database Schema
```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'FLEET_MANAGER', 'BOOKING_AGENT', 'DRIVER')),
    phone VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    is_active BOOLEAN DEFAULT true
);

-- Cars table
CREATE TABLE cars (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    vin VARCHAR(17) UNIQUE,
    color VARCHAR(30),
    category VARCHAR(30),
    daily_rate DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'BOOKED', 'MAINTENANCE', 'DISABLED')),
    location VARCHAR(100),
    description TEXT,
    features JSONB,
    images JSONB,
    owner_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Bookings table
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    car_id UUID NOT NULL REFERENCES cars(id),
    created_by UUID NOT NULL REFERENCES users(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    pickup_location VARCHAR(100),
    dropoff_location VARCHAR(100),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    total_amount DECIMAL(10,2),
    deposit_amount DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_cars_status ON cars(status);
CREATE INDEX idx_cars_category ON cars(category);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(start_date, end_date);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);
```

## 7. Security Implementation

### 7.1 Authentication & Authorization
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cars/add", "/bookings/admin-new").hasAnyRole("ADMIN", "FLEET_MANAGER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            );
        return http.build();
    }
}
```

### 7.2 CSRF Protection
```html
<!-- Include CSRF token in forms -->
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />

<!-- Include in AJAX requests -->
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

fetch('/api/endpoint', {
  method: 'POST',
  headers: {
    [csrfHeader]: csrfToken,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(data)
});
```

## 8. Performance Optimization

### 8.1 Asset Optimization
```html
<!-- Preload critical CSS -->
<link rel="preload" th:href="@{/css/app-styles.css}" as="style">

<!-- Lazy load images -->
<img th:src="@{/images/car-placeholder.png}" 
     th:data-src="@{${car.imageUrl}}" 
     alt="Car Image" 
     class="lazy-load">

<!-- Bundle and minify CSS/JS in production -->
<!-- Use Spring Boot's resource handling for versioning -->
```

### 8.2 Caching Strategy
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**", "/js/**", "/images/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }
}
```

## 9. Testing Strategy

### 9.1 Unit Testing
```java
@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCarListAccess() throws Exception {
        mockMvc.perform(get("/cars"))
               .andExpect(status().isOk())
               .andExpect(view().name("cars/list"));
    }
}
```

### 9.2 Integration Testing
```java
@Test
public void testCompleteBookingFlow() {
    // Test user login
    // Test car selection
    // Test booking creation
    // Test booking confirmation
    // Test booking status updates
}
```

## 10. Deployment Configuration

### 10.1 Application Properties
```properties
# Database Configuration
spring.datasource.url=${SUPABASE_URL}
spring.datasource.username=${SUPABASE_USER}
spring.datasource.password=${SUPABASE_PASSWORD}

# Security
spring.security.user.name=admin
spring.security.user.password=${ADMIN_PASSWORD}

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Caching
spring.resources.cache.cachecontrol.max-age=31536000
spring.resources.cache.cachecontrol.cache-public=true

# Production Settings
server.compression.enabled=true
server.compression.mime-types=text/html,text/css,text/javascript,application/javascript
```

### 10.2 Environment Variables
```bash
# Required environment variables
SUPABASE_URL=postgresql://user:pass@host:port/db
SUPABASE_USER=postgres
SUPABASE_PASSWORD=your_password
ADMIN_PASSWORD=secure_admin_password
JWT_SECRET=your_jwt_secret
FILE_UPLOAD_PATH=/app/uploads
```