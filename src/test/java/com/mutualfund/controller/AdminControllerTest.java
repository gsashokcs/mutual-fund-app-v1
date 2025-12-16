package com.mutualfund.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.model.response.UserResponse;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.service.MutualFundService;
import com.mutualfund.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
@Disabled("Controller tests disabled")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MutualFundService mutualFundService;

    @MockitoBean
    private UserService userService;

    private MutualFund testFund;
    private MutualFundRequest fundRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testFund = new MutualFund();
        testFund.setFundId(1L);
        testFund.setName("Test Fund");
        testFund.setNav(new BigDecimal("100.50"));
        testFund.setNavDate(LocalDate.now());

        fundRequest = new MutualFundRequest();
        fundRequest.setName("New Fund");
        fundRequest.setNav(new BigDecimal("150.00"));

        userResponse = new UserResponse(1L, "testuser", "USER");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addMutualFund_Success() throws Exception {
        when(mutualFundService.addMutualFund(any(MutualFundRequest.class))).thenReturn(testFund);

        mockMvc.perform(post("/api/v1/admin/funds")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fundRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fundId").value(1))
                .andExpect(jsonPath("$.name").value("Test Fund"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void addMutualFund_Forbidden_NonAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/admin/funds")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fundRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateNav_Success() throws Exception {
        NavUpdateRequest navRequest = new NavUpdateRequest(new BigDecimal("120.00"));
        when(mutualFundService.updateNav(anyLong(), any(NavUpdateRequest.class))).thenReturn(testFund);

        mockMvc.perform(put("/api/v1/admin/funds/1/nav")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(navRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fundId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllFunds_Success() throws Exception {
        List<MutualFund> funds = Arrays.asList(testFund);
        when(mutualFundService.getAllMutualFunds()).thenReturn(funds);

        mockMvc.perform(get("/api/v1/admin/funds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundId").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Fund"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteMutualFund_Success() throws Exception {
        doNothing().when(mutualFundService).deleteMutualFund(1L);

        mockMvc.perform(delete("/api/v1/admin/funds/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/admin/users/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
