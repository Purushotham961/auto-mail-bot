package org.purushotham.controller;

import lombok.RequiredArgsConstructor;
import org.purushotham.dto.HistoricalData;
import org.purushotham.dto.InvestmentRecommendation;
import org.purushotham.dto.StockQuote;
import org.purushotham.dto.TechnicalIndicators;
import org.purushotham.service.MarketDataService;
import org.purushotham.service.RecommendationService;
import org.purushotham.service.TechnicalAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarketController {
    
    private final MarketDataService marketDataService;
    private final TechnicalAnalysisService technicalAnalysisService;
    private final RecommendationService recommendationService;
    
    @GetMapping("/quote/{symbol}")
    public ResponseEntity<StockQuote> getStockQuote(@PathVariable String symbol) {
        StockQuote quote = marketDataService.getStockQuote(symbol.toUpperCase());
        return ResponseEntity.ok(quote);
    }
    
    @GetMapping("/historical/{symbol}")
    public ResponseEntity<List<HistoricalData>> getHistoricalData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        List<HistoricalData> data = marketDataService.getHistoricalData(symbol.toUpperCase(), days);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/indicators/{symbol}")
    public ResponseEntity<TechnicalIndicators> getTechnicalIndicators(@PathVariable String symbol) {
        TechnicalIndicators indicators = technicalAnalysisService.calculateIndicators(symbol.toUpperCase());
        return ResponseEntity.ok(indicators);
    }
    
    @GetMapping("/recommendation/{symbol}")
    public ResponseEntity<InvestmentRecommendation> getRecommendation(@PathVariable String symbol) {
        InvestmentRecommendation recommendation = recommendationService.getRecommendation(symbol.toUpperCase());
        return ResponseEntity.ok(recommendation);
    }
    
    @GetMapping("/recommendations/top")
    public ResponseEntity<List<InvestmentRecommendation>> getTopRecommendations() {
        List<InvestmentRecommendation> recommendations = recommendationService.getTopRecommendations();
        return ResponseEntity.ok(recommendations);
    }
}
