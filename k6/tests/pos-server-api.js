import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 20 },  // Ramp up to 20 users
    { duration: '1m', target: 20 },   // Stay at 20 users
    { duration: '30s', target: 100 }, // Ramp up to 100 users
    { duration: '2m', target: 100 },  // Stay at 100 users
    { duration: '30s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% of requests should be below 1s
    errors: ['rate<0.05'],              // Error rate should be less than 5%
  },
};

const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

export default function() {
  // Multiple POS environments for realistic testing
  const environments = [
    { hqId: 1, storeId: 1, posId: 1, storeName: 'Gangnam Store' },
    { hqId: 1, storeId: 1, posId: 2, storeName: 'Gangnam Store' },
    { hqId: 1, storeId: 2, posId: 3, storeName: 'Hongdae Store' },
    { hqId: 1, storeId: 2, posId: 4, storeName: 'Hongdae Store' },
  ];

  // Random environment selection
  const env = environments[Math.floor(Math.random() * environments.length)];
  const { hqId, storeId, posId, storeName } = env;

  // Generate unique bill number
  const billNo = `BILL-${Date.now()}-${__VU}-${__ITER}`;

  // Random product selection - diverse product types
  const products = [
    // Recipe products (will auto-deduct ingredients)
    { id: 201, code: 'REC-AMER-HOT', name: 'Americano (Hot)', price: 4500 },
    { id: 206, code: 'REC-AMER-ICE', name: 'Iced Americano', price: 5000 },
    { id: 202, code: 'REC-LATTE-HOT', name: 'Cafe Latte (Hot)', price: 5000 },

    // Ready products
    { id: 301, code: 'PRD-CROIS-001', name: 'Croissant', price: 3800 },
    { id: 306, code: 'PRD-CHEESE-001', name: 'Cheesecake Slice', price: 6500 }
  ];
  const selectedProduct = products[Math.floor(Math.random() * products.length)];

  // Test: Create Sales Transaction
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
        qty: 1,
        unitAmt: selectedProduct.price,
        saleQty: 1,
        saleType: true,
        saleDate: new Date().toISOString().slice(0, 19),
        saleAmt: selectedProduct.price,
        payAmt: selectedProduct.price,
        dcAmt: 0,
        couponAmt: 0,
        cardAmt: selectedProduct.price,
        cashAmt: 0,
        voucherAmt: 0,
        promotionAmt: 0
      }
    ],
    payments: [
      {
        paymentMethodId: 1,
        payAmt: selectedProduct.price,
        saleType: true,
        paymentDate: new Date().toISOString().slice(0, 19),
        changeAmt: 0
      }
    ]
  });

  const res = http.post(`${POS_URL}/pos/sales`, salesPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'Create Sales status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'Create Sales returns ID': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data?.id !== undefined;
      } catch (e) {
        console.error('Failed to parse response:', r.body);
        return false;
      }
    },
    'Response time < 1000ms': (r) => r.timings.duration < 1000,
  }) || errorRate.add(1);

  sleep(1);

  // Test: Get Sales by Bill Number
  const getRes = http.get(`${POS_URL}/pos/sales/bill/${billNo}`);

  check(getRes, {
    'Get Sales status is 200': (r) => r.status === 200,
    'Get Sales returns correct bill number': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data?.header?.billNo === billNo;
      } catch (e) {
        return false;
      }
    },
  }) || errorRate.add(1);

  sleep(1);
}
