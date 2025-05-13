package com.example.owlapp.model.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private Object description;

    @SerializedName("covers")
    private List<Integer> covers;

    @SerializedName("authors")
    private List<AuthorReference> authors;

    @SerializedName("subjects")
    private List<String> subjects;

    @SerializedName("first_publish_date")
    private String firstPublishDate;

    public String getTitle() {
        return title;
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

    public List<Integer> getCovers() {
        return covers;
    }

    public String getCoverUrl() {
        if (covers != null && !covers.isEmpty()) {
            return "https://covers.openlibrary.org/b/id/" + covers.get(0) + "-L.jpg";
        }
        return null;
    }

    public List<AuthorReference> getAuthors() {
        return authors;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public String getFirstPublishDate() {
        return firstPublishDate;
    }

    public static class AuthorReference {
        @SerializedName("author")
        private Author author;

        @SerializedName("type")
        private JsonObject type;

        @SerializedName("key")
        private String key;

        public Author getAuthor() {
            return author;
        }

        public JsonObject getType() {
            return type;
        }

        public String getKey() {
            return key;
        }
    }

    public static class Author {
        @SerializedName("key")
        private String key;

        @SerializedName("name")
        private String name;

        @SerializedName("birth_date")
        private String birthDate;

        @SerializedName("death_date")
        private String deathDate;

        @SerializedName("bio")
        private Object bio;

        @SerializedName("photos")
        private List<Integer> photos;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public String getDeathDate() {
            return deathDate;
        }

        public String getBio() {
            if (bio instanceof String) {
                return (String) bio;
            } else if (bio instanceof JsonObject) {
                JsonObject bioObj = (JsonObject) bio;
                if (bioObj.has("value")) {
                    return bioObj.get("value").getAsString();
                }
            }
            return "No biography available";
        }

        public List<Integer> getPhotos() {
            return photos;
        }

        public String getPhotoUrl() {
            if (photos != null && !photos.isEmpty()) {
                return "https://covers.openlibrary.org/a/id/" + photos.get(0) + "-L.jpg";
            }
            return null;
        }
    }
}
