import React, { useState, useEffect } from 'react';
import Dashboard from './components/Dashboard';
import StockSearch from './components/StockSearch';
import RecommendationCard from './components/RecommendationCard';
import './index.css';

function App() {
  const [selectedStock, setSelectedStock] = useState('AAPL');
  const [topRecommendations, setTopRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [darkMode, setDarkMode] = useState(false);

  useEffect(() => {
    fetchTopRecommendations();
  }, []);

  const fetchTopRecommendations = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/market/recommendations/top');
      const data = await response.json();
      setTopRecommendations(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching recommendations:', error);
      setLoading(false);
    }
  };

  return (
    <div className={darkMode ? 'dark' : ''}>
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800">
        {/* Header */}
        <header className="bg-white dark:bg-gray-800 shadow-lg">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
            <div className="flex justify-between items-center">
              <div>
                <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                  📈 Market Intelligence Platform
                </h1>
                <p className="text-gray-600 dark:text-gray-300 mt-1">
                  AI-Powered Investment Analysis & Recommendations
                </p>
              </div>
              <button
                onClick={() => setDarkMode(!darkMode)}
                className="px-4 py-2 rounded-lg bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors"
              >
                {darkMode ? '☀️ Light' : '🌙 Dark'}
              </button>
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Search Section */}
          <div className="mb-8">
            <StockSearch onStockSelect={setSelectedStock} selectedStock={selectedStock} />
          </div>

          {/* Dashboard */}
          <div className="mb-8">
            <Dashboard symbol={selectedStock} />
          </div>

          {/* Top Recommendations */}
          <div className="mt-12">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
              🎯 Top Investment Recommendations
            </h2>
            {loading ? (
              <div className="text-center py-12">
                <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                <p className="mt-4 text-gray-600 dark:text-gray-400">Loading recommendations...</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {topRecommendations.map((rec) => (
                  <RecommendationCard
                    key={rec.symbol}
                    recommendation={rec}
                    onSelect={() => setSelectedStock(rec.symbol)}
                  />
                ))}
              </div>
            )}
          </div>
        </main>

        {/* Footer */}
        <footer className="bg-white dark:bg-gray-800 shadow-lg mt-12">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
            <p className="text-center text-gray-600 dark:text-gray-400">
              © 2025 Market Intelligence Platform. Data provided for educational purposes.
            </p>
          </div>
        </footer>
      </div>
    </div>
  );
}

export default App;
