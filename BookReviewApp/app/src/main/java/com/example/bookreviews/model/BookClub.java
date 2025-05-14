package com.example.bookreviews.model;

public class BookClub {
    private int id;
    private String name;
    private String description;
    private int creatorId;
    private boolean isPrivate;
    private int memberCount;

    public BookClub(int id, String name, String description, int creatorId, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.isPrivate = isPrivate;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCreatorId() { return creatorId; }
    public boolean isPrivate() { return isPrivate; }
    public int getMemberCount() { return memberCount; }
    
    // Setters
    public void setMemberCount(int count) { this.memberCount = count; }
} 