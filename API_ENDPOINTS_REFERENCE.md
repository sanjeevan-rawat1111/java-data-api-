# Java Data API - Complete Endpoints Reference

## 🌐 **Base URL**
```
http://localhost:8080/api
```

## 📋 **API Endpoints Overview**

### **1. Health Check Endpoint**

#### `GET /api/data/health`
**Purpose:** Check the health status of both MySQL and Aerospike databases.

**Request:** No body required

**Response:**
```json
{
  "success": true,
  "message": "Health status retrieved",
  "data": {
    "mysql": {
      "record_count": 10,
      "healthy": true
    },
    "aerospike": {
      "healthy": true
    }
  },
  "source": "both",
  "executionTimeMs": 45
}
```

**Status Codes:**
- `200 OK` - Health check successful
- `500 Internal Server Error` - Database connection issues

---

### **2. Data Query Endpoint**

#### `POST /api/data/query`
**Purpose:** Unified endpoint for querying data from MySQL, Aerospike, or both databases.

**Request Body:**
```json
{
  "queryType": "mysql|aerospike|both",
  "queryParameter": "query_type",
  "namespace": "optional_aerospike_namespace",
  "set": "optional_aerospike_set", 
  "key": "optional_key_or_parameter"
}
```

**Supported Query Parameters:**
- **MySQL:**
  - `all_users` - Get all users
  - `count` - Get user count
  - `by_id` - Get user by ID (requires key parameter)
  - `by_email` - Get user by email (requires key parameter)
  - `by_city` - Get users by city (requires key parameter)

- **Aerospike:**
  - `all_records` - Get all records from a set
  - `by_key` - Get record by key (requires key parameter)
  - `count` - Get record count

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": [ ... ],
  "source": "mysql|aerospike|both",
  "executionTimeMs": 120
}
```

**Status Codes:**
- `200 OK` - Query successful
- `400 Bad Request` - Invalid query parameters
- `500 Internal Server Error` - Database error

---

## 🆕 **MySQL Write Operations**

### **3. Create User**

#### `POST /api/data/users`
**Purpose:** Create a new user in MySQL database.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30,
  "city": "New York"
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters
- `email`: Required, valid email format
- `age`: Optional, integer
- `city`: Optional, string

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": {
    "id": 11,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "age": 30,
    "city": "New York"
  },
  "source": "mysql",
  "executionTimeMs": 45
}
```

**Status Codes:**
- `201 Created` - User created successfully
- `400 Bad Request` - Validation errors
- `500 Internal Server Error` - Database error

---

### **4. Update User**

#### `PUT /api/data/users/{id}`
**Purpose:** Update an existing user by ID.

**Path Parameters:**
- `id` (Long) - User ID to update

**Request Body:**
```json
{
  "name": "John Smith",
  "email": "john.smith@example.com",
  "age": 31,
  "city": "Boston"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": {
    "id": 11,
    "name": "John Smith",
    "email": "john.smith@example.com",
    "age": 31,
    "city": "Boston"
  },
  "source": "mysql",
  "executionTimeMs": 38
}
```

**Status Codes:**
- `200 OK` - User updated successfully
- `404 Not Found` - User not found
- `400 Bad Request` - Validation errors
- `500 Internal Server Error` - Database error

---

### **5. Delete User**

#### `DELETE /api/data/users/{id}`
**Purpose:** Delete a user by ID.

