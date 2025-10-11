import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Counter, Trend, Gauge } from 'k6/metrics';
import { SharedArray } from 'k6/data';

// Custom metrics
const errorRate = new Rate('errors');
const salesCount = new Counter('sales_count');
const stockDeductions = new Counter('stock_deductions');
const salesDuration = new Trend('sales_duration');
const recipeSales = new Counter('recipe_sales');
const productSales = new Counter('product_sales');
const activePOS = new Gauge('active_pos_terminals');

// Test configuration - Multi-tier load testing
export const options = {
  stages: [
    { duration: '1m', target: 10 },   // Warm up: 10 concurrent POS
    { duration: '3m', target: 30 },   // Normal: 30 concurrent POS (all terminals active)
    { duration: '3m', target: 50 },   // Peak hours: 50 concurrent transactions
    { duration: '2m', target: 30 },   // Back to normal
    { duration: '1m', target: 0 },    // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(95)<3000'],     // 95% of requests under 3s
    errors: ['rate<0.1'],                   // Error rate under 10%
    sales_duration: ['p(90)<4000'],         // 90% of sales under 4s
    'http_req_duration{name:CreateSales}': ['p(95)<2500'], // Sales creation under 2.5s
  },
};

const MAIN_URL = __ENV.MAIN_URL || 'http://localhost:8080';
const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

// Pre-calculate test data structure
// 3 HQs x 5 Stores x 2 POS = 30 POS terminals
const generatePOSEnvironments = () => {
  const environments = [];

  for (let hqId = 1; hqId <= 3; hqId++) {
    const hqNames = ['Seoul HQ', 'Busan HQ', 'Jeju HQ'];
    const storeNames = ['Gangnam', 'Hongdae', 'Itaewon', 'Myeongdong', 'Jamsil'];

    for (let storeIdx = 0; storeIdx < 5; storeIdx++) {
      const storeId = (hqId - 1) * 5 + storeIdx + 1;
      const containerId = storeId; // Container ID matches Store ID

      for (let posIdx = 1; posIdx <= 2; posIdx++) {
        const posId = (storeId - 1) * 2 + posIdx;

        environments.push({
          hqId,
          hqName: hqNames[hqId - 1],
          storeId,
          storeName: storeNames[storeIdx],
          containerId,
          posId,
          posName: `POS-${posId}`,
          displayName: `${hqNames[hqId - 1]}/${storeNames[storeIdx]}/POS-${posId}`
        });
      }
    }
  }

  return environments;
};

