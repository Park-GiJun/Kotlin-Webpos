-- POS Server Initial Data
-- Database: pos_server

USE pos_server;

-- 샘플 판매 데이터 (강남역점 POS-GN-001)
-- 1. 판매 헤더 #1 (아메리카노 2잔 + 카페라떼 1잔)
INSERT INTO sales_header (id, bill_id, store_id, pos_id, sale_amt, pay_amt, dc_amt, change_amt, sale_date, created_at, updated_at)
VALUES
(1, 'BILL-20250927-001', 1, 1, 14000.00, 14000.00, 0.00, 0.00, '2025-09-27 09:15:30', NOW(), NOW());

-- 판매 상세 #1
INSERT INTO sales_detail (id, bill_id, line_no, product_id, product_name, sale_qty, sale_price, supply_amt, vat_amt, dc_amt, sale_amt, pay_amt, created_at, updated_at)
VALUES
(1, 'BILL-20250927-001', 1, 1, '아메리카노(Tall)', 2.00, 4500.00, 8181.82, 818.18, 0.00, 9000.00, 9000.00, NOW(), NOW()),
(2, 'BILL-20250927-001', 2, 2, '카페라떼(Tall)', 1.00, 5000.00, 4545.45, 454.55, 0.00, 5000.00, 5000.00, NOW(), NOW());

-- 결제 정보 #1
INSERT INTO sales_payment (id, bill_id, line_no, pay_type, pay_amt, approval_no, card_info, created_at, updated_at)
VALUES
(1, 'BILL-20250927-001', 1, '신용카드', 14000.00, 'APP-20250927-001', '신한카드(1234)', NOW(), NOW());


-- 2. 판매 헤더 #2 (카라멜 마키아또 1잔 + 뉴욕 치즈케이크 1개)
INSERT INTO sales_header (id, bill_id, store_id, pos_id, sale_amt, pay_amt, dc_amt, change_amt, sale_date, created_at, updated_at)
VALUES
(2, 'BILL-20250927-002', 1, 1, 12200.00, 12200.00, 0.00, 0.00, '2025-09-27 10:20:15', NOW(), NOW());

-- 판매 상세 #2
INSERT INTO sales_detail (id, bill_id, line_no, product_id, product_name, sale_qty, sale_price, supply_amt, vat_amt, dc_amt, sale_amt, pay_amt, created_at, updated_at)
VALUES
(3, 'BILL-20250927-002', 1, 4, '카라멜 마키아또(Tall)', 1.00, 5900.00, 5363.64, 536.36, 0.00, 5900.00, 5900.00, NOW(), NOW()),
(4, 'BILL-20250927-002', 2, 6, '뉴욕 치즈케이크', 1.00, 6300.00, 5727.27, 572.73, 0.00, 6300.00, 6300.00, NOW(), NOW());

-- 결제 정보 #2
INSERT INTO sales_payment (id, bill_id, line_no, pay_type, pay_amt, approval_no, card_info, created_at, updated_at)
VALUES
(2, 'BILL-20250927-002', 1, '현금', 12200.00, NULL, NULL, NOW(), NOW());


-- 3. 판매 헤더 #3 (홍대입구역점 - 음료 3잔 + 디저트 2개)
INSERT INTO sales_header (id, bill_id, store_id, pos_id, sale_amt, pay_amt, dc_amt, change_amt, sale_date, created_at, updated_at)
VALUES
(3, 'BILL-20250927-003', 2, 3, 28900.00, 28000.00, 900.00, 0.00, '2025-09-27 11:30:45', NOW(), NOW());

-- 판매 상세 #3
INSERT INTO sales_detail (id, bill_id, line_no, product_id, product_name, sale_qty, sale_price, supply_amt, vat_amt, dc_amt, sale_amt, pay_amt, created_at, updated_at)
VALUES
(5, 'BILL-20250927-003', 1, 1, '아메리카노(Tall)', 2.00, 4500.00, 8181.82, 818.18, 0.00, 9000.00, 9000.00, NOW(), NOW()),
(6, 'BILL-20250927-003', 2, 5, '자몽 허니 블랙 티(Tall)', 1.00, 5900.00, 5363.64, 536.36, 0.00, 5900.00, 5900.00, NOW(), NOW()),
(7, 'BILL-20250927-003', 3, 7, '초콜릿 크루아상', 2.00, 4000.00, 7272.73, 727.27, 0.00, 8000.00, 8000.00, NOW(), NOW()),
(8, 'BILL-20250927-003', 4, 8, '블루베리 머핀', 1.00, 4500.00, 4090.91, 409.09, 900.00, 3600.00, 3600.00, NOW(), NOW()); -- 20% 할인

