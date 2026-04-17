package com.example.datapi.service;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Statement;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.Bin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AerospikeService {
    
    @Autowired
    private AerospikeClient aerospikeClient;
    
    @Value("${aerospike.namespace}")
    private String namespace;
    
    @Value("${aerospike.set}")
    private String set;
    
    /**
     * Get a single record by key
     */
    public Map<String, Object> getRecord(String key) {
        try {
            Key aerospikeKey = new Key(namespace, set, key);
            Record record = aerospikeClient.get(new Policy(), aerospikeKey);
            
            if (record == null) {
                return null;
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("key", key);
            result.put("bins", record.bins);
            result.put("generation", record.generation);
            result.put("expiration", record.expiration);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving record from Aerospike: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get multiple records by keys
     */
    public List<Map<String, Object>> getRecords(List<String> keys) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (String key : keys) {
            Map<String, Object> record = getRecord(key);
            if (record != null) {
                results.add(record);
            }
        }
        
        return results;
    }
    
    /**
     * Scan all records in the set
     */
    public List<Map<String, Object>> scanAllRecords() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            Statement statement = new Statement();
            statement.setNamespace(namespace);
            statement.setSetName(set);
            
            QueryPolicy queryPolicy = new QueryPolicy();
            RecordSet recordSet = aerospikeClient.query(queryPolicy, statement);
            
            while (recordSet.next()) {
                Record record = recordSet.getRecord();
                Key key = recordSet.getKey();
                
                Map<String, Object> result = new HashMap<>();
                result.put("key", key.userKey.toString());
                result.put("bins", record.bins);
                result.put("generation", record.generation);
                result.put("expiration", record.expiration);
                
                results.add(result);
            }
            
            recordSet.close();
        } catch (Exception e) {
            throw new RuntimeException("Error scanning records from Aerospike: " + e.getMessage(), e);
        }
        
        return results;
    }
    
    /**
     * Search records by bin value
     */
    public List<Map<String, Object>> searchByBinValue(String binName, String value) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            Statement statement = new Statement();
            statement.setNamespace(namespace);
            statement.setSetName(set);
            statement.setFilter(com.aerospike.client.query.Filter.equal(binName, value));
            
            QueryPolicy queryPolicy = new QueryPolicy();
            RecordSet recordSet = aerospikeClient.query(queryPolicy, statement);
            
            while (recordSet.next()) {
                Record record = recordSet.getRecord();
                Key key = recordSet.getKey();
                
                Map<String, Object> result = new HashMap<>();
                result.put("key", key.userKey.toString());
                result.put("bins", record.bins);
                result.put("generation", record.generation);
                result.put("expiration", record.expiration);
                
                results.add(result);
            }
            
            recordSet.close();
        } catch (Exception e) {
            throw new RuntimeException("Error searching records in Aerospike: " + e.getMessage(), e);
        }
        
        return results;
    }
    
    /**
     * Get record count in the set
     */
    public long getRecordCount() {
        try {
            Statement statement = new Statement();
            statement.setNamespace(namespace);
            statement.setSetName(set);
            
            QueryPolicy queryPolicy = new QueryPolicy();
            RecordSet recordSet = aerospikeClient.query(queryPolicy, statement);
            
            long count = 0;
            while (recordSet.next()) {
                count++;
            }
            
            recordSet.close();
            return count;
        } catch (Exception e) {
            throw new RuntimeException("Error counting records in Aerospike: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if Aerospike connection is healthy
     */
    public boolean isHealthy() {
        try {
            // Try to get cluster info to check connection
            aerospikeClient.getNodes();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Put a record in Aerospike
     */
    public Map<String, Object> putRecord(String key, Map<String, Object> bins) {
        try {
            Key aerospikeKey = new Key(namespace, set, key);
            WritePolicy writePolicy = new WritePolicy();
            
            // Convert Map to Bin array
            Bin[] binArray = new Bin[bins.size()];
            int index = 0;
            for (Map.Entry<String, Object> entry : bins.entrySet()) {
                binArray[index++] = new Bin(entry.getKey(), entry.getValue());
            }
            
            aerospikeClient.put(writePolicy, aerospikeKey, binArray);
            
            Map<String, Object> result = new HashMap<>();
            result.put("key", key);
            result.put("bins", bins);
            result.put("message", "Record saved successfully");
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error saving record to Aerospike: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update a record in Aerospike
     */
    public Map<String, Object> updateRecord(String key, Map<String, Object> bins) {
        try {
            Key aerospikeKey = new Key(namespace, set, key);
            WritePolicy writePolicy = new WritePolicy();
            
            // Convert Map to Bin array
            Bin[] binArray = new Bin[bins.size()];
            int index = 0;
            for (Map.Entry<String, Object> entry : bins.entrySet()) {
                binArray[index++] = new Bin(entry.getKey(), entry.getValue());
            }
            
            aerospikeClient.put(writePolicy, aerospikeKey, binArray);
            
            Map<String, Object> result = new HashMap<>();
            result.put("key", key);
            result.put("bins", bins);
            result.put("message", "Record updated successfully");
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error updating record in Aerospike: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a record from Aerospike
     */
    public Map<String, Object> deleteRecord(String key) {
        try {
            Key aerospikeKey = new Key(namespace, set, key);
            WritePolicy writePolicy = new WritePolicy();
            
            boolean existed = aerospikeClient.delete(writePolicy, aerospikeKey);
            
            Map<String, Object> result = new HashMap<>();
            result.put("key", key);
            result.put("deleted", existed);
            result.put("message", existed ? "Record deleted successfully" : "Record not found");
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting record from Aerospike: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete multiple records from Aerospike
     */
    public Map<String, Object> deleteRecords(List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        List<String> deletedKeys = new ArrayList<>();
        List<String> notFoundKeys = new ArrayList<>();
        
        for (String key : keys) {
            try {
                Map<String, Object> deleteResult = deleteRecord(key);
                if ((Boolean) deleteResult.get("deleted")) {
                    deletedKeys.add(key);
                } else {
                    notFoundKeys.add(key);
                }
            } catch (Exception e) {
                notFoundKeys.add(key);
            }
        }
        
        result.put("deletedKeys", deletedKeys);
        result.put("notFoundKeys", notFoundKeys);
        result.put("totalDeleted", deletedKeys.size());
        result.put("message", "Batch delete completed");
        
        return result;
    }
}
