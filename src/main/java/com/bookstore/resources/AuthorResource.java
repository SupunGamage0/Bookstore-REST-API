package com.bookstore.resources;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.bookstore.exceptions.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Path("/authors")
public class AuthorResource {
    private static final Logger LOGGER = Logger.getLogger(AuthorResource.class.getName());
    public static Map<Integer, Author> authors = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    // POST /authors
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAuthor(Author author) {
        LOGGER.info("[SERVER] [POST /authors] Creating author: " + author.getFirstName() + " " + author.getLastName());
        
        // Validate required fields
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty() ||
            author.getLastName() == null || author.getLastName().trim().isEmpty()) {
            LOGGER.warning("[SERVER] [POST /authors] Missing first/last name");
            throw new InvalidInputException("First name and last name are required");
        }

        author.setId(idCounter.getAndIncrement());
        authors.put(author.getId(), author);
        LOGGER.info("[SERVER] [POST /authors] Author created. ID: " + author.getId());
        return Response.status(Response.Status.CREATED).entity(author).build();
    }

    // GET /authors
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAuthors() {
        LOGGER.info("[SERVER] [GET /authors] Fetching all authors");
        return Response.ok(new ArrayList<>(authors.values())).build();
    }

    // GET /authors/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [GET /authors/" + id + "] Fetching author");
        Author author = authors.get(id);
        if (author == null) {
            LOGGER.warning("[SERVER] [GET /authors/" + id + "] Author not found");
            throw new AuthorNotFoundException("Author with ID " + id + " not found");
        }
        return Response.ok(author).build();
    }

    // PUT /authors/{id}
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthor(@PathParam("id") Integer id, Author updatedAuthor) {
        LOGGER.info("[SERVER] [PUT /authors/" + id + "] Updating author");
        Author existingAuthor = authors.get(id);
        if (existingAuthor == null) {
            LOGGER.warning("[SERVER] [PUT /authors/" + id + "] Author not found");
            throw new AuthorNotFoundException("Author with ID " + id + " not found");
        }

        // Validate required fields
        if (updatedAuthor.getFirstName() == null || updatedAuthor.getFirstName().trim().isEmpty() ||
            updatedAuthor.getLastName() == null || updatedAuthor.getLastName().trim().isEmpty()) {
            LOGGER.warning("[SERVER] [PUT /authors/" + id + "] Missing first/last name");
            throw new InvalidInputException("First name and last name are required");
        }

        existingAuthor.setFirstName(updatedAuthor.getFirstName());
        existingAuthor.setLastName(updatedAuthor.getLastName());
        existingAuthor.setBiography(updatedAuthor.getBiography());
        LOGGER.info("[SERVER] [PUT /authors/" + id + "] Author updated");
        return Response.ok(existingAuthor).build();
    }

    // DELETE /authors/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [DELETE /authors/" + id + "] Deleting author");
        if (!authors.containsKey(id)) {
            LOGGER.warning("[SERVER] [DELETE /authors/" + id + "] Author not found");
            throw new AuthorNotFoundException("Author with ID " + id + " not found");
        }

        // Check if author has books
        boolean hasBooks = BookResource.books.values().stream()
                .anyMatch(book -> book.getAuthorId().equals(id));
        if (hasBooks) {
            LOGGER.warning("[SERVER] [DELETE /authors/" + id + "] Author has books");
            throw new InvalidInputException("Author has existing books and cannot be deleted");
        }

        authors.remove(id);
        LOGGER.info("[SERVER] [DELETE /authors/" + id + "] Author deleted");
        return Response.ok().entity("Aurthor id " +id + " successfully deleted").type(MediaType.TEXT_PLAIN).build();
    }

    // GET /authors/{id}/books
    @GET
    @Path("/{id}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByAuthor(@PathParam("id") Integer id) {
        LOGGER.info("[SERVER] [GET /authors/" + id + "/books] Fetching books");
        if (!authors.containsKey(id)) {
            LOGGER.warning("[SERVER] [GET /authors/" + id + "/books] Author not found");
            throw new AuthorNotFoundException("Author with ID " + id + " not found");
        }

        List<Book> books = BookResource.books.values().stream()
                .filter(book -> book.getAuthorId().equals(id))
                .toList();
        return Response.ok(books).build();
    }
}