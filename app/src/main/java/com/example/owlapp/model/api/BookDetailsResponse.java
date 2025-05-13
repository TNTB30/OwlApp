package com.example.owlapp.model.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class BookDetailsResponse {
    private Map<String, BookInfo> books;

    public Map<String, BookInfo> getBooks() {
        return books;
    }

    public static class BookInfo {
        @SerializedName("bib_key")
        private String bibKey;

        @SerializedName("info_url")
        private String infoUrl;

        @SerializedName("preview")
        private String preview;

        @SerializedName("preview_url")
        private String previewUrl;

        @SerializedName("thumbnail_url")
        private String thumbnailUrl;

        @SerializedName("details")
        private BookDetails details;

        public String getBibKey() {
            return bibKey;
        }

        public String getInfoUrl() {
            return infoUrl;
        }

        public String getPreview() {
            return preview;
        }

        public String getPreviewUrl() {
            return previewUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public BookDetails getDetails() {
            return details;
        }
    }

    public static class BookDetails {
        @SerializedName("title")
        private String title;

        @SerializedName("authors")
        private JsonObject[] authors;

        @SerializedName("number_of_pages")
        private int numberOfPages;

        @SerializedName("publish_date")
        private String publishDate;

        @SerializedName("publishers")
        private String[] publishers;

        @SerializedName("subjects")
        private String[] subjects;

        @SerializedName("description")
        private Object description;

        public String getTitle() {
            return title;
        }

        public JsonObject[] getAuthors() {
            return authors;
        }

        public int getNumberOfPages() {
            return numberOfPages;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public String[] getPublishers() {
            return publishers;
        }

        public String[] getSubjects() {
            return subjects;
        }

        public String getDescription() {
            if (description instanceof String) {
                return (String) description;
            } else if (description instanceof JsonObject) {
                JsonObject descObj = (JsonObject) description;
                if (descObj.has("value")) {
                    return descObj.get("value").getAsString();
                }
            }
            return "No description available";
        }
    }
}
