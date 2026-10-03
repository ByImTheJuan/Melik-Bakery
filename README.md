# Melik Bakery

> E-commerce web platform for an artisan bakery and pastry business.

Melik Bakery is a full-stack e-commerce web application developed for an artisan bakery and pastry business. The platform provides customers with a digital storefront where they can browse products, manage a shopping cart and place orders, while providing administrators with a restricted management interface for products and orders.

The project was developed as a **Software Engineering Final Degree Project (TFG)**, following an individual Scrum-based development methodology and a client-server architecture.

---

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [System Architecture](#system-architecture)
* [Technology Stack](#technology-stack)
* [Domain Model](#domain-model)
* [Project Structure](#project-structure)
* [Application Flows](#application-flows)
* [API Overview](#api-overview)
* [Data Persistence](#data-persistence)
* [Authentication and Security](#authentication-and-security)
* [Caching](#caching)
* [Images](#images)
* [Configuration](#configuration)
* [Running the Project](#running-the-project)
* [Docker Deployment](#docker-deployment)
* [Development](#development)
* [Project Methodology](#project-methodology)
* [Project Scope](#project-scope)
* [Future Improvements](#future-improvements)
* [License](#license)

---

## Overview

Melik Bakery is designed as the digital sales channel for an artisan bakery and pastry business.

The platform addresses the need to move product presentation and order management from informal sales channels to a dedicated web platform. Customers can access the product catalogue, obtain detailed information about products and complete an order through the website.

From the business perspective, the application provides:

* A professional digital storefront.
* A structured product catalogue.
* An online ordering system.
* A shopping cart for anonymous customers.
* Centralized order management.
* An administrative interface for managing products and orders.
* A technological foundation for future expansion.

The system is intentionally designed as a single-business e-commerce platform rather than a marketplace or multi-vendor platform.

---

## Features

### Customer-facing features

* Product catalogue.
* Product detail pages.
* Product descriptions, prices, ingredients and images.
* Shopping cart.
* Add, remove and update cart items.
* Automatic calculation of cart totals.
* Anonymous shopping without requiring an account.
* Checkout process.
* Shipping address collection.
* Customer information collection.
* Order creation and confirmation.
* Order persistence.

### Administrative features

* Restricted administration area.
* Administrator authentication.
* Product creation.
* Product modification.
* Product deletion.
* Product catalogue ordering/reordering.
* Order visualization.
* Order status management.

### Infrastructure features

* REST API between frontend and backend.
* Relational database persistence.
* Redis-based shopping cart storage.
* Dockerized application components.
* Nginx reverse proxy.
* HTTPS-compatible production deployment.
* Environment-specific configuration.
* Automated CI/CD pipeline.

---

## System Architecture

Melik Bakery follows a **client-server architecture** with a separation between presentation, business logic and persistence.

```text
                    ┌─────────────────────────┐
                    │       Web Browser       │
                    │                         │
                    │   React / Vite Frontend │
                    └────────────┬────────────┘
                                 │
                           HTTP / JSON
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │          Nginx          │
                    │                         │
                    │  Static files / Proxy   │
                    └────────────┬────────────┘
                                 │
                            /api requests
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │    Spring Boot Backend  │
                    │                         │
                    │ Controllers             │
                    │ Services                │
                    │ Mappers                 │
                    │ Repositories            │
                    └───────┬─────────┬───────┘
                            │         │
                ┌───────────┘         └────────────┐
                ▼                                  ▼
       ┌─────────────────┐                ┌─────────────────┐
       │      MySQL      │                │      Redis      │
       │                 │                │                 │
       │ Persistent      │                │ Shopping carts  │
       │ application     │                │ with TTL        │
       │ data            │                │                 │
       └─────────────────┘                └─────────────────┘
```

### Frontend

The frontend is a React application built with Vite.

Its responsibilities include:

* Rendering the user interface.
* Managing client-side navigation.
* Managing shopping cart state.
* Consuming the REST API.
* Handling customer interactions.
* Providing the administrative interface.

The frontend communicates with the backend through HTTP requests and JSON representations.

### Backend

The backend is implemented using Java and Spring Boot.

Its responsibilities include:

* Implementing business logic.
* Validating incoming data.
* Exposing REST endpoints.
* Managing orders and products.
* Managing shopping carts.
* Persisting application data.
* Handling authentication and authorization for administrative functionality.
* Communicating with Redis and MySQL.

The backend follows a layered structure based primarily on:

```text
Controller
    ↓
Service
    ↓
Repository
```

DTOs and dedicated mappers are used to keep API representations separated from persistence entities.

### Persistence layer

Persistent business data is stored in a relational MySQL database.

The backend is the only component that directly accesses the database.

### Reverse proxy

Nginx is used in the production environment to:

* Serve the compiled React application.
* Forward `/api` requests to the backend.
* Serve product images.
* Provide a unified entry point for the web application.

---

## Technology Stack

| Layer                      | Technology                |
| -------------------------- | ------------------------- |
| Frontend                   | React 19                  |
| Frontend tooling           | Vite                      |
| Backend                    | Java 21                   |
| Backend framework          | Spring Boot               |
| REST API                   | Spring Web                |
| Persistence                | Spring Data JPA           |
| Database                   | MySQL                     |
| Cache                      | Redis                     |
| Security                   | Spring Security           |
| Authentication             | JWT                       |
| Web server / reverse proxy | Nginx                     |
| Containerization           | Docker                    |
| Orchestration              | Docker Compose            |
| Version control            | Git                       |
| CI/CD                      | GitHub Actions            |
| Container registry         | GitHub Container Registry |

---

## Domain Model

The main domain concepts are `Product`, `ShoppingCart`, `CartItem`, `Order`, `OrderItem` and `Client`.

### Product

Represents a product offered by the bakery.

Typical information includes:

* `id`
* `name`
* `price`
* `description`
* `ingredients`
* `imageURL`

Products are persistent JPA entities.

### Client

Represents a customer of the application.

The same model can represent both:

* Registered customers.
* Customers placing anonymous orders.

A `Client` can have multiple orders, but the `Order` entity does not require a direct client relationship for anonymous purchases.

Client email addresses are unique.

### ShoppingCart

The shopping cart is intentionally **not persisted in the relational database**.

Instead, carts are stored in Redis and identified by a cart ID.

This allows customers to use the shopping cart without having an account.

A cart contains a collection of `CartItem` objects.

### CartItem

`CartItem` is not a JPA entity.

It is exclusively used as part of the shopping cart representation and contains a snapshot of the product information required by the cart:

* `productId`
* `productName`
* `quantity`
* `unitPriceAtAdd`

Keeping `CartItem` independent from the persistence model avoids coupling Redis cart data to the relational order model.

### Order

An `Order` represents a confirmed purchase.

It is persisted in MySQL and contains:

* Order identifier.
* Order items.
* Total amount.
* Order status.
* Creation timestamp.
* Shipping address snapshot.
* Customer snapshot.
* Optional relationship with a `Client`.

The client relationship is nullable because the platform supports anonymous purchases.

### OrderItem

`OrderItem` is a persistent entity belonging to an order.

Unlike `CartItem`, it represents the historical state of an item at the moment the order was placed.

It contains:

* `id`
* `productId`
* `productName`
* `quantity`
* `unitPriceAtPurchase`

The product information is stored as a snapshot so that future modifications to the product catalogue do not alter historical orders.

### AddressSnapshot

The shipping address associated with an order is stored as an embedded snapshot.

It does not depend on a separately persisted address entity.

This preserves the exact delivery information associated with the order at the time it was created.

---

## DTO Architecture

The API does not expose persistence entities directly.

DTOs are used to define explicit contracts between the frontend and backend.

### Shopping cart DTOs

The shopping cart uses intention-oriented DTOs rather than a traditional CRUD DTO:

* `AddCartItemRequestDTO`
* `CartItemResponseDTO`

### Order DTOs

Orders use:

* `OrderRequestDTO`
* `OrderResponseDTO`
* `CheckoutOrderRequestDTO`
* `OrderItemResponseDTO`

### Address DTOs

Address data is represented using:

* `AddressSnapshotDTO` for requests.
* A dedicated response representation where appropriate.

Request and response models are intentionally separated to preserve the API contract independently of the internal persistence model.

---

## Mapping Layer

The backend uses dedicated mapper components to convert between entities and DTOs.

Current mappers include:

```text
ClientMapper
OrderMapper
OrderItemMapper
AddressMapper
```

Mappers are Spring components and are injected into services.

This keeps DTO conversion outside controllers and services, preventing mapping logic from becoming mixed with business logic.

---

## Application Flows

### Shopping cart flow

```text
Customer
   │
   │ Add product
   ▼
Frontend
   │
   │ POST /api/cart/{cartId}/items
   ▼
Backend
   │
   │ Validate / update cart
   ▼
Redis
   │
   │ ShoppingCart
   ▼
Frontend
```

A cart is created when necessary and identified by a cart ID. The cart ID can subsequently be used by the frontend to retrieve and modify the cart.

### Checkout flow

```text
ShoppingCart
     │
     │ Checkout request
     ▼
ShoppingCartService
     │
     │ Validate cart
     │ Build order
     ▼
OrderService
     │
     ├──────────────► MySQL
     │                  │
     │                  ▼
     │               Order
     │
     ▼
Checkout response
```

The checkout process transforms the temporary shopping cart into a persistent order.

The order stores snapshots of relevant product and customer information so that the historical order remains consistent even if catalogue data changes later.

---

## API Overview

The backend exposes a REST API under the `/api` path.

### Products

```text
GET    /api/products
GET    /api/products/{productId}
POST   /api/products
PUT    /api/products/{productId}
DELETE /api/products/{productId}
```

Product management endpoints are restricted to administrators where applicable.

### Shopping cart

```text
POST   /api/cart
GET    /api/cart/{cartId}
POST   /api/cart/{cartId}/items
DELETE /api/cart/{cartId}/items/{productId}
POST   /api/cart/{cartId}/checkout
```

Additional operations allow cart quantities to be modified and carts to be cleared.

### Orders

```text
GET /api/orders
GET /api/orders/{orderId}
```

### Client orders

```text
GET /api/clients/{clientId}/orders
```

The client endpoint delegates order retrieval to the order service rather than duplicating order-related business logic.

---

## Data Persistence

The application deliberately separates **persistent business data** from **temporary shopping cart state**.

### MySQL

MySQL stores long-lived application data such as:

* Products.
* Clients.
* Orders.
* Order items.

### Redis

Redis stores shopping carts.

Cart data is temporary and uses a configured expiration period, reducing unnecessary persistence of abandoned carts.

This separation provides a clear distinction between:

* Transactional business data that must be preserved.
* Temporary state associated with an ongoing shopping session.

---

## Authentication and Security

Administrative functionality is protected using Spring Security.

The authentication mechanism uses:

* JWT-based authentication.
* Secure HTTP cookies.
* CSRF protection for state-changing requests.
* Restricted administrative operations.

Customer accounts are not currently required to place orders.

The system therefore supports anonymous checkout while maintaining authentication requirements for administrative functionality.

Security was deliberately separated from the initial customer shopping experience so that authentication does not become a prerequisite for browsing or purchasing.

---

## Caching

Redis is used as the application's distributed in-memory data store for shopping carts.

The cart identifier is independent from the customer identifier.

This is intentional because a customer does not need to authenticate or even have a persistent account to use the shopping cart.

The cart lifecycle is therefore:

```text
Create cart
    ↓
Add / modify / remove items
    ↓
Checkout
    ↓
Create persistent Order
    ↓
Cart no longer required
```

---

## Images

Product images are stored outside the relational database.

The backend exposes images through the `/api/images/...` path, while Nginx handles the corresponding production routing and static-file serving.

The image storage is mounted into the backend container using a Docker volume, allowing images to remain persistent independently of the lifecycle of the application container.

This avoids storing binary image data directly inside MySQL.

---

## Configuration

The application uses environment-specific configuration.

Typical environments include:

```text
Development
Production
Testing
```

Configuration should be provided through environment variables or environment-specific configuration files.

Sensitive information such as:

* Database passwords.
* JWT secrets.
* Production credentials.
* Infrastructure credentials.

must never be committed to the repository.

A local development environment should provide its own configuration based on the project's expected variables.

---

## Running the Project

### Prerequisites

To run the project locally, the following tools are required:

* Java 21.
* Maven.
* Node.js and npm.
* Docker.
* Docker Compose.
* Git.

The exact versions may evolve as the project is maintained.

### Clone the repository

```bash
git clone <repository-url>
cd melik-bakery
```

### Start infrastructure services

The development Docker Compose configuration can be used to start the required infrastructure services.

```bash
docker compose -f docker-compose.dev.yml up -d
```

### Start the backend

From the backend directory:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

### Start the frontend

From the frontend directory:

```bash
npm install
npm run dev
```

The Vite development server will provide the frontend application.

> The exact commands and paths may change according to the current repository structure. Refer to the project configuration files for the authoritative development setup.

---

## Docker Deployment

The application is designed to run using Docker containers.

The production environment separates the main infrastructure components:

```text
┌─────────────────────────────────────┐
│             Docker Host             │
│                                     │
│  ┌─────────┐   ┌────────────────┐  │
│  │  Nginx  │──►│    Backend     │  │
│  └─────────┘   └───────┬────────┘  │
│                        │           │
│             ┌──────────┴────────┐  │
│             │                   │  │
│          ┌──▼───┐           ┌──▼───┐
│          │MySQL │           │Redis │
│          └──────┘           └──────┘
│                                     │
└─────────────────────────────────────┘
```

Docker provides:

* Reproducible execution environments.
* Dependency isolation.
* Consistent deployments.
* Simplified infrastructure management.
* Independent lifecycle management for application components.

Production deployments are automated through a CI/CD workflow that builds container images, publishes them to a container registry and updates the deployment environment.

---

## Development

The backend follows a separation of responsibilities:

```text
controller/
    REST API endpoints

service/
    Business logic

repository/
    Persistence access

mapper/
    Entity ↔ DTO conversion

dto/
    API contracts

entity/
    Persistent domain model
```

The architecture follows several principles:

* Controllers should remain thin.
* Business logic belongs in services.
* Database access belongs in repositories.
* Entity/DTO conversion belongs in mappers.
* DTOs define API contracts.
* Temporary cart state should remain independent from persistent order state.

This structure is intended to keep the application maintainable and facilitate future evolution.

---

## Testing

The project includes automated tests for the backend and frontend.

The CI pipeline executes the relevant test suites before publishing production container images.

Backend tests are executed using Maven, while frontend tests are executed using the Node.js/npm toolchain.

Additional integration and end-to-end coverage can be expanded as the application evolves.

---

## Continuous Integration and Deployment

The project uses GitHub Actions for CI/CD.

The general pipeline is:

```text
Git push
   │
   ▼
GitHub Actions
   │
   ├── Backend tests
   ├── Frontend tests
   ├── Build application
   ├── Build Docker images
   └── Push images to GHCR
             │
             ▼
       Deployment server
             │
             ▼
       Docker Compose
```

This process reduces manual deployment steps and ensures that container images are produced from the version-controlled source code.

---

## Project Methodology

The project follows an **individual Scrum-based methodology**.

Development is organized into fixed two-week sprints.

The original project roadmap defined six principal development stages:

| Sprint   | Objective           |
| -------- | ------------------- |
| Sprint 1 | Backend foundation  |
| Sprint 2 | Frontend foundation |
| Sprint 3 | Product catalogue   |
| Sprint 4 | Shopping cart       |
| Sprint 5 | Order process       |
| Sprint 6 | Business management |

The project uses an incremental development model, allowing requirements and priorities to evolve as implementation progresses.

Because the project is developed individually, the Product Owner, Scrum Master and Development Team responsibilities are assumed by the developer.

---

## Project Scope

The initial project scope includes:

* Product catalogue.
* Product details.
* Shopping cart.
* Customer checkout.
* Order persistence.
* Basic business management.
* Administrative product management.
* Administrative order management.

The following functionality is outside the initial scope:

* Real payment gateway integration.
* Advanced logistics management.
* Real-time inventory management.
* Customer account registration and authentication.
* Marketing automation.
* Advanced business analytics.
* Native mobile application.

These features may be considered future extensions.

---

## Future Improvements

Potential future developments include:

* Online payment gateway integration.
* Customer accounts and authentication.
* Expanded role-based access control.
* Advanced order management.
* Inventory management.
* Delivery and logistics management.
* Email notifications.
* Business analytics.
* Marketing functionality.
* Native or progressive mobile experience.
* Expanded automated test coverage.
* Improved observability and monitoring.

The architecture has been designed to provide a foundation for these future extensions without requiring a fundamental redesign of the system.

---

## Repository Status

Melik Bakery is an active software engineering project.

Current major components include:

* React frontend.
* Spring Boot REST backend.
* MySQL persistence.
* Redis shopping cart storage.
* Administrative module.
* JWT authentication.
* Docker-based deployment.
* Nginx reverse proxy.
* Automated CI/CD.

---

## License

This repository is publicly accessible for transparency, documentation and project demonstration purposes.

The source code is **proprietary and is not released under an open-source license**.

Use, copying, modification, distribution, sublicensing or commercial exploitation of the source code is not permitted without prior written authorization from the copyright holder.

See [`LICENSE`](LICENSE) for the complete terms.

---

## Author

**Juan Diego Hernández Derch**

Software Engineering — Final Degree Project

**Melik Bakery**
