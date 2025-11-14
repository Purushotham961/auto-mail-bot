package org.purushotham.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRecommendation {
    private String symbol;
    private String name;
    private String recommendation;
    private Integer score;
    private Double confidenceLevel;
    private Double targetPrice;
    private Double potentialReturn;
    private String riskLevel;
    private List<String> reasons;
    private TechnicalIndicators technicalIndicators;
    private StockQuote currentQuote;
}
