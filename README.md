# postgres-db-api

A simple REST API backed by a real PostgreSQL database, built with Java and Spark. Supports full CRUD operations on a `records` table.

---

## Requirements

- Java 21+
- Maven
- PostgreSQL (running locally)

---

## Setup

### 1. Install PostgreSQL

Download and install PostgreSQL from https://www.postgresql.org/download/.

**Windows: Add PostgreSQL to your PATH**

After installation, `psql` may not be recognized in PowerShell. To fix this, add the PostgreSQL `bin` folder to your PATH for the current session (adjust the version number if needed):

```powershell
$env:PATH += ";C:\Program Files\PostgreSQL\18\bin"
```

To make this permanent, open **Start → Edit the system environment variables → Environment Variables**, find **Path** under System variables, and add `C:\Program Files\PostgreSQL\18\bin`.

### 2. Create the database

Open a terminal and connect to PostgreSQL:

```bash
psql -U postgres
```

Then create the database:

```sql
CREATE DATABASE postgresdb;
```

Exit with `\q`.

### 3. Create the table

```bash
psql -U postgres -d postgresdb -f src/main/resources/schema.sql
```

### 4. Configure the connection

The app reads connection details from environment variables. Set them before running:

```bash
# Bash / Git Bash
export DB_URL=jdbc:postgresql://localhost:5432/postgresdb
export DB_USER=postgres
export DB_PASSWORD=your_password
```

```powershell
# PowerShell
$env:DB_URL = "jdbc:postgresql://localhost:5432/postgresdb"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "your_password"
```

If you don't set these, the app uses the defaults (`postgres` user, no password, database named `postgresdb`).

### 5. Run the server

```bash
mvn exec:java
```

The server starts on **http://localhost:4568**.

---

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/records` | List all records |
| GET | `/records/:id` | Get a record by id |
| POST | `/records` | Create a new record |
| PUT | `/records/:id` | Update an existing record |
| DELETE | `/records/:id` | Delete a record |

### Record fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | integer | Auto-assigned by PostgreSQL (do not set manually) |
| `name` | string | A short label for the record |
| `value` | string | Any string payload |

---

## Examples

**List all records**
```bash
curl http://localhost:4568/records
```
```powershell
Invoke-RestMethod http://localhost:4568/records
```

**Get a single record**
```bash
curl http://localhost:4568/records/1
```
```powershell
Invoke-RestMethod http://localhost:4568/records/1
```

**Create a record**
```bash
curl -X POST http://localhost:4568/records \
  -H "Content-Type: application/json" \
  -d '{"name": "my-record", "value": "hello world"}'
```
```powershell
Invoke-RestMethod -Method POST -Uri http://localhost:4568/records `
  -ContentType "application/json" `
  -Body '{"name": "my-record", "value": "hello world"}'
```

**Update a record**
```bash
curl -X PUT http://localhost:4568/records/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "updated-name", "value": "new value"}'
```
```powershell
Invoke-RestMethod -Method PUT -Uri http://localhost:4568/records/1 `
  -ContentType "application/json" `
  -Body '{"name": "updated-name", "value": "new value"}'
```

**Delete a record**
```bash
curl -X DELETE http://localhost:4568/records/1
```
```powershell
Invoke-RestMethod -Method DELETE -Uri http://localhost:4568/records/1
```

---

## Exploring the data directly in PostgreSQL

You can connect to the database and run SQL queries while the server is running:

```bash
psql -U postgres -d postgresdb
```

```sql
SELECT * FROM records;
```

This lets you see exactly what the API is doing under the hood.