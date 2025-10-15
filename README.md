# Personalized Succulent Gifting Platform (Backend)

This repository contains the backend implementation of a Personalized Succulent Gifting Platform, built with Spring Boot.

## Project Overview

The Personalized Succulent Gifting Platform is a specialized e-commerce system that allows users to purchase and customize succulent arrangements. The platform supports various customization options including Feng Shui and zodiac-based recommendations.

## Technologies Used

-   Java
-   Spring Boot
-   Spring Security with JWT Authentication
-   WebSocket for real-time notifications
-   PostgreSQL Database
-   Docker for containerization
-   Maven for dependency management
-   Swagger for API documentation

## Key Features

-   User authentication and authorization
-   Custom product creation and management
-   Order processing
-   Real-time notifications
-   Coupon system
-   Comment/Review system
-   Feng Shui and Zodiac-based recommendations

## Getting Started

### Prerequisites

-   JDK 17 or later
-   Maven
-   Docker and Docker Compose
-   PostgreSQL

### Running with Docker

1. Start the PostgreSQL database:

    ```bash
    docker-compose -f postgre-docker-compose.yml up -d
    ```

2. Build and run the application:

    ```bash
    docker-compose up --build
    ```

> Or use the all-in-one compose file:

```bash
docker-compose -f postgres-docker-compose.yml up --build
```

### Running Locally

1. Start the PostgreSQL database using the development compose file:

    ```bash
    cd development
    docker-compose -f database-compose.yml up -d
    ```

2. Run the Spring Boot application:

    ```bash
    ./mvnw spring-boot:run
    ```

## Project Structure

```plaintext
src/
├── main/
│   ├── java/com/exe201/group1/psgp_be/
│   │   ├── configs/         # Configuration classes
│   │   ├── controllers/     # REST API controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── enums/          # Enumerations
│   │   ├── models/         # Entity models
│   │   ├── repositories/   # Data access layer
│   │   ├── services/       # Business logic
│   │   └── utils/          # Utility classes
│   └── resources/
│       └── application.properties  # Application configuration
```

## API Documentation

Once the application is running, you can access the Swagger UI documentation at:

```plaintext
http://localhost:8080/swagger-ui/
```
