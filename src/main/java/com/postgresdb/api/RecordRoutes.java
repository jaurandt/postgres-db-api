package com.postgresdb.api;

import com.google.gson.Gson;
import com.postgresdb.model.Record;
import com.postgresdb.store.RecordStore;

import static spark.Spark.*;

/**
 * Registers the five REST routes for the /records resource.
 *
 * GET    /records        -> list all records
 * GET    /records/:id    -> get one record by id
 * POST   /records        -> create a new record  (body: {"name":"x","value":"y"})
 * PUT    /records/:id    -> update a record       (body: {"name":"x","value":"y"})
 * DELETE /records/:id    -> delete a record
 */
public class RecordRoutes {

    private final RecordStore store;
    private final Gson gson;

    public RecordRoutes(RecordStore store) {
        this.store = store;
        this.gson  = new Gson();
    }

    public void register() {

        // Send JSON content-type on every response
        before((req, res) -> res.type("application/json"));

        // GET /records — list all
        get("/records", (req, res) ->
            gson.toJson(store.findAll())
        );

        // GET /records/:id — get one
        get("/records/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Record record = store.findById(id);

            if (record == null) {
                res.status(404);
                return gson.toJson(new ErrorResponse("Record not found: " + id));
            }
            return gson.toJson(record);
        });

        // POST /records — create
        post("/records", (req, res) -> {
            Record incoming = gson.fromJson(req.body(), Record.class);

            if (incoming.getName() == null || incoming.getValue() == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Body must include 'name' and 'value'"));
            }

            Record created = store.create(incoming.getName(), incoming.getValue());
            res.status(201);
            return gson.toJson(created);
        });

        // PUT /records/:id — update
        put("/records/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Record incoming = gson.fromJson(req.body(), Record.class);
            Record updated = store.update(id, incoming.getName(), incoming.getValue());

            if (updated == null) {
                res.status(404);
                return gson.toJson(new ErrorResponse("Record not found: " + id));
            }
            return gson.toJson(updated);
        });

        // DELETE /records/:id — delete
        delete("/records/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            boolean deleted = store.delete(id);

            if (!deleted) {
                res.status(404);
                return gson.toJson(new ErrorResponse("Record not found: " + id));
            }
            return gson.toJson(new MessageResponse("Deleted record " + id));
        });

        // Handle non-integer IDs (e.g. GET /records/abc)
        exception(NumberFormatException.class, (e, req, res) -> {
            res.status(400);
            res.type("application/json");
            res.body(gson.toJson(new ErrorResponse("ID must be an integer")));
        });

        // Handle database errors
        exception(java.sql.SQLException.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body(gson.toJson(new ErrorResponse("Database error: " + e.getMessage())));
        });
    }

    private static class ErrorResponse {
        String error;
        ErrorResponse(String msg) { this.error = msg; }
    }

    private static class MessageResponse {
        String message;
        MessageResponse(String msg) { this.message = msg; }
    }
}