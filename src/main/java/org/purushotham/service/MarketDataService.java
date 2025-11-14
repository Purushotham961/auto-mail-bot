package org.purushotham.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.purushotham.dto.HistoricalData;
import org.purushotham.dto.StockQuote;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class MarketDataService {
    
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    
    private static final String API_KEY = "demo";
    private static final String BASE_URL = "https://www.alphavantage.co/query";
    
    @Cacheable(value = "stockQuotes", key = "#symbol")
    public StockQuote getStockQuote(String symbol) {
        try {
            String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", 
                BASE_URL, symbol, API_KEY);
            
            Request request = new Request.Builder()
                .url(url)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JsonNode root = objectMapper.readTree(jsonData);
                    JsonNode quote = root.get("Global Quote");
                    
                    if (quote != null && !quote.isEmpty()) {
                        return parseStockQuote(symbol, quote);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error fetching stock quote for {}: {}", symbol, e.getMessage());
        }
        
        return generateMockStockQuote(symbol);
    }
    
    private StockQuote parseStockQuote(String symbol, JsonNode quote) {
        return StockQuote.builder()
            .symbol(symbol)
            .name(getCompanyName(symbol))
            .currentPrice(quote.get("05. price").asDouble())
            .change(quote.get("09. change").asDouble())
            .changePercent(Double.parseDouble(quote.get("10. change percent").asText().replace("%", "")))
            .open(quote.get("02. open").asDouble())
            .high(quote.get("03. high").asDouble())
            .low(quote.get("04. low").asDouble())
            .previousClose(quote.get("08. previous close").asDouble())
            .volume(quote.get("06. volume").asLong())
            .lastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }
    
    @Cacheable(value = "historicalData", key = "#symbol")
    public List<HistoricalData> getHistoricalData(String symbol, int days) {
        try {
            String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&outputsize=compact&apikey=%s", 
                BASE_URL, symbol, API_KEY);
            
            Request request = new Request.Builder()
                .url(url)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JsonNode root = objectMapper.readTree(jsonData);
                    JsonNode timeSeries = root.get("Time Series (Daily)");
                    
                    if (timeSeries != null && !timeSeries.isEmpty()) {
                        return parseHistoricalData(timeSeries, days);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error fetching historical data for {}: {}", symbol, e.getMessage());
        }
        
        return generateMockHistoricalData(symbol, days);
    }
    
    private List<HistoricalData> parseHistoricalData(JsonNode timeSeries, int days) {
        List<HistoricalData> data = new ArrayList<>();
        timeSeries.fields().forEachRemaining(entry -> {
            if (data.size() < days) {
                JsonNode dayData = entry.getValue();
                data.add(HistoricalData.builder()
                    .date(entry.getKey())
                    .open(dayData.get("1. open").asDouble())
                    .high(dayData.get("2. high").asDouble())
                    .low(dayData.get("3. low").asDouble())
                    .close(dayData.get("4. close").asDouble())
                    .volume(dayData.get("5. volume").asLong())
                    .build());
            }
        });
        return data;
    }
    
    private StockQuote generateMockStockQuote(String symbol) {
        double basePrice = 100 + random.nextDouble() * 400;
        double change = (random.nextDouble() - 0.5) * 10;
        double changePercent = (change / basePrice) * 100;
        
        return StockQuote.builder()
            .symbol(symbol)
            .name(getCompanyName(symbol))
            .currentPrice(basePrice)
            .change(change)
            .changePercent(changePercent)
            .open(basePrice - random.nextDouble() * 5)
            .high(basePrice + random.nextDouble() * 10)
            .low(basePrice - random.nextDouble() * 10)
            .previousClose(basePrice - change)
            .volume((long) (1000000 + random.nextInt(10000000)))
            .lastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }
    
    private List<HistoricalData> generateMockHistoricalData(String symbol, int days) {
        List<HistoricalData> data = new ArrayList<>();
        double basePrice = 100 + random.nextDouble() * 400;
        
        for (int i = days - 1; i >= 0; i--) {
            double volatility = random.nextDouble() * 5;
            double open = basePrice + (random.nextDouble() - 0.5) * volatility;
            double close = open + (random.nextDouble() - 0.5) * volatility;
            double high = Math.max(open, close) + random.nextDouble() * 2;
            double low = Math.min(open, close) - random.nextDouble() * 2;
            
            data.add(HistoricalData.builder()
                .date(LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .open(open)
                .high(high)
                .low(low)
                .close(close)
                .volume((long) (1000000 + random.nextInt(10000000)))
                .build());
            
            basePrice = close;
        }
        
        return data;
    }
    
    private String getCompanyName(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "AAPL" -> "Apple Inc.";
            case "GOOGL" -> "Alphabet Inc.";
            case "MSFT" -> "Microsoft Corporation";
            case "AMZN" -> "Amazon.com Inc.";
            case "TSLA" -> "Tesla Inc.";
            case "META" -> "Meta Platforms Inc.";
            case "NVDA" -> "NVIDIA Corporation";
            case "JPM" -> "JPMorgan Chase & Co.";
            case "V" -> "Visa Inc.";
            case "WMT" -> "Walmart Inc.";
            default -> symbol + " Corporation";
        };
    }
}
