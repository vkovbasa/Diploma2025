package com.example.bookreviews.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.bookreviews.model.Review;

@Database(entities = {Review.class}, version = 1, exportSchema = false)
public abstract class ReviewDatabase extends RoomDatabase {
    public abstract ReviewDao reviewDao();
} 