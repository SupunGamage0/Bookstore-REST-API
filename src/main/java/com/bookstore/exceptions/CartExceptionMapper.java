package com.bookstore.exceptions;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class CartExceptionMapper implements ExceptionMapper<CartNotFoundException> {
    @Override
    public Response toResponse(CartNotFoundException ex) {
        JsonObject error = Json.createObjectBuilder()
            .add("error", "Cart Not Found")
            .add("message", ex.getMessage())
            .build();
        
        return Response.status(Response.Status.NOT_FOUND)
            .entity(error)
            .type("application/json")
            .build();
    }
}