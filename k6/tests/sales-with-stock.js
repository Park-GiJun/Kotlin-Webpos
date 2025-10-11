import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Counter, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const salesCount = new Counter('sales_count');
const stockAdjustments = new Counter('stock_adjustments');
const salesDuration = new Trend('sales_duration');

// Test configuration - Simulate real POS usage
export const options = {
  stages: [
    { duration: '1m', target: 5 },    // Warm up: 5 concurrent POS terminals
    { duration: '3m', target: 20 },   // Normal operation: 20 concurrent POS
    { duration: '2m', target: 50 },   // Peak hours: 50 concurrent POS
    { duration: '2m', target: 20 },   // Back to normal
    { duration: '1m', target: 0 },    // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],  // 95% of requests should be below 2s
    errors: ['rate<0.05'],               // Error rate should be less than 5%
    sales_duration: ['p(90)<3000'],      // 90% of sales should complete in 3s
  },
};

const MAIN_URL = __ENV.MAIN_URL || 'http://localhost:8080';
const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

export function setup() {
  console.log('Setting up test data...');

  // Multiple HQ/Store/POS configurations for realistic testing
  // Each virtual user will randomly select from these environments
  const environments = [
    { hqId: 1, storeId: 1, posId: 1, storeName: 'Gangnam Store' },
    { hqId: 1, storeId: 1, posId: 2, storeName: 'Gangnam Store' },
    { hqId: 1, storeId: 1, posId: 3, storeName: 'Gangnam Store' },
    { hqId: 1, storeId: 2, posId: 4, storeName: 'Hongdae Store' },
    { hqId: 1, storeId: 2, posId: 5, storeName: 'Hongdae Store' },
  ];

  // Comprehensive test data with RECIPE and PRODUCT types
  // RECIPE products will automatically deduct ingredients
  // PRODUCT items are ready-to-sell
  const products = [
    // Recipe Products (Hot Beverages)
    { id: 201, code: 'REC-AMER-HOT', name: 'Americano (Hot)', price: 4500, type: 'RECIPE' },
    { id: 202, code: 'REC-LATTE-HOT', name: 'Cafe Latte (Hot)', price: 5000, type: 'RECIPE' },
    { id: 204, code: 'REC-VANLAT-HOT', name: 'Vanilla Latte (Hot)', price: 5500, type: 'RECIPE' },
    { id: 205, code: 'REC-CARMAC-HOT', name: 'Caramel Macchiato (Hot)', price: 5800, type: 'RECIPE' },

    // Recipe Products (Cold Beverages)
    { id: 206, code: 'REC-AMER-ICE', name: 'Iced Americano', price: 5000, type: 'RECIPE' },
    { id: 207, code: 'REC-LATTE-ICE', name: 'Iced Cafe Latte', price: 5500, type: 'RECIPE' },
    { id: 209, code: 'REC-CARMAC-ICE', name: 'Iced Caramel Macchiato', price: 6300, type: 'RECIPE' },

    // Ready-to-Sell Products (Bakery)
    { id: 301, code: 'PRD-CROIS-001', name: 'Croissant', price: 3800, type: 'PRODUCT' },
    { id: 302, code: 'PRD-CHOCRO-001', name: 'Chocolate Croissant', price: 4200, type: 'PRODUCT' },
    { id: 303, code: 'PRD-MUFFIN-BLU', name: 'Blueberry Muffin', price: 4000, type: 'PRODUCT' },

    // Desserts
    { id: 306, code: 'PRD-CHEESE-001', name: 'Cheesecake Slice', price: 6500, type: 'PRODUCT' },
    { id: 308, code: 'PRD-BROWNIE-001', name: 'Brownie', price: 4500, type: 'PRODUCT' },
  ];

  console.log(`✓ Configured ${environments.length} POS terminals across ${[...new Set(environments.map(e => e.storeId))].length} stores`);
  console.log(`✓ Available ${products.length} products for testing`);

  return { environments, products };
}

