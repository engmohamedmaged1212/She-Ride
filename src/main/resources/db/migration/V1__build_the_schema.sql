USE sheride;
CREATE TABLE users(
       id BIGINT PRIMARY KEY ,
       first_name   VARCHAR(50)  NOT NULL,
       last_name    VARCHAR(50)  NOT NULL,
       phone_number VARCHAR(20),
       date_of_birth DATE,
       national_id_number VARCHAR(14) UNIQUE,
       role  ENUM('CUSTOMER','DRIVER','ADMIN') NOT NULL ,
       gender  ENUM('FEMALE' ,'MALE')  NOT NULL DEFAULT 'FEMALE' ,
       profile_picture_url VARCHAR(500),
       is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
       is_blocked          BOOLEAN         NOT NULL DEFAULT FALSE,
       block_reason        VARCHAR(255) ,
       created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers(
    id BIGINT PRIMARY KEY ,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    national_id_number VARCHAR(14) UNIQUE ,
    verification_status ENUM ('PENDING' , 'APPROVED' , 'REJECTED') DEFAULT 'PENDING',
    verified_at DATETIME DEFAULT CURRENT_TIMESTAMP ,
    verified_by BIGINT , -- admin who approved
    total_trips INT ,
    CONSTRAINT fk_customer_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE admins(
    id BIGINT PRIMARY KEY ,
    permission JSON ,
    last_login_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE vehicles(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
);

