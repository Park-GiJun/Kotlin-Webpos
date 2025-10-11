-- =========================================
-- Complete Test Data for Main Server
-- =========================================
-- Creates:
--   - 3 HQs
--   - 15 Stores (5 per HQ)
--   - 30 POS (2 per Store)
--   - 15 Containers (1 per Store)
--   - 8 Ingredients
--   - 30 Recipe Products (10 per HQ)
--   - 30 Finished Products (10 per HQ)
-- =========================================

USE main_server;

TRUNCATE TABLE hq;
TRUNCATE TABLE store;
TRUNCATE TABLE product_containers;
TRUNCATE TABLE pos;
TRUNCATE TABLE product;
TRUNCATE TABLE product_cost;
TRUNCATE TABLE product_stock;

-- Disable foreign key checks for easier insertion
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- Step 1: Create 3 HQs
-- =========================================
INSERT INTO hq (id, name, representative, street, city, zip_code, email, phone_number, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
(1, 'Seoul Headquarters', 'CEO 1', 'Gangnam-daero 123', 'Seoul', '12341', 'hq1@test.com', '02-123-4561', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(2, 'Busan Headquarters', 'CEO 2', 'Haeundae-ro 456', 'Busan', '12342', 'hq2@test.com', '02-123-4562', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(3, 'Jeju Headquarters', 'CEO 3', 'Nohyeong-ro 789', 'Jeju', '12343', 'hq3@test.com', '02-123-4563', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 2: Create 15 Stores (5 per HQ)
-- =========================================
INSERT INTO store (id, hq_id, name, representative, street, city, zip_code, email, phone_number, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
(1, 1, 'Gangnam Store', 'Manager 1', 'Street 1', 'City 1', '50001', 'store1@test.com', '02-200-1000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(2, 1, 'Hongdae Store', 'Manager 2', 'Street 2', 'City 1', '50002', 'store2@test.com', '02-200-2000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(3, 1, 'Itaewon Store', 'Manager 3', 'Street 3', 'City 1', '50003', 'store3@test.com', '02-200-3000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(4, 1, 'Myeongdong Store', 'Manager 4', 'Street 4', 'City 1', '50004', 'store4@test.com', '02-200-4000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(5, 1, 'Jamsil Store', 'Manager 5', 'Street 5', 'City 1', '50005', 'store5@test.com', '02-200-5000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(6, 2, 'Gangnam Store', 'Manager 6', 'Street 6', 'City 2', '50006', 'store6@test.com', '02-200-6000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(7, 2, 'Hongdae Store', 'Manager 7', 'Street 7', 'City 2', '50007', 'store7@test.com', '02-200-7000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(8, 2, 'Itaewon Store', 'Manager 8', 'Street 8', 'City 2', '50008', 'store8@test.com', '02-200-8000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(9, 2, 'Myeongdong Store', 'Manager 9', 'Street 9', 'City 2', '50009', 'store9@test.com', '02-200-9000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(10, 2, 'Jamsil Store', 'Manager 10', 'Street 10', 'City 2', '500010', 'store10@test.com', '02-200-10000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(11, 3, 'Gangnam Store', 'Manager 11', 'Street 11', 'City 3', '500011', 'store11@test.com', '02-200-11000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(12, 3, 'Hongdae Store', 'Manager 12', 'Street 12', 'City 3', '500012', 'store12@test.com', '02-200-12000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(13, 3, 'Itaewon Store', 'Manager 13', 'Street 13', 'City 3', '500013', 'store13@test.com', '02-200-13000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(14, 3, 'Myeongdong Store', 'Manager 14', 'Street 14', 'City 3', '500014', 'store14@test.com', '02-200-14000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(15, 3, 'Jamsil Store', 'Manager 15', 'Street 15', 'City 3', '500015', 'store15@test.com', '02-200-15000', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 3: Create 15 ProductContainers
-- =========================================
INSERT INTO product_containers (id, hq_id, container_id, container_name, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
(1, 1, 1, 'Store 1 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(2, 1, 2, 'Store 2 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(3, 1, 3, 'Store 3 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(4, 1, 4, 'Store 4 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(5, 1, 5, 'Store 5 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(6, 2, 6, 'Store 6 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(7, 2, 7, 'Store 7 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(8, 2, 8, 'Store 8 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(9, 2, 9, 'Store 9 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(10, 2, 10, 'Store 10 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(11, 3, 11, 'Store 11 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(12, 3, 12, 'Store 12 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(13, 3, 13, 'Store 13 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(14, 3, 14, 'Store 14 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(15, 3, 15, 'Store 15 Container', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 4: Create 30 POS
-- =========================================
INSERT INTO pos (id, hq_id, store_id, pos_number, device_type, status, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
(1, 1, 1, 'POS-1', 'AA:BB:CC:DD:EE:1', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(2, 1, 1, 'POS-2', 'AA:BB:CC:DD:EE:2', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(3, 1, 2, 'POS-3', 'AA:BB:CC:DD:EE:3', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(4, 1, 2, 'POS-4', 'AA:BB:CC:DD:EE:4', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(5, 1, 3, 'POS-5', 'AA:BB:CC:DD:EE:5', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(6, 1, 3, 'POS-6', 'AA:BB:CC:DD:EE:6', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(7, 1, 4, 'POS-7', 'AA:BB:CC:DD:EE:7', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(8, 1, 4, 'POS-8', 'AA:BB:CC:DD:EE:8', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(9, 1, 5, 'POS-9', 'AA:BB:CC:DD:EE:9', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(10, 1, 5, 'POS-10', 'AA:BB:CC:DD:EE:10', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(11, 2, 6, 'POS-11', 'AA:BB:CC:DD:EE:11', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(12, 2, 6, 'POS-12', 'AA:BB:CC:DD:EE:12', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(13, 2, 7, 'POS-13', 'AA:BB:CC:DD:EE:13', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(14, 2, 7, 'POS-14', 'AA:BB:CC:DD:EE:14', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(15, 2, 8, 'POS-15', 'AA:BB:CC:DD:EE:15', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(16, 2, 8, 'POS-16', 'AA:BB:CC:DD:EE:16', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(17, 2, 9, 'POS-17', 'AA:BB:CC:DD:EE:17', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(18, 2, 9, 'POS-18', 'AA:BB:CC:DD:EE:18', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(19, 2, 10, 'POS-19', 'AA:BB:CC:DD:EE:19', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(20, 2, 10, 'POS-20', 'AA:BB:CC:DD:EE:20', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(21, 3, 11, 'POS-21', 'AA:BB:CC:DD:EE:21', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(22, 3, 11, 'POS-22', 'AA:BB:CC:DD:EE:22', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(23, 3, 12, 'POS-23', 'AA:BB:CC:DD:EE:23', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(24, 3, 12, 'POS-24', 'AA:BB:CC:DD:EE:24', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(25, 3, 13, 'POS-25', 'AA:BB:CC:DD:EE:25', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(26, 3, 13, 'POS-26', 'AA:BB:CC:DD:EE:26', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(27, 3, 14, 'POS-27', 'AA:BB:CC:DD:EE:27', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(28, 3, 14, 'POS-28', 'AA:BB:CC:DD:EE:28', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(29, 3, 15, 'POS-29', 'AA:BB:CC:DD:EE:29', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(30, 3, 15, 'POS-30', 'AA:BB:CC:DD:EE:30', 'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 5: Create 8 Ingredient Products
-- =========================================
INSERT INTO product (id, hq_id, name, price, product_type, product_code, supply_amt, unit, usage_unit, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
(1, 1, 'Coffee Beans', 15000.00, 'INGREDIENT', 'ING-BEAN-001', 12000.00, 'KG', 'G', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(2, 1, 'Milk', 5000.00, 'INGREDIENT', 'ING-MILK-001', 4000.00, 'L', 'ML', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(3, 1, 'Sugar', 3000.00, 'INGREDIENT', 'ING-SUGAR-001', 2500.00, 'KG', 'G', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(4, 1, 'Vanilla Syrup', 8000.00, 'INGREDIENT', 'ING-VAN-001', 6500.00, 'L', 'ML', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(5, 1, 'Caramel Syrup', 8500.00, 'INGREDIENT', 'ING-CAR-001', 7000.00, 'L', 'ML', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(6, 1, 'Ice', 1000.00, 'INGREDIENT', 'ING-ICE-001', 500.00, 'KG', 'G', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(7, 1, 'Whipped Cream', 6000.00, 'INGREDIENT', 'ING-CREAM-001', 5000.00, 'L', 'ML', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(8, 1, 'Chocolate Sauce', 7000.00, 'INGREDIENT', 'ING-CHOC-001', 5500.00, 'L', 'ML', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 6: Create 30 Recipe Products
-- =========================================
INSERT INTO product (id, hq_id, name, price, product_type, product_code, supply_amt, unit, usage_unit, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
-- HQ 1 Recipes (9-18)
(9, 1, 'Americano Hot HQ1', 4500.00, 'RECIPE', 'REC-AMER-HOT-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(10, 1, 'Cafe Latte Hot HQ1', 5000.00, 'RECIPE', 'REC-LATTE-HOT-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(11, 1, 'Vanilla Latte Hot HQ1', 5500.00, 'RECIPE', 'REC-VAN-HOT-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(12, 1, 'Caramel Macchiato Hot HQ1', 5800.00, 'RECIPE', 'REC-CAR-HOT-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(13, 1, 'Iced Americano HQ1', 5000.00, 'RECIPE', 'REC-AMER-ICE-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(14, 1, 'Iced Cafe Latte HQ1', 5500.00, 'RECIPE', 'REC-LATTE-ICE-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(15, 1, 'Iced Vanilla Latte HQ1', 6000.00, 'RECIPE', 'REC-VAN-ICE-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(16, 1, 'Iced Caramel Macchiato HQ1', 6300.00, 'RECIPE', 'REC-CAR-ICE-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(17, 1, 'Mocha Hot HQ1', 5800.00, 'RECIPE', 'REC-MOCHA-HOT-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(18, 1, 'Iced Mocha HQ1', 6300.00, 'RECIPE', 'REC-MOCHA-ICE-1', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
-- HQ 2 Recipes (19-28)
(19, 2, 'Americano Hot HQ2', 4500.00, 'RECIPE', 'REC-AMER-HOT-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(20, 2, 'Cafe Latte Hot HQ2', 5000.00, 'RECIPE', 'REC-LATTE-HOT-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(21, 2, 'Vanilla Latte Hot HQ2', 5500.00, 'RECIPE', 'REC-VAN-HOT-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(22, 2, 'Caramel Macchiato Hot HQ2', 5800.00, 'RECIPE', 'REC-CAR-HOT-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(23, 2, 'Iced Americano HQ2', 5000.00, 'RECIPE', 'REC-AMER-ICE-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(24, 2, 'Iced Cafe Latte HQ2', 5500.00, 'RECIPE', 'REC-LATTE-ICE-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(25, 2, 'Iced Vanilla Latte HQ2', 6000.00, 'RECIPE', 'REC-VAN-ICE-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(26, 2, 'Iced Caramel Macchiato HQ2', 6300.00, 'RECIPE', 'REC-CAR-ICE-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(27, 2, 'Mocha Hot HQ2', 5800.00, 'RECIPE', 'REC-MOCHA-HOT-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(28, 2, 'Iced Mocha HQ2', 6300.00, 'RECIPE', 'REC-MOCHA-ICE-2', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
-- HQ 3 Recipes (29-38)
(29, 3, 'Americano Hot HQ3', 4500.00, 'RECIPE', 'REC-AMER-HOT-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(30, 3, 'Cafe Latte Hot HQ3', 5000.00, 'RECIPE', 'REC-LATTE-HOT-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(31, 3, 'Vanilla Latte Hot HQ3', 5500.00, 'RECIPE', 'REC-VAN-HOT-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(32, 3, 'Caramel Macchiato Hot HQ3', 5800.00, 'RECIPE', 'REC-CAR-HOT-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(33, 3, 'Iced Americano HQ3', 5000.00, 'RECIPE', 'REC-AMER-ICE-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(34, 3, 'Iced Cafe Latte HQ3', 5500.00, 'RECIPE', 'REC-LATTE-ICE-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(35, 3, 'Iced Vanilla Latte HQ3', 6000.00, 'RECIPE', 'REC-VAN-ICE-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(36, 3, 'Iced Caramel Macchiato HQ3', 6300.00, 'RECIPE', 'REC-CAR-ICE-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(37, 3, 'Mocha Hot HQ3', 5800.00, 'RECIPE', 'REC-MOCHA-HOT-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(38, 3, 'Iced Mocha HQ3', 6300.00, 'RECIPE', 'REC-MOCHA-ICE-3', 0.00, 'CUP', 'CUP', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 7: Create 30 Finished Products
-- =========================================
INSERT INTO product (id, hq_id, name, price, product_type, product_code, supply_amt, unit, usage_unit, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
-- HQ 1 Products (39-48)
(39, 1, 'Croissant HQ1', 3800.00, 'PRODUCT', 'PRD-CROIS-1', 2500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(40, 1, 'Chocolate Croissant HQ1', 4200.00, 'PRODUCT', 'PRD-CHOC-1', 2800.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(41, 1, 'Blueberry Muffin HQ1', 4000.00, 'PRODUCT', 'PRD-MUFFIN-1', 2600.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(42, 1, 'Cheesecake Slice HQ1', 6500.00, 'PRODUCT', 'PRD-CHEESE-1', 4500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(43, 1, 'Brownie HQ1', 4500.00, 'PRODUCT', 'PRD-BROWN-1', 3000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(44, 1, 'Club Sandwich HQ1', 7500.00, 'PRODUCT', 'PRD-SAND-1', 5000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(45, 1, 'Plain Bagel HQ1', 3500.00, 'PRODUCT', 'PRD-BAGEL-1', 2200.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(46, 1, 'Chocolate Chip Cookie HQ1', 2500.00, 'PRODUCT', 'PRD-COOKIE-1', 1500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(47, 1, 'Plain Scone HQ1', 3800.00, 'PRODUCT', 'PRD-SCONE-1', 2400.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(48, 1, 'Glazed Donut HQ1', 3200.00, 'PRODUCT', 'PRD-DONUT-1', 2000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
-- HQ 2 Products (49-58)
(49, 2, 'Croissant HQ2', 3800.00, 'PRODUCT', 'PRD-CROIS-2', 2500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(50, 2, 'Chocolate Croissant HQ2', 4200.00, 'PRODUCT', 'PRD-CHOC-2', 2800.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(51, 2, 'Blueberry Muffin HQ2', 4000.00, 'PRODUCT', 'PRD-MUFFIN-2', 2600.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(52, 2, 'Cheesecake Slice HQ2', 6500.00, 'PRODUCT', 'PRD-CHEESE-2', 4500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(53, 2, 'Brownie HQ2', 4500.00, 'PRODUCT', 'PRD-BROWN-2', 3000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(54, 2, 'Club Sandwich HQ2', 7500.00, 'PRODUCT', 'PRD-SAND-2', 5000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(55, 2, 'Plain Bagel HQ2', 3500.00, 'PRODUCT', 'PRD-BAGEL-2', 2200.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(56, 2, 'Chocolate Chip Cookie HQ2', 2500.00, 'PRODUCT', 'PRD-COOKIE-2', 1500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(57, 2, 'Plain Scone HQ2', 3800.00, 'PRODUCT', 'PRD-SCONE-2', 2400.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(58, 2, 'Glazed Donut HQ2', 3200.00, 'PRODUCT', 'PRD-DONUT-2', 2000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
-- HQ 3 Products (59-68)
(59, 3, 'Croissant HQ3', 3800.00, 'PRODUCT', 'PRD-CROIS-3', 2500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(60, 3, 'Chocolate Croissant HQ3', 4200.00, 'PRODUCT', 'PRD-CHOC-3', 2800.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(61, 3, 'Blueberry Muffin HQ3', 4000.00, 'PRODUCT', 'PRD-MUFFIN-3', 2600.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(62, 3, 'Cheesecake Slice HQ3', 6500.00, 'PRODUCT', 'PRD-CHEESE-3', 4500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(63, 3, 'Brownie HQ3', 4500.00, 'PRODUCT', 'PRD-BROWN-3', 3000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(64, 3, 'Club Sandwich HQ3', 7500.00, 'PRODUCT', 'PRD-SAND-3', 5000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(65, 3, 'Plain Bagel HQ3', 3500.00, 'PRODUCT', 'PRD-BAGEL-3', 2200.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(66, 3, 'Chocolate Chip Cookie HQ3', 2500.00, 'PRODUCT', 'PRD-COOKIE-3', 1500.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(67, 3, 'Plain Scone HQ3', 3800.00, 'PRODUCT', 'PRD-SCONE-3', 2400.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(68, 3, 'Glazed Donut HQ3', 3200.00, 'PRODUCT', 'PRD-DONUT-3', 2000.00, 'EA', 'EA', NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- =========================================
-- Step 8: Create Initial Stock for ALL Containers
-- =========================================
INSERT INTO product_stock (product_id, container_id, unit_qty, usage_qty, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
-- HQ 1 Products (39-48) in Containers 1-5
(39, 1, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (40, 1, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(41, 1, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (42, 1, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(43, 1, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (44, 1, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(45, 1, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (46, 1, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(47, 1, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (48, 1, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(39, 2, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (40, 2, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(41, 2, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (42, 2, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(43, 2, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (44, 2, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(45, 2, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (46, 2, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(47, 2, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (48, 2, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(39, 3, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (40, 3, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(41, 3, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (42, 3, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(43, 3, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (44, 3, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(45, 3, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (46, 3, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(47, 3, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (48, 3, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(39, 4, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (40, 4, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(41, 4, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (42, 4, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(43, 4, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (44, 4, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(45, 4, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (46, 4, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(47, 4, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (48, 4, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(39, 5, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (40, 5, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(41, 5, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (42, 5, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(43, 5, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (44, 5, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(45, 5, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (46, 5, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(47, 5, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (48, 5, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

-- HQ 2 Products (49-58) in Containers 6-10
(49, 6, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (50, 6, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(51, 6, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (52, 6, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(53, 6, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (54, 6, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(55, 6, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (56, 6, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(57, 6, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (58, 6, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(49, 7, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (50, 7, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(51, 7, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (52, 7, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(53, 7, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (54, 7, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(55, 7, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (56, 7, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(57, 7, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (58, 7, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(49, 8, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (50, 8, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(51, 8, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (52, 8, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(53, 8, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (54, 8, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(55, 8, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (56, 8, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(57, 8, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (58, 8, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(49, 9, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (50, 9, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(51, 9, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (52, 9, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(53, 9, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (54, 9, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(55, 9, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (56, 9, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(57, 9, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (58, 9, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(49, 10, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (50, 10, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(51, 10, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (52, 10, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(53, 10, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (54, 10, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(55, 10, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (56, 10, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(57, 10, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (58, 10, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

-- HQ 3 Products (59-68) in Containers 11-15
(59, 11, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (60, 11, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(61, 11, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (62, 11, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(63, 11, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (64, 11, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(65, 11, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (66, 11, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(67, 11, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (68, 11, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(59, 12, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (60, 12, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(61, 12, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (62, 12, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(63, 12, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (64, 12, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(65, 12, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (66, 12, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(67, 12, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (68, 12, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(59, 13, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (60, 13, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(61, 13, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (62, 13, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(63, 13, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (64, 13, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(65, 13, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (66, 13, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(67, 13, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (68, 13, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(59, 14, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (60, 14, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(61, 14, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (62, 14, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(63, 14, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (64, 14, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(65, 14, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (66, 14, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(67, 14, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (68, 14, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),

(59, 15, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (60, 15, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(61, 15, 90.00, 90.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (62, 15, 50.00, 50.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(63, 15, 70.00, 70.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (64, 15, 60.00, 60.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(65, 15, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (66, 15, 150.00, 150.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0),
(67, 15, 80.00, 80.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0), (68, 15, 120.00, 120.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- Summary
-- =========================================
SELECT 'Test data creation completed!' AS Status;
SELECT COUNT(*) AS 'HQs' FROM hq;
SELECT COUNT(*) AS 'Stores' FROM store;
SELECT COUNT(*) AS 'POS' FROM pos;
SELECT COUNT(*) AS 'Containers' FROM product_containers;
SELECT COUNT(*) AS 'Products' FROM product;
SELECT COUNT(*) AS 'Product Stock' FROM product_stock;