export default function(data) {
  const { environments, products } = data;

  // Randomly select environment (HQ/Store/POS) for this transaction
  const env = environments[Math.floor(Math.random() * environments.length)];
  const { hqId, storeId, posId, storeName } = env;

  group('Complete Sales Transaction with Stock Deduction', () => {
    const startTime = Date.now();
    const billNo = `BILL-${Date.now()}-${__VU}-${__ITER}`;

    // Select random product
    const selectedProduct = products[Math.floor(Math.random() * products.length)];

    // Step 1: Check product stock availability (optional but realistic)
    group('Check Product Stock', () => {
      const checkRes = http.get(`${MAIN_URL}/main/product/product/${selectedProduct.id}`);

      check(checkRes, {
        'Product check status is 200': (r) => r.status === 200,
        'Product exists': (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.id === selectedProduct.id;
          } catch (e) {
            return false;
          }
        },
      }) || errorRate.add(1);
    });

    sleep(0.5);

    // Step 2: Create a sales transaction (this will trigger stock deduction)
    let salesSuccess = false;
    group('Create Sales Transaction', () => {
      const saleQty = 1; // Fixed quantity for testing
      const unitPrice = selectedProduct.price;
      const totalAmount = saleQty * unitPrice;

      const salesPayload = JSON.stringify({
        hqId: hqId,
        storeId: storeId,
        posId: posId,
        billNo: billNo,
        saleType: true,
        saleDate: new Date().toISOString().slice(0, 19),
        details: [
          {
            hqId: hqId,
            storeId: storeId,
            posId: posId,
            lineNo: 1,
            productId: selectedProduct.id,
            productCode: selectedProduct.code,
            qty: saleQty,
            unitAmt: unitPrice,
            saleQty: saleQty,
            saleType: true,
            saleDate: new Date().toISOString().slice(0, 19),
            saleAmt: totalAmount,
            payAmt: totalAmount,
            dcAmt: 0,
            couponAmt: 0,
            cardAmt: totalAmount,
            cashAmt: 0,
            voucherAmt: 0,
            promotionAmt: 0
          }
        ],
        payments: [
          {
            paymentMethodId: 1,
            payAmt: totalAmount,
            saleType: true,
            paymentDate: new Date().toISOString().slice(0, 19),
            changeAmt: 0
          }
        ]
      });

      const salesRes = http.post(`${POS_URL}/pos/sales`, salesPayload, {
        headers: { 'Content-Type': 'application/json' },
        timeout: '10s',
      });

      salesSuccess = check(salesRes, {
        'Sales creation status is 200 or 201': (r) => r.status === 200 || r.status === 201,
        'Sales returns valid ID': (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.data?.id !== undefined;
          } catch (e) {
            console.error(`VU${__VU}-ITER${__ITER}: Failed to parse sales response:`, r.body);
            return false;
          }
        },
        'Sales response time < 5s': (r) => r.timings.duration < 5000,
      });

      if (salesSuccess) {
        salesCount.add(1);
        stockAdjustments.add(1); // Stock is automatically adjusted
        if (__ITER % 50 === 0) {
          console.log(`✓ [${storeName} POS-${posId}] VU${__VU}: ${__ITER} sales completed`);
        }
      } else {
        errorRate.add(1);
        console.error(`✗ [${storeName} POS-${posId}] VU${__VU}-ITER${__ITER}: Sales failed for ${billNo}`);
      }
    });

    sleep(1);

    // Step 3: Verify sales was created
    if (salesSuccess) {
      group('Verify Sales Record', () => {
        const verifyRes = http.get(`${POS_URL}/pos/sales/bill/${billNo}`);

        check(verifyRes, {
          'Sales verification status is 200': (r) => r.status === 200,
          'Sales record exists': (r) => {
            try {
              const body = JSON.parse(r.body);
              return body.data?.header?.billNo === billNo;
            } catch (e) {
              return false;
            }
          },
        }) || errorRate.add(1);
      });
    }

    const endTime = Date.now();
    salesDuration.add(endTime - startTime);

    sleep(2);
  });
}

export function teardown(data) {
  console.log('Test completed. Check stock levels in main-server.');
}

export function handleSummary(data) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');

  // Create reports directory if it doesn't exist (k6 will handle this)
  console.log('\n📊 Generating reports...');

  const summary = {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    [`../reports/k6/sales-with-stock-${timestamp}.json`]: JSON.stringify(data, null, 2),
    [`../reports/k6/sales-with-stock-${timestamp}.html`]: htmlReport(data),
  };

  console.log(`✓ JSON report: reports/k6/sales-with-stock-${timestamp}.json`);
  console.log(`✓ HTML report: reports/k6/sales-with-stock-${timestamp}.html`);

  return summary;
}

