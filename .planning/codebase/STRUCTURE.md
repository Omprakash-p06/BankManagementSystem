# Codebase Structure

**Analysis Date:** 2026-05-20

## Directory Layout

```
BankManagementSystem/
├── .planning/                  # Project planning artifacts and code maps
│   └── codebase/               # Generated codebase maps
├── Sql/                        # Database scripts
│   └── Bank Management System.sql # Schema initialization and seed script
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── bank/
│       │           ├── controller/  # Web Servlets & Spring RestControllers
│       │           ├── dao/         # Data Access Objects (JDBC)
│       │           ├── filter/      # Audit Logging filters
│       │           ├── model/       # Data representation classes (POJOs)
│       │           ├── service/     # Core business services
│       │           └── util/        # Common utilities (email, db, scheduler)
│       ├── resources/
│       │   └── application.properties # Spring configuration properties
│       └── webapp/             # Static web pages and web assets
│           ├── WEB-INF/        # Configuration folder
│           │   └── web.xml     # Deployment descriptor config
│           └── [pages].html    # Client-side user interfaces
├── Dockerfile                  # Container config
├── docker-compose.yml          # Orchestrated containers config
└── pom.xml                     # Maven dependency config
```

## Directory Purposes

**Sql/**
- Purpose: Houses SQL initialization scripts.
- Contains: Database initialization DDL, initial data seeding commands.
- Key files: `Sql/Bank Management System.sql` - Complete schema structure.

**src/main/java/com/bank/controller/**
- Purpose: Intercepts and parses incoming HTTP requests.
- Contains: Servlets (`jakarta.servlet.http.HttpServlet`) and Spring Web Controllers (`@RestController`).
- Key files: `LoginServlet.java` (Auth), `TransferServlet.java` (Funds transfer), `PdfController.java` (PDF generation).

**src/main/java/com/bank/dao/**
- Purpose: Encapsulates SQL database communications.
- Contains: Java classes executing JDBC queries or transaction groups.
- Key files: `CustomerDAO.java`, `AccountDAO.java`, `TransactionDAO.java`.

**src/main/java/com/bank/filter/**
- Purpose: Intercepts request chains for logging or authentication guards.
- Contains: Standard `WebFilter` components.
- Key files: `AuditFilter.java` (Logs requests and status codes).

**src/main/java/com/bank/model/**
- Purpose: Models mapping to database tables.
- Contains: Standard Java Beans (POJOs).
- Key files: `Customer.java`, `Account.java`, `Transaction.java`, `Employee.java`.

**src/main/java/com/bank/service/**
- Purpose: Business rules separate from controllers or raw SQL mapping.
- Contains: Application service classes.
- Key files: `InterestService.java` (Manages daily/monthly interest actions).

**src/main/java/com/bank/util/**
- Purpose: Reusable application utility singletons.
- Contains: SMTP mailers, JDBC connection builders, background schedulers.
- Key files: `DatabaseConnector.java` (JDBC connection pool helper), `EmailService.java` (GMail SMTP wrapper), `InterestScheduler.java` (Periodic execution scheduler).

**src/main/webapp/**
- Purpose: Direct user interfaces rendered in browsers.
- Contains: HTML templates, CSS styles, and client-side JavaScript.
- Key files: `login.html`, `register.html`, `dashboard.html` (Customer interface), `employee-dashboard.html` (Employee interface).

## Key File Locations

**Entry Points:**
- `src/main/java/com/bank/MainApplication.java` - Spring Boot main application class.
- `src/main/webapp/index.html` - Static home landing page.

**Configuration:**
- `pom.xml` - Maven project specification.
- `src/main/resources/application.properties` - Spring Database configuration.
- `src/main/webapp/WEB-INF/web.xml` - Traditional servlet filter mappings.

**Core Logic:**
- `src/main/java/com/bank/util/DatabaseConnector.java` - JDBC connection credentials.
- `src/main/java/com/bank/util/InterestScheduler.java` - Scheduler initializing thread pools.

## Naming Conventions

**Files:**
- PascalCase for all Java classes: `AccountController.java`, `EmailService.java`.
- lowercase / kebab-case for static files: `employee-dashboard.html`, `style.css`.
- `*Servlet.java` for Servlet-based HTTP handlers.
- `*Controller.java` for Spring MVC REST controllers.
- `*DAO.java` for database mapping classes.

**Directories:**
- lowercase package naming conventions: `com.bank.controller`, `com.bank.dao`.

## Where to Add New Code

**New API Endpoint:**
- Servlet-based: Create class under `src/main/java/com/bank/controller/` inheriting `HttpServlet` annotated with `@WebServlet`.
- Spring-based: Create/modify class under `src/main/java/com/bank/controller/` annotated with `@RestController` or `@Controller`.

**New Database Entity/Mapping:**
- Model representation: Create POJO under `src/main/java/com/bank/model/`.
- Database operations: Create DAO class under `src/main/java/com/bank/dao/` handling JDBC parameters.
- DB Table: Update `Sql/Bank Management System.sql`.

**New Client Page:**
- Create HTML file directly under `src/main/webapp/`.
- Reference stylesheet `style.css`.

---

*Structure analysis: 2026-05-20*
*Update when directory structure changes*