// Generate product catalog
const generateProducts = () => {
  const products = [];

  // For each HQ, add 10 recipe products and 10 finished products
  for (let hqId = 1; hqId <= 3; hqId++) {
    // Recipe products (IDs will be calculated based on creation order)
    // Assuming ingredients take IDs 1-8, then recipes start
    const recipeStartId = 9 + (hqId - 1) * 20;

    products.push(
      // Recipe Products (Hot Beverages)
      {
        id: recipeStartId,
        code: `REC-AMER-HOT-${hqId}`,
        name: `Americano (Hot) HQ${hqId}`,
        price: 4500,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 1,
        code: `REC-LATTE-HOT-${hqId}`,
        name: `Cafe Latte (Hot) HQ${hqId}`,
        price: 5000,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 2,
        code: `REC-VAN-HOT-${hqId}`,
        name: `Vanilla Latte (Hot) HQ${hqId}`,
        price: 5500,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 3,
        code: `REC-CAR-HOT-${hqId}`,
        name: `Caramel Macchiato (Hot) HQ${hqId}`,
        price: 5800,
        type: 'RECIPE',
        hqId
      },

      // Recipe Products (Cold Beverages)
      {
        id: recipeStartId + 4,
        code: `REC-AMER-ICE-${hqId}`,
        name: `Iced Americano HQ${hqId}`,
        price: 5000,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 5,
        code: `REC-LATTE-ICE-${hqId}`,
        name: `Iced Cafe Latte HQ${hqId}`,
        price: 5500,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 6,
        code: `REC-VAN-ICE-${hqId}`,
        name: `Iced Vanilla Latte HQ${hqId}`,
        price: 6000,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 7,
        code: `REC-CAR-ICE-${hqId}`,
        name: `Iced Caramel Macchiato HQ${hqId}`,
        price: 6300,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 8,
        code: `REC-MOCHA-HOT-${hqId}`,
        name: `Mocha (Hot) HQ${hqId}`,
        price: 5800,
        type: 'RECIPE',
        hqId
      },
      {
        id: recipeStartId + 9,
        code: `REC-MOCHA-ICE-${hqId}`,
        name: `Iced Mocha HQ${hqId}`,
        price: 6300,
        type: 'RECIPE',
        hqId
      },

      // Finished Products
      {
        id: recipeStartId + 10,
        code: `PRD-CROIS-${hqId}`,
        name: `Croissant HQ${hqId}`,
        price: 3800,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 11,
        code: `PRD-CHOC-${hqId}`,
        name: `Chocolate Croissant HQ${hqId}`,
        price: 4200,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 12,
        code: `PRD-MUFFIN-${hqId}`,
        name: `Blueberry Muffin HQ${hqId}`,
        price: 4000,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 13,
        code: `PRD-CHEESE-${hqId}`,
        name: `Cheesecake Slice HQ${hqId}`,
        price: 6500,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 14,
        code: `PRD-BROWN-${hqId}`,
        name: `Brownie HQ${hqId}`,
        price: 4500,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 15,
        code: `PRD-SAND-${hqId}`,
        name: `Club Sandwich HQ${hqId}`,
        price: 7500,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 16,
        code: `PRD-BAGEL-${hqId}`,
        name: `Plain Bagel HQ${hqId}`,
        price: 3500,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 17,
        code: `PRD-COOKIE-${hqId}`,
        name: `Chocolate Chip Cookie HQ${hqId}`,
        price: 2500,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 18,
        code: `PRD-SCONE-${hqId}`,
        name: `Plain Scone HQ${hqId}`,
        price: 3800,
        type: 'PRODUCT',
        hqId
      },
      {
        id: recipeStartId + 19,
        code: `PRD-DONUT-${hqId}`,
        name: `Glazed Donut HQ${hqId}`,
        price: 3200,
        type: 'PRODUCT',
        hqId
      }
    );
  }

  return products;
};

export function setup() {
  console.log('========================================');
  console.log('Multi-Tier Sales Load Test Setup');
  console.log('========================================');
  console.log('');

  const environments = generatePOSEnvironments();
  const products = generateProducts();

  console.log(`✓ Configured ${environments.length} POS terminals`);
  console.log(`  - 3 HQs (Seoul, Busan, Jeju)`);
  console.log(`  - 15 Stores (5 per HQ)`);
  console.log(`  - 30 POS terminals (2 per Store)`);
  console.log('');
  console.log(`✓ Available ${products.length} products for testing`);
  console.log(`  - 30 Recipe products (10 per HQ)`);
  console.log(`  - 30 Finished products (10 per HQ)`);
  console.log('');

  // Test connectivity
  console.log('Testing server connectivity...');
  const mainHealthCheck = http.get(`${MAIN_URL}/actuator/health`);
  const posHealthCheck = http.get(`${POS_URL}/actuator/health`);

  if (mainHealthCheck.status === 200) {
    console.log('✓ Main server is available');
  } else {
    console.warn('⚠ Main server health check failed');
  }

  if (posHealthCheck.status === 200) {
    console.log('✓ POS server is available');
  } else {
    console.warn('⚠ POS server health check failed');
  }

  console.log('');
  console.log('========================================');
  console.log('Starting load test...');
  console.log('========================================');
  console.log('');

  return { environments, products };
}

