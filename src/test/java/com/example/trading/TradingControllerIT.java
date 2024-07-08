package com.example.trading;

import com.example.trading.controller.TradingController;
import com.example.trading.domain.entity.UserAccountEntity;
import com.example.trading.domain.repository.UserAccountRepository;
import com.example.trading.dto.ApiResponse;
import com.example.trading.dto.TransactionDTO;
import com.example.trading.dto.WalletDTO;
import com.example.trading.service.PriceFetchingScheduler;
import com.fasterxml.classmate.GenericType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CryptoTradingApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TradingController.class})
public class TradingControllerIT {

    @MockBean
    PriceFetchingScheduler priceFetchingScheduler;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void initTests() throws Exception {

        Mockito.doNothing().when(priceFetchingScheduler).performPriceFetching();
    }

    @Test
    public void shouldExchangeSuccessfully() throws Exception {

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setFromUser("user1");
        transactionDTO.setToUser("user2");
        transactionDTO.setAmount(600.0);
        // When
        String content = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/trading/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk()) // Then
                .andReturn();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/trading/wallets/user1"))
                .andExpect(status().is2xxSuccessful()) // Then
                .andReturn();

        String responseBody = response.getResponse().getContentAsString();
        TypeReference<ApiResponse<WalletDTO>> typeRef = new TypeReference<>() {};
        ApiResponse<WalletDTO> result = objectMapper.readValue(responseBody, typeRef);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertEquals(4400.0, result.getResult().getBalance(), 0.0001);
    }
}
