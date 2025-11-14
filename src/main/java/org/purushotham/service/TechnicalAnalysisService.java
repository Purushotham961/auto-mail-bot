package org.purushotham.service;

import lombok.RequiredArgsConstructor;
import org.purushotham.dto.HistoricalData;
import org.purushotham.dto.TechnicalIndicators;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechnicalAnalysisService {
    
    private final MarketDataService marketDataService;
    
    public TechnicalIndicators calculateIndicators(String symbol) {
        List<HistoricalData> historicalData = marketDataService.getHistoricalData(symbol, 200);
        
        if (historicalData.isEmpty()) {
            return null;
        }
        
        double rsi = calculateRSI(historicalData, 14);
        double[] macd = calculateMACD(historicalData);
        double sma50 = calculateSMA(historicalData, 50);
        double sma200 = calculateSMA(historicalData, 200);
        double[] bollingerBands = calculateBollingerBands(historicalData, 20);
        
        return TechnicalIndicators.builder()
            .symbol(symbol)
            .rsi(rsi)
            .rsiSignal(getRSISignal(rsi))
            .macd(macd[0])
            .macdSignal(macd[1])
            .macdTrend(getMACDTrend(macd[0], macd[1]))
            .sma50(sma50)
            .sma200(sma200)
            .movingAverageTrend(getMovingAverageTrend(sma50, sma200))
            .upperBollingerBand(bollingerBands[0])
            .lowerBollingerBand(bollingerBands[1])
            .bollingerSignal(getBollingerSignal(historicalData.get(0).getClose(), bollingerBands))
            .build();
    }
    
    private double calculateRSI(List<HistoricalData> data, int period) {
        if (data.size() < period + 1) {
            return 50.0;
        }
        
        double avgGain = 0;
        double avgLoss = 0;
        
        for (int i = 1; i <= period; i++) {
            double change = data.get(i - 1).getClose() - data.get(i).getClose();
            if (change > 0) {
                avgGain += change;
            } else {
                avgLoss += Math.abs(change);
            }
        }
        
        avgGain /= period;
        avgLoss /= period;
        
        if (avgLoss == 0) {
            return 100.0;
        }
        
        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }
    
    private double[] calculateMACD(List<HistoricalData> data) {
        double ema12 = calculateEMA(data, 12);
        double ema26 = calculateEMA(data, 26);
        double macdLine = ema12 - ema26;
        double signalLine = macdLine * 0.9;
        
        return new double[]{macdLine, signalLine};
    }
    
    private double calculateEMA(List<HistoricalData> data, int period) {
        if (data.isEmpty()) {
            return 0;
        }
        
        double multiplier = 2.0 / (period + 1);
        double ema = data.get(Math.min(period, data.size() - 1)).getClose();
        
        for (int i = Math.min(period - 1, data.size() - 1); i >= 0; i--) {
            ema = (data.get(i).getClose() - ema) * multiplier + ema;
        }
        
        return ema;
    }
    
    private double calculateSMA(List<HistoricalData> data, int period) {
        if (data.size() < period) {
            period = data.size();
        }
        
        double sum = 0;
        for (int i = 0; i < period; i++) {
            sum += data.get(i).getClose();
        }
        
        return sum / period;
    }
    
    private double[] calculateBollingerBands(List<HistoricalData> data, int period) {
        double sma = calculateSMA(data, period);
        double sumSquaredDiff = 0;
        
        int actualPeriod = Math.min(period, data.size());
        for (int i = 0; i < actualPeriod; i++) {
            double diff = data.get(i).getClose() - sma;
            sumSquaredDiff += diff * diff;
        }
        
        double stdDev = Math.sqrt(sumSquaredDiff / actualPeriod);
        double upperBand = sma + (2 * stdDev);
        double lowerBand = sma - (2 * stdDev);
        
        return new double[]{upperBand, lowerBand};
    }
    
    private String getRSISignal(double rsi) {
        if (rsi < 30) {
            return "OVERSOLD - Strong Buy Signal";
        } else if (rsi < 40) {
            return "Buy Signal";
        } else if (rsi > 70) {
            return "OVERBOUGHT - Strong Sell Signal";
        } else if (rsi > 60) {
            return "Sell Signal";
        } else {
            return "Neutral";
        }
    }
    
    private String getMACDTrend(double macd, double signal) {
        if (macd > signal) {
            return "BULLISH - Buy Signal";
        } else if (macd < signal) {
            return "BEARISH - Sell Signal";
        } else {
            return "Neutral";
        }
    }
    
    private String getMovingAverageTrend(double sma50, double sma200) {
        if (sma50 > sma200) {
            return "GOLDEN CROSS - Strong Bullish";
        } else if (sma50 < sma200) {
            return "DEATH CROSS - Strong Bearish";
        } else {
            return "Neutral";
        }
    }
    
    private String getBollingerSignal(double currentPrice, double[] bands) {
        if (currentPrice > bands[0]) {
            return "OVERBOUGHT - Near Upper Band";
        } else if (currentPrice < bands[1]) {
            return "OVERSOLD - Near Lower Band";
        } else {
            return "Within Normal Range";
        }
    }
}
