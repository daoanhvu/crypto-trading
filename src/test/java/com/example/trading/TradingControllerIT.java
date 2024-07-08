package com.example.trading;

import com.example.trading.controller.TradingController;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = CryptoTradingApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TradingController.class})
public class TradingControllerIT {
}
