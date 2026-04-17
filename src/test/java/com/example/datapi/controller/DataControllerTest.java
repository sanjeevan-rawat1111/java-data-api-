package com.example.datapi.controller;

import com.example.datapi.model.DataRequest;
import com.example.datapi.model.DataResponse;
import com.example.datapi.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataController.class)
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testQueryData_Success() throws Exception {
        // Given
        DataRequest request = new DataRequest("mysql", "all_users", null, null, null);
        DataResponse response = DataResponse.success("Test data", "mysql", 100L);
        
        when(dataService.processDataRequest(any(DataRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/data/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.source").value("mysql"));
    }

    @Test
    void testQueryData_ValidationError() throws Exception {
        // Given
        DataRequest request = new DataRequest("", "", null, null, null);

        // When & Then
        mockMvc.perform(post("/data/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHealth_Success() throws Exception {
        // Given
        DataResponse response = DataResponse.success("Health check", null, "both", 0L);
        when(dataService.getHealthStatus()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/data/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        // Given
        DataResponse response = DataResponse.success("Users retrieved", null, "mysql", 50L);
        when(dataService.processDataRequest(any(DataRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/data/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetUserByEmail_Success() throws Exception {
        // Given
        String email = "test@example.com";
        DataResponse response = DataResponse.success("User found", null, "mysql", 25L);
        when(dataService.processDataRequest(any(DataRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/data/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAerospikeRecord_Success() throws Exception {
        // Given
        String key = "test-key";
        DataResponse response = DataResponse.success("Record found", null, "aerospike", 30L);
        when(dataService.processDataRequest(any(DataRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/data/aerospike/record/{key}", key))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
