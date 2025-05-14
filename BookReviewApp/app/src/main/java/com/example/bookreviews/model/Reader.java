package com.example.bookreviews.model;

public class Reader {
    private int id;
    private String username;
    private String bio;
    private int reviewCount;

    public Reader(int id, String username, String bio, int reviewCount) {
        this.id = id;
        this.username = username;
        this.bio = bio;
        this.reviewCount = reviewCount;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public int getReviewCount() { return reviewCount; }
}
