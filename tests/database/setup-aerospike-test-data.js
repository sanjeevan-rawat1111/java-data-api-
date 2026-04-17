// Aerospike Test Data Setup Script
// This script sets up test data in Aerospike for API testing

const Aerospike = require('aerospike');

// Aerospike configuration
const config = {
  hosts: 'localhost:3004',
  namespace: 'test',
  set: 'users'
};

// Test data to insert
const testRecords = [
  {
    key: 'test_user_1',
    bins: {
      name: 'Test User 1',
      email: 'testuser1@example.com',
      age: 25,
      city: 'Test City 1',
      created_at: new Date().toISOString(),
      type: 'test_user'
    }
  },
  {
    key: 'test_user_2',
    bins: {
      name: 'Test User 2',
      email: 'testuser2@example.com',
      age: 30,
      city: 'Test City 2',
      created_at: new Date().toISOString(),
      type: 'test_user'
    }
  },
  {
    key: 'test_user_3',
    bins: {
      name: 'Test User 3',
      email: 'testuser3@example.com',
      age: 35,
      city: 'Test City 3',
      created_at: new Date().toISOString(),
      type: 'test_user'
    }
  },
  {
    key: 'updated_test_user',
    bins: {
      name: 'Updated Test User',
      email: 'updated@example.com',
      age: 28,
      city: 'Updated City',
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString(),
      type: 'test_user'
    }
  },
  {
    key: 'api_test_user',
    bins: {
      name: 'API Test User',
      email: 'apitest@example.com',
      age: 22,
      city: 'API City',
      created_at: new Date().toISOString(),
      type: 'test_user'
    }
  }
];

async function setupTestData() {
  let client;
  
  try {
    // Connect to Aerospike
    console.log('Connecting to Aerospike...');
    client = Aerospike.client(config);
    await client.connect();
    console.log('Connected to Aerospike successfully');

    // Clear existing test data
    console.log('Clearing existing test data...');
    for (const record of testRecords) {
      try {
        await client.remove(record.key);
        console.log(`Removed existing record: ${record.key}`);
      } catch (error) {
        // Record might not exist, that's okay
        console.log(`No existing record to remove: ${record.key}`);
      }
    }

    // Insert test records
    console.log('Inserting test records...');
    for (const record of testRecords) {
      await client.put(record.key, record.bins);
      console.log(`Inserted record: ${record.key}`);
    }

    // Verify test data
    console.log('Verifying test data...');
    let count = 0;
    for (const record of testRecords) {
      const result = await client.get(record.key);
      if (result) {
        count++;
        console.log(`Verified record: ${record.key} - ${result.bins.name}`);
      }
    }

    console.log(`Successfully set up ${count} test records in Aerospike`);

  } catch (error) {
    console.error('Error setting up test data:', error);
    process.exit(1);
  } finally {
    if (client) {
      await client.close();
      console.log('Disconnected from Aerospike');
    }
  }
}

// Run the setup
setupTestData();
