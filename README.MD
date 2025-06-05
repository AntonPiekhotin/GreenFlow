# Welcome to GreenFlow 🌿

**GreenFlow** is a system for managing lawn care and landscaping services. The platform is designed for clients to create orders and care plans, and for workers to accept and fulfill these tasks using company equipment. The system accounts for seasonality, task dependencies, and equipment tracking.

The application is based on a **microservices architecture** and deployed in the cloud using Kubernetes.

## Table of Contents
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Services](#services)

## Technology Stack

- 🍃 [Spring Boot](https://spring.io/projects/spring-boot)
- ⚡ [Spring Web](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- 🛡️ [Spring Security + OAuth2](https://spring.io/projects/spring-security)
- 🗃️ [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- 🧠 [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- 🍃 [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- 🐇 [RabbitMQ](https://www.rabbitmq.com/)
- 🐘 [PostgreSQL](https://www.postgresql.org/)
- 🧠 [Redis](https://redis.io/)
- 🍃 [MongoDB](https://www.mongodb.com/)
- ✅ [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/)
- ✨ [Lombok](https://projectlombok.org/)
- 🧪 [JUnit 5](https://junit.org/junit5/)
- 🐳 [Docker](https://www.docker.com/)
- ☸️ [Kubernetes](https://kubernetes.io/)
- ☁️ [DigitalOcean](https://www.digitalocean.com/)
- ⚙️ [GitHub Actions (CI/CD)](https://docs.github.com/en/actions)
- 🗂️ [Amazon S3-compatible storage (Spaces)](https://docs.digitalocean.com/products/spaces/)

## Features

### 🧩 Microservices Architecture
Each service is responsible for a distinct domain. Services interact asynchronously (via RabbitMQ) or synchronously(via REST), and are deployed as separate containers in Kubernetes.

### 🔒 Security
- JWT-based authentication and role-based access control
- Google OAuth2 login support
- Secure storage of passwords using BCrypt

### 🌱 Lawn and Garden Automation
- Clients create care plans consisting of tasks
- Tasks based on season, service type, and dependencies
- Workers accept tasks based on geolocation

### 📍 Geolocation Features
- Orders are shown on a map
- Workers are assigned a work zone (e.g., 20 km radius)

### ⚙️ Infrastructure and DevOps
- Dockerized microservices
- CI/CD pipeline with GitHub Actions
- Deployment to Kubernetes cluster on DigitalOcean
- Domain name registration and SSL support
- Image uploads via S3-compatible storage

### ✅ Testing
- Unit tests using JUnit 5 and Mockito
- Test stage integrated into CI workflow

## Services

### 🌐 API Gateway
- Entry point for all external requests
- Performs routing, authentication filtering

### 🔑 Auth Service
- Handles login, registration, and JWT generation
- Supports standard login and Google OAuth2

### 💲 Billing Service
- Manages user balances and payments
- Connects to PayPal API for payment processing
- Listens for billing events from other services via RabbitMQ 

### 👤 Client Service
- Manages client accounts and profiles
- Handles billing

### 🧑‍🌾 Worker Service
- Manages worker accounts and profiles
- Hangles billing

### 📦 Equipment Service
- Manages assignment of tools and materials to workers
- Tracks availability and usage history

### 🌱 Garden Service 
- Provides REST API for managing gardens

### 📤 Notification Service
- Sends messages (e.g., order confirmation, task completed) to clients and workers via email
- Listens for events from other services via RabbitMQ

### 📅 Order Service
- Manages client orders and care plans of their lawns and gardens
- Provides REST API for order management
- Asynchronously connects to RabbitMQ

### 📍 Open Order Service
- Stores coordinates of open orders
- Provides REST API for geo-search orders for workers


### 🧠 Common Module
- Shared library containing common logic, DTOs, exceptions, and utilities
- Imported as a dependency into other services
