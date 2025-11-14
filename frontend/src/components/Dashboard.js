import React, { useState, useEffect, useMemo } from 'react';
import {
  LineChart, Line, BarChart, Bar, AreaChart, Area,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';

function Dashboard({ symbol }) {
  const [quote, setQuote] = useState(null);
  const [historicalData, setHistoricalData] = useState([]);
  const [indicators, setIndicators] = useState(null);
  const [recommendation, setRecommendation] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAllData();
  }, [symbol]);

  const fetchAllData = async () => {
    setLoading(true);
    try {
      const [quoteRes, histRes, indRes, recRes] = await Promise.all([
        fetch(`http://localhost:8080/api/market/quote/${symbol}`),
        fetch(`http://localhost:8080/api/market/historical/${symbol}?days=30`),
        fetch(`http://localhost:8080/api/market/indicators/${symbol}`),
        fetch(`http://localhost:8080/api/market/recommendation/${symbol}`)
      ]);

      const [quoteData, histData, indData, recData] = await Promise.all([
        quoteRes.json(),
        histRes.json(),
        indRes.json(),
        recRes.json()
      ]);

      setQuote(quoteData);
      setHistoricalData(histData.reverse());
      setIndicators(indData);
      setRecommendation(recData);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching data:', error);
      setLoading(false);
    }
  };

  const chartData = useMemo(() => {
    return historicalData.map(item => ({
      date: item.date.substring(5),
      price: item.close,
      volume: item.volume / 1000000
    }));
  }, [historicalData]);

  if (loading) {
    return (
      <div className="text-center py-12">
        <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p className="mt-4 text-gray-600 dark:text-gray-400">Loading market data...</p>
      </div>
    );
  }

  if (!quote || !recommendation) {
    return <div className="text-center py-12 text-gray-600 dark:text-gray-400">No data available</div>;
  }

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
    <div className="space-y-6">
      {/* Stock Overview */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6">
        <div className="flex justify-between items-start mb-6">
          <div>
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white">{quote.symbol}</h2>
            <p className="text-gray-600 dark:text-gray-400">{quote.name}</p>
          </div>
          <div className="text-right">
            <p className="text-3xl font-bold text-gray-900 dark:text-white">
              ${quote.currentPrice.toFixed(2)}
            </p>
            <p className={`text-lg font-semibold ${quote.change >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              {quote.change >= 0 ? '+' : ''}{quote.change.toFixed(2)} ({quote.changePercent.toFixed(2)}%)
            </p>
          </div>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-600 dark:text-gray-400">Open</p>
            <p className="text-lg font-semibold text-gray-900 dark:text-white">${quote.open.toFixed(2)}</p>
          </div>
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-600 dark:text-gray-400">High</p>
            <p className="text-lg font-semibold text-gray-900 dark:text-white">${quote.high.toFixed(2)}</p>
          </div>
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-600 dark:text-gray-400">Low</p>
            <p className="text-lg font-semibold text-gray-900 dark:text-white">${quote.low.toFixed(2)}</p>
          </div>
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-600 dark:text-gray-400">Volume</p>
            <p className="text-lg font-semibold text-gray-900 dark:text-white">
              {(quote.volume / 1000000).toFixed(2)}M
            </p>
          </div>
        </div>
      </div>

      {/* Recommendation Card */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6">
        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-4">Investment Analysis</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center">
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">Recommendation</p>
            <span className={`inline-block px-6 py-3 rounded-lg text-white font-bold text-lg ${getRecommendationColor(recommendation.recommendation)}`}>
              {recommendation.recommendation}
            </span>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-2">
              Score: {recommendation.score}/100
            </p>
          </div>
          <div className="text-center">
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">Target Price</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              ${recommendation.targetPrice.toFixed(2)}
            </p>
            <p className={`text-sm font-semibold mt-2 ${recommendation.potentialReturn >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              {recommendation.potentialReturn >= 0 ? '+' : ''}{recommendation.potentialReturn.toFixed(2)}% Potential
            </p>
          </div>
          <div className="text-center">
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">Risk Level</p>
            <p className={`text-2xl font-bold ${getRiskColor(recommendation.riskLevel)}`}>
              {recommendation.riskLevel}
            </p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-2">
              Confidence: {recommendation.confidenceLevel.toFixed(0)}%
            </p>
          </div>
        </div>

        <div className="mt-6 bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <h4 className="font-semibold text-gray-900 dark:text-white mb-3">Key Insights</h4>
          <ul className="space-y-2">
            {recommendation.reasons.map((reason, index) => (
              <li key={index} className="flex items-start text-sm text-gray-700 dark:text-gray-300">
                <span className="text-blue-600 mr-2">•</span>
                {reason}
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Price Chart */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6">
          <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-4">Price Trend (30 Days)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={chartData}>
              <defs>
                <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="date" stroke="#6b7280" />
              <YAxis stroke="#6b7280" />
              <Tooltip />
              <Area type="monotone" dataKey="price" stroke="#3b82f6" fillOpacity={1} fill="url(#colorPrice)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Volume Chart */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6">
          <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-4">Volume (Millions)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="date" stroke="#6b7280" />
              <YAxis stroke="#6b7280" />
              <Tooltip />
              <Bar dataKey="volume" fill="#8b5cf6" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Technical Indicators */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6">
        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-4">Technical Indicators</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h4 className="font-semibold text-gray-900 dark:text-white mb-2">RSI (14)</h4>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">{indicators.rsi.toFixed(2)}</p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{indicators.rsiSignal}</p>
          </div>
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h4 className="font-semibold text-gray-900 dark:text-white mb-2">MACD</h4>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">{indicators.macd.toFixed(2)}</p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{indicators.macdTrend}</p>
          </div>
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h4 className="font-semibold text-gray-900 dark:text-white mb-2">Moving Averages</h4>
            <p className="text-sm text-gray-900 dark:text-white">SMA 50: ${indicators.sma50.toFixed(2)}</p>
            <p className="text-sm text-gray-900 dark:text-white">SMA 200: ${indicators.sma200.toFixed(2)}</p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{indicators.movingAverageTrend}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
