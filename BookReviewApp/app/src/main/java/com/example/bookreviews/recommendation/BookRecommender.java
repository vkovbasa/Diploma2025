package com.example.bookreviews.recommendation;

import com.example.bookreviews.model.Book;
import com.example.bookreviews.model.Review;
import com.example.bookreviews.model.User;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class BookRecommender {
    private Map<String, List<Review>> userReviews;
    private Map<String, List<Book>> genreBooks;

    public BookRecommender() {
        userReviews = new HashMap<>();
        genreBooks = new HashMap<>();
    }

    public void addUserReview(String userId, Review review) {
        userReviews.computeIfAbsent(userId, k -> new ArrayList<>()).add(review);
    }

    public void addBookToGenre(String genre, Book book) {
        genreBooks.computeIfAbsent(genre, k -> new ArrayList<>()).add(book);
    }

    public List<Book> getRecommendations(User user) {
        List<Book> recommendations = new ArrayList<>();
        
        // Отримуємо улюблені жанри користувача
        String[] preferences = user.getPreferences().split(",");
        
        // Додаємо книги з улюблених жанрів
        for (String genre : preferences) {
            if (genreBooks.containsKey(genre)) {
                recommendations.addAll(genreBooks.get(genre));
            }
        }
        
        // TODO: Додати більш складну логіку рекомендацій
        // наприклад, колаборативну фільтрацію
        
        return recommendations;
    }
} 