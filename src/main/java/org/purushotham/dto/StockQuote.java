package org.purushotham.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQuote {
    private String symbol;
    private String name;
    private Double currentPrice;
    private Double change;
    private Double changePercent;
    private Double open;
    private Double high;
    private Double low;
    private Double previousClose;
    private Long volume;
    private String lastUpdated;
}
