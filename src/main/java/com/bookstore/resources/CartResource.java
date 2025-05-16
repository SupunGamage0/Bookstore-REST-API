package com.bookstore.resources;

import com.bookstore.models.*;
import com.bookstore.exceptions.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.Logger;

@Path("/customers/{customerId}/cart")
public class CartResource {
    private static final Logger LOGGER = Logger.getLogger(CartResource.class.getName());

    // POST /customers/{customerId}/cart/items
    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(
        @PathParam("customerId") Integer customerId,
        CartItemRequest itemRequest
    ) {
        LOGGER.info(String.format(
            "[SERVER] [POST /customers/%d/cart/items] Adding item: Book ID %d, Quantity %d",
            customerId, itemRequest.getBookId(), itemRequest.getQuantity()
        ));

        // Validate customer exists
        Customer customer = getValidCustomer(customerId);

        // Validate book exists
        Book book = getValidBook(itemRequest.getBookId());

        // Validate quantity
        validateQuantity(itemRequest.getQuantity());

        // Check stock availability
        validateStock(book, itemRequest.getQuantity());

        // Update cart and stock
        updateCartAndStock(customer, book, itemRequest.getBookId(), itemRequest.getQuantity());
        
        return Response.ok()
            .entity(Map.of("message", "Item successfully added to cart"))
            .build();
    }

    // GET /customers/{customerId}/cart
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@PathParam("customerId") Integer customerId) {
        LOGGER.info(String.format("[SERVER] [GET /customers/%d/cart]", customerId));
        Customer customer = getValidCustomer(customerId);
        return Response.ok(customer.getCart().getItems()).build();
    }

    // PUT /customers/{customerId}/cart/items/{bookId}
    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartItem(
        @PathParam("customerId") Integer customerId,
        @PathParam("bookId") Integer bookId,
        QuantityUpdateRequest request
    ) {
        LOGGER.info(String.format(
            "[SERVER] [PUT /customers/%d/cart/items/%d] Updating quantity to %d",
            customerId, bookId, request.getQuantity()
        ));

        Customer customer = getValidCustomer(customerId);
        Book book = getValidBook(bookId);
        validateQuantity(request.getQuantity());

        Map<Integer, Integer> cartItems = customer.getCart().getItems();
        int currentQuantity = cartItems.getOrDefault(bookId, 0);
        int stockDifference = request.getQuantity() - currentQuantity;

        validateStockForUpdate(book, stockDifference);

        // Update cart and stock
        cartItems.put(bookId, request.getQuantity());
        book.setStock(book.getStock() - stockDifference);
        
        return Response.ok()
            .entity(Map.of(
                "message", "Cart item updated successfully",
                "bookId", bookId,
                "newQuantity", request.getQuantity()
            ))
            .build();
    }

    // DELETE /customers/{customerId}/cart/items/{bookId}
    @DELETE
    @Path("/items/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCartItem(
        @PathParam("customerId") Integer customerId,
        @PathParam("bookId") Integer bookId
    ) {
        LOGGER.info(String.format(
            "[SERVER] [DELETE /customers/%d/cart/items/%d]", customerId, bookId
        ));

        Customer customer = getValidCustomer(customerId);
        Map<Integer, Integer> cartItems = customer.getCart().getItems();
        
        Integer quantity = cartItems.remove(bookId);
        if (quantity == null) {
            throw new CartNotFoundException("Book ID " + bookId + " not found in cart");
        }

        // Restore stock
        Book book = BookResource.books.get(bookId);
        if (book != null) {
            book.setStock(book.getStock() + quantity);
        }
        
        return Response.ok()
            .entity(Map.of(
                "message", "Item removed from cart",
                "bookId", bookId,
                "removedQuantity", quantity
            ))
            .build();
    }

    // Helper methods
    private Customer getValidCustomer(Integer customerId) {
        Customer customer = CustomerResource.customers.get(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer ID " + customerId + " not found");
        }
        return customer;
    }

    private Book getValidBook(Integer bookId) {
        Book book = BookResource.books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book ID " + bookId + " not found");
        }
        return book;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidInputException("Quantity must be a positive integer");
        }
    }

    private void validateStock(Book book, Integer requestedQuantity) {
        if (book.getStock() < requestedQuantity) {
            throw new OutOfStockException(String.format(
                "Insufficient stock for Book ID %d. Available: %d, Requested: %d",
                book.getId(), book.getStock(), requestedQuantity
            ));
        }
    }

    private void validateStockForUpdate(Book book, int stockDifference) {
        if (stockDifference > 0 && book.getStock() < stockDifference) {
            throw new OutOfStockException(String.format(
                "Insufficient stock for update. Available: %d, Needed: %d",
                book.getStock(), stockDifference
            ));
        }
    }

    private void updateCartAndStock(Customer customer, Book book, Integer bookId, Integer quantity) {
        customer.getCart().addItem(bookId, quantity);
        book.setStock(book.getStock() - quantity);
    }

    // Request DTOs
    public static class CartItemRequest {
        private Integer bookId;
        private Integer quantity;

        // Getters and Setters
        public Integer getBookId() { return bookId; }
        public void setBookId(Integer bookId) { this.bookId = bookId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class QuantityUpdateRequest {
        private Integer quantity;

        // Getters and Setters
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}