# Java Data API

A Spring Boot REST API application that retrieves data from both MySQL and Aerospike databases.

## Features

- **REST API** with POST and GET endpoints
- **MySQL Integration** using Spring Data JPA
- **Aerospike Integration** with custom service layer
- **Flexible Query System** supporting multiple query types
- **Health Check** endpoints for database connectivity
- **Comprehensive Error Handling** with detailed error messages
- **Performance Monitoring** with execution time tracking

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Aerospike Server 6.0 or higher
- Docker (optional, for containerized deployment)

## Quick Start

### 1. Database Setup

#### MySQL Setup
```sql
-- Create database
CREATE DATABASE mydatabase;

-- Run the schema script
mysql -u root -p mydatabase < src/main/resources/sql/schema.sql
```

#### Aerospike Setup
```bash
# Start Aerospike server (using Docker)
docker run -d --name aerospike -p 3000:3000 aerospike/aerospike-server

# Or install locally following Aerospike documentation
```

### 2. Configuration

Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password

aerospike:
  hosts:
    - host: localhost
      port: 3000
  namespace: test
  set: users
```

### 3. Build and Run

```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar target/java-data-api-1.0.0.jar
```

The API will be available at `http://localhost:8080/api`

## API Endpoints

### POST /api/data/query

Main endpoint for querying data from MySQL and/or Aerospike.

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

**MySQL Query Parameters:**
- `all_users` - Get all users
- `by_email` - Get user by email (requires key)
- `by_name` - Search users by name (requires key)
- `by_city` - Get users by city (requires key)
- `by_age_range` - Get users by age range (requires key in format "minAge,maxAge")
- `count_by_city` - Get user count grouped by city
- `by_email_domain` - Get users by email domain (requires key)

**Aerospike Query Parameters:**
- `get_record` - Get single record by key (requires key)
- `get_records` - Get multiple records by keys (requires key as comma-separated list)
- `scan_all` - Scan all records in the set
- `search_by_bin` - Search by bin value (requires key in format "binName,value")
- `count` - Get total record count
- `health` - Check Aerospike health

### GET Endpoints

- `GET /api/data/health` - Check database health status
- `GET /api/data/users` - Get all users from MySQL
- `GET /api/data/users/email/{email}` - Get user by email
- `GET /api/data/aerospike/records` - Get all Aerospike records
- `GET /api/data/aerospike/record/{key}` - Get specific Aerospike record

## Example Usage

### Get all users from MySQL
```bash
curl -X POST http://localhost:8080/api/data/query \
  -H "Content-Type: application/json" \
  -d '{
    "queryType": "mysql",
    "queryParameter": "all_users"
  }'
```

### Get user by email
```bash
curl -X POST http://localhost:8080/api/data/query \
  -H "Content-Type: application/json" \
  -d '{
    "queryType": "mysql",
    "queryParameter": "by_email",
    "key": "john.doe@example.com"
  }'
```

### Get Aerospike record
```bash
curl -X POST http://localhost:8080/api/data/query \
  -H "Content-Type: application/json" \
  -d '{
    "queryType": "aerospike",
    "queryParameter": "get_record",
    "key": "user123"
  }'
```

### Query both databases
```bash
curl -X POST http://localhost:8080/api/data/query \
  -H "Content-Type: application/json" \
  -d '{
    "queryType": "both",
    "queryParameter": "all_users"
  }'
```

## Response Format

All responses follow this format:

```json
{
  "success": true,
  "message": "Data retrieved successfully",
  "data": { ... },
  "source": "mysql|aerospike|both",
  "executionTimeMs": 150
}
```

## Error Handling

The API provides detailed error messages for:
- Invalid query parameters
- Database connection issues
- Missing required parameters
- Data validation errors

## Development

### Project Structure
```
src/main/java/com/example/datapi/
├── DataApiApplication.java          # Main application class
├── config/
│   └── AerospikeConfig.java         # Aerospike configuration
├── controller/
│   └── DataController.java          # REST controllers
├── model/
│   ├── User.java                    # User entity
│   ├── DataRequest.java             # Request model
│   └── DataResponse.java            # Response model
├── repository/
│   └── UserRepository.java          # MySQL repository
└── service/
    ├── DataService.java             # Main business logic
    └── AerospikeService.java        # Aerospike operations
```

### Adding New Query Types

1. Add new query parameter handling in `DataService.java`
2. Implement corresponding methods in `UserRepository.java` or `AerospikeService.java`
3. Update API documentation

## Testing

### Unit Tests
```bash
# Run unit tests
mvn test

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

### API Testing

API tests for this service are AI-generated by [java-data-api-testgen](https://github.com/sanjeevan-rawat1111/java-data-api-testgen) — an LLM-powered pipeline that reads this codebase, generates Postman collections with real setup/teardown, and runs them via Newman.

## Monitoring

The application includes:
- Execution time tracking for all queries
- Health check endpoints
- Comprehensive logging
- Error tracking and reporting

## License

This project is licensed under the MIT License.
