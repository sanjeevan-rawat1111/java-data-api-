package com.example.datapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class AerospikeRequest {
    
    @NotBlank(message = "Key is required")
    private String key;
    
    @NotNull(message = "Bins data is required")
    private Map<String, Object> bins;
    
    private String namespace;
    private String set;
    
    // Default constructor
    public AerospikeRequest() {}
    
    // Constructor with parameters
    public AerospikeRequest(String key, Map<String, Object> bins, String namespace, String set) {
        this.key = key;
        this.bins = bins;
        this.namespace = namespace;
        this.set = set;
    }
    
    // Getters and Setters
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Map<String, Object> getBins() {
        return bins;
    }
    
    public void setBins(Map<String, Object> bins) {
        this.bins = bins;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getSet() {
        return set;
    }
    
    public void setSet(String set) {
        this.set = set;
    }
    
    @Override
    public String toString() {
        return "AerospikeRequest{" +
                "key='" + key + '\'' +
                ", bins=" + bins +
                ", namespace='" + namespace + '\'' +
                ", set='" + set + '\'' +
                '}';
    }
}
