package com.example.trading.dto;

import java.util.List;

public class HuobiPriceResponseDTO {
    private List<HuobiPriceDTO> data;

    public List<HuobiPriceDTO> getData() {
        return data;
    }

    public void setData(List<HuobiPriceDTO> data) {
        this.data = data;
    }
}
