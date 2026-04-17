#!/bin/bash

# Complete Test Suite Runner for Java Data API
# This script runs the entire test suite including database setup and cleanup

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 Java Data API - Complete Test Suite${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""

# Check if we're in the tests directory
if [ ! -f "package.json" ]; then
    echo -e "${RED}❌ Please run this script from the tests directory${NC}"
    exit 1
fi

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check dependencies
echo -e "${YELLOW}🔍 Checking dependencies...${NC}"

if ! command_exists node; then
    echo -e "${RED}❌ Node.js is not installed. Please install it first.${NC}"
    exit 1
fi

if ! command_exists npm; then
    echo -e "${RED}❌ npm is not installed. Please install it first.${NC}"
    exit 1
fi

if ! command_exists newman; then
    echo -e "${YELLOW}📦 Installing Newman...${NC}"
    npm install -g newman newman-reporter-html
fi

echo -e "${GREEN}✅ Dependencies check passed${NC}"
echo ""

# Install test dependencies
echo -e "${YELLOW}📦 Installing test dependencies...${NC}"
npm install
echo -e "${GREEN}✅ Dependencies installed${NC}"
echo ""

# Check if application is running
echo -e "${YELLOW}🔍 Checking if application is running...${NC}"
if curl -s http://localhost:8080/api/data/health > /dev/null; then
    echo -e "${GREEN}✅ Application is running${NC}"
else
    echo -e "${RED}❌ Application is not running. Please start it first.${NC}"
    echo -e "${YELLOW}   Run: mvn spring-boot:run${NC}"
    exit 1
fi
echo ""

# Setup test data
echo -e "${YELLOW}🗄️  Setting up test data...${NC}"
npm run setup:all
echo -e "${GREEN}✅ Test data setup completed${NC}"
echo ""

# Run tests
echo -e "${YELLOW}🧪 Running test suite...${NC}"
npm run test:ci

# Check test results
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ All tests passed successfully!${NC}"
    echo -e "${GREEN}📊 Test report generated in newman/reports/test-report.html${NC}"
else
    echo ""
    echo -e "${RED}❌ Some tests failed. Check the output above for details.${NC}"
    echo -e "${GREEN}📊 Test report generated in newman/reports/test-report.html${NC}"
fi

# Cleanup test data
echo ""
echo -e "${YELLOW}🧹 Cleaning up test data...${NC}"
npm run clean:all
echo -e "${GREEN}✅ Test data cleanup completed${NC}"

echo ""
echo -e "${BLUE}🎉 Test suite execution completed!${NC}"
