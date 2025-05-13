package com.example.owlapp.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("numFound")
    private int numFound;

    @SerializedName("start")
    private int start;

    @SerializedName("docs")
    private List<Doc> docs;

    public int getNumFound() {
        return numFound;
    }

    public int getStart() {
        return start;
    }

    public List<Doc> getDocs() {
        return docs;
    }

    public static class Doc {
        @SerializedName("key")
        private String key;

        @SerializedName("title")
        private String title;

        @SerializedName("author_name")
        private List<String> authorNames;

        @SerializedName("isbn")
        private List<String> isbns;

        @SerializedName("cover_i")
        private int coverId;

        @SerializedName("first_publish_year")
        private int firstPublishYear;

        @SerializedName("publisher")
        private List<String> publishers;

        @SerializedName("language")
        private List<String> languages;

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getAuthorNames() {
            return authorNames;
        }

        public String getAuthor() {
            if (authorNames != null && !authorNames.isEmpty()) {
                return authorNames.get(0);
            }
            return "Unknown Author";
        }

        public List<String> getIsbns() {
            return isbns;
        }

        public String getIsbn() {
            if (isbns != null && !isbns.isEmpty()) {
                return isbns.get(0);
            }
            return null;
        }

        public int getCoverId() {
            return coverId;
        }

        public String getCoverUrl() {
            if (coverId > 0) {
                return "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
            } else if (isbns != null && !isbns.isEmpty()) {
                return "https://covers.openlibrary.org/b/isbn/" + isbns.get(0) + "-L.jpg";
            }
            return null;
        }

        public int getFirstPublishYear() {
            return firstPublishYear;
        }

        public List<String> getPublishers() {
            return publishers;
        }

        public String getPublisher() {
            if (publishers != null && !publishers.isEmpty()) {
                return publishers.get(0);
            }
            return "Unknown Publisher";
        }

        public List<String> getLanguages() {
            return languages;
        }
    }
}
