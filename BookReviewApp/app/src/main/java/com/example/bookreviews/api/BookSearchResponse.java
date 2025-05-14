package com.example.bookreviews.api;

import com.example.bookreviews.model.Book;
import java.util.List;

public class BookSearchResponse {
    private List<Items> items;

    public static class Items {
        private VolumeInfo volumeInfo;
        private String id;

        public static class VolumeInfo {
            private String title;
            private List<String> authors;
            private String description;
            private ImageLinks imageLinks;

            public static class ImageLinks {
                private String thumbnail;
                public String getThumbnail() { return thumbnail; }
            }

            public String getTitle() { return title; }
            public List<String> getAuthors() { return authors; }
            public String getDescription() { return description; }
            public ImageLinks getImageLinks() { return imageLinks; }
        }

        public String getId() { return id; }
        public VolumeInfo getVolumeInfo() { return volumeInfo; }
    }

    public List<Items> getItems() { return items; }
} 