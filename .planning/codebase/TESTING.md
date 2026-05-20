# Testing Patterns

**Analysis Date:** 2026-05-20

## Test Framework

**Runner & Assertion Libraries:**
- **None:** The codebase does not have any automated testing framework (such as JUnit, Mockito, or Spring Boot Starter Test) configured in `pom.xml`.
- There are no unit, integration, or end-to-end tests written in the source folder (`src/`).

## Verification Methodology (Manual Testing)

Currently, all verification is conducted manually through local deployments, API client requests, and direct database queries.

### Run & Build Commands
```bash
mvn clean package                       # Build and compile application (generates .war)
mvn spring-boot:run                    # Launch application locally on default port
```

### Manual Verification Flows

**1. Authentication Flows:**
- Navigate to `http://localhost:8080/login.html` or `register.html`.
- Submit forms, checking input validation on both the client (HTML form inputs) and server (servlet outputs).
- Verify successful redirects to `dashboard.html` (for customers) or `employee-dashboard.html` (for bank staff).

**2. Transaction and Transfer Validation:**
- Perform deposits and withdrawals via the web interfaces.
- Execute funds transfers across accounts on `transfer.html`.
- Inspect values inside the database to verify the correct adjustments:
  - Verify account balance modifications (`balance` attribute in table `account`).
  - Verify records inserted into `transactions` and `transfers`.

**3. Report Output Generation:**
- Request statements via the endpoint:
  `GET http://localhost:8080/api/reports/statement?accno=[ACCOUNT_NUMBER]`
- Verify that a valid PDF file downloads and details all correct historical transactions.

**4. Database Telemetry Audits:**
- Query the `audit_log` table:
  `SELECT * FROM audit_log ORDER BY timestamp DESC;`
- Confirm that every request path, remote IP, HTTP status, and user-agent has been recorded correctly by the servlet filter.

---

*Testing analysis: 2026-05-20*
*Update when test patterns change*
