package com.example.owlapp.model.api;

import java.util.List;

public class BookContentResponse {
    private String title;
    private List<Author> authors;
    private List<Chapter> chapters;
    private String coverUrl;
    private String description;
    private String publishDate;
    private String publisher;
    private int pageCount;
    private List<String> genres;
    private float rating;

    public static class Author {
        private String name;
        private String bio;

        public String getName() {
            return name;
        }

        public String getBio() {
            return bio;
        }
    }

    public static class Chapter {
        private int number;
        private String title;
        private String content;

        public int getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    public String getTitle() {
        return title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPageCount() {
        return pageCount;
    }

    public List<String> getGenres() {
        return genres;
    }

    public float getRating() {
        return rating;
    }
}
