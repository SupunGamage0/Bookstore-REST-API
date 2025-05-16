package com.bookstore.exceptions;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BookExceptionMapper implements ExceptionMapper<BookNotFoundException> {
    @Override
    public Response toResponse(BookNotFoundException ex) {
        JsonObject error = Json.createObjectBuilder()
            .add("error", "Book Not Found")
            .add("message", ex.getMessage())
            .build();
        
        return Response.status(Response.Status.NOT_FOUND)
            .entity(error)
            .type("application/json")
            .build();
    }
}