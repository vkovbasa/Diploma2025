package com.example.bookreviews.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Модель даних для рецензії на книгу.
 * Зберігає інформацію про відгук користувача
 */
@Entity(tableName = "reviews")
public class Review {
    @PrimaryKey
    @NonNull
    private String id;          // Унікальний ідентифікатор рецензії
    private String bookId;      // Ідентифікатор книги, до якої відноситься рецензія
    private String comment;     // Текст рецензії
    private float rating;       // Оцінка (від 0 до 5 зірок)
    private long timestamp;     // Час створення рецензії
    
    public Review(String id, String bookId, String comment, float rating) {
        this.id = id;
        this.bookId = bookId;
        this.comment = comment;
        this.rating = rating;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 