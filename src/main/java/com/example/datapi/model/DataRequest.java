package com.example.datapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DataRequest {
    
    @NotBlank(message = "Query type is required")
    private String queryType;
    
    @NotBlank(message = "Query parameter is required")
    private String queryParameter;
    
    private String namespace;
    private String set;
    private String key;
    
    // Default constructor
    public DataRequest() {}
    
    // Constructor with parameters
    public DataRequest(String queryType, String queryParameter, String namespace, String set, String key) {
        this.queryType = queryType;
        this.queryParameter = queryParameter;
        this.namespace = namespace;
        this.set = set;
        this.key = key;
    }
    
    // Getters and Setters
    public String getQueryType() {
        return queryType;
    }
    
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    
    public String getQueryParameter() {
        return queryParameter;
    }
    
    public void setQueryParameter(String queryParameter) {
        this.queryParameter = queryParameter;
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
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        return "DataRequest{" +
                "queryType='" + queryType + '\'' +
                ", queryParameter='" + queryParameter + '\'' +
                ", namespace='" + namespace + '\'' +
                ", set='" + set + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
