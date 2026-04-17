package com.example.datapi.controller;

import com.example.datapi.model.DataRequest;
import com.example.datapi.model.DataResponse;
import com.example.datapi.model.UserRequest;
import com.example.datapi.model.AerospikeRequest;
import com.example.datapi.service.DataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "*")
public class DataController {
    
    @Autowired
    private DataService dataService;
    
    /**
     * POST endpoint to retrieve data from MySQL and/or Aerospike
     * 
     * @param request DataRequest containing query parameters
     * @return DataResponse with the requested data
     */
    @PostMapping("/query")
    public ResponseEntity<DataResponse> queryData(@Valid @RequestBody DataRequest request) {
        try {
            DataResponse response = dataService.processDataRequest(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET endpoint to check database health
     * 
     * @return DataResponse with health status
     */
    @GetMapping("/health")
    public ResponseEntity<DataResponse> getHealth() {
        try {
            DataResponse response = dataService.getHealthStatus();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Health check failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET endpoint to retrieve all users from MySQL
     * 
     * @return DataResponse with all users
     */
    @GetMapping("/users")
    public ResponseEntity<DataResponse> getAllUsers() {
        try {
            DataRequest request = new DataRequest("mysql", "all_users", null, null, null);
            DataResponse response = dataService.processDataRequest(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Failed to retrieve users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET endpoint to retrieve all records from Aerospike
     * 
     * @return DataResponse with all Aerospike records
     */
    @GetMapping("/aerospike/records")
    public ResponseEntity<DataResponse> getAllAerospikeRecords() {
        try {
            DataRequest request = new DataRequest("aerospike", "scan_all", null, null, null);
            DataResponse response = dataService.processDataRequest(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Failed to retrieve Aerospike records: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET endpoint to retrieve a specific user by email
     * 
     * @param email User's email address
     * @return DataResponse with user data
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<DataResponse> getUserByEmail(@PathVariable String email) {
        try {
            DataRequest request = new DataRequest("mysql", "by_email", null, null, email);
            DataResponse response = dataService.processDataRequest(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Failed to retrieve user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET endpoint to retrieve a specific Aerospike record by key
     * 
     * @param key Aerospike record key
     * @return DataResponse with record data
     */
    @GetMapping("/aerospike/record/{key}")
    public ResponseEntity<DataResponse> getAerospikeRecord(@PathVariable String key) {
        try {
            DataRequest request = new DataRequest("aerospike", "get_record", null, null, key);
            DataResponse response = dataService.processDataRequest(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Failed to retrieve Aerospike record: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== MYSQL WRITE OPERATIONS ====================
    
    /**
     * POST endpoint to create a new user in MySQL
     * 
     * @param userRequest UserRequest containing user data
     * @return DataResponse with the created user
     */
    @PostMapping("/users")
    public ResponseEntity<DataResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            DataResponse response = dataService.createUser(userRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT endpoint to update an existing user in MySQL
     * 
     * @param id User ID to update
     * @param userRequest UserRequest containing updated user data
     * @return DataResponse with the updated user
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<DataResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        try {
            DataResponse response = dataService.updateUser(id, userRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE endpoint to delete a user from MySQL
     * 
     * @param id User ID to delete
     * @return DataResponse with deletion status
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<DataResponse> deleteUser(@PathVariable Long id) {
        try {
            DataResponse response = dataService.deleteUser(id);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== AEROSPIKE WRITE OPERATIONS ====================
    
    /**
     * POST endpoint to create a new record in Aerospike
     * 
     * @param aerospikeRequest AerospikeRequest containing record data
     * @return DataResponse with the created record
     */
    @PostMapping("/aerospike/records")
    public ResponseEntity<DataResponse> createAerospikeRecord(@Valid @RequestBody AerospikeRequest aerospikeRequest) {
        try {
            DataResponse response = dataService.createAerospikeRecord(aerospikeRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT endpoint to update an existing record in Aerospike
     * 
     * @param aerospikeRequest AerospikeRequest containing updated record data
     * @return DataResponse with the updated record
     */
    @PutMapping("/aerospike/records")
    public ResponseEntity<DataResponse> updateAerospikeRecord(@Valid @RequestBody AerospikeRequest aerospikeRequest) {
        try {
            DataResponse response = dataService.updateAerospikeRecord(aerospikeRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE endpoint to delete a record from Aerospike
     * 
     * @param key Record key to delete
     * @return DataResponse with deletion status
     */
    @DeleteMapping("/aerospike/records/{key}")
    public ResponseEntity<DataResponse> deleteAerospikeRecord(@PathVariable String key) {
        try {
            DataResponse response = dataService.deleteAerospikeRecord(key);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE endpoint to delete multiple records from Aerospike
     * 
     * @param keys List of record keys to delete
     * @return DataResponse with deletion status
     */
    @DeleteMapping("/aerospike/records")
    public ResponseEntity<DataResponse> deleteAerospikeRecords(@RequestBody java.util.List<String> keys) {
        try {
            DataResponse response = dataService.deleteAerospikeRecords(keys);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            DataResponse errorResponse = DataResponse.error("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
