ALTER TABLE users
    ADD COLUMN password_hash VARCHAR(255) NOT NULL,
ADD CONSTRAINT uq_users_phone UNIQUE(phone_number);