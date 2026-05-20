# Architecture

**Analysis Date:** 2026-05-20

## Pattern Overview

**Overall:** Layered Monolithic Architecture (Web Servlet API + Spring Boot REST)

**Key Characteristics:**
- **Dual Routing Engines:** Merges traditional Jakarta Servlet API routing (mapped in `src/main/webapp/WEB-INF/web.xml` or via `@WebServlet`) and modern Spring Web MVC routing (managed by `@RestController` classes).
- **Session-Based State:** Uses standard server-side HTTP session storage (`HttpSession`) to maintain customer and employee user states.
- **DAO Data Access Pattern:** Employs standard Data Access Object (DAO) classes managing raw JDBC connections via a shared utility pool, alongside Spring Boot configuration mapping.
- **Client-Side Rendering (Partial):** Serving static HTML/CSS/JS resources directly from `src/main/webapp/` which perform fetch-based Ajax calls to JSON API endpoints.

## Layers

**Presentation Layer:**
- Purpose: Direct user interfaces and interactions.
- Contains: Static web resources: HTML files (`index.html`, `login.html`, `dashboard.html`), CSS styling, and client-side JavaScript.
- Location: `src/main/webapp/`
- Depends on: Web API Layer (accessed via JavaScript `fetch()`).

**Web API / Controller Layer:**
- Purpose: Entry point for requests, parameter mapping, session lookup, and HTTP responses.
- Contains:
  - Custom Servlets extending `HttpServlet` and annotated with `@WebServlet` (e.g., `LoginServlet`, `DashboardServlet`, `TransferServlet`).
  - Spring controllers annotated with `@RestController` (e.g., `AccountController`, `PdfController`).
- Location: `src/main/java/com/bank/controller/`
- Depends on: DAO Layer for database read/write actions, Utility layer for helper processes.
- Used by: Presentation Layer.

**DAO / Data Access Layer:**
- Purpose: Low-level database command execution and mapper logic.
- Contains: DAO classes executing prepared statements or invoking connection wrappers (e.g., `CustomerDAO`, `AccountDAO`, `TransactionDAO`).
- Location: `src/main/java/com/bank/dao/`
- Depends on: DatabaseConnector utility, Model layer.
- Used by: Web API / Controller Layer.

**Model Layer:**
- Purpose: Entity models mapping to MySQL database tables.
- Contains: Plain Old Java Objects (POJOs) with attributes, getters, setters, and JSON serialization helpers (e.g., `Customer.java`, `Account.java`, `Transaction.java`).
- Location: `src/main/java/com/bank/model/`
- Depends on: Standard Java libraries and `org.json`.
- Used by: Controller and DAO Layers.

## Data Flow

**Customer Login & Dashboard Fetch Flow:**

1. **User Action:** Customer inputs User ID and password at `login.html` and clicks login.
2. **API Request:** Client-side JavaScript makes an asynchronous POST request to `/api/login` containing a JSON body.
3. **Authentication Servlet:** `LoginServlet.java` intercepts the request:
   - Reads request body using a `BufferedReader`.
   - Parses payload using `org.json.JSONObject`.
   - Invokes `CustomerDAO.getCustomerByIdentificationNo()`.
4. **DAO Query:** `CustomerDAO.java` opens a connection from `DatabaseConnector`, queries the database, maps results to a `Customer` object, and closes the connection.
5. **Credential Verification:** `LoginServlet` checks password hashes using `BCrypt.checkpw()`.
6. **Session & Response:**
   - If valid, binds the `Customer` object to an `HttpSession` and responds with a `200 OK` JSON success message.
   - If invalid, responds with a `401 Unauthorized` JSON message.
7. **View Transition:** On success, JavaScript redirects the user to `dashboard.html`, which fetches current profile/account data.

**State Management:**
- Application state is server-bound via Tomcat's `HttpSession`.
- Persistent state lives entirely inside the MySQL database instance.

## Key Abstractions

**DatabaseConnector:**
- Purpose: Provides raw database Connection instances to DAOs.
- Location: `src/main/java/com/bank/util/DatabaseConnector.java`
- Pattern: Static helper singleton wrapper utilizing the JDBC `DriverManager`.

**Data Access Object (DAO):**
- Purpose: Isolates database details from controller layer.
- Examples: `src/main/java/com/bank/dao/AccountDAO.java`, `src/main/java/com/bank/dao/CustomerDAO.java`.
- Pattern: DAO structural pattern.

**InterestScheduler / InterestService:**
- Purpose: Calculates and applies interest rates at specific calendar offsets.
- Location: `src/main/java/com/bank/util/InterestScheduler.java`, `src/main/java/com/bank/service/InterestService.java`.
- Pattern: Background executor scheduling via `ScheduledExecutorService`.

## Entry Points

**Spring Boot Application Launcher:**
- Location: `src/main/java/com/bank/MainApplication.java`
- Trigger: JVM startup.
- Responsibilities: Initializes the Spring container and starts the embedded Web container.

**Servlet Filters / Mappings:**
- Location: `src/main/webapp/WEB-INF/web.xml`
- Trigger: HTTP Requests match filter mapping `/*`.
- Responsibilities: Intercepts all requests via `AuditFilter.java` for telemetry logging.

## Error Handling

**Strategy:** Exception bubbling caught at controller borders.

**Patterns:**
- DAO operations throw `SQLException` up to the servlet or controller.
- Servlets wrap execution in standard try-catch blocks:
  - Prints stack trace (`e.printStackTrace()`) or logs error.
  - Sets appropriate HTTP status codes (e.g. `500 Internal Server Error`, `401 Unauthorized`).
  - Writes standard JSON error responses using `org.json.JSONObject`.

## Cross-Cutting Concerns

**Audit Logging:**
- Handled at the gateway boundary by `src/main/java/com/bank/filter/AuditFilter.java`.
- Intercepts requests, collects telemetry, calls `AuditDAO.logActivity()` to log to database table `audit_log`, then forwards request.

**Password Security:**
- Hashing performed using BCrypt via `org.mindrot.jbcrypt.BCrypt`.
- Password verification done at authentication endpoints.

---

*Architecture analysis: 2026-05-20*
*Update when major patterns change*
