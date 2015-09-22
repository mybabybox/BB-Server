-- Table Engine Type
SHOW TABLE STATUS WHERE Name = 'xxx';

-- Alter Table and Column Type
-- NOTE: Only need to alter those columns which are indexed as keys
ALTER TABLE xxx CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE xxx CHANGE column_name column_name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

