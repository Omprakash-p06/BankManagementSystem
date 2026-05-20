# Technology Stack

**Analysis Date:** 2026-05-20

## Languages

**Primary:**
- Java 17 - Backend application logic, controllers, models, DAOs, filters, and utilities.

**Secondary:**
- SQL (MySQL Dialect) - Database schema scripts and initial data setups in `Sql/Bank Management System.sql`.
- HTML5 / CSS3 / JavaScript - Static frontend user interfaces in `src/main/webapp/`.

## Runtime

**Environment:**
- JDK 17 - Backend execution environment.
- Spring Boot 3.1.5 - Main application container and framework.
- Jakarta Servlet API 5.0 (Jakarta EE 9+) - Traditional servlet container interface for `@WebServlet` custom components.

**Package Manager:**
- Maven 3.x - Build, dependency, and project management defined in `pom.xml`.

## Frameworks

**Core:**
- Spring Boot 3.1.5 - Used for dependency injection, Spring Data JPA configuration, and select MVC controllers.
- Jakarta Servlet API - Traditional servlet routing engine handling core API routes like logins, dashboard actions, and transfers.

**Testing:**
- None - No test frameworks (JUnit, Spring Boot Test, Mockito) are configured in `pom.xml`, and no tests exist in `src/`.

**Build/Dev:**
- `maven-compiler-plugin` (v3.11.0 or default) - Configured for Java 17 target compiler compatibility.
- `spring-boot-maven-plugin` - Packaging and execution helper.

## Key Dependencies

**Critical:**
- `spring-boot-starter-data-jpa` (v3.1.5) - ORM framework using Hibernate for entity persistence and repository interfaces.
- `mysql-connector-j` - Type 4 JDBC driver for communication with the MySQL database.
- `jbcrypt` (v0.4) - Cryptographic hashing algorithm used for password hashing (`BCrypt.hashpw`) and verification (`BCrypt.checkpw`).
- `json` (v20240303) - JSON parsing and generation library for servlet-based request/response payloads.

**Infrastructure:**
- `spring-boot-starter-web` (v3.1.5) - Web framework support including embedded Tomcat and Spring MVC.
- `spring-boot-starter-tomcat` (provided scope) - Server engine requirement for compilation as a WAR package.
- `spring-boot-starter-mail` & `angus-mail` (v2.0.3) - Implementation classes for standard mail transmission support.
- `kernel` & `layout` (v7.2.5 from `com.itextpdf`) - PDF rendering engine for statement generation.

## Configuration

**Environment:**
- `src/main/resources/application.properties` - Declares Spring datasource configurations (supports variables like `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and default credentials/passwords for MySQL), and Hibernate DDL configuration (`spring.jpa.hibernate.ddl-auto=update`).
- Hardcoded JDBC properties - Hardcoded credentials inside utility class `src/main/java/com/bank/util/DatabaseConnector.java`.

**Build:**
- `pom.xml` - Defines compilation properties, Maven dependencies, build plugins, and packaging type (`war`).

**Web / Deployment:**
- `src/main/webapp/WEB-INF/web.xml` - Traditional servlet descriptor declaring audit filter associations and session configuration.

## Platform Requirements

**Development:**
- Windows/macOS/Linux with JDK 17 installed.
- Local instance of MySQL 8.x running on port 3306 with database `bank_management` initialized.

**Production:**
- Servlet Container supporting Jakarta EE 9+ (e.g., Apache Tomcat 10.x, Jetty 11.x, WildFly) to host the generated `.war` package.
- Managed MySQL Database (e.g., AWS RDS, self-hosted MySQL instance).

---

*Stack analysis: 2026-05-20*
*Update after major dependency changes*
