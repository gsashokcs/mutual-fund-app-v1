package com.mutualfund.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutualfund.model.response.HoldingResponse;
import com.mutualfund.model.request.TransactionRequest;
import com.mutualfund.model.response.TransactionResponse;
import com.mutualfund.service.TransactionService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(TestSecurityConfig.class)
@Disabled("Controller tests disabled")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionRequest transactionRequest;
    private TransactionResponse transactionResponse;
    private HoldingResponse holdingResponse;

    @BeforeEach
    void setUp() {
        transactionRequest = new TransactionRequest();
        transactionRequest.setFundId(1L);
        transactionRequest.setUnits(new BigDecimal("10.0000"));

        transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(1L);
        transactionResponse.setUserId(1L);
        transactionResponse.setFundId(1L);
        transactionResponse.setFundName("Test Fund");
        transactionResponse.setUnits(new BigDecimal("10.0000"));
        transactionResponse.setNav(new BigDecimal("100.00"));
        transactionResponse.setType("BUY");
        transactionResponse.setTransactionDate(LocalDateTime.now());

        holdingResponse = new HoldingResponse();
        holdingResponse.setFundId(1L);
        holdingResponse.setFundName("Test Fund");
        holdingResponse.setUnits(new BigDecimal("10.0000"));
        holdingResponse.setCurrentNav(new BigDecimal("100.00"));
        holdingResponse.setTotalValue(new BigDecimal("1000.00"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void buyUnits_Success() throws Exception {
        when(transactionService.buyUnits(anyLong(), any(TransactionRequest.class)))
                .thenReturn(transactionResponse);

        mockMvc.perform(post("/api/v1/users/1/buy")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.type").value("BUY"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void redeemUnits_Success() throws Exception {
        transactionResponse.setType("REDEEM");
        when(transactionService.redeemUnits(anyLong(), any(TransactionRequest.class)))
                .thenReturn(transactionResponse);

        mockMvc.perform(post("/api/v1/users/1/redeem")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.type").value("REDEEM"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUserHoldings_Success() throws Exception {
        List<HoldingResponse> holdings = Arrays.asList(holdingResponse);
        when(transactionService.getUserHoldings(1L)).thenReturn(holdings);

        mockMvc.perform(get("/api/v1/users/1/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundId").value(1))
                .andExpect(jsonPath("$[0].fundName").value("Test Fund"))
                .andExpect(jsonPath("$[0].units").value(10.0000));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUserTransactions_Success() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(transactionResponse);
        when(transactionService.getUserTransactions(1L)).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/users/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(1))
                .andExpect(jsonPath("$[0].type").value("BUY"));
    }

    @Test
    void buyUnits_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/users/1/buy")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isUnauthorized());
    }
}
