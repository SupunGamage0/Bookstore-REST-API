package com.bookstore.resources;

import com.bookstore.models.Customer;
import com.bookstore.exceptions.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Path("/customers")
public class CustomerResource {
    private static final Logger LOGGER = Logger.getLogger(CustomerResource.class.getName());
    public static Map<Integer, Customer> customers = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // POST /customers
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer customer) {
        LOGGER.info("[SERVER] [POST /customers] Creating customer: " + customer.getEmail());
        
        // Validate email format
        if (!EMAIL_REGEX.matcher(customer.getEmail()).matches()) {
            LOGGER.warning("[SERVER] [POST /customers] Invalid email: " + customer.getEmail());
            throw new InvalidInputException("Invalid email format");
        }

        // Validate required fields
        if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            LOGGER.warning("[SERVER] [POST /customers] Missing password");
            throw new InvalidInputException("Password is required");
        }

        customer.setId(idCounter.getAndIncrement());
        customers.put(customer.getId(), customer);
        LOGGER.info("[SERVER] [POST /customers] Customer created. ID: " + customer.getId());
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    // GET /customers
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        LOGGER.info("[SERVER] [GET /customers] Fetching all customers");
        return Response.ok(new ArrayList<>(customers.values())).build();
    }

    // GET /customers/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [GET /customers/" + id + "] Fetching customer");
        Customer customer = customers.get(id);
        if (customer == null) {
            LOGGER.warning("[SERVER] [GET /customers/" + id + "] Customer not found");
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }
        return Response.ok(customer).build();
    }

    // PUT /customers/{id}
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") Integer id, Customer updatedCustomer) {
        LOGGER.info("[SERVER] [PUT /customers/" + id + "] Updating customer");
        Customer existingCustomer = customers.get(id);
        if (existingCustomer == null) {
            LOGGER.warning("[SERVER] [PUT /customers/" + id + "] Customer not found");
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }

        // Validate email format
        if (!EMAIL_REGEX.matcher(updatedCustomer.getEmail()).matches()) {
            LOGGER.warning("[SERVER] [PUT /customers/" + id + "] Invalid email: " + updatedCustomer.getEmail());
            throw new InvalidInputException("Invalid email format");
        }

        // Update fields
        existingCustomer.setFirstName(updatedCustomer.getFirstName());
        existingCustomer.setLastName(updatedCustomer.getLastName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setPassword(updatedCustomer.getPassword());
        LOGGER.info("[SERVER] [PUT /customers/" + id + "] Customer updated");
        return Response.ok(existingCustomer).build();
    }

    // DELETE /customers/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [DELETE /customers/" + id + "] Deleting customer");
        if (!customers.containsKey(id)) {
            LOGGER.warning("[SERVER] [DELETE /customers/" + id + "] Customer not found");
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }
        customers.remove(id);
        LOGGER.info("[SERVER] [DELETE /customers/" + id + "] Customer deleted");
        return Response.ok().entity("Customer id " +id + " successfully deleted").type(MediaType.TEXT_PLAIN).build();
    }
}