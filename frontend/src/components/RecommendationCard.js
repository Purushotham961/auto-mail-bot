import React from 'react';

function RecommendationCard({ recommendation, onSelect }) {
  const getRecommendationColor = (rec) => {
    if (rec === 'STRONG BUY') return 'bg-green-500';
    if (rec === 'BUY') return 'bg-green-400';
    if (rec === 'HOLD') return 'bg-yellow-500';
    if (rec === 'SELL') return 'bg-orange-500';
    return 'bg-red-500';
  };

  const getRiskColor = (risk) => {
    if (risk === 'LOW') return 'text-green-600 dark:text-green-400';
    if (risk === 'MEDIUM') return 'text-yellow-600 dark:text-yellow-400';
    return 'text-red-600 dark:text-red-400';
  };

  return (
    <div 
      onClick={onSelect}
      className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow cursor-pointer border-2 border-transparent hover:border-blue-500"
    >
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-xl font-bold text-gray-900 dark:text-white">{recommendation.symbol}</h3>
          <p className="text-sm text-gray-600 dark:text-gray-400">{recommendation.name}</p>
        </div>
        <span className={`px-3 py-1 rounded-lg text-white text-sm font-bold ${getRecommendationColor(recommendation.recommendation)}`}>
          {recommendation.recommendation}
        </span>
      </div>

      <div className="space-y-3 mb-4">
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600 dark:text-gray-400">Current Price</span>
          <span className="text-lg font-bold text-gray-900 dark:text-white">
            ${recommendation.currentQuote.currentPrice.toFixed(2)}
          </span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600 dark:text-gray-400">Target Price</span>
          <span className="text-lg font-bold text-gray-900 dark:text-white">
            ${recommendation.targetPrice.toFixed(2)}
          </span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600 dark:text-gray-400">Potential Return</span>
          <span className={`text-lg font-bold ${recommendation.potentialReturn >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {recommendation.potentialReturn >= 0 ? '+' : ''}{recommendation.potentialReturn.toFixed(2)}%
          </span>
        </div>
      </div>

      <div className="border-t border-gray-200 dark:border-gray-700 pt-4">
        <div className="flex justify-between items-center mb-2">
          <span className="text-sm text-gray-600 dark:text-gray-400">Risk Level</span>
          <span className={`text-sm font-bold ${getRiskColor(recommendation.riskLevel)}`}>
            {recommendation.riskLevel}
          </span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600 dark:text-gray-400">Confidence</span>
          <span className="text-sm font-bold text-gray-900 dark:text-white">
            {recommendation.confidenceLevel.toFixed(0)}%
          </span>
        </div>
      </div>

      <div className="mt-4 bg-gray-50 dark:bg-gray-700 rounded-lg p-3">
        <p className="text-xs text-gray-600 dark:text-gray-400 line-clamp-2">
          {recommendation.reasons[0]}
        </p>
      </div>

      <div className="mt-4">
        <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
          <div 
            className={`h-2 rounded-full ${getRecommendationColor(recommendation.recommendation)}`}
            style={{ width: `${recommendation.score}%` }}
          ></div>
        </div>
        <p className="text-xs text-gray-600 dark:text-gray-400 text-center mt-1">
          Score: {recommendation.score}/100
        </p>
      </div>
    </div>
  );
}

export default RecommendationCard;
