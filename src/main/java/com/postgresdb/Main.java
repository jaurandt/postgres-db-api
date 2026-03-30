package com.postgresdb;

import com.postgresdb.api.RecordRoutes;
import com.postgresdb.store.RecordStore;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static spark.Spark.*;

/**
 * Application entry point.
 * Starts an HTTP server on port 4568.
 * Access the API at: http://localhost:4568/records
 *
 * Reads database connection details from environment variables:
 *   DB_URL      - JDBC connection string  (default: jdbc:postgresql://localhost:5432/postgresdb)
 *   DB_USER     - database username       (default: postgres)
 *   DB_PASSWORD - database password       (default: empty)
 */
public class Main {

    public static void main(String[] args) {

        // Read connection settings from environment variables
        String dbUrl      = System.getenv().getOrDefault("DB_URL",      "jdbc:postgresql://localhost:5432/postgresdb");
        String dbUser     = System.getenv().getOrDefault("DB_USER",     "postgres");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "");

        // Configure HikariCP connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);

        HikariDataSource dataSource = new HikariDataSource(config);

        port(4568);

        RecordStore store = new RecordStore(dataSource);
        new RecordRoutes(store).register();

        awaitInitialization();
        System.out.println("=========================================");
        System.out.println(" Postgres DB API is running!");
        System.out.println(" Base URL: http://localhost:4568/records");
        System.out.println("=========================================");
    }
}