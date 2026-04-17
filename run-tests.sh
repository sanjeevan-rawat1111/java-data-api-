#!/bin/bash

# Main Test Runner for Java Data API
# This script runs the complete test suite from the project root

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 Java Data API - Test Automation Suite${NC}"
echo -e "${BLUE}==========================================${NC}"
echo ""

# Check if we're in the project root
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ Please run this script from the project root directory${NC}"
    exit 1
fi

# Check if tests directory exists
if [ ! -d "tests" ]; then
    echo -e "${RED}❌ Tests directory not found. Please ensure the test suite is properly set up.${NC}"
    exit 1
fi

# Check if application is running
echo -e "${YELLOW}🔍 Checking if application is running...${NC}"
if curl -s http://localhost:8080/api/data/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Application is running${NC}"
else
    echo -e "${YELLOW}⚠️  Application is not running. Starting it now...${NC}"
    echo -e "${YELLOW}   This may take a few moments...${NC}"
    
    # Start the application in the background
    mvn spring-boot:run > application.log 2>&1 &
    APP_PID=$!
    
    # Wait for application to start
    echo -e "${YELLOW}   Waiting for application to start...${NC}"
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/data/health > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Application started successfully${NC}"
            break
        fi
        if [ $i -eq 30 ]; then
            echo -e "${RED}❌ Application failed to start within 30 seconds${NC}"
            echo -e "${YELLOW}   Check application.log for details${NC}"
            kill $APP_PID 2>/dev/null || true
            exit 1
        fi
        sleep 1
    done
fi
echo ""

# Run the test suite
echo -e "${YELLOW}🧪 Running test suite...${NC}"
cd tests
./scripts/run-all-tests.sh

# Check test results
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}🎉 All tests completed successfully!${NC}"
    echo -e "${GREEN}📊 Test report: tests/newman/reports/test-report.html${NC}"
else
    echo ""
    echo -e "${RED}❌ Some tests failed. Check the output above for details.${NC}"
    echo -e "${GREEN}📊 Test report: tests/newman/reports/test-report.html${NC}"
fi

# Return to project root
cd ..

echo ""
echo -e "${BLUE}🏁 Test automation completed!${NC}"
