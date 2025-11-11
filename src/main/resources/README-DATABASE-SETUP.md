# Database Setup

## Quick Start

### 1️⃣ One-Time Database Setup (All Environments)

Run the admin setup script **once** with MySQL root privileges:

```bash
mysql -u root -p < src/main/resources/db/db-setup-admin.sql
```

This creates:
- Database: `db_experiment`
- User: `dbexp` with password `A1b212345`
- Grants all privileges on the database

### 2️⃣ Run the Application

#### Development Mode (Default)
```bash
# Using Maven
mvn spring-boot:run

# Using Maven Wrapper
./mvnw spring-boot:run

# Or explicitly specify profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**What happens:**
- ✅ Drops and recreates all tables on startup
- ✅ Loads sample test data automatically
- ⚠️ **All data is lost on every restart!**

#### Production Mode
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Using JAR
java -jar target/db-experiment-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Using environment variable
export SPRING_PROFILES_ACTIVE=prod
java -jar target/db-experiment-0.0.1-SNAPSHOT.jar
```

**What happens:**
- ✅ No automatic schema changes
- ✅ Existing data is preserved
- ✅ Safe for production deployment

---

## File Structure

```
src/main/resources/
├── application.properties              # Common configuration (all environments)
├── application-dev.properties          # Development profile (mode=always)
├── application-prod.properties         # Production profile (mode=never)
└── db/
    ├── db-setup-admin.sql             # One-time DB & user creation (manual)
    ├── schema.sql                     # Dev: DROP + CREATE tables
    ├── schema-prod.sql                # Prod: CREATE IF NOT EXISTS (manual)
    ├── data.sql                       # Dev: Sample test data
    └── db.sql                         # Original script (reference)
```

---

## Configuration Details

### Development Profile (`application-dev.properties`)

```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:db/schema.sql
spring.sql.init.data-locations=classpath:db/data.sql
```

**Behavior:**
- Executes [`schema.sql`](db/schema.sql) on **every** startup
- Drops all existing tables first
- Creates fresh schema
- Loads sample data from [`data.sql`](db/data.sql)

### Production Profile (`application-prod.properties`)

```properties
spring.sql.init.mode=never
```

**Behavior:**
- **No** automatic SQL script execution
- Schema must be managed manually or via migration tools
- Existing data is **never** touched

---

## Profile Comparison

| Feature | Development (`dev`) | Production (`prod`) |
|---------|-------------------|-------------------|
| **SQL Init Mode** | `always` | `never` |
| **Schema Script** | [`schema.sql`](db/schema.sql) (DROP + CREATE) | Manual or Flyway |
| **Data Loading** | [`data.sql`](db/data.sql) (sample data) | None |
| **Data Persistence** | Lost on restart | Persisted |
| **Startup Behavior** | Fresh DB each time | No schema changes |
| **Connection Pool** | 10 max connections | 20 max connections |
| **Logging** | DEBUG | WARN |
