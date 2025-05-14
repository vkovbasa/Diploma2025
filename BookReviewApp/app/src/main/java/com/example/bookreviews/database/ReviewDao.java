package com.example.bookreviews.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.bookreviews.model.Review;
import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY timestamp DESC")
    List<Review> getReviewsForBook(String bookId);
} 