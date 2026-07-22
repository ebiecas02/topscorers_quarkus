# Top Scorers API

A RESTful API built with Quarkus that manages test scores, stores them in a database, and retrieves top performers.

[![Quarkus](https://img.shields.io/badge/Quarkus-3.6.0-4695EB.svg)](https://quarkus.io/)
[![Java](https://img.shields.io/badge/Java-17%2F21-007396.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Testing the API](#testing-the-api)

---

## Overview

This project provides a complete solution for managing test scores. It allows users to:

- ✅ Upload CSV files containing test scores
- ✅ Add individual scores via REST API
- ✅ Retrieve scores for specific persons
- ✅ Get top scorers (handling ties alphabetically)
- ✅ Store all data in a database

**Key Demonstrations:**
- RESTful API design
- Database integration with JPA/Hibernate
- File upload handling
- CSV parsing
- Error handling and validation

---

## Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Quarkus | 3.6.0 | Framework |
| Java | 17/21 | Programming Language |
| H2 Database | Latest | In-memory DB |
| Hibernate ORM with Panache | Latest | JPA Implementation |
| RESTEasy Reactive | Latest | REST API Framework |
| Jackson | Latest | JSON Serialization |
| Maven | 3.9.0+ | Build Tool |

---

## Project Structure
topscorers_quarkus/
├── src/
│ └── main/
│ ├── java/
│ │ └── org/
│ │ └── ninetyone/
│ │ ├── model/
│ │ │ ├── Person.java
│ │ │ ├── PersonEntity.java
│ │ │ ├── ScoreRequest.java
│ │ │ ├── ScoreResponse.java
│ │ │ └── TopScorersResponse.java
│ │ ├── repository/
│ │ │ └── PersonRepository.java
│ │ ├── resource/
│ │ │ ├── TopScorersResource.java
│ │ │ └── ScoreResource.java
│ │ ├── service/
│ │ │ ├── CSVProcessorService.java
│ │ │ └── ScoreService.java
│ │ └── TopScorersApplication.java
│ └── resources/
│ └── application.properties
├── TestData.csv
├── pom.xml
└── README.md


---

## Features

### Core Functionality
- **CSV Upload**: Upload CSV files with test scores
- **Score Management**: Add and update individual scores via REST API
- **Score Retrieval**: Get scores for specific persons
- **Top Scorers**: Retrieve highest score(s) with alphabetical ordering for ties

### Database Features
- Automatic table creation on startup
- H2 in-memory database for local development
- Full CRUD operations via JPA
- Transaction management

### API Features
- RESTful endpoints with JSON responses
- Input validation
- Proper HTTP status codes
- Error handling with meaningful messages
- CORS support for frontend integration

---

## API Endpoints

### Score Management

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/api/scores` | Add a new score | `{"firstName":"John","lastName":"Doe","score":85}` | `{"fullName":"John Doe","score":85,"message":"Score added successfully"}` |
| `GET` | `/api/scores/person?fullName=John%20Doe` | Get score by full name | - | `{"fullName":"John Doe","score":85}` |
| `GET` | `/api/scores/person?firstName=John&lastName=Doe` | Get score by first/last name | - | `{"fullName":"John Doe","score":85}` |
| `GET` | `/api/scores/top` | Get top scorer(s) | - | `{"topScore":92,"topScorers":["Jane Smith"],"totalEntries":5}` |

### CSV Upload

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/api/topscorers/upload` | Upload CSV file | Multipart form with file | `{"topScore":78,"topScorers":["George Of The Jungle","Sipho Lolo"],"totalEntries":4}` |
| `POST` | `/api/topscorers/process-file` | Process CSV by file path | `"C:/path/to/file.csv"` | Same as above |

### Database Queries

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| `GET` | `/api/topscorers/all` | Get all persons | `[{"id":1,"firstName":"John","lastName":"Doe","fullName":"John Doe","score":85}]` |
| `GET` | `/api/topscorers/database/top` | Get top scorers from DB | `{"topScore":92,"topScorers":["Jane Smith"],"totalEntries":5}` |
| `GET` | `/api/topscorers/database/count` | Get total count | `{"count":5}` |
| `DELETE` | `/api/topscorers/database/clear` | Clear all data | `{"message":"Database cleared successfully"}` |
| `GET` | `/api/topscorers/sample` | Sample response for testing | `{"topScore":78,"topScorers":["George Of The Jungle Sipho","Lolo"],"totalEntries":5}` |

---

## Getting Started

### Prerequisites

- **Java 17 or 21** (Java 17 LTS recommended)
- **Maven 3.9.0+** (or use the included Maven wrapper)
- **Git** (optional)

### Installation

# Clone the repository
git clone https://github.com/ebiecas02/topscorers_quarkus.git
cd topscorers-api

Verify Java version
java -version
Should show Java 17 or 21

# Build the project
./mvnw clean compile

# Running the Application

Development Mode with (Hot Reload)	

./mvnw quarkus:dev

The application will start on: http://localhost:8080

## Testing the API

PowerShell Commands

# 1. Add a New Score
Invoke-RestMethod -Uri "http://localhost:8080/api/scores" -Method Post -Body (@{firstName="John";lastName="Doe";score=85} | ConvertTo-Json) -ContentType "application/json"

# 2. Get Score by Full Name
Invoke-RestMethod -Uri "http://localhost:8080/api/scores/person?fullName=John%20Doe" -Method Get

# 3. Get Score by First and Last Name
Invoke-RestMethod -Uri "http://localhost:8080/api/scores/person?firstName=John&lastName=Doe" -Method Get

# 4. Get Top Scorers
Invoke-RestMethod -Uri "http://localhost:8080/api/scores/top" -Method Get

# 5. Upload Provided CSV Sample File
curl.exe -X POST -F "fileData=@C:\path\to\TestData.csv" -F "fileName=TestData.csv" http://localhost:8080/api/topscorers/upload

# 6. Get All Persons
Invoke-RestMethod -Uri "http://localhost:8080/api/topscorers/all" -Method Get

# 7. Clear Database
Invoke-RestMethod -Uri "http://localhost:8080/api/topscorers/database/clear" -Method Delete