export default function(data) {
  const { environments, products } = data;

  // Randomly select a POS terminal for this transaction
  const env = environments[Math.floor(Math.random() * environments.length)];
  const { hqId, storeId, containerId, posId, displayName } = env;

  // Filter products for this HQ only
  const hqProducts = products.filter(p => p.hqId === hqId);

  group(`Sales Transaction - ${displayName}`, () => {
    const startTime = Date.now();
    const billNo = `BILL-${Date.now()}-${__VU}-${__ITER}`;

    // Randomly select 1-3 products for this transaction
    const itemCount = Math.floor(Math.random() * 3) + 1;
    const selectedProducts = [];
    const usedIndices = new Set();

    for (let i = 0; i < itemCount; i++) {
      let idx;
      do {
        idx = Math.floor(Math.random() * hqProducts.length);
      } while (usedIndices.has(idx));

      usedIndices.add(idx);
      selectedProducts.push(hqProducts[idx]);
    }

    // Small delay to simulate cashier selecting products
    sleep(0.3);

    // Create sales transaction
    let salesSuccess = false;
    group('Create Sales Transaction', () => {
      const details = [];
      let totalAmount = 0;

      selectedProducts.forEach((product, index) => {
        const qty = 1; // Always 1 for simplicity
        const lineAmount = qty * product.price;
        totalAmount += lineAmount;

        details.push({
          hqId,
          storeId,
          posId,
          lineNo: index + 1,
          productId: product.id,
          productCode: product.code,
          qty,
          unitAmt: product.price,
          saleQty: qty,
          saleType: true,
          saleDate: new Date().toISOString().slice(0, 19),
          saleAmt: lineAmount,
          payAmt: lineAmount,
          dcAmt: 0,
          couponAmt: 0,
          cardAmt: lineAmount,
          cashAmt: 0,
          voucherAmt: 0,
          promotionAmt: 0
        });
      });

      const salesPayload = JSON.stringify({
        hqId,
        storeId,
        posId,
        billNo,
        saleType: true,
        saleDate: new Date().toISOString().slice(0, 19),
        details,
        payments: [
          {
            paymentMethodId: 1, // Card payment
            payAmt: totalAmount,
            saleType: true,
            paymentDate: new Date().toISOString().slice(0, 19),
            changeAmt: 0
          }
        ]
      });

      const salesRes = http.post(
        `${POS_URL}/pos/sales`,
        salesPayload,
        {
          headers: { 'Content-Type': 'application/json' },
          timeout: '15s',
          tags: { name: 'CreateSales' }
        }
      );

      salesSuccess = check(salesRes, {
        'Sales creation status is 200/201': (r) => r.status === 200 || r.status === 201,
        'Sales returns valid response': (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.data?.id !== undefined || body.id !== undefined;
          } catch (e) {
            console.error(`[${displayName}] Failed to parse response: ${r.body.substring(0, 100)}`);
            return false;
          }
        },
        'Sales response time < 10s': (r) => r.timings.duration < 10000,
      });

      if (salesSuccess) {
        salesCount.add(1);
        stockDeductions.add(selectedProducts.length);

        // Track recipe vs product sales
        selectedProducts.forEach(p => {
          if (p.type === 'RECIPE') {
            recipeSales.add(1);
          } else {
            productSales.add(1);
          }
        });

        // Periodic logging
        if (__ITER % 20 === 0) {
          const productNames = selectedProducts.map(p => p.name).join(', ');
          console.log(`✓ [${displayName}] VU${__VU}-${__ITER}: ₩${totalAmount.toLocaleString()} - ${productNames}`);
        }
      } else {
        errorRate.add(1);
        const productInfo = selectedProducts.map(p => `${p.code}(${p.id})`).join(', ');
        console.error(`✗ [${displayName}] VU${__VU}-${__ITER}: Failed - ${productInfo} - Status: ${salesRes.status}`);
      }
    });

    const endTime = Date.now();
    salesDuration.add(endTime - startTime);

    // Update active POS gauge
    activePOS.add(__VU);

    // Random delay between transactions (1-4 seconds)
    sleep(Math.random() * 3 + 1);
  });
}

