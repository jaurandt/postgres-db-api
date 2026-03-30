package com.postgresdb.store;

import com.postgresdb.model.Record;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the records table.
 *
 * Uses HikariCP to borrow connections from a pool rather than opening
 * a new connection on every request — this is standard production practice.
 *
 * Each method follows the same pattern:
 *   1. Borrow a connection from the pool  (try-with-resources closes it automatically)
 *   2. Prepare a SQL statement
 *   3. Execute it and map the result back to a Record object
 */
public class RecordStore {

    private final HikariDataSource dataSource;

    public RecordStore(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // CREATE
    public Record create(String name, String value) throws SQLException {
        String sql = "INSERT INTO records (name, value) VALUES (?, ?) RETURNING id, name, value";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, value);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return mapRow(rs);
        }
    }

    // READ - all
    public List<Record> findAll() throws SQLException {
        String sql = "SELECT id, name, value FROM records ORDER BY id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Record> records = new ArrayList<>();
            while (rs.next()) {
                records.add(mapRow(rs));
            }
            return records;
        }
    }

    // READ - one
    public Record findById(int id) throws SQLException {
        String sql = "SELECT id, name, value FROM records WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    // UPDATE
    public Record update(int id, String name, String value) throws SQLException {
        String sql = "UPDATE records SET name = ?, value = ? WHERE id = ? RETURNING id, name, value";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, value);
            stmt.setInt(3, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    // DELETE
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM records WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Maps a row from a ResultSet into a Record object
    private Record mapRow(ResultSet rs) throws SQLException {
        return new Record(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("value")
        );
    }
}