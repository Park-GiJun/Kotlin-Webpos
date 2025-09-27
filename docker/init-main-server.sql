-- Main Server Initial Data
-- Database: main_server

USE main_server;

-- 1. HQ (본사) 데이터
INSERT INTO hq (id, name, representative, street, city, zip_code, email, phone_number, created_at, updated_at)
VALUES
(1, '스타벅스코리아', '송호섭', '을지로 29', '서울특별시 중구', '04522', 'info@starbucks.co.kr', '02-3015-7000', NOW(), NOW());

-- 2. Store (매장) 데이터
INSERT INTO store (id, hq_id, name, representative, street, city, zip_code, email, phone_number, created_at, updated_at)
VALUES
(1, 1, '스타벅스 강남역점', '김매니저', '강남대로 396', '서울특별시 강남구', '06241', 'gangnam@starbucks.co.kr', '02-538-0960', NOW(), NOW()),
(2, 1, '스타벅스 홍대입구역점', '이매니저', '양화로 160', '서울특별시 마포구', '04050', 'hongdae@starbucks.co.kr', '02-332-2051', NOW(), NOW()),
(3, 1, '스타벅스 잠실롯데월드타워점', '박매니저', '올림픽로 300', '서울특별시 송파구', '05551', 'jamsil@starbucks.co.kr', '02-3213-4050', NOW(), NOW());

-- 3. POS (단말기) 데이터
INSERT INTO pos (id, hq_id, store_id, pos_number, device_type, status, created_at, updated_at)
VALUES
(1, 1, 1, 'POS-GN-001', 'Windows POS Terminal', 'ACTIVE', NOW(), NOW()),
(2, 1, 1, 'POS-GN-002', 'Windows POS Terminal', 'ACTIVE', NOW(), NOW()),
(3, 1, 2, 'POS-HD-001', 'Windows POS Terminal', 'ACTIVE', NOW(), NOW()),
(4, 1, 2, 'POS-HD-002', 'Windows POS Terminal', 'ACTIVE', NOW(), NOW()),
(5, 1, 3, 'POS-JS-001', 'iPad POS Terminal', 'ACTIVE', NOW(), NOW()),
(6, 1, 3, 'POS-JS-002', 'iPad POS Terminal', 'ACTIVE', NOW(), NOW());

-- 4. Product Container (재고 컨테이너) 데이터
INSERT INTO product_containers (id, hq_id, container_id, container_name, created_at, updated_at)
VALUES
(1, 1, 1, '본사 중앙창고', NOW(), NOW()),
(2, 1, 2, '강남역점 매장', NOW(), NOW()),
(3, 1, 3, '홍대입구역점 매장', NOW(), NOW()),
(4, 1, 4, '잠실롯데월드타워점 매장', NOW(), NOW()),
(5, 1, 5, '강남역점 창고', NOW(), NOW()),
(6, 1, 6, '홍대입구역점 창고', NOW(), NOW());

-- 5. Product (상품) 데이터
INSERT INTO product (id, hq_id, name, price, product_type, product_code, supply_amt, unit, usage_unit, created_at, updated_at)
VALUES
-- 음료 (레시피 상품)
(1, 1, '아메리카노(Tall)', 4500.00, 'RECIPE', 'BEV-AME-T', 4000.00, '잔', 'EA', NOW(), NOW()),
(2, 1, '카페라떼(Tall)', 5000.00, 'RECIPE', 'BEV-LAT-T', 4300.00, '잔', 'EA', NOW(), NOW()),
(3, 1, '카푸치노(Tall)', 5000.00, 'RECIPE', 'BEV-CAP-T', 4300.00, '잔', 'EA', NOW(), NOW()),
(4, 1, '카라멜 마키아또(Tall)', 5900.00, 'RECIPE', 'BEV-CAR-T', 5000.00, '잔', 'EA', NOW(), NOW()),
(5, 1, '자몽 허니 블랙 티(Tall)', 5900.00, 'RECIPE', 'BEV-TEA-T', 5000.00, '잔', 'EA', NOW(), NOW()),
-- 푸드
(6, 1, '뉴욕 치즈케이크', 6300.00, 'PRODUCT', 'FOD-CHZ-001', 5500.00, '개', 'EA', NOW(), NOW()),
(7, 1, '초콜릿 크루아상', 4000.00, 'PRODUCT', 'FOD-CRO-001', 3500.00, '개', 'EA', NOW(), NOW()),
(8, 1, '블루베리 머핀', 4500.00, 'PRODUCT', 'FOD-MUF-001', 3800.00, '개', 'EA', NOW(), NOW()),
-- 원재료
(9, 1, '에스프레소 원두(1kg)', 0.00, 'INGREDIENT', 'ING-COF-001', 35000.00, 'kg', 'g', NOW(), NOW()),
(10, 1, '우유(1L)', 0.00, 'INGREDIENT', 'ING-MLK-001', 2500.00, 'L', 'ml', NOW(), NOW()),
-- MD 상품
(11, 1, '스타벅스 텀블러 473ml', 25000.00, 'PRODUCT', 'MD-TUM-001', 18000.00, '개', 'EA', NOW(), NOW()),
(12, 1, '스타벅스 머그컵 355ml', 18000.00, 'PRODUCT', 'MD-MUG-001', 12000.00, '개', 'EA', NOW(), NOW());

