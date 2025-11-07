# POS Backend - Simple Design

## ✅ Simplification Complete!

The POS backend has been simplified to use a basic role-based system without complex permissions.

## Architecture

### Role System (Simple Enum)
```java
public enum Role {
    ADMIN,      // Full access
    RESPONSIBLE, // Management without user admin
    POS_USER    // Basic cashier operations
}
```

### User Model
Each user has a **single role** (enum field):
```java
@Enumerated(EnumType.STRING)
private Role role;
```

## Database Structure

### UserAccount Table
- `id` - Primary key
- `username` - Unique
- `password` - BCrypt encrypted
- `full_name` - User's full name
- `email` - Email address
- `active` - Boolean (enabled/disabled)
- `role` - Enum string (ADMIN, RESPONSIBLE, POS_USER)
- Base fields: `created_at`, `updated_at`, `created_by`, `updated_by`

## API Endpoints

### Authentication
```
POST /pos/api/login
Headers: 
  username=admin
  password=Admin@123

Response:
{
  "role": "ADMIN",
  "fullName": "System Administrator",
  "token": "JWT_TOKEN...",
  "status": 200
}
```

### User Management

#### List All Users
```
GET /pos/api/user/all

Response:
[
  {
    "id": 1,
    "username": "admin",
    "fullName": "System Administrator",
    "email": "admin@pos-system.com",
    "active": true,
    "role": "ADMIN"
  }
]
```

#### Get User Details
```
GET /pos/api/user/detail/1

Response:
{
  "id": 1,
  "username": "admin",
  "fullName": "System Administrator",
  "email": "admin@pos-system.com",
  "active": true,
  "role": "ADMIN"
}
```

#### Create User
```
POST /pos/api/user/create
Content-Type: application/json

{
  "username": "cashier01",
  "password": "Cashier123!",
  "fullName": "Jane Cashier",
  "email": "jane@store.com",
  "role": "POS_USER"
}

Response:
{
  "id": 2,
  "username": "cashier01",
  "fullName": "Jane Cashier",
  "email": "jane@store.com",
  "active": true,
  "role": "POS_USER"
}
```

#### Update User Role
```
PUT /pos/api/user/2/role
Content-Type: application/json

"RESPONSIBLE"

Response:
{
  "id": 2,
  "username": "cashier01",
  "fullName": "Jane Cashier",
  "email": "jane@store.com",
  "active": true,
  "role": "RESPONSIBLE"
}
```

#### Toggle User Status
```
PUT /pos/api/user/2/toggle-status

Response:
{
  "id": 2,
  "username": "cashier01",
  "fullName": "Jane Cashier",
  "email": "jane@store.com",
  "active": false,
  "role": "POS_USER"
}
```

#### Delete User
```
DELETE /pos/api/user/2
Response: 204 No Content
```

## Default Admin User

**Automatically created on first startup:**

- **Username:** admin
- **Password:** Admin@123
- **Role:** ADMIN

## Role Definitions

### ADMIN
- Full system access
- Can manage users
- Can perform all operations

### RESPONSIBLE
- Management access
- Cannot manage users
- Can view reports, inventory, etc.

### POS_USER
- Basic cashier operations
- Can process sales
- Limited access

## Authentication Flow

1. Client sends login request with username/password
2. Server validates credentials
3. Server returns JWT token + role
4. Client includes JWT token in subsequent requests
5. Server validates token and grants access based on role

## Usage in Frontend

### After Login
```javascript
const loginData = await login(username, password);
localStorage.setItem('token', loginData.token);
localStorage.setItem('role', loginData.role);

// Use role to control UI
if (loginData.role === 'ADMIN') {
  showAdminPanel();
}
```

### Making Authenticated Requests
```javascript
fetch('/pos/api/user/all', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`,
    'X-Company-ID': '1'
  }
});
```

## File Structure

```
src/main/java/com/digithink/pos/
├── model/
│   ├── UserAccount.java          # User entity with single role enum
│   └── enumeration/
│       └── Role.java             # Simple enum (ADMIN, RESPONSIBLE, POS_USER)
├── dto/
│   ├── UserAccountDTO.java       # User data transfer
│   └── CreateUserRequestDTO.java # Create user request
├── service/
│   └── UserAccountService.java   # Business logic
├── controller/
│   └── UseAccountAPI.java        # REST endpoints
├── security/
│   ├── SecurityConfig.java       # Spring Security config
│   ├── JWTAuthenticationFilter.java # Login filter
│   └── JWTAuthorizationFilter.java  # Token validation
└── repository/
    └── UserAccountRepository.java # Data access
```

## What Was Removed

- ❌ Permission entity
- ❌ PermissionPage enum
- ❌ PermissionAction enum
- ❌ Role entity
- ❌ Role-Permission mapping
- ❌ Complex permission checks
- ❌ Role API endpoints

## What Remains

- ✅ Simple Role enum
- ✅ User with single role
- ✅ JWT authentication
- ✅ User management API
- ✅ Clean, maintainable code

## Next Steps

You can now:
1. Add business entities (Product, Order, etc.)
2. Implement business logic
3. Build the frontend
4. Add more roles if needed later

---

**Design:** Simple and Clean  
**Status:** ✅ Ready for Development

