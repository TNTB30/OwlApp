package com.example.owlapp.model;

public class ReadingList {
    private int id;
    private String name;
    private int bookCount;

    public ReadingList(int id, String name, int bookCount) {
        this.id = id;
        this.name = name;
        this.bookCount = bookCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBookCount() {
        return bookCount;
    }

    public String getFormattedBookCount() {
        return bookCount + " stories";
    }
}