-- 6. Product Stock (재고) 데이터
-- 본사 중앙창고 재고
INSERT INTO product_stock (id, product_id, container_id, unit_qty, usage_qty, created_at, updated_at)
VALUES
(1, 1, 1, 0.00, 0.00, NOW(), NOW()),
(2, 2, 1, 0.00, 0.00, NOW(), NOW()),
(3, 9, 1, 500.00, 500000.00, NOW(), NOW()),  -- 원두 500kg
(4, 10, 1, 1000.00, 1000000.00, NOW(), NOW()), -- 우유 1000L
(5, 11, 1, 200.00, 200.00, NOW(), NOW()),    -- 텀블러 200개
(6, 12, 1, 150.00, 150.00, NOW(), NOW());    -- 머그컵 150개

-- 강남역점 매장 재고
INSERT INTO product_stock (id, product_id, container_id, unit_qty, usage_qty, created_at, updated_at)
VALUES
(7, 1, 2, 0.00, 0.00, NOW(), NOW()),
(8, 2, 2, 0.00, 0.00, NOW(), NOW()),
(9, 3, 2, 0.00, 0.00, NOW(), NOW()),
(10, 4, 2, 0.00, 0.00, NOW(), NOW()),
(11, 5, 2, 0.00, 0.00, NOW(), NOW()),
(12, 6, 2, 30.00, 30.00, NOW(), NOW()),      -- 치즈케이크 30개
(13, 7, 2, 40.00, 40.00, NOW(), NOW()),      -- 크루아상 40개
(14, 8, 2, 35.00, 35.00, NOW(), NOW()),      -- 머핀 35개
(15, 9, 2, 15.00, 15000.00, NOW(), NOW()),   -- 원두 15kg
(16, 10, 2, 50.00, 50000.00, NOW(), NOW()),  -- 우유 50L
(17, 11, 2, 20.00, 20.00, NOW(), NOW()),     -- 텀블러 20개
(18, 12, 2, 15.00, 15.00, NOW(), NOW());     -- 머그컵 15개

-- 홍대입구역점 매장 재고
INSERT INTO product_stock (id, product_id, container_id, unit_qty, usage_qty, created_at, updated_at)
VALUES
(19, 1, 3, 0.00, 0.00, NOW(), NOW()),
(20, 2, 3, 0.00, 0.00, NOW(), NOW()),
(21, 3, 3, 0.00, 0.00, NOW(), NOW()),
(22, 4, 3, 0.00, 0.00, NOW(), NOW()),
(23, 5, 3, 0.00, 0.00, NOW(), NOW()),
(24, 6, 3, 25.00, 25.00, NOW(), NOW()),      -- 치즈케이크 25개
(25, 7, 3, 35.00, 35.00, NOW(), NOW()),      -- 크루아상 35개
(26, 8, 3, 30.00, 30.00, NOW(), NOW()),      -- 머핀 30개
(27, 9, 3, 12.00, 12000.00, NOW(), NOW()),   -- 원두 12kg
(28, 10, 3, 45.00, 45000.00, NOW(), NOW()),  -- 우유 45L
(29, 11, 3, 15.00, 15.00, NOW(), NOW()),     -- 텀블러 15개
(30, 12, 3, 10.00, 10.00, NOW(), NOW());     -- 머그컵 10개

