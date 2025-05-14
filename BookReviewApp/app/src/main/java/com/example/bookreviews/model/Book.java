package com.example.bookreviews.model;

/**
 * Модель даних для книги.
 * Використовується для зберігання інформації про книгу, отриману з Google Books API
 */
public class Book {
    private String id;          // Унікальний ідентифікатор книги
    private String title;       // Назва книги
    private String author;      // Автор книги
    private String description; // Опис книги
    private String thumbnailUrl; // URL обкладинки книги
    
    public Book(String id, String title, String author, String description, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
} 