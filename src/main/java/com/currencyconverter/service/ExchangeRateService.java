package com.currencyconverter.service;

import com.currencyconverter.model.CurrencyData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ExchangeRateService {
    // Note: In a real app, keep keys in a config file or environment variable
    private static final String API_KEY = "YOUR API KEY";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    private final HttpClient client;
    private final Gson gson;

    public ExchangeRateService() {

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }


    public CurrencyData getExchangeRates(String baseCurrency) throws Exception {
        String url = BASE_URL + API_KEY + "/latest/" + baseCurrency;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check for HTTP errors (404, 500, etc)
        if (response.statusCode() != 200) {
            throw new Exception("Server returned HTTP " + response.statusCode());
        }

        try {
            CurrencyData data = gson.fromJson(response.body(), CurrencyData.class);

            // Validate that GSON actually mapped the object
            if (data == null || !"success".equals(data.getResult())) {
                String errorInfo = (data != null) ? data.getResult() : "Unknown Error";
                throw new Exception("API returned failure status: " + errorInfo);
            }

            return data;
        } catch (JsonSyntaxException e) {
            throw new Exception("Failed to parse API response. The JSON format might have changed.");
        }
    }

    /**
     * Performs the conversion calculation.
     */
    public double convert(double amount, String from, String to) throws Exception {
        // Input validation
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        if (from.equals(to)) {
            return amount;
        }

        // Fetch fresh data
        CurrencyData data = getExchangeRates(from);

        // Access the rate map
        if (data.getConversion_rates() == null) {
            throw new Exception("Conversion rates are missing from the API response.");
        }

        Double rate = data.getConversion_rates().get(to);

        if (rate == null) {
            throw new Exception("Currency code '" + to + "' is not supported by the API.");
        }

        return amount * rate;
    }
}