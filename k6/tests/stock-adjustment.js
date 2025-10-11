import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const stockAdjustments = new Counter('stock_adjustments');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp up to 10 users
    { duration: '2m', target: 10 },   // Stay at 10 users
    { duration: '30s', target: 30 },  // Ramp up to 30 users
    { duration: '1m', target: 30 },   // Stay at 30 users
    { duration: '30s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'],  // 95% of requests should be below 800ms
    errors: ['rate<0.1'],               // Error rate should be less than 10%
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function() {
  // Test stock adjustment with ingredient products (풍부한 재고)
  const ingredients = [101, 102, 103, 104, 105]; // Coffee, Milk, Syrups, Chocolate
  const productId = ingredients[Math.floor(Math.random() * ingredients.length)];
  const storeId = 1; // Container ID 1 (maps to Store 1)

  // Test 1: Increase Stock
  const increasePayload = JSON.stringify({
    adjustmentType: 'INCREASE',
    unitQty: 10,
    usageQty: 100,
    reason: `k6 load test - INCREASE - VU:${__VU} ITER:${__ITER}`
  });

  let res = http.put(
    `${BASE_URL}/main/product-stock/product/${productId}/store/${storeId}/adjust`,
    increasePayload,
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  const increaseSuccess = check(res, {
    'Increase stock status is 200': (r) => r.status === 200,
    'Increase stock returns valid response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.productStockId !== undefined && body.usageQtyAfter !== undefined;
      } catch (e) {
        console.error('Failed to parse increase response:', r.body);
        return false;
      }
    },
    'Increase stock response time < 800ms': (r) => r.timings.duration < 800,
  });

  if (increaseSuccess) {
    stockAdjustments.add(1);
  } else {
    errorRate.add(1);
  }

  sleep(2);

  // Test 2: Decrease Stock
  const decreasePayload = JSON.stringify({
    adjustmentType: 'DECREASE',
    unitQty: 0,
    usageQty: 5,
    reason: `k6 load test - DECREASE - VU:${__VU} ITER:${__ITER}`
  });

  res = http.put(
    `${BASE_URL}/main/product-stock/product/${productId}/store/${storeId}/adjust`,
    decreasePayload,
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  const decreaseSuccess = check(res, {
    'Decrease stock status is 200': (r) => r.status === 200,
    'Decrease stock returns valid response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.productStockId !== undefined && body.usageQtyAfter !== undefined;
      } catch (e) {
        console.error('Failed to parse decrease response:', r.body);
        return false;
      }
    },
    'Stock decreased correctly': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.usageQtyBefore > body.usageQtyAfter;
      } catch (e) {
        return false;
      }
    },
    'Decrease stock response time < 800ms': (r) => r.timings.duration < 800,
  });

  if (decreaseSuccess) {
    stockAdjustments.add(1);
  } else {
    errorRate.add(1);
  }

  sleep(2);

  // Test 3: Get Product with Stock Info
  res = http.get(`${BASE_URL}/main/product/product/${productId}`);

  check(res, {
    'Get product status is 200': (r) => r.status === 200,
    'Get product returns stock info': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.id === productId;
      } catch (e) {
        return false;
      }
    },
  }) || errorRate.add(1);

  sleep(1);
}

export function handleSummary(data) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');

  console.log('\n📊 Generating reports...');

  const summary = {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    [`../reports/k6/stock-adjustment-${timestamp}.json`]: JSON.stringify(data, null, 2),
    [`../reports/k6/stock-adjustment-${timestamp}.html`]: generateHtmlReport(data),
  };

  console.log(`✓ JSON report: reports/k6/stock-adjustment-${timestamp}.json`);
  console.log(`✓ HTML report: reports/k6/stock-adjustment-${timestamp}.html`);

  return summary;
}

function generateHtmlReport(data) {
  const metrics = data.metrics;

  return `<!DOCTYPE html>
<html>
<head>
  <title>K6 Load Test Report - Stock Adjustment</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
    .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; }
    h1 { color: #333; border-bottom: 3px solid #7d64ff; padding-bottom: 10px; }
    .metric { margin: 20px 0; padding: 15px; background: #f9f9f9; border-left: 4px solid #7d64ff; }
    .metric-title { font-weight: bold; font-size: 18px; color: #555; }
    .metric-value { font-size: 24px; color: #7d64ff; margin: 10px 0; }
    .metric-details { font-size: 14px; color: #777; }
    .success { color: #28a745; }
    .error { color: #dc3545; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Stock Adjustment - Load Test Report</h1>
    <p>Test completed at: ${new Date().toLocaleString()}</p>

    <div class="metric">
      <div class="metric-title">Total Stock Adjustments</div>
      <div class="metric-value">${metrics.stock_adjustments?.values.count || 0}</div>
    </div>

    <div class="metric">
      <div class="metric-title">Average Request Duration</div>
      <div class="metric-value">${metrics.http_req_duration?.values.avg.toFixed(2) || 0}ms</div>
      <div class="metric-details">
        Min: ${metrics.http_req_duration?.values.min.toFixed(2)}ms |
        Max: ${metrics.http_req_duration?.values.max.toFixed(2)}ms |
        P95: ${metrics.http_req_duration?.values['p(95)'].toFixed(2)}ms
      </div>
    </div>

    <div class="metric">
      <div class="metric-title">Error Rate</div>
      <div class="metric-value ${(metrics.errors?.values.rate || 0) < 0.1 ? 'success' : 'error'}">
        ${((metrics.errors?.values.rate || 0) * 100).toFixed(2)}%
      </div>
    </div>

    <div class="metric">
      <div class="metric-title">Total HTTP Requests</div>
      <div class="metric-value">${metrics.http_reqs?.values.count || 0}</div>
    </div>
  </div>
</body>
</html>`;
}

function textSummary(data, options) {
  const indent = options.indent || '';
  const enableColors = options.enableColors || false;

  let summary = '\n';
  summary += `${indent}Stock Adjustment Load Test Results\n`;
  summary += `${indent}${'='.repeat(50)}\n`;

  const metrics = data.metrics;

  if (metrics.http_req_duration) {
    summary += `${indent}HTTP Request Duration:\n`;
    summary += `${indent}  avg: ${metrics.http_req_duration.values.avg.toFixed(2)}ms\n`;
    summary += `${indent}  min: ${metrics.http_req_duration.values.min.toFixed(2)}ms\n`;
    summary += `${indent}  max: ${metrics.http_req_duration.values.max.toFixed(2)}ms\n`;
    summary += `${indent}  p(95): ${metrics.http_req_duration.values['p(95)'].toFixed(2)}ms\n`;
  }

  if (metrics.stock_adjustments) {
    summary += `${indent}Stock Adjustments: ${metrics.stock_adjustments.values.count}\n`;
  }

  if (metrics.errors) {
    summary += `${indent}Error Rate: ${(metrics.errors.values.rate * 100).toFixed(2)}%\n`;
  }

  return summary;
}
