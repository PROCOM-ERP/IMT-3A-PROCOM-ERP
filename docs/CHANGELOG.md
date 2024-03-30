# Changelog

## Metadata
- **Timestamp**: 2023-11-11
- **Last update**: 2024-03-31
- **Status**: In development
- **Current Version**: 1.1.0

## Description
This document described all changes of a new version. 
The structure is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and the version format follows the [Semantic Versioning Format](https://semver.org/spec/v2.0.0.html).

## [1.1.0](https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP/releases/tag/v1.1.0) - 2024-03-31

### Added
- Authentication service REST API validation + Javadoc
- Directory service REST API validation + Javadoc
- Order service REST API + REST API validation + Javadoc
- Inventory service draft REST API
- Frontend available pages for Authentication, Directory, and Order services
- Documentation for features available, architectural diagrams, and usage guide
- Roadmap for future changes

### Updated
- System infrastructure setup (see [DEPLOYING.md](DEPLOYING.md))
- Authentication service REST API
- Directory service REST API

### Fixed
- RabbitMQ messages management for security internal communications

## [1.0.0](https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP/releases/tag/v1.0.0) - 2024-02-22

### Added
- New deployment method (see [DEPLOYING.md](DEPLOYING.md))
- Frontend for Authentication and Directory services

### Updated
- Authentication service REST API
- Directory service REST API
- Docker container security management at runtime

## [0.1.0](https://github.com/PROCOM-ERP/IMT-3A-PROCOM-ERP/releases/tag/v0.1.0) - 2024-01-15

### Added
- Authentication service REST API (Java Spring Boot 3.1.5 Docker container + PostgreSQL 13 Docker container)
- Directory service REST API (Java Spring Boot 3.1.5 Docker container + PostgreSQL 13 Docker container)
- API Gateway (Java Spring Cloud Gateway 2022.0.4)
- RabbitMQ Management 3 Docker container
- Docker compose deployment script
- CA certificate generation script for HTTPS

_NOTE: This version is the MVP version_