-- 잠실롯데월드타워점 매장 재고
INSERT INTO product_stock (id, product_id, container_id, unit_qty, usage_qty, created_at, updated_at)
VALUES
(31, 1, 4, 0.00, 0.00, NOW(), NOW()),
(32, 2, 4, 0.00, 0.00, NOW(), NOW()),
(33, 3, 4, 0.00, 0.00, NOW(), NOW()),
(34, 4, 4, 0.00, 0.00, NOW(), NOW()),
(35, 5, 4, 0.00, 0.00, NOW(), NOW()),
(36, 6, 4, 40.00, 40.00, NOW(), NOW()),      -- 치즈케이크 40개
(37, 7, 4, 50.00, 50.00, NOW(), NOW()),      -- 크루아상 50개
(38, 8, 4, 45.00, 45.00, NOW(), NOW()),      -- 머핀 45개
(39, 9, 4, 20.00, 20000.00, NOW(), NOW()),   -- 원두 20kg
(40, 10, 4, 60.00, 60000.00, NOW(), NOW()),  -- 우유 60L
(41, 11, 4, 25.00, 25.00, NOW(), NOW()),     -- 텀블러 25개
(42, 12, 4, 20.00, 20.00, NOW(), NOW());     -- 머그컵 20개

-- 7. Product Cost (가격 이력) 데이터
INSERT INTO product_cost (id, hq_id, product_id, start_date, end_date, price, created_at, updated_at)
VALUES
-- 현재 가격
(1, 1, 1, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 4500.00, NOW(), NOW()),
(2, 1, 2, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 5000.00, NOW(), NOW()),
(3, 1, 3, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 5000.00, NOW(), NOW()),
(4, 1, 4, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 5900.00, NOW(), NOW()),
(5, 1, 5, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 5900.00, NOW(), NOW()),
(6, 1, 6, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 6300.00, NOW(), NOW()),
(7, 1, 7, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 4000.00, NOW(), NOW()),
(8, 1, 8, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 4500.00, NOW(), NOW()),
(9, 1, 11, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 25000.00, NOW(), NOW()),
(10, 1, 12, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 18000.00, NOW(), NOW()),

-- 과거 가격 (2024년 가격)
(11, 1, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 4300.00, NOW(), NOW()),
(12, 1, 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 4800.00, NOW(), NOW()),
(13, 1, 6, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 6000.00, NOW(), NOW());

-- 8. Recipe Composition (레시피 구성) 데이터
-- 아메리카노(Tall) = 에스프레소 원두 18g + 물
INSERT INTO recipe_composition (id, recipe_product_id, ingredient_product_id, required_qty, created_at, updated_at)
VALUES
(1, 1, 9, 18.00, NOW(), NOW());  -- 아메리카노 = 원두 18g

-- 카페라떼(Tall) = 에스프레소 원두 18g + 우유 300ml
INSERT INTO recipe_composition (id, recipe_product_id, ingredient_product_id, required_qty, created_at, updated_at)
VALUES
(2, 2, 9, 18.00, NOW(), NOW()),  -- 카페라떼 = 원두 18g
(3, 2, 10, 300.00, NOW(), NOW()); -- 카페라떼 = 우유 300ml

-- 카푸치노(Tall) = 에스프레소 원두 18g + 우유 240ml
INSERT INTO recipe_composition (id, recipe_product_id, ingredient_product_id, required_qty, created_at, updated_at)
VALUES
(4, 3, 9, 18.00, NOW(), NOW()),  -- 카푸치노 = 원두 18g
(5, 3, 10, 240.00, NOW(), NOW()); -- 카푸치노 = 우유 240ml

-- 카라멜 마키아또(Tall) = 에스프레소 원두 18g + 우유 300ml
INSERT INTO recipe_composition (id, recipe_product_id, ingredient_product_id, required_qty, created_at, updated_at)
VALUES
(6, 4, 9, 18.00, NOW(), NOW()),  -- 카라멜 마키아또 = 원두 18g
(7, 4, 10, 300.00, NOW(), NOW()); -- 카라멜 마키아또 = 우유 300ml

-- 자몽 허니 블랙 티(Tall) = 티백 + 물 (원재료 없이 판매 가능 상품으로 레시피 구성 없음)