export function teardown(data) {
  console.log('');
  console.log('========================================');
  console.log('Load Test Completed');
  console.log('========================================');
  console.log('');
  console.log('Next steps:');
  console.log('  1. Check stock levels in main-server database');
  console.log('  2. Verify sales records in pos-server database');
  console.log('  3. Review detailed HTML report');
  console.log('');
}

export function handleSummary(data) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').substring(0, 19);

  console.log('');
  console.log('📊 Generating test reports...');

  const summary = {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    [`../reports/k6/multi-tier-sales-${timestamp}.json`]: JSON.stringify(data, null, 2),
    [`../reports/k6/multi-tier-sales-${timestamp}.html`]: htmlReport(data),
  };

  console.log(`  ✓ JSON: reports/k6/multi-tier-sales-${timestamp}.json`);
  console.log(`  ✓ HTML: reports/k6/multi-tier-sales-${timestamp}.html`);
  console.log('');

  return summary;
}

function textSummary(data, options) {
  const indent = options.indent || '';
  const metrics = data.metrics;

  let summary = '\n';
  summary += `${indent}${'='.repeat(80)}\n`;
  summary += `${indent}Multi-Tier Sales Load Test - Results Summary\n`;
  summary += `${indent}${'='.repeat(80)}\n\n`;

  // Sales metrics
  summary += `${indent}📊 SALES METRICS\n`;
  summary += `${indent}${'-'.repeat(80)}\n`;
  if (metrics.sales_count) {
    summary += `${indent}  Total Sales Transactions:  ${metrics.sales_count.values.count}\n`;
  }
  if (metrics.recipe_sales) {
    summary += `${indent}  Recipe Sales:              ${metrics.recipe_sales.values.count}\n`;
  }
  if (metrics.product_sales) {
    summary += `${indent}  Product Sales:             ${metrics.product_sales.values.count}\n`;
  }
  if (metrics.stock_deductions) {
    summary += `${indent}  Stock Deductions:          ${metrics.stock_deductions.values.count}\n`;
  }

  // Performance metrics
  summary += `\n${indent}⚡ PERFORMANCE METRICS\n`;
  summary += `${indent}${'-'.repeat(80)}\n`;
  if (metrics.sales_duration) {
    summary += `${indent}  Sales Transaction Duration:\n`;
    summary += `${indent}    Average:     ${metrics.sales_duration.values.avg.toFixed(2)} ms\n`;
    summary += `${indent}    Min:         ${metrics.sales_duration.values.min.toFixed(2)} ms\n`;
    summary += `${indent}    Max:         ${metrics.sales_duration.values.max.toFixed(2)} ms\n`;
    summary += `${indent}    Median:      ${metrics.sales_duration.values.med.toFixed(2)} ms\n`;
    summary += `${indent}    P90:         ${metrics.sales_duration.values['p(90)'].toFixed(2)} ms\n`;
    summary += `${indent}    P95:         ${metrics.sales_duration.values['p(95)'].toFixed(2)} ms\n`;
  }

  if (metrics.http_req_duration) {
    summary += `\n${indent}  HTTP Request Duration:\n`;
    summary += `${indent}    Average:     ${metrics.http_req_duration.values.avg.toFixed(2)} ms\n`;
    summary += `${indent}    P95:         ${metrics.http_req_duration.values['p(95)'].toFixed(2)} ms\n`;
    summary += `${indent}    P99:         ${metrics.http_req_duration.values['p(99)'].toFixed(2)} ms\n`;
  }

  // Error metrics
  summary += `\n${indent}🎯 RELIABILITY METRICS\n`;
  summary += `${indent}${'-'.repeat(80)}\n`;
  if (metrics.errors) {
    const errorPct = (metrics.errors.values.rate * 100).toFixed(2);
    const status = metrics.errors.values.rate < 0.1 ? '✓' : '✗';
    summary += `${indent}  Error Rate:                ${status} ${errorPct}% (threshold: <10%)\n`;
  }

  if (metrics.http_reqs) {
    summary += `${indent}  Total HTTP Requests:       ${metrics.http_reqs.values.count}\n`;
  }

  if (metrics.http_req_failed) {
    const failedPct = (metrics.http_req_failed.values.rate * 100).toFixed(2);
    summary += `${indent}  Failed Requests:           ${failedPct}%\n`;
  }

  // VU metrics
  if (metrics.vus_max) {
    summary += `\n${indent}👥 VIRTUAL USERS\n`;
    summary += `${indent}${'-'.repeat(80)}\n`;
    summary += `${indent}  Peak VUs:                  ${metrics.vus_max.values.max}\n`;
  }

  summary += `\n${indent}${'='.repeat(80)}\n`;

  return summary;
}

