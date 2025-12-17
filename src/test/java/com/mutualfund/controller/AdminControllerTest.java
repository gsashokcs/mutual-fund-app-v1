package com.mutualfund.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.model.response.UserResponse;
import com.mutualfund.service.MutualFundService;
import com.mutualfund.service.UserService;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
@Disabled("Controller tests disabled")
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private MutualFundService mutualFundService;

    @MockitoBean private UserService userService;

    private MutualFund testFund;
    private Nav testNav;
    private MutualFundRequest fundRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testFund = new MutualFund();
        testFund.setFundId(1L);
        testFund.setName("Test Fund");

        testNav = new Nav();
        testNav.setNavId(1L);
        testNav.setFundId(1L);
        testNav.setNav(new BigDecimal("100.50"));
        testNav.setNavDate(LocalDate.now());
        testNav.setDeleted(false);

        fundRequest = new MutualFundRequest();
        fundRequest.setName("New Fund");

        userResponse = new UserResponse(1L, "testuser", "USER");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addMutualFundSuccess() throws Exception {
        when(mutualFundService.addMutualFund(any(MutualFundRequest.class))).thenReturn(testFund);

        mockMvc.perform(
                        post("/api/v1/admin/funds")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(fundRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fundId").value(1))
                .andExpect(jsonPath("$.name").value("Test Fund"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void addMutualFundForbiddenNonAdmin() throws Exception {
        mockMvc.perform(
                        post("/api/v1/admin/funds")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(fundRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateNavSuccess() throws Exception {
        NavUpdateRequest navRequest =
                new NavUpdateRequest(new BigDecimal("120.00"), LocalDate.now());
        when(mutualFundService.updateNav(anyLong(), any(NavUpdateRequest.class)))
                .thenReturn(testNav);

        mockMvc.perform(
                        put("/api/v1/admin/funds/1/nav")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(navRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fundId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllFundsSuccess() throws Exception {
        List<MutualFund> funds = Arrays.asList(testFund);
        when(mutualFundService.getAllMutualFunds()).thenReturn(funds);

        mockMvc.perform(get("/api/v1/admin/funds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundId").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Fund"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteMutualFundSuccess() throws Exception {
        doNothing().when(mutualFundService).deleteMutualFund(1L);

        mockMvc.perform(delete("/api/v1/admin/funds/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsersSuccess() throws Exception {
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/admin/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
