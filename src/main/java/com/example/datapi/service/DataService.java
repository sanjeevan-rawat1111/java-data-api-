package com.example.datapi.service;

import com.example.datapi.model.DataRequest;
import com.example.datapi.model.DataResponse;
import com.example.datapi.model.User;
import com.example.datapi.model.UserRequest;
import com.example.datapi.model.AerospikeRequest;
import com.example.datapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AerospikeService aerospikeService;
    
    /**
     * Process data request and return data from appropriate source
     */
    public DataResponse processDataRequest(DataRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String queryType = request.getQueryType().toLowerCase();
            Object data = null;
            String source = null;
            
            switch (queryType) {
                case "mysql":
                    data = processMysqlQuery(request);
                    source = "mysql";
                    break;
                case "aerospike":
                    data = processAerospikeQuery(request);
                    source = "aerospike";
                    break;
                case "both":
                    data = processBothQueries(request);
                    source = "both";
                    break;
                default:
                    return DataResponse.error("Invalid query type. Supported types: mysql, aerospike, both");
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            return DataResponse.success(data, source, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error processing request: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Process MySQL queries
     */
    private Object processMysqlQuery(DataRequest request) {
        String queryParam = request.getQueryParameter().toLowerCase();
        
        switch (queryParam) {
            case "all_users":
                return userRepository.findAll();
            case "by_email":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Email parameter is required for email query");
                }
                Optional<User> user = userRepository.findByEmail(request.getKey());
                return user.orElse(null);
            case "by_name":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Name parameter is required for name query");
                }
                return userRepository.findByNameContainingIgnoreCase(request.getKey());
            case "by_city":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("City parameter is required for city query");
                }
                return userRepository.findByCity(request.getKey());
            case "by_age_range":
                // Assuming key contains "minAge,maxAge" format
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Age range parameter is required (format: minAge,maxAge)");
                }
                String[] ageRange = request.getKey().split(",");
                if (ageRange.length != 2) {
                    throw new IllegalArgumentException("Invalid age range format. Use: minAge,maxAge");
                }
                Integer minAge = Integer.parseInt(ageRange[0]);
                Integer maxAge = Integer.parseInt(ageRange[1]);
                return userRepository.findByAgeBetween(minAge, maxAge);
            case "count_by_city":
                return userRepository.getUserCountByCity();
            case "by_email_domain":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Email domain parameter is required");
                }
                return userRepository.findUsersByEmailDomain(request.getKey());
            default:
                throw new IllegalArgumentException("Unsupported MySQL query parameter: " + queryParam);
        }
    }
    
    /**
     * Process Aerospike queries
     */
    private Object processAerospikeQuery(DataRequest request) {
        String queryParam = request.getQueryParameter().toLowerCase();
        
        switch (queryParam) {
            case "get_record":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Key parameter is required for get_record query");
                }
                return aerospikeService.getRecord(request.getKey());
            case "get_records":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Keys parameter is required for get_records query (comma-separated)");
                }
                List<String> keys = List.of(request.getKey().split(","));
                return aerospikeService.getRecords(keys);
            case "scan_all":
                return aerospikeService.scanAllRecords();
            case "search_by_bin":
                if (request.getKey() == null) {
                    throw new IllegalArgumentException("Bin name and value are required (format: binName,value)");
                }
                String[] binParams = request.getKey().split(",", 2);
                if (binParams.length != 2) {
                    throw new IllegalArgumentException("Invalid bin search format. Use: binName,value");
                }
                return aerospikeService.searchByBinValue(binParams[0], binParams[1]);
            case "count":
                return Map.of("count", aerospikeService.getRecordCount());
            case "health":
                return Map.of("healthy", aerospikeService.isHealthy());
            default:
                throw new IllegalArgumentException("Unsupported Aerospike query parameter: " + queryParam);
        }
    }
    
    /**
     * Process both MySQL and Aerospike queries
     */
    private Object processBothQueries(DataRequest request) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            Object mysqlData = processMysqlQuery(request);
            result.put("mysql", mysqlData);
        } catch (Exception e) {
            result.put("mysql_error", e.getMessage());
        }
        
        try {
            Object aerospikeData = processAerospikeQuery(request);
            result.put("aerospike", aerospikeData);
        } catch (Exception e) {
            result.put("aerospike_error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get database health status
     */
    public DataResponse getHealthStatus() {
        Map<String, Object> health = new java.util.HashMap<>();
        
        // Check MySQL health
        try {
            long count = userRepository.count();
            health.put("mysql", Map.of("healthy", true, "record_count", count));
        } catch (Exception e) {
            health.put("mysql", Map.of("healthy", false, "error", e.getMessage()));
        }
        
        // Check Aerospike health
        try {
            boolean aerospikeHealthy = aerospikeService.isHealthy();
            long aerospikeCount = aerospikeService.getRecordCount();
            health.put("aerospike", Map.of("healthy", aerospikeHealthy, "record_count", aerospikeCount));
        } catch (Exception e) {
            health.put("aerospike", Map.of("healthy", false, "error", e.getMessage()));
        }
        
        return DataResponse.success("Health status retrieved", health, "both", 0);
    }
    
    /**
     * Create a new user in MySQL
     */
    public DataResponse createUser(UserRequest userRequest) {
        long startTime = System.currentTimeMillis();
        
        try {
            User user = new User(
                userRequest.getName(),
                userRequest.getEmail(),
                userRequest.getAge(),
                userRequest.getCity()
            );
            
            User savedUser = userRepository.save(user);
            long executionTime = System.currentTimeMillis() - startTime;
            
            return DataResponse.success(savedUser, "mysql", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error creating user: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Update an existing user in MySQL
     */
    public DataResponse updateUser(Long id, UserRequest userRequest) {
        long startTime = System.currentTimeMillis();
        
        try {
            User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            
            existingUser.setName(userRequest.getName());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setAge(userRequest.getAge());
            existingUser.setCity(userRequest.getCity());
            
            User updatedUser = userRepository.save(existingUser);
            long executionTime = System.currentTimeMillis() - startTime;
            
            return DataResponse.success(updatedUser, "mysql", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error updating user: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Delete a user from MySQL
     */
    public DataResponse deleteUser(Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (!userRepository.existsById(id)) {
                return DataResponse.error("User not found with id: " + id);
            }
            
            userRepository.deleteById(id);
            long executionTime = System.currentTimeMillis() - startTime;
            
            Map<String, Object> result = Map.of(
                "id", id,
                "message", "User deleted successfully"
            );
            
            return DataResponse.success(result, "mysql", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error deleting user: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Create a record in Aerospike
     */
    public DataResponse createAerospikeRecord(AerospikeRequest aerospikeRequest) {
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> result = aerospikeService.putRecord(
                aerospikeRequest.getKey(),
                aerospikeRequest.getBins()
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            return DataResponse.success(result, "aerospike", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error creating Aerospike record: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Update a record in Aerospike
     */
    public DataResponse updateAerospikeRecord(AerospikeRequest aerospikeRequest) {
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> result = aerospikeService.updateRecord(
                aerospikeRequest.getKey(),
                aerospikeRequest.getBins()
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            return DataResponse.success(result, "aerospike", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error updating Aerospike record: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Delete a record from Aerospike
     */
    public DataResponse deleteAerospikeRecord(String key) {
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> result = aerospikeService.deleteRecord(key);
            long executionTime = System.currentTimeMillis() - startTime;
            
            return DataResponse.success(result, "aerospike", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error deleting Aerospike record: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    /**
     * Delete multiple records from Aerospike
     */
    public DataResponse deleteAerospikeRecords(List<String> keys) {
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> result = aerospikeService.deleteRecords(keys);
            long executionTime = System.currentTimeMillis() - startTime;
            
            return DataResponse.success(result, "aerospike", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DataResponse errorResponse = DataResponse.error("Error deleting Aerospike records: " + e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
}
