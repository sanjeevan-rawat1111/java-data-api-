# Java Data API - Test Automation Suite

This directory contains a comprehensive test automation suite for the Java Data API, including Postman collections, Newman test runner, and database setup scripts.

## 📁 Directory Structure

```
tests/
├── postman/                          # Postman collections and environment
│   ├── Java-Data-API-Tests.postman_collection.json
│   └── Java-Data-API-Environment.postman_environment.json
├── newman/                           # Newman test runner and reports
│   ├── run-tests.sh
│   ├── custom-template.hbs
│   └── reports/                      # Generated test reports
├── database/                         # Database setup and cleanup scripts
│   ├── setup-test-data.sql
│   ├── setup-aerospike-test-data.js
│   └── clean-aerospike-test-data.js
├── scripts/                          # Test runner scripts
│   └── run-all-tests.sh
├── docker-compose.test.yml           # Docker Compose for test environment
└── package.json                      # Test dependencies and scripts
```

## 🚀 Quick Start

### Prerequisites

1. **Node.js and npm** - For running Newman and test scripts
2. **Java Data API running** - Application should be running on `http://localhost:8080`
3. **MySQL and Aerospike** - Databases should be running and accessible

### Running Tests

#### Option 1: Complete Test Suite (Recommended)
```bash
cd tests
chmod +x scripts/run-all-tests.sh
./scripts/run-all-tests.sh
```

#### Option 2: Individual Test Commands
```bash
cd tests

# Install dependencies
npm install

# Setup test data
npm run setup:all

# Run tests
npm run test:ci

# Cleanup test data
npm run clean:all
```

#### Option 3: Using Docker Compose
```bash
cd tests
docker-compose -f docker-compose.test.yml up --build
```

## 📋 Test Coverage

### API Endpoints Tested

#### Health Check
- ✅ GET `/api/data/health` - Database health status

#### Data Query Operations
- ✅ POST `/api/data/query` - MySQL queries (all_users, count)
- ✅ POST `/api/data/query` - Aerospike queries (all_records)

#### MySQL Write Operations
- ✅ POST `/api/data/users` - Create user
- ✅ PUT `/api/data/users/{id}` - Update user
- ✅ DELETE `/api/data/users/{id}` - Delete user

#### Aerospike Write Operations
- ✅ POST `/api/data/aerospike/records` - Create record
- ✅ PUT `/api/data/aerospike/records` - Update record
- ✅ DELETE `/api/data/aerospike/records/{key}` - Delete record

#### Error Handling
- ✅ Invalid query types
- ✅ Validation errors
- ✅ Database connection errors

## 🗄️ Database Testing

### MySQL Test Data
- Test users with various data patterns
- Validation of CRUD operations
- Data integrity checks

### Aerospike Test Data
- Test records with different bin structures
- Namespace and set operations
- Record lifecycle management

## 📊 Test Reports

Test reports are generated in HTML format and saved to:
- `newman/reports/test-report.html` - Detailed HTML report
- Console output - Real-time test results

## 🔧 Configuration

### Environment Variables
- `baseUrl` - API base URL (default: http://localhost:8080/api)
- `mysqlHost` - MySQL host and port (default: localhost:3307)
- `aerospikeHost` - Aerospike host and port (default: localhost:3004)

### Test Data Configuration
- MySQL database: `mydatabase`
- Aerospike namespace: `test`
- Aerospike set: `users`

## 🛠️ Customization

### Adding New Tests
1. Edit `postman/Java-Data-API-Tests.postman_collection.json`
2. Add new request items with test scripts
3. Update environment variables if needed

### Modifying Test Data
1. Edit `database/setup-test-data.sql` for MySQL
2. Edit `database/setup-aerospike-test-data.js` for Aerospike
3. Update cleanup scripts accordingly

### Custom Report Template
- Edit `newman/custom-template.hbs` for custom HTML reports
- Modify `newman/run-tests.sh` for different report formats

## 🐛 Troubleshooting

### Common Issues

#### Application Not Running
```bash
# Start the application
mvn spring-boot:run
```

#### Database Connection Issues
```bash
# Check MySQL connection
mysql -h localhost -P 3307 -u root -ppassword -e "SELECT 1;"

# Check Aerospike connection
telnet localhost 3004
```

#### Newman Not Found
```bash
# Install Newman globally
npm install -g newman newman-reporter-html
```

#### Test Data Issues
```bash
# Clean and reset test data
npm run clean:all
npm run setup:all
```

## 📈 Continuous Integration

### GitHub Actions Example
```yaml
name: API Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Java
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Start Application
        run: mvn spring-boot:run &
      - name: Wait for Application
        run: sleep 30
      - name: Run Tests
        run: |
          cd tests
          npm install
          npm run test:ci
```

## 📚 Additional Resources

- [Newman Documentation](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/)
- [Postman Testing](https://learning.postman.com/docs/writing-scripts/test-scripts/)
- [Aerospike Node.js Client](https://www.aerospike.com/docs/client/nodejs/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add your tests to the Postman collection
4. Update test data scripts if needed
5. Run the test suite to ensure everything works
6. Submit a pull request

## 📄 License

This test suite is part of the Java Data API project and follows the same license terms.
