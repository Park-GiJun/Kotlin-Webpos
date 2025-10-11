import http from 'k6/http';
import { check, sleep } from 'k6';

// Smoke test - Quick validation with minimal load
export const options = {
  vus: 1,           // 1 virtual user
  duration: '30s',  // Run for 30 seconds
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% of requests should be below 1s
    http_req_failed: ['rate<0.01'],    // Less than 1% of requests should fail
  },
};

const MAIN_URL = __ENV.MAIN_URL || 'http://localhost:8080';
const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

export default function() {
  // Test 1: Main Server Health Check
  let res = http.get(`${MAIN_URL}/actuator/health`);
  check(res, {
    'Main server is healthy': (r) => r.status === 200,
  });

  sleep(1);

  // Test 2: POS Server Health Check
  res = http.get(`${POS_URL}/pos/actuator/health`);
  check(res, {
    'POS server is healthy': (r) => r.status === 200,
  });

  sleep(1);

  // Test 3: Get HQs
  res = http.get(`${MAIN_URL}/main/organization/hq`);
  check(res, {
    'Get HQs status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test 4: Get Products
  res = http.get(`${MAIN_URL}/main/product`);
  check(res, {
    'Get Products status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // Test 5: Get specific product with stock
  res = http.get(`${MAIN_URL}/main/product/7`);
  check(res, {
    'Get Product 7 status is 200': (r) => r.status === 200,
    'Product 7 exists': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data?.id === 7;
      } catch (e) {
        return false;
      }
    },
  });

  sleep(1);
}

export function handleSummary(data) {
  console.log('\n=== Smoke Test Results ===');
  console.log(`Total Requests: ${data.metrics.http_reqs.values.count}`);
  console.log(`Failed Requests: ${data.metrics.http_req_failed.values.passes}`);
  console.log(`Average Duration: ${data.metrics.http_req_duration.values.avg.toFixed(2)}ms`);
  console.log(`P95 Duration: ${data.metrics.http_req_duration.values['p(95)'].toFixed(2)}ms`);

  return {
    'stdout': textSummary(data),
  };
}

function textSummary(data) {
  return '\nSmoke test completed!\n';
}
