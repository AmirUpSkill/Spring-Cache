
# Spring Boot Redis Cache Example (spring-redis)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This project demonstrates the effective implementation of Redis as a distributed caching layer within a standard Spring Boot web application. It provides a RESTful API for managing Product entities (CRUD operations) and leverages Spring's Caching abstraction with Redis to significantly reduce database load and improve API response times for read operations.

## Table of Contents

- [Spring Boot Redis Cache Example (spring-redis)](#spring-boot-redis-cache-example-spring-redis)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Goals](#goals)
  - [Key Features](#key-features)
  - [Technology Stack](#technology-stack)
  - [Architecture](#architecture)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Cloning](#cloning)
  - [Running the Application](#running-the-application)
    - [1. Start Redis](#1-start-redis)
    - [2. Run the Spring Boot Application](#2-run-the-spring-boot-application)
  - [Testing the Cache](#testing-the-cache)
    - [Manual Testing (Postman/curl)](#manual-testing-postmancurl)
    - [Integration Testing (TestContainers)](#integration-testing-testcontainers)
  - [API Endpoints](#api-endpoints)
  - [Caching Strategy Explained](#caching-strategy-explained)
  - [Configuration Highlights](#configuration-highlights)
  - [Project Structure](#project-structure)

## Introduction

The primary purpose of this project is to serve as a clear, practical example of integrating Redis for caching in a Spring Boot application. It focuses on using Spring's declarative caching annotations (`@Cacheable`, `@CachePut`, `@CacheEvict`) to transparently manage cache interactions within the service layer.

## Goals

*   Implement a functional RESTful CRUD API for `Product` entities.
*   Seamlessly integrate Redis as a caching layer using Spring Boot's abstractions.
*   Demonstrate performance improvements (latency reduction) for read operations via caching.
*   Reduce direct database (H2) queries for frequently accessed data.
*   Ensure data consistency between the cache and database using appropriate cache invalidation/update strategies.
*   Provide a working example of Spring Caching annotations.
*   (Future Goal) Include integration tests using TestContainers to validate caching behavior against a real Redis instance.

## Key Features

*   **RESTful CRUD API:** Endpoints for Create, Read, Update, Delete operations on Products.
*   **Redis Cache Integration:** Uses Redis managed via Docker Compose for caching.
*   **Declarative Caching:** Leverages `@Cacheable`, `@CachePut`, `@CacheEvict` for clean separation of concerns.
*   **Automatic Cache Management:** Create, update, and delete operations automatically update or invalidate the cache.
*   **Data Persistence:** Uses H2 in-memory database for simplicity.
*   **DTO Pattern:** Uses `ProductDto` for API interactions and cache storage.
*   **JSON Serialization:** Configured to store DTOs as JSON in Redis for readability and interoperability.
*   **Docker Support:** Includes `docker-compose.yml` for easy Redis setup.
*   **Validation:** Uses Bean Validation (`jakarta.validation`) on DTOs.

## Technology Stack

| Technology         | Purpose                                  |
| ------------------ | ---------------------------------------- |
| Java 21            | Core programming language                |
| Spring Boot 3.x    | Application framework                    |
| Spring Web         | Building RESTful APIs (Spring MVC)       |
| Spring Data JPA    | Data access and persistence (ORM)        |
| Spring Data Redis  | Redis integration                        |
| Spring Cache       | Caching abstraction layer                |
| Spring Validation  | Bean validation                          |
| H2 Database        | In-memory relational database            |
| Redis              | Distributed In-Memory Cache              |
| Jedis/Lettuce      | Redis Java client (via Spring Data Redis)|
| Jackson            | JSON serialization/deserialization       |
| TestContainers     | Integration testing with containers      |
| Maven              | Build and dependency management          |
| Lombok             | Reduce boilerplate code                  |
| Docker / Docker Compose | Containerization for Redis             |

## Architecture

The application follows a standard layered architecture:

```

```

*   **Controller:** Handles HTTP requests (`/api/product`), uses `ProductDto`, delegates to Service.
*   **Service:** Contains business logic (`ProductServiceImpl`), interacts with Repository, applies caching annotations, maps between Entity and DTO.
*   **Repository:** Data access interface (`ProductRepository`) using Spring Data JPA.
*   **Entity:** `Product` JPA entity representing database table.
*   **DTO:** `ProductDto` for API and cache data transfer.
*   **Config:** `RedisCacheConfig` sets up Redis connection, serialization (JSON), and TTL.
*   **External:** Redis (via Docker) and H2 Database.

## Getting Started

### Prerequisites

*   **Java 21 JDK** (or later compatible version)
*   **Maven** 3.6+
*   **Docker** and **Docker Compose**
*   **Git**
*   An IDE (like IntelliJ IDEA, VS Code, Eclipse) - Optional but recommended
*   An API Client (like Postman or curl) for testing

### Cloning

Clone the repository from GitHub:

```bash
git clone https://github.com/AmirUpSkill/Spring-Cache.git
cd Spring-Cache
```
*(Note: The repository folder might be named `Spring-Cache`)*

## Running the Application

### 1. Start Redis

The project requires a running Redis instance. Use Docker Compose to start one easily:

```bash
# Navigate to the project's root directory (where docker-compose.yml is)
cd Spring-Cache # Or your project root folder

# Start Redis in detached mode
docker-compose up -d

# Verify Redis is running
docker-compose ps
# You should see the spring-redis-redis-1 container running and port 6379 mapped.

# Optional: Test connection
docker-compose exec redis redis-cli ping
# Should return PONG
```

Redis will now be running and accessible on `localhost:6379`.

### 2. Run the Spring Boot Application

You can run the application using Maven or your IDE:

**Using Maven:**

```bash
# From the project's root directory
mvn spring-boot:run
```

**Using an IDE:**

*   Import the project as a Maven project.
*   Locate the `SpringRedisApplication.java` file in `src/main/java/com/amir/spring_redis`.
*   Right-click and run it as a Java application.

The application will start, connect to the H2 database (creating tables) and the Redis instance. Check the console logs for output similar to:

```
... Tomcat started on port 8080 (http) ...
... Started SpringRedisApplication in X.XXX seconds ...
```

The application is now running and accessible at `http://localhost:8080`.

## Testing the Cache

The core goal is to observe caching in action. Pay attention to the **Hibernate SQL logs** in the application console output.

### Manual Testing (Postman/curl)

Follow the scenarios detailed previously (Create -> Get (Miss) -> Get (Hit) -> Update -> Get (Hit) -> Delete -> Get (Fail)).

*   **Key Observation for Cache Hit:** When you perform a GET request for an ID *that has already been fetched recently* (and not updated/deleted), you should **NOT** see a `Hibernate: select ... from product ...` log for that specific request in the console. This indicates the data was served from the Redis cache.
*   **Check Redis:** Use `docker-compose exec redis redis-cli` and commands like `KEYS *`, `GET "productCache::<id>"` (e.g., `GET "productCache::1"`) to inspect cache contents directly.

### Integration Testing (TestContainers)

This project is configured with the TestContainers dependency. To run integration tests (assuming `ProductCachingIntegrationTest.java` exists in `src/test/java/.../integration`):

```bash
# From the project's root directory
mvn test
# or
mvn verify
```

These tests would ideally spin up a dedicated Redis container using TestContainers, run tests against it to verify cache hits, misses, puts, and evictions programmatically, and then tear down the container. *Note: The actual integration test classes need to be implemented as per the specification.*

## API Endpoints

Base Path: `/api/product`

| Operation | HTTP Method | Endpoint | Request Body Example         | Success Response | Caching Annotation | Description                |
| :-------- | :---------- | :------- | :--------------------------- | :--------------- | :----------------- | :------------------------- |
| Create    | POST        | `/`      | `{"name":"X","price":10.0}`  | `ProductDto` (201) | `@CachePut`        | Creates a new product.     |
| Read      | GET         | `/{id}`  | -                            | `ProductDto` (200) | `@Cacheable`       | Retrieves product by ID.   |
| Update    | PUT         | `/`      | `{"id":1,"name":"Y","price":12.0}` | `ProductDto` (200) | `@CachePut`        | Updates existing product.  |
| Delete    | DELETE      | `/{id}`  | -                            | `Void` (204)     | `@CacheEvict`      | Deletes product by ID.     |

*(Note: 404 Not Found is returned if the ID doesn't exist for GET/PUT/DELETE operations where applicable).*

## Caching Strategy Explained

*   **Store:** Redis (managed via `docker-compose`).
*   **Mechanism:** Spring Cache abstraction (`@EnableCaching` on main class).
*   **Cache Name:** `productCache` (defined in `ProductService` interface).
*   **Annotations:**
    *   `@Cacheable(value = "productCache", key = "#id")`: On `getProductById`. Checks cache first. If miss, executes method, caches result, returns. If hit, returns directly from cache.
    *   `@CachePut(value = "productCache", key = "#result.id")`: On `createProduct` and `updateProduct`. *Always* executes the method (to interact with DB). *Always* puts the method's return value (`ProductDto`) into the cache, overwriting any existing entry for that key.
    *   `@CacheEvict(value = "productCache", key = "#id")`: On `deleteProduct`. Executes the method. Upon successful completion, removes the entry corresponding to the key (`id`) from the cache.
*   **Data Format:** `ProductDto` objects are stored as JSON strings in Redis, configured via `RedisCacheConfig`.
*   **Consistency:** `@CachePut` and `@CacheEvict` ensure cache updates/invalidations occur on modification operations.
*   **TTL (Time-To-Live):** A default TTL (e.g., 10 minutes) is set in `RedisCacheConfig` to automatically expire cache entries, preventing indefinitely stale data.

## Configuration Highlights

*   **`application.properties`:**
    *   `spring.cache.type=redis`: Enables Redis as the cache provider.
    *   `spring.data.redis.host=localhost`: Redis connection details.
    *   `spring.data.redis.port=6379`
    *   H2 datasource configuration.
    *   JPA/Hibernate settings (`spring.jpa.show-sql=true` is useful for observing caching).
*   **`RedisCacheConfig.java`:**
    *   Defines the `RedisCacheManager` bean.
    *   Configures JSON serialization for `ProductDto` values using `Jackson2JsonRedisSerializer`.
    *   Sets a default TTL for cache entries.
*   **`SpringRedisApplication.java`:**
    *   Annotated with `@EnableCaching` to activate Spring's caching infrastructure.
*   **`.gitignore`:** Properly configured to exclude build artifacts (`target/`), IDE files, etc., from version control.

## Project Structure

The project follows a standard Maven project structure:

```
Spring-Cache/
├── .git/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/com/amir/spring/redis/
│   │   │   ├── config/          # RedisCacheConfig
│   │   │   ├── controller/      # ProductController
│   │   │   ├── dto/             # ProductDto
│   │   │   ├── entity/          # Product
│   │   │   ├── repository/      # ProductRepository
│   │   │   ├── service/         # ProductService, ProductServiceImpl
│   │   │   └── SpringRedisApplication.java # Main class (@EnableCaching)
│   │   └── resources/
│   │       └── application.properties # Config
│   └── test/
│       ├── java/com/amir/spring/redis/ # Unit & Integration Tests
│       └── resources/                # Test-specific config (Optional)
├── .gitignore
├── docker-compose.yml           # Redis service definition
├── mvnw                         # Maven wrapper script
├── mvnw.cmd                     # Maven wrapper script (Windows)
└── pom.xml                      # Maven build configuration
```