function htmlReport(data) {
  const metrics = data.metrics;
  const timestamp = new Date().toLocaleString();

  const salesCount = metrics.sales_count?.values.count || 0;
  const recipeSales = metrics.recipe_sales?.values.count || 0;
  const productSales = metrics.product_sales?.values.count || 0;
  const stockDeductions = metrics.stock_deductions?.values.count || 0;
  const errorRate = ((metrics.errors?.values.rate || 0) * 100).toFixed(2);
  const errorStatus = (metrics.errors?.values.rate || 0) < 0.1 ? 'success' : 'error';

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Multi-Tier Sales Load Test Report</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
      color: #333;
    }
    .container {
      max-width: 1400px;
      margin: 0 auto;
      background: white;
      padding: 40px;
      border-radius: 12px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.2);
    }
    h1 {
      color: #667eea;
      border-bottom: 4px solid #667eea;
      padding-bottom: 15px;
      margin-bottom: 10px;
      font-size: 32px;
    }
    .subtitle {
      color: #666;
      margin-bottom: 30px;
      font-size: 14px;
    }
    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 20px;
      margin: 30px 0;
    }
    .metric-card {
      padding: 25px;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      border-radius: 10px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
      transition: transform 0.2s;
    }
    .metric-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 6px 12px rgba(0,0,0,0.15);
    }
    .metric-card.highlight {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }
    .metric-title {
      font-weight: 600;
      font-size: 14px;
      color: #555;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 10px;
    }
    .metric-card.highlight .metric-title {
      color: rgba(255,255,255,0.9);
    }
    .metric-value {
      font-size: 36px;
      font-weight: bold;
      margin: 15px 0;
    }
    .metric-card.highlight .metric-value {
      color: white;
    }
    .metric-details {
      font-size: 13px;
      color: #666;
      margin-top: 10px;
      line-height: 1.6;
    }
    .metric-card.highlight .metric-details {
      color: rgba(255,255,255,0.8);
    }
    .success { color: #28a745; }
    .warning { color: #ffc107; }
    .error { color: #dc3545; }
    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: #667eea;
      margin: 40px 0 20px 0;
      padding-bottom: 10px;
      border-bottom: 2px solid #e0e0e0;
    }
    .info-box {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      border-left: 4px solid #667eea;
      margin: 20px 0;
    }
    .info-box h3 {
      color: #667eea;
      margin-bottom: 10px;
    }
    .info-box ul {
      list-style: none;
      padding-left: 0;
    }
    .info-box li {
      padding: 5px 0;
      color: #555;
    }
    .info-box li:before {
      content: "✓ ";
      color: #28a745;
      font-weight: bold;
      margin-right: 5px;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>🚀 Multi-Tier Sales Load Test Report</h1>
    <div class="subtitle">Generated at: ${timestamp}</div>

    <div class="info-box">
      <h3>Test Configuration</h3>
      <ul>
        <li>3 Headquarters (Seoul, Busan, Jeju)</li>
        <li>15 Stores (5 per HQ)</li>
        <li>30 POS Terminals (2 per Store)</li>
        <li>60 Products (30 Recipe + 30 Finished, 20 per HQ)</li>
        <li>Load Profile: 0 → 10 → 30 → 50 → 30 → 0 VUs over 10 minutes</li>
      </ul>
    </div>

    <div class="section-title">📊 Sales Metrics</div>
    <div class="grid">
      <div class="metric-card highlight">
        <div class="metric-title">Total Sales</div>
        <div class="metric-value">${salesCount.toLocaleString()}</div>
        <div class="metric-details">Completed transactions</div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Recipe Sales</div>
        <div class="metric-value">${recipeSales.toLocaleString()}</div>
        <div class="metric-details">Beverages with ingredient deduction</div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Product Sales</div>
        <div class="metric-value">${productSales.toLocaleString()}</div>
        <div class="metric-details">Ready-to-sell items</div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Stock Deductions</div>
        <div class="metric-value">${stockDeductions.toLocaleString()}</div>
        <div class="metric-details">Total items deducted from inventory</div>
      </div>
    </div>

    <div class="section-title">⚡ Performance Metrics</div>
    <div class="grid">
      <div class="metric-card">
        <div class="metric-title">Avg Sales Duration</div>
        <div class="metric-value">${metrics.sales_duration?.values.avg.toFixed(0) || 0}<span style="font-size:20px">ms</span></div>
        <div class="metric-details">
          Min: ${metrics.sales_duration?.values.min.toFixed(0)}ms |
          Max: ${metrics.sales_duration?.values.max.toFixed(0)}ms<br>
          P90: ${metrics.sales_duration?.values['p(90)'].toFixed(0)}ms |
          P95: ${metrics.sales_duration?.values['p(95)'].toFixed(0)}ms
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-title">HTTP Request Duration</div>
        <div class="metric-value">${metrics.http_req_duration?.values.avg.toFixed(0) || 0}<span style="font-size:20px">ms</span></div>
        <div class="metric-details">
          P95: ${metrics.http_req_duration?.values['p(95)'].toFixed(0)}ms |
          P99: ${metrics.http_req_duration?.values['p(99)'].toFixed(0)}ms
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Total Requests</div>
        <div class="metric-value">${metrics.http_reqs?.values.count.toLocaleString() || 0}</div>
        <div class="metric-details">HTTP requests sent to servers</div>
      </div>

      <div class="metric-card ${errorStatus}">
        <div class="metric-title">Error Rate</div>
        <div class="metric-value ${errorStatus}">${errorRate}%</div>
        <div class="metric-details">Target: &lt;10% | Status: ${errorRate < 10 ? '✓ Pass' : '✗ Fail'}</div>
      </div>
    </div>

    <div class="section-title">📈 Additional Insights</div>
    <div class="grid">
      <div class="metric-card">
        <div class="metric-title">Peak Virtual Users</div>
        <div class="metric-value">${metrics.vus_max?.values.max || 0}</div>
        <div class="metric-details">Maximum concurrent transactions</div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Throughput</div>
        <div class="metric-value">${(salesCount / 600).toFixed(2)}</div>
        <div class="metric-details">Transactions per second (avg)</div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Data Transferred</div>
        <div class="metric-value">${((metrics.data_received?.values.count || 0) / 1024 / 1024).toFixed(2)}<span style="font-size:20px">MB</span></div>
        <div class="metric-details">Total data received</div>
      </div>

      <div class="metric-card">
        <div class="metric-title">Data Sent</div>
        <div class="metric-value">${((metrics.data_sent?.values.count || 0) / 1024 / 1024).toFixed(2)}<span style="font-size:20px">MB</span></div>
        <div class="metric-details">Total data sent</div>
      </div>
    </div>

    <div class="info-box" style="margin-top: 40px; border-left-color: #28a745;">
      <h3>✓ Test Completed Successfully</h3>
      <p style="color: #555; margin-top: 10px;">
        The multi-tier sales load test has been completed. Please verify:
      </p>
      <ul>
        <li>Stock levels in the main-server database for all 15 stores</li>
        <li>Sales records in the pos-server database across all 30 POS terminals</li>
        <li>Recipe ingredient deductions for beverage sales</li>
        <li>Product stock deductions for finished goods</li>
      </ul>
    </div>
  </div>
</body>
</html>`;
}
