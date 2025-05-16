package com.bookstore.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class AuthorExceptionMapper implements ExceptionMapper<AuthorNotFoundException> {
    @Override
    public Response toResponse(AuthorNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Author Not Found");
        error.put("message", ex.getMessage());
        return Response.status(404).entity(error).build();
    }
}