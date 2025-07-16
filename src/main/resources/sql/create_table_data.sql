-- テーブル削除 (存在する場合)
DROP TABLE IF EXISTS expense_applications;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS statuses;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS departments;
DROP SEQUENCE IF EXISTS seq_expense_application_id;

-- 部門テーブル
CREATE TABLE departments (
    dept_id   SERIAL PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE
);

-- 役職マスタ
CREATE TABLE roles (
    role_id   SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- ユーザテーブル
CREATE TABLE users (
    user_id       VARCHAR(50) PRIMARY KEY, -- 社員番号など
    password      VARCHAR(100) NOT NULL,  -- TODO: 本来はハッシュ化すべき
    salt          VARCHAR(255) NOT NULL, -- パスワードハッシュ化用のソルト値
    user_name     VARCHAR(100) NOT NULL,
    department_id INTEGER NOT NULL REFERENCES departments(dept_id),
    role_id       INTEGER NOT NULL REFERENCES roles(role_id)
);

-- 勘定科目マスタ
CREATE TABLE accounts (
    account_id   SERIAL PRIMARY KEY,
    account_name VARCHAR(50) NOT NULL UNIQUE
);

-- ステータスマスタ
CREATE TABLE statuses (
    status_id   INTEGER PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

-- 経費申請テーブル
CREATE TABLE expense_applications (
    application_id    SERIAL PRIMARY KEY,
    applicant_user_id VARCHAR(50) NOT NULL REFERENCES users(user_id),
    application_date  DATE NOT NULL,
    account_id        INTEGER NOT NULL REFERENCES accounts(account_id),
    payment_date      DATE NOT NULL,
    payee             VARCHAR(100) NOT NULL,
    amount            INTEGER NOT NULL CHECK (amount > 0), -- TODO: 上限チェックは？
    description       VARCHAR(500) NOT NULL,
    receipt_path      VARCHAR(255),
    status_id         INTEGER NOT NULL REFERENCES statuses(status_id),
    approver1_user_id VARCHAR(50) REFERENCES users(user_id), -- 課長
    approval1_date    TIMESTAMP,
    approver2_user_id VARCHAR(50) REFERENCES users(user_id), -- 部長
    approval2_date    TIMESTAMP,
    rejection_reason  VARCHAR(500)
);

-- シーケンス (必要に応じて)
-- CREATE SEQUENCE seq_expense_application_id START 1; -- SERIALで代用可

-- 初期データ挿入
INSERT INTO departments (dept_name) VALUES ('営業部'), ('開発部'), ('人事部');
INSERT INTO roles (role_name) VALUES ('一般'), ('課長'), ('部長');
INSERT INTO roles (role_id, role_name) VALUES (9, 'システム管理者');

INSERT INTO accounts (account_name) VALUES ('交通費'), ('備品費'), ('その他');
INSERT INTO statuses (status_id, status_name) VALUES
(1, '申請中'),
(2, '課長承認済'),
(3, '承認完了'),
(8, '課長却下'),
(9, '部長却下');

-- ユーザーデータ例 (パスワードはハッシュ化されたもの、ソルトはランダムな文字列とする - 演習用)
INSERT INTO users (user_id, password, salt, user_name, department_id, role_id) VALUES
('emp001', 'e46e334f3e4f84be1a93d1905ad07dcfa5704a2b3e571b94208cb6f1e01d64fb', 'abcdefghijklmnop', '一般 太郎', 2, 1), -- 開発部 一般（パスワード: pass1）
('mgr001', '2f4be749c34bdc90ae88aa5549503c69339b59a74fc5a1ab8c991da3e811e64f', 'qrstuvwxyzabcdef', '課長 次郎', 2, 2), -- 開発部 課長（パスワード: pass2）
('emp002', 'b45d305392719cf6ba0f210af04fcc51df2b37c9a867ca33089bdc32e87e2d1c', 'ghijklmnopqrstu', '営業 花子', 1, 1), -- 営業部 一般（パスワード: pass3）
('mgr002', '9aa4258696949339f4c15c1022277775fb0f3d084727635a40a93b4ac9c7a097', 'vwxyzabcdefghij', '営業 部長', 1, 3), -- 営業部 部長（パスワード: pass4）
('mgr003', '27d5b79150dbac1be6e1a51bc972303ddb471080b5153486ff7f90fd6c53718e', 'klmnopqrstuvwxy', '開発 部長', 2, 3), -- 開発部 部長（パスワード: pass5）
('admin001', '006609f26371f0eaf2984bd066e40c0d5664bd6104c11cc2f2d07bd08241dc0f', 'zabcdefghijklmn', 'システム 管理者', 2, 9); -- システム管理者（パスワード: adminpass）
-- 経費申請データ例
INSERT INTO expense_applications (applicant_user_id, application_date, account_id, payment_date, payee, amount, description, status_id, approver1_user_id, approval1_date, approver2_user_id, approval2_date, rejection_reason) VALUES
('emp001', CURRENT_DATE - INTERVAL '3 day', 1, CURRENT_DATE - INTERVAL '3 day', 'JR東日本', 880, '客先訪問交通費 (往復)', 1, NULL, NULL, NULL, NULL, NULL), -- 申請中
('emp001', CURRENT_DATE - INTERVAL '1 day', 2, CURRENT_DATE - INTERVAL '1 day', '文具店XYZ', 45000, '開発用ディスプレイ購入', 1, NULL, NULL, NULL, NULL, NULL), -- 申請中
('emp002', CURRENT_DATE - INTERVAL '5 day', 1, CURRENT_DATE - INTERVAL '5 day', '高速バス', 60000, '遠方出張交通費', 2, 'mgr002', CURRENT_DATE - INTERVAL '4 day', NULL, NULL, NULL), -- 課長承認済 (部長待ち)
('emp001', CURRENT_DATE - INTERVAL '10 day', 3, CURRENT_DATE - INTERVAL '10 day', '懇親会会場', 70000, '部署懇親会費用', 3, 'mgr001', CURRENT_DATE - INTERVAL '9 day', 'mgr003', CURRENT_DATE - INTERVAL '8 day', NULL), -- 部長承認済
('emp002', CURRENT_DATE - INTERVAL '7 day', 2, CURRENT_DATE - INTERVAL '7 day', '事務用品店ABC', 3000, '事務用品購入', 8, 'mgr002', CURRENT_DATE - INTERVAL '6 day', NULL, NULL, '予算超過のため却下'), -- 課長却下
('emp001', CURRENT_DATE - INTERVAL '6 day', 1, CURRENT_DATE - INTERVAL '6 day', '日本タクシー', 20000, '客先訪問交通費 (往復)', 1, NULL, NULL, NULL, NULL, NULL), -- 課長却下予定
('emp001', CURRENT_DATE - INTERVAL '6 day', 1, CURRENT_DATE - INTERVAL '6 day', '叙々苑', 60000, '懇親会費用', 2, 'mgr001', CURRENT_DATE - INTERVAL '5 day', NULL, NULL, NULL), -- 部長却下予定
('emp001', CURRENT_DATE - INTERVAL '2 day', 2, CURRENT_DATE - INTERVAL '2 day', 'ビックカメラ', 80000, 'ノートパソコン', 1, NULL, NULL, NULL, NULL, NULL), -- 申請中
('emp001', CURRENT_DATE - INTERVAL '4 day', 2, CURRENT_DATE - INTERVAL '4 day', 'ユニクロ', 80000, 'シャツ', 2, 'mgr001', CURRENT_DATE - INTERVAL '3 day', NULL, NULL, NULL); -- 課長承認済み
