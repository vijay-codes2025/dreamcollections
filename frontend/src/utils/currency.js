/**
 * Currency utility functions for DreamCollections
 * Handles Indian Rupee formatting and display
 */

// Currency configuration
export const CURRENCY_CONFIG = {
  code: 'INR',
  symbol: '₹',
  locale: 'en-IN',
  name: 'Indian Rupee'
};

/**
 * Format price in Indian Rupees with proper formatting
 * @param {number} amount - The amount to format
 * @param {object} options - Formatting options
 * @returns {string} Formatted currency string
 */
export const formatCurrency = (amount, options = {}) => {
  const {
    showSymbol = true,
    showDecimals = true,
    locale = CURRENCY_CONFIG.locale
  } = options;

  if (amount === null || amount === undefined || isNaN(amount)) {
    return showSymbol ? `${CURRENCY_CONFIG.symbol}0` : '0';
  }

  const formatOptions = {
    style: 'currency',
    currency: CURRENCY_CONFIG.code,
    minimumFractionDigits: showDecimals ? 2 : 0,
    maximumFractionDigits: showDecimals ? 2 : 0
  };

  try {
    const formatted = new Intl.NumberFormat(locale, formatOptions).format(amount);
    
    // For Indian locale, the symbol might not be positioned correctly
    // Ensure ₹ symbol is at the beginning
    if (showSymbol && !formatted.startsWith(CURRENCY_CONFIG.symbol)) {
      return `${CURRENCY_CONFIG.symbol}${formatted.replace(/[₹\s]/g, '')}`;
    }
    
    return formatted;
  } catch (error) {
    console.error('Error formatting currency:', error);
    // Fallback formatting
    const numberFormatted = amount.toLocaleString('en-IN', {
      minimumFractionDigits: showDecimals ? 2 : 0,
      maximumFractionDigits: showDecimals ? 2 : 0
    });
    return showSymbol ? `${CURRENCY_CONFIG.symbol}${numberFormatted}` : numberFormatted;
  }
};

/**
 * Format price for display in product cards and lists
 * @param {number} amount - The amount to format
 * @returns {string} Formatted currency string
 */
export const formatPrice = (amount) => {
  return formatCurrency(amount, { showSymbol: true, showDecimals: true });
};

/**
 * Format price without decimals (for whole numbers)
 * @param {number} amount - The amount to format
 * @returns {string} Formatted currency string without decimals
 */
export const formatPriceWhole = (amount) => {
  return formatCurrency(amount, { showSymbol: true, showDecimals: false });
};

/**
 * Format price for input fields (without symbol)
 * @param {number} amount - The amount to format
 * @returns {string} Formatted number string
 */
export const formatPriceInput = (amount) => {
  return formatCurrency(amount, { showSymbol: false, showDecimals: true });
};

/**
 * Parse currency string to number
 * @param {string} currencyString - The currency string to parse
 * @returns {number} Parsed number
 */
export const parseCurrency = (currencyString) => {
  if (!currencyString) return 0;
  
  // Remove currency symbol, commas, and spaces
  const cleanString = currencyString
    .replace(/[₹,\s]/g, '')
    .replace(/[^\d.-]/g, '');
  
  const parsed = parseFloat(cleanString);
  return isNaN(parsed) ? 0 : parsed;
};

/**
 * Calculate discount percentage
 * @param {number} originalPrice - Original price
 * @param {number} discountedPrice - Discounted price
 * @returns {number} Discount percentage
 */
export const calculateDiscountPercentage = (originalPrice, discountedPrice) => {
  if (!originalPrice || originalPrice <= 0) return 0;
  if (!discountedPrice || discountedPrice <= 0) return 0;
  if (discountedPrice >= originalPrice) return 0;
  
  return Math.round(((originalPrice - discountedPrice) / originalPrice) * 100);
};

/**
 * Format discount display
 * @param {number} originalPrice - Original price
 * @param {number} discountedPrice - Discounted price
 * @returns {object} Formatted discount information
 */
export const formatDiscount = (originalPrice, discountedPrice) => {
  const percentage = calculateDiscountPercentage(originalPrice, discountedPrice);
  const savings = originalPrice - discountedPrice;
  
  return {
    percentage,
    savings: formatPrice(savings),
    hasDiscount: percentage > 0
  };
};

/**
 * Convert USD to INR (approximate conversion for demo purposes)
 * @param {number} usdAmount - Amount in USD
 * @param {number} exchangeRate - Exchange rate (default: 83)
 * @returns {number} Amount in INR
 */
export const convertUsdToInr = (usdAmount, exchangeRate = 83) => {
  return Math.round(usdAmount * exchangeRate * 100) / 100;
};

/**
 * Format price range
 * @param {number} minPrice - Minimum price
 * @param {number} maxPrice - Maximum price
 * @returns {string} Formatted price range
 */
export const formatPriceRange = (minPrice, maxPrice) => {
  if (!minPrice && !maxPrice) return '';
  if (!maxPrice || minPrice === maxPrice) return formatPrice(minPrice);
  
  return `${formatPrice(minPrice)} - ${formatPrice(maxPrice)}`;
};

export default {
  formatCurrency,
  formatPrice,
  formatPriceWhole,
  formatPriceInput,
  parseCurrency,
  calculateDiscountPercentage,
  formatDiscount,
  convertUsdToInr,
  formatPriceRange,
  CURRENCY_CONFIG
};
