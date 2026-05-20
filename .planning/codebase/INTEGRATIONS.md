# External Integrations

**Analysis Date:** 2026-05-20

## APIs & External Services

**Email Delivery:**
- SMTP (Gmail) - Used to send transactional emails (such as registration confirmations or password resets).
  - SDK/Client: Jakarta Mail (`jakarta.mail-api` and implementation `angus-mail` configured in `pom.xml`).
  - Auth: Credentials hardcoded in `src/main/java/com/bank/util/EmailService.java` (`username` and `password` variables).
  - Protocol/Host: SMTP on `smtp.gmail.com` (Port 587, STARTTLS enabled).

## Data Storage

**Databases:**
- MySQL Database (`bank_management`) - Holds system tables: `customer`, `employee`, `account`, `transactions`, `transfers`, and `audit_log`.
  - Client / Driver: `mysql-connector-j` loaded explicitly via `Class.forName("com.mysql.cj.jdbc.Driver")`.
  - Connection Pattern 1 (Servlets): Direct JDBC connections established via `src/main/java/com/bank/util/DatabaseConnector.java` using hardcoded URL `jdbc:mysql://localhost:3306/bank_management`, username `root`, and password `J7ZgR$n1^`.
  - Connection Pattern 2 (Spring Components): Configured in `src/main/resources/application.properties` (delegated to Hibernate JPA ORM). Reads from environment variables or defaults back to local URL, `root`, and `J7ZgR$n1^`.
  - Schema/Migrations: Initial schema script in `Sql/Bank Management System.sql`. Hibernate `ddl-auto=update` handles runtime tables creation/updates for JPA components.

## Caching & Auth Providers
- **Caching:** None (queries executed synchronously directly to database).
- **Authentication:** Custom session-based authentication. Stored in standard `HttpSession` (`session.setAttribute("customer", customer)` in `src/main/java/com/bank/controller/LoginServlet.java` or `session.setAttribute("employee", employee)` in `src/main/java/com/bank/controller/EmployeeLoginServlet.java`).

## Document Generation
- **PDF Export:** Generates statements.
  - SDK/Client: iText PDF (`com.itextpdf` kernel & layout) v7.2.5.
  - Integration: Writes statement document to output stream on demand.

## Monitoring & Observability
- **Audit Logs:** Logged database-wide for all HTTP requests via servlet filter `src/main/java/com/bank/filter/AuditFilter.java` using `src/main/java/com/bank/dao/AuditDAO.java`.
- **System Logs:** slf4j + logback integration writing to console stdout/stderr.

## Environment Configuration

**Development:**
- Required Database: Local MySQL 8.x server with schema `bank_management` matching `Sql/Bank Management System.sql`.
- Required SMTP credentials: Valid Google account with App Password to support SMTP authentication if email sending is invoked.

**Production:**
- Secrets/credentials must be moved from source-code files to environment properties before deployment.

---

*Integration audit: 2026-05-20*
*Update when adding/removing external services*
