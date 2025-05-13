package com.example.owlapp.model;

public class Book {
    private int id;
    private String title;
    private String englishTitle;
    private String author;
    private String coverUrl;
    private int views;
    private int chapters;
    private String category;
    private String description;
    private String publishDate;
    private String olid; // Open Library ID
    private String isbn;

    public Book(int id, String title, String englishTitle, String author, String coverUrl, int views, int chapters, String category) {
        this.id = id;
        this.title = title;
        this.englishTitle = englishTitle;
        this.author = author;
        this.coverUrl = coverUrl;
        this.views = views;
        this.chapters = chapters;
        this.category = category;
    }

    public Book(int id, String title, String englishTitle, String author, String coverUrl, int views, int chapters, String category, String description, String publishDate, String olid, String isbn) {
        this.id = id;
        this.title = title;
        this.englishTitle = englishTitle;
        this.author = author;
        this.coverUrl = coverUrl;
        this.views = views;
        this.chapters = chapters;
        this.category = category;
        this.description = description;
        this.publishDate = publishDate;
        this.olid = olid;
        this.isbn = isbn;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public String getFullTitle() {
        if (englishTitle != null && !englishTitle.isEmpty() && !englishTitle.equals(title)) {
            return title + " (" + englishTitle + ")";
        }
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public int getViews() {
        return views;
    }

    public String getFormattedViews() {
        if (views >= 1000) {
            return (views / 1000) + "K";
        }
        return String.valueOf(views);
    }

    public int getChapters() {
        return chapters;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getOlid() {
        return olid;
    }

    public void setOlid(String olid) {
        this.olid = olid;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
