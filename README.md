# Cloud Explorer

Cloud Explorer is a user-friendly application for browsing and discovering files stored in cloud storage.

The initial version focuses on **Amazon S3**, providing an intuitive file-explorer-like experience for navigating buckets, prefixes, and objects without requiring users to interact directly with the AWS Console.

## Project Goals

This project is being built as a learning-focused, end-to-end engineering project covering:

* Java and Spring Boot
* Frontend development
* AWS and Amazon S3
* IAM and cloud security
* System architecture
* Docker and containerization
* Authentication and authorization
* Deployment and DevOps

## Initial Scope

The first version will focus on **read-only access** to S3-compatible storage.

Planned capabilities include:

* Browse storage using a file-explorer-like interface
* Navigate prefixes and objects
* Search for objects
* Filter objects
* View object metadata
* Preview supported files
* Download authorized objects
* Restrict users to authorized buckets and prefixes

## Security Principles

The project will follow these principles:

* Read-only access by default
* Least-privilege access
* Backend-enforced authorization
* Scoped access to buckets and prefixes
* Defense in depth
* Temporary credentials where applicable
* No trust in frontend-only authorization

## Architecture

The initial architecture direction is:

```text id="qcy4b5"
User
  |
  v
Cloud Explorer Frontend
  |
  v
Spring Boot Backend
  |
  v
Authorization Layer
  |
  v
Storage Provider
  |
  +--> Amazon S3
  |
  +--> Other providers (future)
```

## Local Development

Local development currently uses:

* Docker
* Docker Compose
* LocalStack
* AWS CLI
* S3-compatible APIs

The infrastructure configuration is available under:

```text id="n69fb8"
infrastructure/local/
```

## Technology Stack

### Backend

* Java
* Spring Boot
* AWS SDK for Java v2

### Frontend

To be finalized during the architecture phase.

### Infrastructure

* Docker
* Docker Compose
* LocalStack

### Cloud

* Amazon S3
* AWS IAM
* AWS STS

## Project Status

🚧 **Under active development**

Current phase:

**Core Explorer API → S3 Object Operations**

## Roadmap

* [x] S3 fundamentals
* [x] IAM fundamentals
* [x] AWS Console vs application access
* [x] Local S3 environment
* [x] AWS CLI exploration
* [x] S3 bucket and object exploration
* [x] Prefix-based navigation
* [x] S3 pagination fundamentals
* [x] Spring Boot project setup
* [x] AWS SDK integration
* [x] Prefix-based navigation API
* [x] S3 pagination
* [x] S3 object metadata, download, preview serach, copy
* [ ] Authentication and authorization
* [ ] Frontend application
* [ ] Search and filtering
* [ ] Deployment

## Spring Boot Backend

The backend has been initialized as a Spring Boot application.

Current backend setup includes:

* Java 25
* Spring Boot 4.1.1
* Maven
* Spring Web MVC
* Spring Boot Actuator
* Embedded Tomcat

The backend application currently runs locally on: http://localhost:8081

## AWS SDK and LocalStack Integration

The Spring Boot backend is now connected to LocalStack using the AWS SDK for Java v2.

The integration currently includes:

* AWS SDK for Java v2
* AWS SDK BOM for dependency version management
* `S3Client` configured as a Spring-managed Bean
* LocalStack S3 endpoint configuration
* Externalized S3 configuration through `application.properties`

### Current S3 Configuration

Local development uses LocalStack:

Endpoint: http://localhost:4566
Region: us-east-1

## S3 Object Operations

The backend currently supports the following S3 object operations:

### Object Metadata

GET /api/buckets/{bucket}/object?key={key}

## Future Vision

Cloud Explorer is designed with extensibility in mind.

While the initial implementation focuses on Amazon S3, the architecture may eventually support additional cloud storage providers.

Potential future integrations include:

* Other cloud object storage providers
* Application integrations
* Multiple storage connections
* Advanced search
* Audit logging
* File operations with controlled permissions

---

Built as a hands-on learning project to explore backend development, cloud architecture, security, frontend development, and deployment.
