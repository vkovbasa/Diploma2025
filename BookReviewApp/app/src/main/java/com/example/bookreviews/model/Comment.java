package com.example.bookreviews.model;

public class Comment {
    private int id;
    private int discussionId;
    private int userId;
    private String username;
    private String content;
    private long createdDate;

    public Comment(int id, int discussionId, int userId, String username, String content, long createdDate) {
        this.id = id;
        this.discussionId = discussionId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.createdDate = createdDate;
    }

    // Getters
    public int getId() { return id; }
    public int getDiscussionId() { return discussionId; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public long getCreatedDate() { return createdDate; }
} 