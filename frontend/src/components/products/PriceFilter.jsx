import React, { useState } from 'react';
import { formatPrice, parseCurrency } from '../../utils/currency';

const PriceFilter = ({ priceRange, onPriceRangeChange }) => {
  const [localRange, setLocalRange] = useState(priceRange);

  // Predefined price ranges in Indian Rupees
  const priceRanges = [
    { label: 'Under ₹2,000', min: 0, max: 2000 },
    { label: '₹2,000 - ₹5,000', min: 2000, max: 5000 },
    { label: '₹5,000 - ₹10,000', min: 5000, max: 10000 },
    { label: '₹10,000 - ₹25,000', min: 10000, max: 25000 },
    { label: '₹25,000 - ₹50,000', min: 25000, max: 50000 },
    { label: '₹50,000 - ₹1,00,000', min: 50000, max: 100000 },
    { label: 'Above ₹1,00,000', min: 100000, max: null }
  ];

  const handleRangeSelect = (range) => {
    const newRange = {
      min: range.min.toString(),
      max: range.max ? range.max.toString() : ''
    };
    setLocalRange(newRange);
    onPriceRangeChange(newRange);
  };

  const handleCustomRangeChange = (field, value) => {
    const newRange = {
      ...localRange,
      [field]: value
    };
    setLocalRange(newRange);
  };

  const applyCustomRange = () => {
    onPriceRangeChange(localRange);
  };

  const clearPriceFilter = () => {
    const emptyRange = { min: '', max: '' };
    setLocalRange(emptyRange);
    onPriceRangeChange(emptyRange);
  };

  const isRangeSelected = (range) => {
    const currentMin = parseInt(localRange.min) || 0;
    const currentMax = parseInt(localRange.max) || Infinity;
    const rangeMax = range.max || Infinity;
    
    return currentMin === range.min && currentMax === rangeMax;
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h4 className="font-medium text-gray-900">Price Range</h4>
        {(localRange.min || localRange.max) && (
          <button
            onClick={clearPriceFilter}
            className="text-xs text-blue-600 hover:text-blue-700"
          >
            Clear
          </button>
        )}
      </div>

      {/* Predefined Price Ranges */}
      <div className="space-y-2">
        {priceRanges.map((range, index) => (
          <button
            key={index}
            onClick={() => handleRangeSelect(range)}
            className={`w-full text-left px-3 py-2 rounded-md text-sm transition-colors ${
              isRangeSelected(range)
                ? 'bg-blue-100 text-blue-700 border border-blue-200'
                : 'text-gray-600 hover:bg-gray-50 border border-transparent'
            }`}
          >
            {range.label}
          </button>
        ))}
      </div>

      {/* Custom Price Range */}
      <div className="pt-4 border-t border-gray-200">
        <h5 className="text-sm font-medium text-gray-700 mb-3">Custom Range</h5>
        <div className="space-y-3">
          <div>
            <label className="block text-xs text-gray-500 mb-1">Min Price (₹)</label>
            <input
              type="number"
              placeholder="0"
              value={localRange.min}
              onChange={(e) => handleCustomRangeChange('min', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-xs text-gray-500 mb-1">Max Price (₹)</label>
            <input
              type="number"
              placeholder="No limit"
              value={localRange.max}
              onChange={(e) => handleCustomRangeChange('max', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>
          <button
            onClick={applyCustomRange}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md text-sm hover:bg-blue-700 transition-colors"
          >
            Apply Range
          </button>
        </div>
      </div>

      {/* Current Selection Display */}
      {(localRange.min || localRange.max) && (
        <div className="pt-3 border-t border-gray-200">
          <div className="text-xs text-gray-500">Current range:</div>
          <div className="text-sm font-medium text-gray-900">
            {localRange.min ? formatPrice(parseInt(localRange.min)) : '₹0'} - {' '}
            {localRange.max ? formatPrice(parseInt(localRange.max)) : 'No limit'}
          </div>
        </div>
      )}
    </div>
  );
};

export default PriceFilter;
