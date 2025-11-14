package org.purushotham.service;

import lombok.RequiredArgsConstructor;
import org.purushotham.dto.HistoricalData;
import org.purushotham.dto.InvestmentRecommendation;
import org.purushotham.dto.StockQuote;
import org.purushotham.dto.TechnicalIndicators;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    
    private final MarketDataService marketDataService;
    private final TechnicalAnalysisService technicalAnalysisService;
    
    public InvestmentRecommendation getRecommendation(String symbol) {
        StockQuote quote = marketDataService.getStockQuote(symbol);
        TechnicalIndicators indicators = technicalAnalysisService.calculateIndicators(symbol);
        List<HistoricalData> historicalData = marketDataService.getHistoricalData(symbol, 30);
        
        int score = calculateScore(quote, indicators, historicalData);
        String recommendation = getRecommendationText(score);
        String riskLevel = calculateRiskLevel(quote, historicalData);
        double targetPrice = calculateTargetPrice(quote, score);
        double potentialReturn = ((targetPrice - quote.getCurrentPrice()) / quote.getCurrentPrice()) * 100;
        List<String> reasons = generateReasons(quote, indicators, score);
        
        return InvestmentRecommendation.builder()
            .symbol(symbol)
            .name(quote.getName())
            .recommendation(recommendation)
            .score(score)
            .confidenceLevel(calculateConfidence(score))
            .targetPrice(targetPrice)
            .potentialReturn(potentialReturn)
            .riskLevel(riskLevel)
            .reasons(reasons)
            .technicalIndicators(indicators)
            .currentQuote(quote)
            .build();
    }
    
    public List<InvestmentRecommendation> getTopRecommendations() {
        String[] popularStocks = {"AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "META", "NVDA", "JPM"};
        List<InvestmentRecommendation> recommendations = new ArrayList<>();
        
        for (String symbol : popularStocks) {
            try {
                recommendations.add(getRecommendation(symbol));
            } catch (Exception e) {
                // Skip stocks that fail
            }
        }
        
        recommendations.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        return recommendations.subList(0, Math.min(5, recommendations.size()));
    }
    
    private int calculateScore(StockQuote quote, TechnicalIndicators indicators, List<HistoricalData> historicalData) {
        int score = 50;
        
        // RSI scoring
        if (indicators.getRsi() < 30) {
            score += 15;
        } else if (indicators.getRsi() < 40) {
            score += 10;
        } else if (indicators.getRsi() > 70) {
            score -= 15;
        } else if (indicators.getRsi() > 60) {
            score -= 10;
        }
        
        // MACD scoring
        if (indicators.getMacd() > indicators.getMacdSignal()) {
            score += 10;
        } else {
            score -= 10;
        }
        
        // Moving Average scoring
        if (indicators.getSma50() > indicators.getSma200()) {
            score += 15;
        } else {
            score -= 15;
        }
        
        // Price momentum
        if (quote.getChangePercent() > 2) {
            score += 5;
        } else if (quote.getChangePercent() < -2) {
            score -= 5;
        }
        
        // Volume analysis
        if (historicalData.size() > 1) {
            long avgVolume = historicalData.stream()
                .mapToLong(HistoricalData::getVolume)
                .sum() / historicalData.size();
            
            if (quote.getVolume() > avgVolume * 1.5) {
                score += 5;
            }
        }
        
        // Bollinger Bands
        if (indicators.getBollingerSignal().contains("OVERSOLD")) {
            score += 10;
        } else if (indicators.getBollingerSignal().contains("OVERBOUGHT")) {
            score -= 10;
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private String getRecommendationText(int score) {
        if (score >= 75) {
            return "STRONG BUY";
        } else if (score >= 60) {
            return "BUY";
        } else if (score >= 45) {
            return "HOLD";
        } else if (score >= 30) {
            return "SELL";
        } else {
            return "STRONG SELL";
        }
    }
    
    private String calculateRiskLevel(StockQuote quote, List<HistoricalData> historicalData) {
        if (historicalData.size() < 2) {
            return "MEDIUM";
        }
        
        double avgVolatility = 0;
        for (int i = 0; i < historicalData.size() - 1; i++) {
            double change = Math.abs(historicalData.get(i).getClose() - historicalData.get(i + 1).getClose());
            avgVolatility += (change / historicalData.get(i + 1).getClose()) * 100;
        }
        avgVolatility /= (historicalData.size() - 1);
        
        if (avgVolatility > 5) {
            return "HIGH";
        } else if (avgVolatility > 2) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private double calculateTargetPrice(StockQuote quote, int score) {
        double multiplier = 1.0 + ((score - 50) / 100.0);
        return quote.getCurrentPrice() * multiplier;
    }
    
    private double calculateConfidence(int score) {
        return Math.abs(score - 50) * 2.0;
    }
    
    private List<String> generateReasons(StockQuote quote, TechnicalIndicators indicators, int score) {
        List<String> reasons = new ArrayList<>();
        
        if (indicators.getRsi() < 30) {
            reasons.add("RSI indicates oversold conditions - potential buying opportunity");
        } else if (indicators.getRsi() > 70) {
            reasons.add("RSI indicates overbought conditions - consider taking profits");
        }
        
        if (indicators.getMacd() > indicators.getMacdSignal()) {
            reasons.add("MACD shows bullish momentum");
        } else {
            reasons.add("MACD shows bearish momentum");
        }
        
        if (indicators.getSma50() > indicators.getSma200()) {
            reasons.add("Golden Cross pattern - strong long-term uptrend");
        } else {
            reasons.add("Death Cross pattern - caution advised");
        }
        
        if (quote.getChangePercent() > 0) {
            reasons.add(String.format("Positive price momentum: +%.2f%%", quote.getChangePercent()));
        } else {
            reasons.add(String.format("Negative price momentum: %.2f%%", quote.getChangePercent()));
        }
        
        if (indicators.getBollingerSignal().contains("OVERSOLD")) {
            reasons.add("Price near lower Bollinger Band - potential reversal");
        } else if (indicators.getBollingerSignal().contains("OVERBOUGHT")) {
            reasons.add("Price near upper Bollinger Band - potential correction");
        }
        
        return reasons;
    }
}
