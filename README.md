# SheDrive Backend

## Overview

SheDrive is a secure ride-hailing backend application designed primarily for women, where only verified female customers and drivers can use the platform. The project is built with Java and Spring Boot following clean architecture principles and modern backend development practices.

The application focuses on security, authentication, account verification, and scalable design rather than simple CRUD operations.

## Key Features

* User registration for Customers and Drivers.
* Phone number verification using One-Time Password (OTP).
* Redis-based OTP storage with automatic expiration.
* JWT Authentication and Authorization.
* Role-based access control (Customer, Driver, Admin).
* BCrypt password hashing.
* Account activation only after successful OTP verification.
* Admin management with secure bootstrap initialization.
* Driver verification workflow.
* User blocking and unblocking.
* Password reset using OTP.
* Flyway database versioning.
* RESTful API design.

## Security

The project follows several security best practices:

* Passwords are never stored in plain text.
* BCrypt automatically generates a unique salt for every password.
* JWT Access Tokens are digitally signed.
* Stateless authentication using Spring Security.
* OTP codes are stored inside Redis with configurable expiration.
* Invalid OTP attempts can be limited to reduce brute-force attacks.
* Protected endpoints require valid JWT tokens.
* Role-based authorization using Spring Security.

## Technologies

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL
* Redis
* Flyway
* JWT
* Hibernate
* Maven
* Lombok
* MapStruct

## Project Goals

This project is intended to demonstrate production-oriented backend development concepts including:

* Authentication & Authorization
* Secure API Design
* Database Modeling
* Transaction Management
* Redis Caching
* OTP Verification Flow
* Clean Architecture
* DTO Mapping
* Exception Handling
* REST API Best Practices

The project is continuously evolving with additional business features and improvements.
