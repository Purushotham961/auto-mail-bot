package org.purushotham.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalIndicators {
    private String symbol;
    private Double rsi;
    private String rsiSignal;
    private Double macd;
    private Double macdSignal;
    private String macdTrend;
    private Double sma50;
    private Double sma200;
    private String movingAverageTrend;
    private Double upperBollingerBand;
    private Double lowerBollingerBand;
    private String bollingerSignal;
}
