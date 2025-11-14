import React, { useState } from 'react';

function StockSearch({ onStockSelect, selectedStock }) {
  const [searchTerm, setSearchTerm] = useState('');

  const popularStocks = [
    { symbol: 'AAPL', name: 'Apple' },
    { symbol: 'GOOGL', name: 'Google' },
    { symbol: 'MSFT', name: 'Microsoft' },
    { symbol: 'AMZN', name: 'Amazon' },
    { symbol: 'TSLA', name: 'Tesla' },
    { symbol: 'META', name: 'Meta' },
    { symbol: 'NVDA', name: 'NVIDIA' },
    { symbol: 'JPM', name: 'JPMorgan' },
    { symbol: 'V', name: 'Visa' },
    { symbol: 'WMT', name: 'Walmart' }
  ];

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      onStockSelect(searchTerm.toUpperCase());
      setSearchTerm('');
    }
  };

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6">
      <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-4">Search Stock</h3>
      
      <form onSubmit={handleSearch} className="mb-6">
        <div className="flex gap-2">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Enter stock symbol (e.g., AAPL)"
            className="flex-1 px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors"
          >
            Search
          </button>
        </div>
      </form>

      <div>
        <h4 className="text-sm font-semibold text-gray-600 dark:text-gray-400 mb-3">Popular Stocks</h4>
        <div className="flex flex-wrap gap-2">
          {popularStocks.map((stock) => (
            <button
              key={stock.symbol}
              onClick={() => onStockSelect(stock.symbol)}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                selectedStock === stock.symbol
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
              }`}
            >
              {stock.symbol}
              <span className="text-xs ml-1 opacity-75">{stock.name}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

export default StockSearch;
