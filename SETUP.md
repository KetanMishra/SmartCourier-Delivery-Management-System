# Smart Courier — Setup and Run (MySQL / Aiven)

This guide explains how to run the Smart Courier microservices stack and how to use **[Aiven for MySQL](https://aiven.io/mysql)** with a project `.env` file (same MySQL driver; no database vendor change).

## Prerequisites

| Requirement | Notes |
|-------------|--------|
| **Java 21** | The parent `pom.xml` targets Java 21. |
| **Maven 3.9+** | Used to build and run modules. |
| **Docker Desktop** (optional) | Easiest way to run Eureka, Config Server, RabbitMQ, and optionally local MySQL. |
| **Aiven account** (optional) | [https://aiven.io](https://aiven.io) — if you use hosted MySQL instead of the Compose `mysql` service. |

**Windows — several JDKs installed:** Maven follows **`JAVA_HOME`**. Set the user variable `JAVA_HOME` to your JDK 21 folder (for example `C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot`), then open a **new** terminal. If `java -version` still shows another version, **System** `Path` probably lists another JDK first — change order in *Environment Variables*, or uninstall that JDK (e.g. elevated PowerShell: `winget uninstall Amazon.Corretto.25.JDK`).

---

## 1. Clone and build

From the repository root (`Smart_Courier`):

```powershell
mvn clean verify
```

This compiles all modules and runs tests. For a faster compile without tests:

```powershell
mvn clean install -DskipTests
```

The project targets **Java 21** (`java.version` in the root `pom.xml`). If you run Maven on **JDK 24 or newer**, the root POM already bumps **Byte Buddy** and **JaCoCo** so tests and coverage agents understand newer class file versions; **JDK 21 LTS** is still the smoothest choice for day-to-day development.

---

## 2. Run with Docker Compose (default: includes local MySQL)

The included `docker-compose.yml` starts **MySQL**, **RabbitMQ**, Eureka, Config Server, and all microservices.

If you have a **`.env`** file in the project root (for example after copying `.env.example` and filling `DB_PASSWORD` for Aiven), Docker Compose **automatically reads it** and passes `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, and `DB_USE_SSL` into `auth-service`, `delivery-service`, `tracking-service`, and `admin-service`. Defaults still point at the Compose `mysql` service when those variables are not set.

```powershell
docker compose up --build
```

**Service order** is handled by Compose `depends_on`. Typical URLs after startup:

| Component | URL |
|-----------|-----|
| API Gateway | [http://localhost:8080](http://localhost:8080) |
| Auth Swagger | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| Delivery Swagger | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| Tracking Swagger | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |
| Admin Swagger | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) |
| Eureka | [http://localhost:8761](http://localhost:8761) |
| Config Server (example) | [http://localhost:8888/gateway-service/default](http://localhost:8888/gateway-service/default) |
| RabbitMQ UI | [http://localhost:15672](http://localhost:15672) (guest / guest) |

MySQL is exposed on **3306** with user `root` / password `root`. Databases are initialized from `docs/init-multiple-dbs.sql`.

---

## 3. Run without Docker (Maven + local infrastructure)

You still need **Eureka**, **Config Server**, **RabbitMQ**, and a **MySQL 8** instance unless you move those to cloud equivalents.

**Suggested startup order:**

1. `discovery-server` (port **8761**)
2. `config-server` (port **8888**)
3. `auth-service` (**8081**)
4. `delivery-service` (**8082**)
5. `tracking-service` (**8083**)
6. `admin-service` (**8084**)
7. `gateway-service` (**8080**)

Example (run each in its own terminal from the repo root):

```powershell
cd discovery-server; mvn spring-boot:run
```

```powershell
cd config-server; mvn spring-boot:run
```

Repeat for `auth-service`, `delivery-service`, `tracking-service`, `admin-service`, `gateway-service`.

Ensure MySQL has the four databases created (same as `docs/init-multiple-dbs.sql`) and that RabbitMQ is reachable at `localhost:5672`.

---

## 4. Environment variables for databases

Each data service reads:

| Variable | Purpose | Default in `application.yml` |
|----------|---------|------------------------------|
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `smartcourier_auth`, `smartcourier_delivery`, etc. |
| `DB_USERNAME` | User | `root` |
| `DB_PASSWORD` | Password | `root` |
| `DB_USE_SSL` | JDBC `useSSL` (`true` / `false`) | `false` (use `true` for Aiven and most cloud MySQL) |

Docker Compose sets these for you when using the bundled MySQL service, or substitutes values from your project `.env` file.

---

## 5. Aiven cloud MySQL (no driver change)

This stack already uses **MySQL**; [Aiven for MySQL](https://aiven.io/mysql) works with the same connector. You only set **environment variables** (or a local `.env` file — see `.env.example`). **Do not commit passwords** to Git.

### 5.1 Connect with the MySQL CLI

Use your password **interactively** (safer than putting it on the command line):

```bash
mysql --user=avnadmin --password --host=mysql-37546c95-delivery-system-01.e.aivencloud.com --port=24515 defaultdb
```




After you run the command, the client prompts for the password. Equivalent one-liner with an env var (PowerShell):

```powershell
$env:MYSQL_PWD = "<paste-password-only-in-session-not-in-files>"
mysql --user=avnadmin --host=mysql-37546c95-delivery-system-01.e.aivencloud.com --port=24515 defaultdb
```

Unset when done: `Remove-Item Env:MYSQL_PWD`.

**Security:** If you ever pasted your Aiven password in chat or committed it, **rotate the password** in the Aiven console and update your local `.env` only.

### 5.2 Create the four application databases

Smart Courier expects **four** databases (see `docs/init-multiple-dbs.sql`). In the MySQL session (or Aiven SQL / query UI), run:

```sql
CREATE DATABASE IF NOT EXISTS smartcourier_auth;
CREATE DATABASE IF NOT EXISTS smartcourier_delivery;
CREATE DATABASE IF NOT EXISTS smartcourier_tracking;
CREATE DATABASE IF NOT EXISTS smartcourier_admin;
```

If your plan does not allow creating extra databases, use Aiven support/docs or a single DB only after aligning all services to one `DB_NAME` (not the default layout of this project).

### 5.3 Create your `.env` file

1. In the **project root** (same folder as `docker-compose.yml`), copy the template:
   ```powershell
   Copy-Item .env.example .env
   ```
2. Open `.env` and set **`DB_PASSWORD`** to the password from the Aiven console (Service overview → connection details).
3. Confirm **`DB_USE_SSL=true`** (required for Aiven).
4. Adjust **`DB_HOST`**, **`DB_PORT`**, and **`DB_USERNAME`** if Aiven shows different values than `.env.example`.

`.env` is listed in `.gitignore` — do not commit it.

---

### 5.4 After `.env` is ready — checklist before starting the app

| Step | Action |
|------|--------|
| 1 | **Create the four databases** on Aiven (section **5.2**). Without them, services fail on first DB access. |
| 2 | **Test the network path** from your machine: use the MySQL CLI (section **5.1**) and run `SHOW DATABASES;` — you should see `smartcourier_auth`, `smartcourier_delivery`, `smartcourier_tracking`, `smartcourier_admin` (and `defaultdb`). |
| 3 | **RabbitMQ** must still be reachable where your services expect it (`RABBITMQ_HOST`). With Docker Compose it is the `rabbitmq` service; with plain Maven on the host, run RabbitMQ locally or via `docker run` (see **5.6**). |

---

### 5.5 Run everything with Docker Compose (Aiven as database)

With `.env` in the project root:

```powershell
docker compose up --build
```

Compose substitutes `${DB_HOST}`, `${DB_PORT}`, `${DB_USERNAME}`, `${DB_PASSWORD}`, and `${DB_USE_SSL}` into the four data services, so containers connect to **Aiven** using your `.env` values.

**Note:** The **local `mysql` container still starts** by default (and `depends_on` still waits for it). That is only used if you switch back to local DB variables; with Aiven in `.env`, the app ignores that container for data. To save resources, you may locally comment out the `mysql` service and remove `mysql` from each service’s `depends_on` list in `docker-compose.yml` (keep a backup or use Git to revert).

---

### 5.6 Run with Maven on your machine (Aiven as database)

Spring Boot **does not load `.env` files by itself**. You must put the same variables into the process environment before `mvn spring-boot:run`.

**Option A — PowerShell: load `.env` into the current session** (run from the project root):

```powershell
Get-Content .\.env | ForEach-Object {
  $t = $_.Trim()
  if ($t -eq '' -or $t.StartsWith('#')) { return }
  $i = $t.IndexOf('=')
  if ($i -lt 1) { return }
  $k = $t.Substring(0, $i).Trim()
  $v = $t.Substring($i + 1).Trim()
  Set-Item -Path "Env:$k" -Value $v
}
```

Then verify:

```powershell
$env:DB_HOST
$env:DB_USE_SSL
```

**Option B — IntelliJ / VS Code:** define the same variables in the run configuration for each Spring Boot module.

**RabbitMQ on the host:** if you are not using Compose, start RabbitMQ so `localhost:5672` is available, for example:

```powershell
docker run -d --name smartcourier-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management
```

**Startup order** (each in its own terminal **after** loading env vars in that terminal, or use your IDE with env configured):

1. `discovery-server` → **8761**  
2. `config-server` → **8888**  
3. `auth-service` → **8081**  
4. `delivery-service` → **8082**  
5. `tracking-service` → **8083**  
6. `admin-service` → **8084**  
7. `gateway-service` → **8080**

Example:

```powershell
cd discovery-server; mvn spring-boot:run
```

Repeat for the other modules in order. Hibernate `ddl-auto: update` will create tables in each Aiven database on first startup.

---

### 5.7 Environment variables reference (Aiven / any cloud MySQL)

| Variable | Purpose | Example (Aiven) |
|----------|---------|-----------------|
| `DB_HOST` | MySQL hostname | `mysql-xxxx.a.aivencloud.com` |
| `DB_PORT` | MySQL port | `24515` (your service port) |
| `DB_USERNAME` | User | `avnadmin` |
| `DB_PASSWORD` | Password | (from console only; keep in `.env`) |
| `DB_USE_SSL` | TLS for JDBC | `true` |

Each service sets **`DB_NAME`** in `application.yml` (`smartcourier_auth`, `smartcourier_delivery`, etc.) — you normally do not put `DB_NAME` in `.env` unless you override per deployment.

---

## 6. Config Server and Git profile (optional)

To use a Git-backed config repo with Docker:

```powershell
$env:CONFIG_SERVER_PROFILE = "git"
$env:CONFIG_REPO_URI = "https://github.com/<your-user>/<your-config-repo>.git"
docker compose up --build
```

If the repo is private, set `CONFIG_REPO_USERNAME` and `CONFIG_REPO_PASSWORD` as in the main `README.md`.

If your **config-repo YAML** files override datasource settings, keep them aligned with the same `DB_*` / SSL values you use locally or on Aiven.

---

## 7. Quick troubleshooting

| Issue | What to check |
|-------|----------------|
| Services cannot register | Eureka (`8761`) is up before other services. |
| Config fetch failures | Config Server (`8888`) is running; `CONFIG_SERVER_URL` matches your setup. |
| Database connection errors | Host, port, user/password. For Aiven set `DB_USE_SSL=true` in `.env`. For Maven, confirm env vars are set (`$env:DB_HOST`) after loading `.env` (section **5.6**). |
| `mvn clean` fails: cannot delete `target\*.jar` or `jacoco.exec` | On Windows another process (IDE, running app, antivirus) has the file open. Stop the app / close the IDE’s run, then delete the module’s `target` folder manually, or run `mvn verify` without `clean`. The root POM enables `retryOnError` on the clean plugin to reduce flakes. |

---

## Summary

- **Default project:** Java 21 + Maven; **MySQL** per service + RabbitMQ + Eureka + Config Server + Gateway.
- **Docker:** `docker compose up --build` from the repo root. A `.env` file overrides `DB_*` / `DB_USE_SSL` for the four data services via Compose variable substitution.
- **Aiven / cloud MySQL:** Copy **`.env.example` → `.env`**, set **`DB_PASSWORD`** and **`DB_USE_SSL=true`**, create the four databases (**5.2**), then either run **Docker Compose** (**5.5**) or **load `.env` into the shell** and start modules with Maven (**5.6**). MySQL CLI: **5.1**.
