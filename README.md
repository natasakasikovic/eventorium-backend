<a id="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Unlicense License][license-shield]][license-url]
[![Last Commit](https://img.shields.io/github/last-commit/kzi-nastava/iss-project-event-planner-siit-2024-team-13?branch=main&style=for-the-badge)](https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-13/commits/main)

<div align="center">

  <h1 align="center">EVENT PLANNER API</h1>

  <p align="center">
    <br />
    <a href="https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-13/issues/new?labels=bug">Report Bug</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#-about-the-project">About The Project</a>
      <ul>
        <li><a href="#-built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#-getting-started">Getting Started</a>
      <ul>
        <li><a href="#-installation-steps">Installation Steps</a></li>
      </ul>
    </li>
    <li><a href="#api-documentation">API Documentation</a></li>
    <li><a href="#-code-quality--sonar">Code Quality – Sonar</a></li>
    <li><a href="#-running-tests">Running Tests</a></li>
    <li><a href="#-available-roles-and-credentials">Available Roles and Credentials</a></li>
  </ol>
</details>


##  📋 About The Project

Eventorium is a web API for event planning, built with Java and Spring Boot. It enables users to efficiently organize and manage events such as weddings, corporate gatherings, birthday parties, and more. This is the backend application for the Eventorium platform. The project follows a package-by-feature structure and includes comprehensive documentation, testing, and integration tools.

Happy planning with Eventorium! 🎉

<br/>

## 🔧 Built With

This project is built using the following core technologies:

  [![Java][Java-img]][Java-url]

  [![Spring Boot][SpringBoot-img]][SpringBoot-url]

  [![Maven][Maven-img]][Maven-url]
  
  [![PostgreSQL][PostgreSQL-img]][PostgreSQL-url]

</br>

## 🚀 Getting Started

Follow the steps below to set up and run the project locally.

### 🛠️ Installation Steps

1. 🔁 Clone the repository

    ```sh
    git clone https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-13.git
    cd eventorium
    ```

2. ⚙️ Update configuration

    Open the `src/main/resources/application.properties` file and update the necessary values such as:
    - Database connection
    - Mail credentials
    - Any other environment-specific settings

3. 📦 Build the project

    ```sh
    ./mvnw clean install
    ```

4. ▶️ Run the application

    ```sh
    ./mvnw spring-boot:run
    ```

The application will start on:
📍 **http://localhost:8080**

<br/>

## API Documentation

Swagger UI will be available at:
📖 http://localhost:8080/swagger-ui.html

A fully documented REST API is provided using _Springdoc OpenAPI_, allowing you to explore all available endpoints, request/response schemas, and try out API calls directly from the browser.

<br/>

## 📊 Code Quality – Sonar

SonarQube is available for static code analysis.

▶️ Start Sonar locally:
  
  ```sh
  start sonar
  ```

Make sure your SonarQube server is running locally (default: http://localhost:9000).

<br/>

## 🧪 Running Tests

The project uses JUnit 5 for testing. Tests are separated by layers:

    ✅ Unit tests for Service and Repository layers

    ✅ Integration tests for REST controllers

🔧 Run all tests:

  ```sh
  ./mvnw test
  ```

Or (Windows):

  ```sh
  mvn test
  ```

<br/>


## 👥 Available Roles and Credentials

The system supports the following roles with corresponding credentials:

- **Organizer**
  - Email: `organizer@gmail.com`
  - Password: `pera`

- **Service and product provider**
  - Email: `provider@gmail.com`
  - Password: `pera`

- **Administrator**
  - Email: `admin@gmail.com`
  - Password: `pera`

<p align="right">(<a href="#readme-top">back to top</a>)</p>

[Java-img]: https://img.shields.io/badge/Java-17+-red?logo=java&logoColor=white
[Java-url]: https://www.oracle.com/java/

[SpringBoot-img]: https://img.shields.io/badge/Spring%20Boot-3.3.5-success?logo=springboot
[SpringBoot-url]: https://spring.io/projects/spring-boot

[Maven-img]: https://img.shields.io/badge/Maven-3-blue?logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/

[PostgreSQL-img]: https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/

[contributors-shield]: https://img.shields.io/github/contributors/kzi-nastava/iss-project-event-planner-siit-2024-team-13.svg?style=for-the-badge
[contributors-url]: https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-13/graphs/contributors
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-13/blob/master/LICENSE.txt
