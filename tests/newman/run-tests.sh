#!/bin/bash

# Newman Test Runner Script for Java Data API
# This script runs the Postman collection tests using Newman

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
COLLECTION_FILE="../postman/Java-Data-API-Tests.postman_collection.json"
ENVIRONMENT_FILE="../postman/Java-Data-API-Environment.postman_environment.json"
REPORTS_DIR="./reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create reports directory if it doesn't exist
mkdir -p "$REPORTS_DIR"

echo -e "${BLUE}🧪 Java Data API Test Suite${NC}"
echo -e "${BLUE}==============================${NC}"
echo ""

# Check if Newman is installed
if ! command -v newman &> /dev/null; then
    echo -e "${RED}❌ Newman is not installed. Please install it first:${NC}"
    echo -e "${YELLOW}   npm install -g newman${NC}"
    echo -e "${YELLOW}   npm install -g newman-reporter-html${NC}"
    exit 1
fi

# Check if collection file exists
if [ ! -f "$COLLECTION_FILE" ]; then
    echo -e "${RED}❌ Collection file not found: $COLLECTION_FILE${NC}"
    exit 1
fi

# Check if environment file exists
if [ ! -f "$ENVIRONMENT_FILE" ]; then
    echo -e "${RED}❌ Environment file not found: $ENVIRONMENT_FILE${NC}"
    exit 1
fi

echo -e "${YELLOW}📋 Running test collection...${NC}"
echo -e "${YELLOW}Collection: $COLLECTION_FILE${NC}"
echo -e "${YELLOW}Environment: $ENVIRONMENT_FILE${NC}"
echo ""

# Run Newman tests
newman run "$COLLECTION_FILE" \
    --environment "$ENVIRONMENT_FILE" \
    --reporters cli,html \
    --reporter-html-export "$REPORTS_DIR/test-report-$TIMESTAMP.html" \
    --reporter-html-template "$REPORTS_DIR/custom-template.hbs" \
    --delay-request 1000 \
    --timeout-request 30000 \
    --timeout-script 30000 \
    --bail

# Check exit code
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ All tests passed successfully!${NC}"
    echo -e "${GREEN}📊 HTML report generated: $REPORTS_DIR/test-report-$TIMESTAMP.html${NC}"
else
    echo ""
    echo -e "${RED}❌ Some tests failed. Check the output above for details.${NC}"
    echo -e "${GREEN}📊 HTML report generated: $REPORTS_DIR/test-report-$TIMESTAMP.html${NC}"
    exit 1
fi
