package com.bookstore.exceptions;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {
    @Override
    public Response toResponse(InvalidInputException ex) {
        JsonObject error = Json.createObjectBuilder()
            .add("error", "Invalid Input")
            .add("message", ex.getMessage())
            .build();
        
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(error)
            .type("application/json")
            .build();
    }
}
