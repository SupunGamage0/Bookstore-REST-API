package com.bookstore.exceptions;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OutOfStockExceptionMapper implements ExceptionMapper<OutOfStockException> {
    @Override
    public Response toResponse(OutOfStockException ex) {
        JsonObject error = Json.createObjectBuilder()
            .add("error", "Out of Stock")
            .add("message", ex.getMessage())
            .build();
        
        return Response.status(Response.Status.CONFLICT) // 409 Conflict
            .entity(error)
            .type("application/json")
            .build();
    }
}