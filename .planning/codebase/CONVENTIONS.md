# Coding Conventions

**Analysis Date:** 2026-05-20

## Naming Patterns

**Files:**
- PascalCase for all Java classes: `DatabaseConnector.java`, `AccountDAO.java`, `AuditFilter.java`.
- lowercase and kebab-case for static files: `dashboard.html`, `employee-dashboard.html`, `style.css`.
- `.sql` files for database schemas: `Bank Management System.sql`.

**Classes:**
- `*Servlet` for traditional servlets extending `HttpServlet`: `LoginServlet`, `RegisterServlet`.
- `*Controller` for Spring rest controllers: `PdfController`, `AccountController`.
- `*DAO` for data access objects: `CustomerDAO`, `AccountDAO`.
- Models represent database entities named as singular nouns: `Customer`, `Employee`, `Account`.
- `*Filter` for servlet request filters: `AuditFilter`.
- `*Service` for business services: `InterestService`, `EmailService`.

**Methods:**
- camelCase for all method names: `calculateMonthlyInterest()`, `addCustomer()`, `storeResetToken()`.
- Early exit/Early return guard statements: e.g., `if (customer == null) return null;`.

**Variables:**
- camelCase for all instance variables: `identificationNo`, `passwordHash`, `homebranch`.
- UPPER_SNAKE_CASE for constant values: `URL`, `USER`, `PASSWORD`.

## Code Style

**Formatting:**
- Java standard indentation (4 spaces per tab).
- Strict semicolon usage (mandatory in Java).
- Bracket placement: Opening brace on same line as declaration, closing brace on its own line.
```java
public void myMethod() {
    if (condition) {
        // logic
    }
}
```

**Resource Management:**
- Try-with-resources blocks are strictly required when opening JDBC components (`Connection`, `PreparedStatement`, `ResultSet`) or IO streams (`PdfWriter`, `BufferedReader`) to ensure they are closed automatically.
```java
try (Connection conn = DatabaseConnector.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // operations
}
```

## Import Organization

**Order:**
1. Core Java library packages (`java.sql.*`, `java.io.*`, `java.util.*`).
2. Jakarta EE extensions (`jakarta.servlet.*`, `jakarta.mail.*`).
3. External framework packages (`org.springframework.*`, `com.itextpdf.*`, `org.json.*`).
4. Internal project classes (`com.bank.model.*`, `com.bank.dao.*`, `com.bank.util.*`).

## Error Handling

**Patterns:**
- DAO operations bubble up raw checked exceptions (`SQLException`) to the controller/servlet boundary.
- Servlets and Spring REST controllers intercept these exceptions within try-catch blocks.
- Logging exceptions at class boundaries: `logger.error("Message: {}", e.getMessage(), e)` or printing stack trace `e.printStackTrace()` during developer sandbox execution.
- Web layer converts exceptions to structured JSON payloads (`{"message": "Login failed due to server error: ..."}`) and returns matching HTTP statuses (`SC_INTERNAL_SERVER_ERROR`).

## Logging

**Framework:**
- SLF4J (`org.slf4j.Logger`, `org.slf4j.LoggerFactory`) backed by Logback (`ch.qos.logback`).
- Declaration format:
```java
private static final Logger logger = LoggerFactory.getLogger(AuditFilter.class);
```
- Usage levels: `info`, `debug`, `error`. Structured messages format using `{}` brackets to avoid string concatenation overhead.

## Comments

**When to Comment:**
- Business logic calculations: e.g. explaining interest calculations or scheduler frequencies.
- Documenting fixes, changes, or database inconsistencies: e.g. comments marking schema modifications.
- Obvious code flows should not be commented.

---

*Convention analysis: 2026-05-20*
*Update when patterns change*