-- 결제 정보 #3
INSERT INTO sales_payment (id, bill_id, line_no, pay_type, pay_amt, approval_no, card_info, created_at, updated_at)
VALUES
(3, 'BILL-20250927-003', 1, '신용카드', 28000.00, 'APP-20250927-003', '현대카드(5678)', NOW(), NOW());


-- 4. 판매 헤더 #4 (잠실롯데월드타워점 - MD 상품)
INSERT INTO sales_header (id, bill_id, store_id, pos_id, sale_amt, pay_amt, dc_amt, change_amt, sale_date, created_at, updated_at)
VALUES
(4, 'BILL-20250927-004', 3, 5, 43000.00, 43000.00, 0.00, 0.00, '2025-09-27 14:45:20', NOW(), NOW());

-- 판매 상세 #4
INSERT INTO sales_detail (id, bill_id, line_no, product_id, product_name, sale_qty, sale_price, supply_amt, vat_amt, dc_amt, sale_amt, pay_amt, created_at, updated_at)
VALUES
(9, 'BILL-20250927-004', 1, 11, '스타벅스 텀블러 473ml', 1.00, 25000.00, 22727.27, 2272.73, 0.00, 25000.00, 25000.00, NOW(), NOW()),
(10, 'BILL-20250927-004', 2, 12, '스타벅스 머그컵 355ml', 1.00, 18000.00, 16363.64, 1636.36, 0.00, 18000.00, 18000.00, NOW(), NOW());

-- 결제 정보 #4
INSERT INTO sales_payment (id, bill_id, line_no, pay_type, pay_amt, approval_no, card_info, created_at, updated_at)
VALUES
(4, 'BILL-20250927-004', 1, '신용카드', 43000.00, 'APP-20250927-004', 'KB국민카드(9012)', NOW(), NOW());


-- 5. 판매 헤더 #5 (강남역점 - 대량 주문)
INSERT INTO sales_header (id, bill_id, store_id, pos_id, sale_amt, pay_amt, dc_amt, change_amt, sale_date, created_at, updated_at)
VALUES
(5, 'BILL-20250927-005', 1, 2, 54500.00, 54500.00, 0.00, 0.00, '2025-09-27 15:10:00', NOW(), NOW());

-- 판매 상세 #5
INSERT INTO sales_detail (id, bill_id, line_no, product_id, product_name, sale_qty, sale_price, supply_amt, vat_amt, dc_amt, sale_amt, pay_amt, created_at, updated_at)
VALUES
(11, 'BILL-20250927-005', 1, 1, '아메리카노(Tall)', 5.00, 4500.00, 20454.55, 2045.45, 0.00, 22500.00, 22500.00, NOW(), NOW()),
(12, 'BILL-20250927-005', 2, 2, '카페라떼(Tall)', 3.00, 5000.00, 13636.36, 1363.64, 0.00, 15000.00, 15000.00, NOW(), NOW()),
(13, 'BILL-20250927-005', 3, 4, '카라멜 마키아또(Tall)', 2.00, 5900.00, 10727.27, 1072.73, 0.00, 11800.00, 11800.00, NOW(), NOW()),
(14, 'BILL-20250927-005', 4, 7, '초콜릿 크루아상', 3.00, 4000.00, 10909.09, 1090.91, 0.00, 12000.00, 12000.00, NOW(), NOW());

-- 결제 정보 #5 (복합 결제: 신용카드 + 현금)
INSERT INTO sales_payment (id, bill_id, line_no, pay_type, pay_amt, approval_no, card_info, created_at, updated_at)
VALUES
(5, 'BILL-20250927-005', 1, '신용카드', 50000.00, 'APP-20250927-005', '삼성카드(3456)', NOW(), NOW()),
(6, 'BILL-20250927-005', 2, '현금', 4500.00, NULL, NULL, NOW(), NOW());