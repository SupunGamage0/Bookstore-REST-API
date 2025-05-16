package com.bookstore;

import com.bookstore.exceptions.*;
import com.bookstore.resources.*;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api") // Base URI for all resources
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // Register resource classes
        register(BookResource.class);
        register(AuthorResource.class);
        register(CustomerResource.class);
        register(CartResource.class);
        register(OrderResource.class);

        // Register exception mappers
        register(BookExceptionMapper.class);
        register(AuthorExceptionMapper.class);
        register(CustomerExceptionMapper.class);
        register(OutOfStockExceptionMapper.class);
        register(CartExceptionMapper.class);
        register(OrderExceptionMapper.class);
        register(InvalidInputExceptionMapper.class);
    }
}


