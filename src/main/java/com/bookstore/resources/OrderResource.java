package com.bookstore.resources;

import com.bookstore.models.*;
import com.bookstore.exceptions.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Path("/customers/{customerId}/orders")
public class OrderResource {
    private static final Logger LOGGER = Logger.getLogger(OrderResource.class.getName());
    private static final Map<Integer, Order> orders = new ConcurrentHashMap<>();
    private static final AtomicInteger orderIdCounter = new AtomicInteger(1);

    // POST /customers/{customerId}/orders
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(@PathParam("customerId") Integer customerId) {
        LOGGER.info("[SERVER] [POST /customers/" + customerId + "/orders] Creating order");

        // Validate customer exists
        Customer customer = CustomerResource.customers.get(customerId);
        if (customer == null) {
            LOGGER.warning("[SERVER] Customer not found: " + customerId);
            throw new CustomerNotFoundException("Customer ID " + customerId + " not found");
        }

        // Check if cart is empty
        Map<Integer, Integer> cartItems = customer.getCart().getItems();
        if (cartItems.isEmpty()) {
            LOGGER.warning("[SERVER] Cart is empty for customer: " + customerId);
            throw new InvalidInputException("Cart is empty");
        }

        // Validate stock for all items before processing
        cartItems.forEach((bookId, quantity) -> {
            Book book = BookResource.books.get(bookId);
            if (book == null) {
                LOGGER.warning("[SERVER] Book not found: " + bookId);
                throw new BookNotFoundException("Book ID " + bookId + " not found");
            }
            if (book.getStock() < quantity) {
                LOGGER.warning(String.format(
                    "[SERVER] Insufficient stock for Book ID %d. Available: %d, Required: %d",
                    bookId, book.getStock(), quantity
                ));
                throw new OutOfStockException("Insufficient stock for book ID " + bookId);
            }
        });

        // Deduct stock and create order
        Order order = new Order();
        order.setId(orderIdCounter.getAndIncrement());
        order.setCustomerId(customerId);
        order.setItems(new HashMap<>(cartItems));
        order.setOrderDate(new Date());

        // Deduct stock from books
        cartItems.forEach((bookId, quantity) -> {
            Book book = BookResource.books.get(bookId);
            book.setStock(book.getStock() - quantity);
        });

        // Clear the cart
        customer.getCart().getItems().clear();

        // Save order
        orders.put(order.getId(), order);

        LOGGER.info("[SERVER] Order created. ID: " + order.getId());
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    // GET /customers/{customerId}/orders
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByCustomer(@PathParam("customerId") Integer customerId) {
        LOGGER.info("[SERVER] [GET /customers/" + customerId + "/orders] Fetching orders");
        List<Order> customerOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerId().equals(customerId)) {
                customerOrders.add(order);
            }
        }
        return Response.ok(customerOrders).build();
    }

    // GET /customers/{customerId}/orders/{orderId}
    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(
        @PathParam("customerId") Integer customerId,
        @PathParam("orderId") Integer orderId
    ) {
        LOGGER.info("[SERVER] [GET /customers/" + customerId + "/orders/" + orderId + "] Fetching order");
        Order order = orders.get(orderId);
        if (order == null || !order.getCustomerId().equals(customerId)) {
            LOGGER.warning("[SERVER] Order not found: " + orderId);
            throw new OrderNotFoundException("Order ID " + orderId + " not found");
        }
        return Response.ok(order).build();
    }
}