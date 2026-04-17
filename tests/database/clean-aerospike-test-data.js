// Aerospike Test Data Cleanup Script
// This script removes test data from Aerospike

const Aerospike = require('aerospike');

// Aerospike configuration
const config = {
  hosts: 'localhost:3004',
  namespace: 'test',
  set: 'users'
};

// Test record keys to remove
const testRecordKeys = [
  'test_user_1',
  'test_user_2',
  'test_user_3',
  'updated_test_user',
  'api_test_user'
];

async function cleanupTestData() {
  let client;
  
  try {
    // Connect to Aerospike
    console.log('Connecting to Aerospike...');
    client = Aerospike.client(config);
    await client.connect();
    console.log('Connected to Aerospike successfully');

    // Remove test records
    console.log('Removing test records...');
    let removedCount = 0;
    
    for (const key of testRecordKeys) {
      try {
        await client.remove(key);
        console.log(`Removed record: ${key}`);
        removedCount++;
      } catch (error) {
        console.log(`Record not found or already removed: ${key}`);
      }
    }

    console.log(`Successfully removed ${removedCount} test records from Aerospike`);

  } catch (error) {
    console.error('Error cleaning up test data:', error);
    process.exit(1);
  } finally {
    if (client) {
      await client.close();
      console.log('Disconnected from Aerospike');
    }
  }
}

// Run the cleanup
cleanupTestData();
