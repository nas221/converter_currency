package com.currencyconverter.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class CurrencyData {

    private String result;


    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    // Standard Getters
    public String getResult() {
        return result;
    }

    public Map<String, Double> getConversion_rates() {
        return conversionRates;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setConversion_rates(Map<String, Double> conversionRates) {
        this.conversionRates = conversionRates;
    }
}