**Path Parameters:**
- `id` (Long) - User ID to delete

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": "User deleted successfully",
  "source": "mysql",
  "executionTimeMs": 25
}
```

**Status Codes:**
- `200 OK` - User deleted successfully
- `404 Not Found` - User not found
- `500 Internal Server Error` - Database error

---

## 🆕 **Aerospike Write Operations**

### **6. Create/Update Aerospike Record**

#### `POST /api/data/aerospike/records`
**Purpose:** Create a new record in Aerospike or update existing one.

**Request Body:**
```json
{
  "key": "user_123",
  "bins": {
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30,
    "city": "New York",
    "timestamp": "2025-09-09T15:30:00Z"
  },
  "namespace": "test",
  "set": "users"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": {
    "key": "user_123",
    "bins": {
      "name": "John Doe",
      "email": "john@example.com",
      "age": 30,
      "city": "New York",
      "timestamp": "2025-09-09T15:30:00Z"
    }
  },
  "source": "aerospike",
  "executionTimeMs": 15
}
```

**Status Codes:**
- `200 OK` - Record created/updated successfully
- `400 Bad Request` - Invalid request data
- `500 Internal Server Error` - Aerospike error

---

### **7. Update Aerospike Record**

#### `PUT /api/data/aerospike/records`
**Purpose:** Update an existing Aerospike record.

**Request Body:**
```json
{
  "key": "user_123",
  "bins": {
    "name": "John Smith",
    "email": "john.smith@example.com",
    "age": 31,
    "city": "Boston",
    "updated": true
  },
  "namespace": "test",
  "set": "users"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": {
    "key": "user_123",
    "bins": {
      "name": "John Smith",
      "email": "john.smith@example.com",
      "age": 31,
      "city": "Boston",
      "updated": true
    }
  },
  "source": "aerospike",
  "executionTimeMs": 12
}
```

**Status Codes:**
- `200 OK` - Record updated successfully
- `400 Bad Request` - Invalid request data
- `500 Internal Server Error` - Aerospike error

---

### **8. Delete Single Aerospike Record**

#### `DELETE /api/data/aerospike/records/{key}`
**Purpose:** Delete a single record by key.

**Path Parameters:**
- `key` (String) - Record key to delete

**Query Parameters:**
- `namespace` (String, optional) - Aerospike namespace (default: from config)
- `set` (String, optional) - Aerospike set (default: from config)

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": "Record deleted successfully",
  "source": "aerospike",
  "executionTimeMs": 8
}
```

**Status Codes:**
- `200 OK` - Record deleted successfully
- `404 Not Found` - Record not found
- `500 Internal Server Error` - Aerospike error

---

### **9. Delete Multiple Aerospike Records**

#### `DELETE /api/data/aerospike/records`
**Purpose:** Delete multiple records by providing a list of keys.

**Request Body:**
```json
{
  "keys": ["user_123", "user_456", "user_789"],
  "namespace": "test",
  "set": "users"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": {
    "deleted_count": 3,
    "deleted_keys": ["user_123", "user_456", "user_789"]
  },
  "source": "aerospike",
  "executionTimeMs": 25
}
```

**Status Codes:**
- `200 OK` - Records deleted successfully
- `400 Bad Request` - Invalid request data
- `500 Internal Server Error` - Aerospike error

---

## 📊 **Data Models**

### **User Model (MySQL)**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30,
  "city": "New York"
}
```

### **UserRequest Model**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30,
  "city": "New York"
}
```

### **AerospikeRequest Model**
```json
{
  "key": "user_123",
  "bins": {
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30,
    "city": "New York"
  },
  "namespace": "test",
  "set": "users"
}
```

### **DataResponse Model**
```json
{
  "success": true,
  "message": "Operation message",
  "data": { ... },
  "source": "mysql|aerospike|both",
  "executionTimeMs": 45
}
```

---

## 🔧 **Configuration**

### **Database Configuration**
- **MySQL:** `localhost:3307` (Docker container)
- **Aerospike:** `localhost:3004` (Docker container)
- **Application:** `localhost:8080` with context path `/api`

### **Docker Services**
- **MySQL Container:** `java-data-api-mysql` (Port 3307)
- **Aerospike Container:** `java-data-api-aerospike` (Port 3004)

---

## 🚀 **Quick Start Commands**

### **Start the Application**
```bash
# Start databases
docker-compose up -d mysql aerospike

# Start application
mvn spring-boot:run
```

### **Test Health Check**
```bash
curl http://localhost:8080/api/data/health
```

### **Create a User**
```bash
curl -X POST http://localhost:8080/api/data/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "age": 25,
    "city": "Test City"
  }'
```

### **Query All Users**
```bash
curl -X POST http://localhost:8080/api/data/query \
  -H "Content-Type: application/json" \
  -d '{
    "queryType": "mysql",
    "queryParameter": "all_users"
  }'
```

---

## 📝 **Notes**

1. **Context Path:** All endpoints are prefixed with `/api`
2. **Content-Type:** Use `application/json` for all POST/PUT requests
3. **Validation:** Request validation is applied to all write operations
4. **Error Handling:** All endpoints return consistent error response format
5. **Execution Time:** All responses include execution time in milliseconds
6. **Database:** MySQL and Aerospike are running in Docker containers

---

*Last Updated: September 9, 2025*
