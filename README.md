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
- [Database](#database)
- [CSV File Format](#csv-file-format)
- [Error Handling](#error-handling)
- [Technical Implementation](#technical-implementation)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

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
