package com.example.owlapp.model;

public class Rating {
    private int bookId;
    private String username;
    private float rating; // 1-5 stars
    private String comment;

    public Rating(int bookId, String username, float rating, String comment) {
        this.bookId = bookId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }

    public int getBookId() {
        return bookId;
    }

    public String getUsername() {
        return username;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
