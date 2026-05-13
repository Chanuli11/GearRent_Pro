-- =============================================================
-- GearRent Pro - Multi-Branch Equipment Rental System
-- MySQL Schema + Sample Data
-- =============================================================

DROP DATABASE IF EXISTS gearrent_pro;
CREATE DATABASE gearrent_pro;
USE gearrent_pro;

-- ---------- Branch ----------
CREATE TABLE branch (
    branch_id    INT AUTO_INCREMENT PRIMARY KEY,
    branch_code  VARCHAR(20) UNIQUE NOT NULL,
    name         VARCHAR(100) NOT NULL,
    address      VARCHAR(255),
    contact      VARCHAR(20)
);

-- ---------- User ----------
CREATE TABLE app_user (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    role      ENUM('ADMIN','BRANCH_MANAGER','STAFF') NOT NULL,
    branch_id INT,
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- ---------- Category ----------
CREATE TABLE category (
    category_id        INT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(50) NOT NULL,
    description        VARCHAR(255),
    base_price_factor  DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    weekend_multiplier DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    late_fee_per_day   DECIMAL(10,2) NOT NULL DEFAULT 500.00,
    active             BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Equipment ----------
CREATE TABLE equipment (
    equipment_id     INT AUTO_INCREMENT PRIMARY KEY,
    category_id      INT NOT NULL,
    branch_id        INT NOT NULL,
    brand            VARCHAR(50),
    model            VARCHAR(50),
    purchase_year    INT,
    daily_base_price DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    status           ENUM('AVAILABLE','RESERVED','RENTED','UNDER_MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (category_id) REFERENCES category(category_id),
    FOREIGN KEY (branch_id)   REFERENCES branch(branch_id)
);

-- ---------- Customer ----------
CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    nic         VARCHAR(20) UNIQUE NOT NULL,
    contact     VARCHAR(20),
    email       VARCHAR(100),
    address     VARCHAR(255),
    membership  ENUM('REGULAR','SILVER','GOLD') NOT NULL DEFAULT 'REGULAR'
);

-- ---------- Membership Config ----------
CREATE TABLE membership_config (
    membership   ENUM('REGULAR','SILVER','GOLD') PRIMARY KEY,
    discount_pct DECIMAL(5,2) NOT NULL
);

-- ---------- Reservation ----------
CREATE TABLE reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id   INT NOT NULL,
    customer_id    INT NOT NULL,
    branch_id      INT NOT NULL,
    start_date     DATE NOT NULL,
    end_date       DATE NOT NULL,
    status         ENUM('ACTIVE','CANCELLED','CONVERTED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id)  REFERENCES customer(customer_id),
    FOREIGN KEY (branch_id)    REFERENCES branch(branch_id)
);

-- ---------- Rental ----------
CREATE TABLE rental (
    rental_id          INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id       INT NOT NULL,
    customer_id        INT NOT NULL,
    branch_id          INT NOT NULL,
    start_date         DATE NOT NULL,
    end_date           DATE NOT NULL,
    actual_return_date DATE,
    rental_amount      DECIMAL(10,2) NOT NULL,
    security_deposit   DECIMAL(10,2) NOT NULL,
    membership_disc    DECIMAL(10,2) DEFAULT 0,
    long_rental_disc   DECIMAL(10,2) DEFAULT 0,
    late_fee           DECIMAL(10,2) DEFAULT 0,
    damage_charge      DECIMAL(10,2) DEFAULT 0,
    damage_notes       VARCHAR(255),
    final_payable      DECIMAL(10,2) NOT NULL,
    payment_status     ENUM('PAID','PARTIALLY_PAID','UNPAID') NOT NULL DEFAULT 'UNPAID',
    rental_status      ENUM('ACTIVE','RETURNED','OVERDUE','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id)  REFERENCES customer(customer_id),
    FOREIGN KEY (branch_id)    REFERENCES branch(branch_id)
);

-- =============================================================
-- SAMPLE DATA
-- =============================================================

INSERT INTO branch (branch_code, name, address, contact) VALUES
('BR001','Panadura Main','12 Galle Rd, Panadura','0382234567'),
('BR002','Galle Coastal','45 Matara Rd, Galle','0912233445'),
('BR003','Colombo Hub','100 Union Pl, Colombo 02','0112345678');

INSERT INTO app_user (username, password, full_name, role, branch_id) VALUES
('admin',   'admin123',   'System Admin',    'ADMIN',          NULL),
('manager1','manager123', 'Kasun Perera',    'BRANCH_MANAGER', 1),
('manager2','manager123', 'Nimal Silva',     'BRANCH_MANAGER', 2),
('staff1',  'staff123',   'Amal Fernando',   'STAFF',          1),
('staff2',  'staff123',   'Sunil Jayasuriya','STAFF',          2),
('staff3',  'staff123',   'Ravi Bandara',    'STAFF',          3);

INSERT INTO category (name, description, base_price_factor, weekend_multiplier, late_fee_per_day) VALUES
('Camera',   'DSLR / Mirrorless cameras', 1.00, 1.20, 500),
('Lens',     'Prime and zoom lenses',     0.80, 1.10, 300),
('Drone',    'Aerial drones',             1.50, 1.30, 1000),
('Lighting', 'Studio lighting kits',      0.90, 1.15, 400),
('Audio',    'Microphones, recorders',    0.85, 1.10, 350);

INSERT INTO membership_config (membership, discount_pct) VALUES
('REGULAR', 0),
('SILVER',  5),
('GOLD',    10);

INSERT INTO equipment (category_id, branch_id, brand, model, purchase_year, daily_base_price, security_deposit, status) VALUES
(1,1,'Canon','EOS R6',2022,3500,30000,'AVAILABLE'),
(1,1,'Sony','A7 IV',2023,4000,35000,'AVAILABLE'),
(1,2,'Nikon','Z6 II',2022,3500,30000,'AVAILABLE'),
(1,3,'Canon','5D Mark IV',2021,3000,28000,'AVAILABLE'),
(2,1,'Canon','24-70mm f/2.8',2022,1500,15000,'AVAILABLE'),
(2,2,'Sony','85mm f/1.4 GM',2023,1800,18000,'AVAILABLE'),
(2,3,'Sigma','35mm f/1.4 Art',2021,1200,12000,'AVAILABLE'),
(3,1,'DJI','Mavic 3',2023,6000,60000,'AVAILABLE'),
(3,2,'DJI','Mini 3 Pro',2023,4500,40000,'AVAILABLE'),
(3,3,'Autel','EVO Lite+',2022,5000,50000,'UNDER_MAINTENANCE'),
(4,1,'Godox','SL-150 II',2022,2000,15000,'AVAILABLE'),
(4,1,'Aputure','120D II',2022,2500,18000,'AVAILABLE'),
(4,2,'Godox','AD600 Pro',2023,2800,20000,'AVAILABLE'),
(4,3,'Neewer','660 LED Panel',2021,1500,10000,'AVAILABLE'),
(5,1,'Rode','Wireless Go II',2023,1200,9000,'AVAILABLE'),
(5,2,'Sennheiser','MKE 600',2022,1500,12000,'AVAILABLE'),
(5,3,'Zoom','H6 Recorder',2022,1300,10000,'AVAILABLE'),
(5,1,'Shure','SM7B',2023,1400,11000,'AVAILABLE'),
(1,2,'Fujifilm','X-T5',2023,3200,28000,'AVAILABLE'),
(3,1,'DJI','Air 2S',2022,5500,50000,'AVAILABLE'),
(2,1,'Tamron','70-200 f/2.8',2022,1600,15000,'AVAILABLE');

INSERT INTO customer (name, nic, contact, email, address, membership) VALUES
('Pradeep Kumara','912345678V','0771112233','pradeep@mail.com','12 Lake Rd, Panadura','GOLD'),
('Nisha Wijesinghe','935678912V','0712223344','nisha@mail.com','5 Main St, Galle','SILVER'),
('Tharindu Bandara','882233445V','0763334455','tharindu@mail.com','7 Park Rd, Colombo','REGULAR'),
('Sanduni Perera','955566778V','0754445566','sanduni@mail.com','3 Hill St, Kandy','GOLD'),
('Ranjith Silva','870011223V','0775556677','ranjith@mail.com','9 Beach Rd, Galle','SILVER'),
('Iresha Jayawardena','940099887V','0716667788','iresha@mail.com','22 Flower Rd, Colombo','REGULAR'),
('Dinesh Mendis','891122334V','0777778899','dinesh@mail.com','55 Union Pl, Colombo','REGULAR'),
('Kavindi Fernando','962233445V','0718889900','kavindi@mail.com','18 Sea Rd, Panadura','SILVER'),
('Mahesh Wijeratne','850033445V','0769990011','mahesh@mail.com','30 Lake Dr, Kandy','GOLD'),
('Anushka Gunasekara','970011556V','0751112233','anushka@mail.com','4 Temple Rd, Galle','REGULAR');

-- Sample rentals (some active, returned, overdue)
INSERT INTO rental (equipment_id, customer_id, branch_id, start_date, end_date, rental_amount, security_deposit, membership_disc, long_rental_disc, final_payable, payment_status, rental_status) VALUES
(1,1,1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 24500, 30000, 2450, 0, 22050, 'PAID', 'ACTIVE'),
(8,4,1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 48000, 60000, 4800, 2160, 41040, 'PAID', 'OVERDUE'),
(5,2,1, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 7500, 15000, 375, 0, 7125, 'PAID', 'RETURNED'),
(3,5,2, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 24500, 30000, 1225, 0, 23275, 'PAID', 'ACTIVE'),
(11,3,1, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), 16000, 15000, 0, 720, 15280, 'PAID', 'RETURNED');

-- Mark rented equipment status
UPDATE equipment SET status='RENTED' WHERE equipment_id IN (1,8,3);

-- Sample reservation
INSERT INTO reservation (equipment_id, customer_id, branch_id, start_date, end_date, status) VALUES
(2, 6, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'ACTIVE'),
(9, 7, 2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'ACTIVE');

-- Mark a damaged returned rental
UPDATE rental SET damage_charge=2000, damage_notes='Lens scratch', actual_return_date=DATE_SUB(CURDATE(), INTERVAL 10 DAY) WHERE rental_id=3;
