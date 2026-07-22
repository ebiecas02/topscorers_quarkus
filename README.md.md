# Top Scorers API

A RESTful API built with Quarkus that manages test scores, stores them in a database, and retrieves top performers.

## Table of Contents
- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Features](#features)
- [API Endpoints](#api-endpoints)
  - [Score Management](#score-management)
  - [CSV Upload](#csv-upload)
  - [Database Queries](#database-queries)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [Testing the API](#testing-the-api)
  - [Testing with PowerShell](#testing-with-powershell)
  - [Testing with cURL](#testing-with-curl)
  - [Testing with Command Prompt](#testing-with-command-prompt)
- [Database](#database)
- [CSV File Format](#csv-file-format)
- [Error Handling](#error-handling)
- [Future Enhancements](#future-enhancements)
- [License](#license)

## Overview

This project provides a complete solution for managing test scores. It allows users to:
- Upload CSV files containing test scores
- Add individual scores via REST API
- Retrieve scores for specific persons
- Get top scorers (handling ties alphabetically)
- Store all data in a database

The application was built as a solution to a programming assignment and demonstrates:
- RESTful API design
- Database integration with JPA/Hibernate
- File upload handling
- CSV parsing
- Error handling and validation

## Technologies Used

- **Quarkus 3.6.0** - Supersonic Subatomic Java framework
- **Java 17/21** - Programming language
- **H2 Database** - In-memory database for development
- **Hibernate ORM with Panache** - JPA implementation
- **RESTEasy Reactive** - REST API framework
- **Jackson** - JSON serialization/deserialization
- **Maven** - Build tool
- **H2 Console** - Database management interface

## Project Architecture
┌─────────────────────────────────────────────────────────────┐
│                     Client (cURL/Postman/UI)                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    REST Resources Layer                     │
│  ┌──────────────────────┐    ┌──────────────────────────┐  │
│  │  TopScorersResource  │    │    ScoreResource         │  │
│  │  /api/topscorers     │    │    /api/scores           │  │
│  └──────────────────────┘    └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                          │
│  ┌──────────────────────┐    ┌──────────────────────────┐  │
│  │  CSVProcessorService │    │    ScoreService          │  │
│  │  (Business Logic)    │    │    (Business Logic)      │  │
│  └──────────────────────┘    └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Repository Layer                         │
│              ┌──────────────────────────────┐               │
│              │    PersonRepository          │               │
│              │    (Data Access)             │               │
│              └──────────────────────────────┘               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Database Layer                         │
│              ┌──────────────────────────────┐               │
│              │      H2 Database             │               │
│              │      (In-Memory)             │               │
│              └──────────────────────────────┘               │
└─────────────────────────────────────────────────────────────┘

## Project Structure
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── org/
│ │ │ └── ninetyone/
│ │ │ ├── model/
│ │ │ │ ├── Person.java
│ │ │ │ ├── PersonEntity.java
│ │ │ │ ├── ScoreRequest.java
│ │ │ │ ├── ScoreResponse.java
│ │ │ │ └── TopScorersResponse.java
│ │ │ ├── repository/
│ │ │ │ └── PersonRepository.java
│ │ │ ├── resource/
│ │ │ │ ├── TopScorersResource.java
│ │ │ │ └── ScoreResource.java
│ │ │ ├── service/
│ │ │ │ ├── CSVProcessorService.java
│ │ │ │ └── ScoreService.java
│ │ │ └── TopScorersApplication.java
│ │ └── resources/
│ │ └── application.properties
│ └── test/
│ └── java/
│ └── org/
│ └── ninetyone/
│ ├── service/
│ │ └── CSVProcessorServiceTest.java
│ └── resource/
│ └── TopScorersResourceTest.java
├── TestData.csv
├── pom.xml
└── README.md

text

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

## API Endpoints

### Score Management

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/scores` | Add a new score | `{"firstName":"John","lastName":"Doe","score":85}` | `{"fullName":"John Doe","score":85,"message":"Score added successfully"}` |
| GET | `/api/scores/person?fullName=John%20Doe` | Get score by full name | - | `{"fullName":"John Doe","score":85}` |
| GET | `/api/scores/person?firstName=John&lastName=Doe` | Get score by first and last name | - | `{"fullName":"John Doe","score":85}` |
| GET | `/api/scores/top` | Get top scorer(s) | - | `{"topScore":92,"topScorers":["Jane Smith"],"totalEntries":5}` |

### CSV Upload

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/topscorers/upload` | Upload CSV file | Multipart form with file | `{"topScore":78,"topScorers":["George Of The Jungle","Sipho Lolo"],"totalEntries":4}` |
| POST | `/api/topscorers/process-file` | Process CSV by file path | `"C:/path/to/file.csv"` | `{"topScore":78,"topScorers":["George Of The Jungle","Sipho Lolo"],"totalEntries":4}` |

### Database Queries

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/api/topscorers/all` | Get all persons | `[{"id":1,"firstName":"John","lastName":"Doe","fullName":"John Doe","score":85}]` |
| GET | `/api/topscorers/database/top` | Get top scorers from DB | `{"topScore":92,"topScorers":["Jane Smith"],"totalEntries":5}` |
| GET | `/api/topscorers/database/count` | Get total count | `{"count":5}` |
| DELETE | `/api/topscorers/database/clear` | Clear all data | `{"message":"Database cleared successfully"}` |
| GET | `/api/topscorers/sample` | Sample response for testing | `{"topScore":78,"topScorers":["George Of The Jungle Sipho","Lolo"],"totalEntries":5}` |

## Getting Started

### Prerequisites

- **Java 17 or 21** (Java 17 LTS recommended)
- **Maven 3.9.0+** (or use the included Maven wrapper)
- **Git** (optional)

### Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd code-with-quarkus
Verify Java version:

bash
java -version
# Should show Java 17 or 21
Build the project:

bash
./mvnw clean compile
Running the Application
Development Mode (Hot Reload)
bash
./mvnw quarkus:dev
Production Mode
bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
Native Mode (GraalVM)
bash
./mvnw package -Pnative
./target/code-with-quarkus-1.0.0-SNAPSHOT-runner
The application will start on: http://localhost:8080

Testing the API
Testing with PowerShell (Recommended)
1. Add a New Score
powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/scores" -Method Post -Body (@{firstName="John";lastName="Doe";score=85} | ConvertTo-Json) -ContentType "application/json"
2. Get Score by Full Name
powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/scores/person?fullName=John%20Doe" -Method Get
3. Get Score by First and Last Name
powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/scores/person?firstName=John&lastName=Doe" -Method Get
4. Get Top Scorers
powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/scores/top" -Method Get
5. Upload CSV File
powershell
curl.exe -X POST -F "fileData=@C:\path\to\TestData.csv" -F "fileName=TestData.csv" http://localhost:8080/api/topscorers/upload
6. Get All Persons from Database
powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/topscorers/all" -Method Get
7. Clear Database
powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/topscorers/database/clear" -Method Delete
Testing with cURL
Add a New Score (using JSON file)
bash
# Create score.json
echo '{"firstName":"John","lastName":"Doe","score":85}' > score.json

# Send the request
curl -X POST -H "Content-Type: application/json" -d @score.json http://localhost:8080/api/scores
Get Top Scorers
bash
curl -X GET http://localhost:8080/api/scores/top
Upload CSV
bash
curl -X POST -F "fileData=@TestData.csv" -F "fileName=TestData.csv" http://localhost:8080/api/topscorers/upload
Testing with Command Prompt (CMD)
cmd
curl -X POST -H "Content-Type: application/json" -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"score\":85}" http://localhost:8080/api/scores
Complete Test Script (PowerShell)
Create test-api.ps1:

powershell
# test-api.ps1 - Complete API Test Script
$baseUrl = "http://localhost:8080/api"

Write-Host "=== Top Scorers API Test ===" -ForegroundColor Cyan

# 1. Clear database
Write-Host "`n1. Clearing database..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "$baseUrl/topscorers/database/clear" -Method Delete

# 2. Upload CSV
Write-Host "`n2. Uploading CSV file..." -ForegroundColor Yellow
curl.exe -X POST -F "fileData=@C:\Users\LeNoVo\code-with-quarkus\TestData.csv" -F "fileName=TestData.csv" $baseUrl/topscorers/upload

# 3. Add new scores
Write-Host "`n3. Adding new scores..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "$baseUrl/scores" -Method Post -Body (@{firstName="John";lastName="Doe";score=85} | ConvertTo-Json) -ContentType "application/json"
Invoke-RestMethod -Uri "$baseUrl/scores" -Method Post -Body (@{firstName="Jane";lastName="Smith";score=92} | ConvertTo-Json) -ContentType "application/json"
Invoke-RestMethod -Uri "$baseUrl/scores" -Method Post -Body (@{firstName="Alice";lastName="Johnson";score=88} | ConvertTo-Json) -ContentType "application/json"

# 4. Get John's score
Write-Host "`n4. Getting John Doe's score..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "$baseUrl/scores/person?fullName=John%20Doe" -Method Get

# 5. Get Jane's score
Write-Host "`n5. Getting Jane Smith's score..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "$baseUrl/scores/person?firstName=Jane&lastName=Smith" -Method Get

# 6. Get top scorers
Write-Host "`n6. Getting top scorers..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "$baseUrl/scores/top" -Method Get

# 7. Get all persons
Write-Host "`n7. Getting all persons..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "$baseUrl/topscorers/all" -Method Get

Write-Host "`n=== Test Complete ===" -ForegroundColor Green
Database
# H2 Database Console
The application includes H2 Console for database management:

URL: http://localhost:8080/h2-console

Driver Class: org.h2.Driver

JDBC URL: jdbc:h2:mem:testdb

User Name: sa

Password: (leave blank)

Database Schema
The persons table structure:

sql
CREATE TABLE persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    full_name VARCHAR(255),
    score INT NOT NULL,
    CHECK (score >= 0 AND score <= 100)
);
Example SQL Queries
sql
-- View all persons
SELECT * FROM persons;

-- Get top scorers
SELECT full_name, score FROM persons 
WHERE score = (SELECT MAX(score) FROM persons) 
ORDER BY full_name;

-- Get average score
SELECT AVG(score) FROM persons;

-- Get person by name
SELECT * FROM persons WHERE full_name = 'John Doe';
CSV File Format
The application supports CSV files with the following formats:

Format 1: Header + Data
csv
First Name,Second Name,Score
Dee,Moore,56
Sipho,Lolo,78
Noosrat,Hoosain,64
George,Of The Jungle,78
Format 2: Simple Format
csv
Name,Score
Alice,75
Bob,78
Charlie,72
Format 3: No Header
csv
Dee Moore,56
Sipho Lolo,78
Noosrat Hoosain,64
The CSV parser automatically detects the format and handles:

Headers (skips first row if it looks like a header)

Different column separators

Extra spaces

Quoted values

# Error Handling
Common HTTP Status Codes
Status Code	Meaning	Example
200 OK	Success	GET request returned data
201 Created	Success	POST request created new resource
400 Bad Request	Invalid input	Missing required fields
404 Not Found	Resource not found	Person doesn't exist
500 Internal Server Error	Server error	Database connection issue
Example Error Responses
Person Not Found
json
{"fullName":"John Doe","score":0,"message":"Person not found"}
Invalid Input
json
{"error":"Score must be between 0 and 100"}
File Not Found
json
{"error":"File not found: C:/path/to/file.csv"}

# Future Enhancements
Authentication and authorization

PostgreSQL/MySQL support

Pagination for large datasets

Export results as CSV/PDF

Historical score tracking

Real-time score updates with WebSockets

Frontend UI with React/Vue

Docker containerization

Kubernetes deployment

CI/CD pipeline

License
This project is for educational purposes.

Support
For issues or questions, please create an issue in the repository.

Built with ❤️ using Quarkus
