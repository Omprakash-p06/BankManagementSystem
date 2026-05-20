# Codebase Concerns

**Analysis Date:** 2026-05-20

## Tech Debt

**Missing database table `audit_logs` in DDL:**
- Issue: `Sql/Bank Management System.sql` does not create the `audit_logs` (or `audit_log`) table.
- Files: `src/main/java/com/bank/dao/AuditDAO.java` (inserts into `audit_logs`), `src/main/java/com/bank/filter/AuditFilter.java` (calls `AuditDAO.logActivity`).
- Why: Schema script forgot to declare the audit logging table.
- Impact: Because `AuditFilter.java` is registered for all paths (`/*`), every single HTTP request will trigger a `SQLException` in `AuditDAO`, degrading stability or breaking execution entirely if the exception propagates.
- Fix approach: Add `CREATE TABLE audit_logs` definition to `Sql/Bank Management System.sql`, or annotate `src/main/java/com/bank/model/AuditLog.java` with `@Entity` so Hibernate can auto-generate it.

**Plural vs Singular table naming mismatch (`account` vs `accounts`):**
- Issue: DDL defines the table as `account` (singular) while the Java DAO executes queries against `accounts` (plural).
- Files: `Sql/Bank Management System.sql` (line 40: `CREATE TABLE account`), `src/main/java/com/bank/dao/AccountDAO.java` (lines 14, 61, 83, 93, 127, 136: uses table `accounts`).
- Why: Development naming inconsistency.
- Impact: Running queries will fail with table-not-found `SQLException`s.
- Fix approach: Standardize table name. Rename table in `Sql/Bank Management System.sql` to `accounts` or update all SQL calls in `AccountDAO.java` to query `account`.

**Missing model annotations for JPA:**
- Issue: `pom.xml` contains the dependency `spring-boot-starter-data-jpa` but none of the Java models (e.g. `Customer`, `Employee`, `Account`) are annotated with `@Entity`, and there are no JPA repositories.
- Files: `pom.xml`, `src/main/java/com/bank/model/*.java`.
- Why: The project was built using standard JDBC DAOs despite carrying the JPA starter dependency.
- Impact: Bloated dependencies and lack of standardized JPA features.
- Fix approach: Either remove the JPA dependency from `pom.xml` or convert the JDBC DAOs to Spring Data JPA Repositories.

## Known Schema Mismatches

**Employee Table Column Mismatch:**
- Issue: DDL columns do not match DAO insert/select statements.
- Files: `Sql/Bank Management System.sql` (lines 12-21), `src/main/java/com/bank/dao/EmployeeDAO.java` (lines 15-29, 35-45, 81-96).
- Mismatches:
  - DDL has `Fname` and `Lname`, but Java expects `empname`.
  - Java inserts/reads `gender`, `birthdate`, and `type`, which are entirely missing in the DDL.
  - Java uses `joiningdate` and `password_hash`, but DDL uses camelCase `JoiningDate` and `PasswordHash`.
  - Java expects `reset_token` and `token_expiry` columns for employee resets which are missing in the DDL.
- Impact: Server-side crashes on any employee-related database operation.
- Fix approach: Update DDL to add the missing columns and match Java property names.

**Customer Table Column Mismatch:**
- Issue: Database table lacks columns utilized by the DAO.
- Files: `Sql/Bank Management System.sql` (lines 25-36), `src/main/java/com/bank/dao/CustomerDAO.java`.
- Mismatches:
  - Java expects `opening_date`, `reset_token`, and `token_expiry` columns, but these are absent from the DDL.
  - Java maps columns as snake_case (`contactno`, `password_hash`, `homebranch`), while DDL defines them in mixed styles (`Contact_No`, `PasswordHash`, `Home_Branch`).
- Impact: Failures on customer creation or login.
- Fix approach: Harmonize database columns in the DDL schema with Java properties.

## Security Considerations

**Hardcoded Database Credentials:**
- Risk: Exposes default connection secrets in source control.
- Files: `src/main/java/com/bank/util/DatabaseConnector.java` (lines 8-10).
- Current mitigation: None.
- Recommendations: Extract credentials to environment variables or load them from Spring's environment properties.

**Hardcoded Email/SMTP Credentials:**
- Risk: Exposes email account secrets.
- Files: `src/main/java/com/bank/util/EmailService.java` (lines 9-10).
- Current mitigation: None.
- Recommendations: Extract credentials to environment variables.

## Performance Bottlenecks

**Manual Connection Allocation in DAOs:**
- Problem: DAOs create and close database connections for every single method invocation rather than using a pooled datasource (e.g. HikariCP).
- Files: `src/main/java/com/bank/dao/*.java`.
- Impact: High database latency due to continuous TCP handshake overhead.
- Improvement path: Migrate to a database connection pool or let Spring manage connection lifecycle via `@Repository`.

## API & Design Inconsistencies

**Ajax requests handling via HTML Redirection:**
- Problem: JSON requests matching `/api/dashboard` receive an HTML page redirect when unauthenticated rather than a `401 Unauthorized` status response.
- File: `src/main/java/com/bank/controller/DashboardServlet.java` (line 29: `response.sendRedirect("login.html")`).
- Impact: Client-side JS fetches will receive redirect HTML instead of descriptive JSON, complicating frontend session checking.
- Fix: Replace redirect with `response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)` similar to `TransferServlet.java`.

## Test Coverage Gaps

**Zero Test Coverage:**
- What's not tested: Entire codebase (no unit, integration, or E2E tests).
- Risk: Changes can easily break database queries, API routing, or business logic without warning.
- Priority: High.
- Difficulty to test: Requires setting up JUnit, Mockito, and a local test database config.

---

*Concerns audit: 2026-05-20*
*Update as issues are fixed or new ones discovered*
