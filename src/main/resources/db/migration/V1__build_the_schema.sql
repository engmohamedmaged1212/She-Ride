USE sheride;
CREATE TABLE users(
       id BIGINT AUTO_INCREMENT PRIMARY KEY ,
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

CREATE TABLE admins(
                       id BIGINT PRIMARY KEY ,
                       permissions JSON ,
                       last_login_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_admin_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE customers(
    id BIGINT PRIMARY KEY,
    verification_status ENUM ('PENDING' , 'APPROVED' , 'REJECTED') DEFAULT 'PENDING',
    verified_at DATETIME NULL,
    verified_by BIGINT ,
    total_trips INT ,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    CONSTRAINT fk_customer_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_verifier FOREIGN KEY (verified_by) REFERENCES admins(id) ON DELETE SET NULL
);

CREATE TABLE drivers(
                        id BIGINT PRIMARY KEY,
                        national_id_front_url       VARCHAR(500),
                        national_id_back_url        VARCHAR(500),
                        driver_license_url          VARCHAR(500),
                        id_verification_status      ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
                        id_verified_at              TIMESTAMP,
                        id_verified_by              BIGINT,
                        is_online                   BOOLEAN         NOT NULL DEFAULT FALSE,
                        current_latitude            DECIMAL(10,8),
                        current_longitude           DECIMAL(11,8),
                        total_trips                 INT             NOT NULL DEFAULT 0,
                        average_rating              DECIMAL(3,2)    DEFAULT 0.00,
                        CONSTRAINT fk_driver_user       FOREIGN KEY (id)            REFERENCES users(id) ON DELETE CASCADE,
                        CONSTRAINT fk_driver_verifier   FOREIGN KEY (id_verified_by) REFERENCES admins(id) ON DELETE SET NULL
);
CREATE TABLE vehicles(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT ,
    plate_number VARCHAR(20) NOT NULL UNIQUE ,
    brand VARCHAR(50) ,
    model VARCHAR(50) ,
    year YEAR ,
    color VARCHAR(50) ,
    license_image_url VARCHAR(500) ,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE ,
    approval_status ENUM('PENDING' , 'REJECTED' , 'APPROVED') NOT NULL DEFAULT 'PENDING' ,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP ,
    approved_by BIGINT , -- who is the admin

    CONSTRAINT fk_vehicles_driver FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_vehicles_verifier FOREIGN KEY (approved_by) REFERENCES admins(id) ON DELETE SET NULL
);
CREATE TABLE trips(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      customer_id BIGINT NOT NULL ,
                      driver_id BIGINT ,
                      pickup_address varchar(255) NOT NULL,
                      pickup_latitude  DECIMAL(9,6) NOT NULL,
                      pickup_longitude DECIMAL(9,6) NOT NULL,

                      dropoff_address varchar(255) NOT NULL,
                      dropoff_latitude  DECIMAL(9,6) NOT NULL,
                      dropoff_longitude DECIMAL(9,6) NOT NULL,

                      status ENUM(
                            'REQUESTED',
                            'ACCEPTED',
                            'IN_PROGRESS',
                            'COMPLETED',
                            'CANCELLED',
                            'WAIT_DRIVER',
                            'WAIT_CUSTOMER'
                     ) NOT NULL DEFAULT 'REQUESTED',

                      is_cancelled BOOLEAN DEFAULT false ,
                      cancellation_reason varchar(500),
                      cancelled_by BIGINT ,

                      final_price             DECIMAL(8,2) NOT NULL,
                      distance_km             DECIMAL(6,2) NOT NULL,
                      duration_minutes        INT,
                      price_per_km_used DECIMAL(8,2),

                      requested_at            DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      accepted_at             DATETIME,
                      driver_arrived_at       DATETIME,
                      started_at              DATETIME,
                      completed_at            DATETIME,
                      cancelled_at            DATETIME,

                      CONSTRAINT fk_trip_customer     FOREIGN KEY (customer_id)   REFERENCES customers(id),
                      CONSTRAINT fk_trip_driver       FOREIGN KEY (driver_id)     REFERENCES drivers(id),
                      CONSTRAINT fk_trip_cancelled_by FOREIGN KEY (cancelled_by)  REFERENCES users(id)
);


CREATE TABLE trip_ratings (
    id BIGINT AUTO_INCREMENT  PRIMARY KEY ,
    trip_id BIGINT NOT NULL UNIQUE ,

    driver_rating TINYINT CHECK ( driver_rating BETWEEN 1 AND 5),
    driver_comment VARCHAR(500),

    customer_rating TINYINT CHECK ( customer_rating BETWEEN 1 AND 5),
    customer_comment VARCHAR(500),
    CONSTRAINT fk_rating_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);
CREATE TABLE reports(
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        reporter_id BIGINT NOT NULL ,
                        reported_user_id BIGINT NOT NULL ,
                        trip_id BIGINT ,
                        report_type ENUM('HARASSMENT','FAKE_GENDER','SPAM','INAPPROPRIATE_BEHAVIOR','OTHER') NOT NULL,
                        description VARCHAR(1000),
                        status ENUM('RESOLVED' , 'INVALID', 'UNDER_REVIEW') NOT NULL DEFAULT 'UNDER_REVIEW' ,
                        resolved_by BIGINT,
                        resolution_note VARCHAR(500),
                        created_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_report_reporter      FOREIGN KEY (reporter_id)       REFERENCES users(id),
                        CONSTRAINT fk_report_reported      FOREIGN KEY (reported_user_id)  REFERENCES users(id),
                        CONSTRAINT fk_report_trip          FOREIGN KEY (trip_id)           REFERENCES trips(id) ON DELETE SET NULL,
                        CONSTRAINT fk_report_resolver      FOREIGN KEY (resolved_by)       REFERENCES admins(id)
);

CREATE TABLE system_settings(
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL,
     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);
CREATE TABLE otp_verifications (
                                   id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
                                   phone_number    VARCHAR(20)     NOT NULL,
                                   otp_code        VARCHAR(10)     NOT NULL,
                                   purpose         ENUM('REGISTER','LOGIN','RESET_PASSWORD') NOT NULL,
                                   is_used         BOOLEAN         NOT NULL DEFAULT FALSE,
                                   expires_at      TIMESTAMP       NOT NULL,
                                   created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   INDEX idx_otp_phone (phone_number)
);

CREATE INDEX idx_users_phone     ON users(phone_number);
CREATE INDEX idx_users_role      ON users(role);
CREATE INDEX idx_users_blocked   ON users(is_blocked);

CREATE INDEX idx_trips_customer  ON trips(customer_id);
CREATE INDEX idx_trips_driver    ON trips(driver_id);
CREATE INDEX idx_trips_status    ON trips(status);
CREATE INDEX idx_trips_requested ON trips(requested_at);