function textSummary(data, options) {
  const indent = options.indent || '';

  let summary = '\n';
  summary += `${indent}${'='.repeat(70)}\n`;
  summary += `${indent}Sales with Stock Deduction - Load Test Results\n`;
  summary += `${indent}${'='.repeat(70)}\n\n`;

  const metrics = data.metrics;

  if (metrics.sales_count) {
    summary += `${indent}Total Sales Transactions: ${metrics.sales_count.values.count}\n`;
  }

  if (metrics.stock_adjustments) {
    summary += `${indent}Stock Adjustments: ${metrics.stock_adjustments.values.count}\n`;
  }

  if (metrics.sales_duration) {
    summary += `${indent}\nSales Transaction Duration:\n`;
    summary += `${indent}  avg: ${metrics.sales_duration.values.avg.toFixed(2)}ms\n`;
    summary += `${indent}  min: ${metrics.sales_duration.values.min.toFixed(2)}ms\n`;
    summary += `${indent}  max: ${metrics.sales_duration.values.max.toFixed(2)}ms\n`;
    summary += `${indent}  p(90): ${metrics.sales_duration.values['p(90)'].toFixed(2)}ms\n`;
  }

  if (metrics.http_req_duration) {
    summary += `${indent}\nHTTP Request Duration:\n`;
    summary += `${indent}  avg: ${metrics.http_req_duration.values.avg.toFixed(2)}ms\n`;
    summary += `${indent}  p(95): ${metrics.http_req_duration.values['p(95)'].toFixed(2)}ms\n`;
    summary += `${indent}  p(99): ${metrics.http_req_duration.values['p(99)'].toFixed(2)}ms\n`;
  }

  if (metrics.errors) {
    const errorRate = (metrics.errors.values.rate * 100).toFixed(2);
    summary += `${indent}\nError Rate: ${errorRate}%\n`;
  }

  if (metrics.http_reqs) {
    summary += `${indent}Total HTTP Requests: ${metrics.http_reqs.values.count}\n`;
  }

  summary += `${indent}\n${'='.repeat(70)}\n`;

  return summary;
}

function htmlReport(data) {
  const metrics = data.metrics;

  return `<!DOCTYPE html>
<html>
<head>
  <title>k6 Load Test Report - Sales with Stock</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
    .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; }
    h1 { color: #333; border-bottom: 3px solid #7d64ff; padding-bottom: 10px; }
    .metric { margin: 20px 0; padding: 15px; background: #f9f9f9; border-left: 4px solid #7d64ff; }
    .metric-title { font-weight: bold; font-size: 18px; color: #555; }
    .metric-value { font-size: 24px; color: #7d64ff; margin: 10px 0; }
    .metric-details { font-size: 14px; color: #777; }
    .success { color: #28a745; }
    .warning { color: #ffc107; }
    .error { color: #dc3545; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Sales with Stock Deduction - Load Test Report</h1>
    <p>Test completed at: ${new Date().toLocaleString()}</p>

    <div class="metric">
      <div class="metric-title">Total Sales Transactions</div>
      <div class="metric-value">${metrics.sales_count?.values.count || 0}</div>
    </div>

    <div class="metric">
      <div class="metric-title">Stock Adjustments</div>
      <div class="metric-value">${metrics.stock_adjustments?.values.count || 0}</div>
    </div>

    <div class="metric">
      <div class="metric-title">Average Sales Duration</div>
      <div class="metric-value">${metrics.sales_duration?.values.avg.toFixed(2) || 0}ms</div>
      <div class="metric-details">
        Min: ${metrics.sales_duration?.values.min.toFixed(2)}ms |
        Max: ${metrics.sales_duration?.values.max.toFixed(2)}ms |
        P90: ${metrics.sales_duration?.values['p(90)'].toFixed(2)}ms
      </div>
    </div>

    <div class="metric">
      <div class="metric-title">HTTP Request Performance</div>
      <div class="metric-value">${metrics.http_req_duration?.values.avg.toFixed(2) || 0}ms</div>
      <div class="metric-details">
        P95: ${metrics.http_req_duration?.values['p(95)'].toFixed(2)}ms |
        P99: ${metrics.http_req_duration?.values['p(99)'].toFixed(2)}ms
      </div>
    </div>

    <div class="metric">
      <div class="metric-title">Error Rate</div>
      <div class="metric-value ${(metrics.errors?.values.rate || 0) < 0.05 ? 'success' : 'error'}">
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
