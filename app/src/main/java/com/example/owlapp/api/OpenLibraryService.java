package com.example.owlapp.api;

import com.example.owlapp.model.api.BookDetailsResponse;
import com.example.owlapp.model.api.SearchResponse;
import com.example.owlapp.model.api.WorkResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenLibraryService {

    // Search for books
    @GET("search.json")
    Call<SearchResponse> searchBooks(@Query("q") String query);

    // Get book details by ISBN
    @GET("api/books")
    Call<BookDetailsResponse> getBookByIsbn(@Query("bibkeys") String isbn, @Query("format") String format, @Query("jscmd") String command);

    // Get book details by Open Library ID
    @GET("works/{workId}.json")
    Call<WorkResponse> getWorkDetails(@Path("workId") String workId);

    // Get author details
    @GET("authors/{authorId}.json")
    Call<WorkResponse.Author> getAuthorDetails(@Path("authorId") String authorId);
}
