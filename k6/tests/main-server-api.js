import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp up to 10 users
    { duration: '1m', target: 10 },   // Stay at 10 users
    { duration: '30s', target: 50 },  // Ramp up to 50 users
    { duration: '2m', target: 50 },   // Stay at 50 users
    { duration: '30s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
    errors: ['rate<0.1'],              // Error rate should be less than 10%
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export function setup() {
  // Setup: Create HQ for testing
  const hqPayload = JSON.stringify({
    name: `TestHQ-${Date.now()}`,
    location: 'Seoul',
    contact: '010-1234-5678'
  });

  const hqRes = http.post(`${BASE_URL}/main/organization/hq`, hqPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  if (hqRes.status === 200 || hqRes.status === 201) {
    const hqData = JSON.parse(hqRes.body);
    console.log(`Created HQ with ID: ${hqData.id}`);
    return { hqId: hqData.id };
  }

  console.error('Failed to create HQ for setup');
  return { hqId: null };
}

export default function(data) {
  const hqId = data.hqId;

  if (!hqId) {
    console.error('No HQ ID available for testing');
    return;
  }

  // Test 1: Get HQ by ID
  let res = http.get(`${BASE_URL}/main/organization/hq/${hqId}`);
  check(res, {
    'Get HQ status is 200': (r) => r.status === 200,
    'Get HQ has correct ID': (r) => {
      try {
        return JSON.parse(r.body).id === hqId;
      } catch (e) {
        return false;
      }
    },
  }) || errorRate.add(1);

  sleep(1);

  // Test 2: Get all HQs
  res = http.get(`${BASE_URL}/main/organization/hq`);
  check(res, {
    'Get all HQs status is 200': (r) => r.status === 200,
    'Get all HQs returns array': (r) => {
      try {
        return Array.isArray(JSON.parse(r.body));
      } catch (e) {
        return false;
      }
    },
  }) || errorRate.add(1);

  sleep(1);

  // Test 3: Create Store
  const storePayload = JSON.stringify({
    hqId: hqId,
    name: `TestStore-${Date.now()}-${__VU}`,
    location: 'Gangnam',
    contact: '010-9876-5432'
  });

  res = http.post(`${BASE_URL}/main/organization/store`, storePayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  const storeCreated = check(res, {
    'Create Store status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'Create Store returns ID': (r) => {
      try {
        return JSON.parse(r.body).id !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  if (!storeCreated) {
    errorRate.add(1);
  }

  sleep(1);

  // Test 4: Create Product
  const productPayload = JSON.stringify({
    hqId: hqId,
    productCode: `PROD-${Date.now()}-${__VU}`,
    productName: `Test Product ${__VU}`,
    productType: 'GENERAL',
    useYn: true,
    salePrice: 10000,
    taxYn: true
  });

  res = http.post(`${BASE_URL}/main/product/product`, productPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'Create Product status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'Create Product returns ID': (r) => {
      try {
        return JSON.parse(r.body).id !== undefined;
      } catch (e) {
        return false;
      }
    },
  }) || errorRate.add(1);

  sleep(1);

  // Test 5: Get Products by HQ ID
  res = http.get(`${BASE_URL}/main/product/product/hq/${hqId}`);
  check(res, {
    'Get Products status is 200': (r) => r.status === 200,
    'Get Products returns array': (r) => {
      try {
        return Array.isArray(JSON.parse(r.body));
      } catch (e) {
        return false;
      }
    },
  }) || errorRate.add(1);

  sleep(1);
}

export function teardown(data) {
  // Cleanup: Delete HQ if needed
  if (data.hqId) {
    const res = http.del(`${BASE_URL}/main/organization/hq/${data.hqId}`);
    console.log(`Cleanup HQ ${data.hqId}: ${res.status}`);
  }
}
