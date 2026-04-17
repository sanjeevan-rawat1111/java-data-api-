package com.example.datapi.model;

import java.util.List;
import java.util.Map;

public class DataResponse {
    
    private boolean success;
    private String message;
    private Object data;
    private String source; // "mysql" or "aerospike"
    private long executionTimeMs;
    
    // Default constructor
    public DataResponse() {}
    
    // Constructor with parameters
    public DataResponse(boolean success, String message, Object data, String source, long executionTimeMs) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.source = source;
        this.executionTimeMs = executionTimeMs;
    }
    
    // Static factory methods for success responses
    public static DataResponse success(Object data, String source, long executionTimeMs) {
        return new DataResponse(true, "Data retrieved successfully", data, source, executionTimeMs);
    }
    
    public static DataResponse success(String message, Object data, String source, long executionTimeMs) {
        return new DataResponse(true, message, data, source, executionTimeMs);
    }
    
    // Static factory methods for error responses
    public static DataResponse error(String message) {
        return new DataResponse(false, message, null, null, 0);
    }
    
    public static DataResponse error(String message, String source) {
        return new DataResponse(false, message, null, source, 0);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    @Override
    public String toString() {
        return "DataResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", source='" + source + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
