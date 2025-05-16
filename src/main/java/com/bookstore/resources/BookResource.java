package com.bookstore.resources;

import com.bookstore.models.Book;
import com.bookstore.exceptions.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.Calendar;

@Path("/books")
public class BookResource {
    private static final Logger LOGGER = Logger.getLogger(BookResource.class.getName());
    public static Map<Integer, Book> books = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    // POST /books
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(Book book) {
        LOGGER.info("[SERVER] [POST /books] Creating book: " + book.getTitle());
        validateBook(book);

        // Validate publication year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (book.getPublicationYear() > currentYear) {
            LOGGER.warning("[SERVER] [POST /books] Invalid publication year: " + book.getPublicationYear());
            throw new InvalidInputException("Publication year cannot be in the future");
        }

        // Validate author exists
        if (!AuthorResource.authors.containsKey(book.getAuthorId())) {
            LOGGER.warning("[SERVER] [POST /books] Author not found: " + book.getAuthorId());
            throw new AuthorNotFoundException("Author with ID " + book.getAuthorId() + " does not exist");
        }

        book.setId(idCounter.getAndIncrement());
        books.put(book.getId(), book);
        LOGGER.info("[SERVER] [POST /books] Book created. ID: " + book.getId());
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    // GET /books
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks() {
        LOGGER.info("[SERVER] [GET /books] Fetching all books");
        return Response.ok(new ArrayList<>(books.values())).build();
    }

    // GET /books/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [GET /books/" + id + "] Fetching book");
        Book book = books.get(id);
        if (book == null) {
            LOGGER.warning("[SERVER] [GET /books/" + id + "] Book not found");
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        return Response.ok(book).build();
    }

    // PUT /books/{id}
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("id") Integer id, Book updatedBook) {
        LOGGER.info("[SERVER] [PUT /books/" + id + "] Updating book");
        Book existingBook = books.get(id);
        if (existingBook == null) {
            LOGGER.warning("[SERVER] [PUT /books/" + id + "] Book not found");
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }

        validateBook(updatedBook);
        
        // Validate publication year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (updatedBook.getPublicationYear() > currentYear) {
            LOGGER.warning("[SERVER] [PUT /books/" + id + "] Invalid publication year");
            throw new InvalidInputException("Publication year cannot be in the future");
        }

        // Validate author exists
        if (!AuthorResource.authors.containsKey(updatedBook.getAuthorId())) {
            LOGGER.warning("[SERVER] [PUT /books/" + id + "] Author not found");
            throw new AuthorNotFoundException("Author with ID " + updatedBook.getAuthorId() + " not found");
        }

        // Update fields
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthorId(updatedBook.getAuthorId());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPublicationYear(updatedBook.getPublicationYear());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setStock(updatedBook.getStock());

        LOGGER.info("[SERVER] [PUT /books/" + id + "] Book updated");
        return Response.ok(existingBook).build();
    }

    // DELETE /books/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [DELETE /books/" + id + "] Deleting book");
        if (!books.containsKey(id)) {
            LOGGER.warning("[SERVER] [DELETE /books/" + id + "] Book not found");
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        books.remove(id);
        LOGGER.info("[SERVER] [DELETE /books/" + id + "] Book deleted");
        return Response.ok().entity("Book id " +id + " successfully deleted").type(MediaType.TEXT_PLAIN).build();
    }

    // Validation logic
    private void validateBook(Book book) {
        // Required fields
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title is required");
        }
        if (book.getAuthorId() == null) {
            throw new InvalidInputException("Author ID is required");
        }
        
        // ISBN validation
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new InvalidInputException("ISBN is required");
        }
        if (!book.getIsbn().matches("\\d{3}-\\d{10}")) {
            throw new InvalidInputException("Invalid ISBN format. Expected format: XXX-XXXXXXXXXX");
        }

        // Numeric validations
        if (book.getPrice() == null || book.getPrice() <= 0) {
            throw new InvalidInputException("Price must be a positive value");
        }
        if (book.getStock() == null || book.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be negative");
        }
    }
}