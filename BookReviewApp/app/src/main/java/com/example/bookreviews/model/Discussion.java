package com.example.bookreviews.model;

public class Discussion {
    private int id;
    private String title;
    private String content;
    private String author;
    private long createdDate;
    private int commentCount;

    public Discussion(int id, String title, String content, String author, long createdDate, int commentCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdDate = createdDate;
        this.commentCount = commentCount;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public long getCreatedDate() { return createdDate; }
    public int getCommentCount() { return commentCount; }
}