package com.example.bookreviews.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Інтерфейс для роботи з Google Books API.
 * Використовується Retrofit для здійснення HTTP-запитів
 */
public interface GoogleBooksService {
    /**
     * Пошук книг за запитом
     * @param query пошуковий запит
     * @return Call об'єкт з відповіддю від API
     */
    @GET("volumes")
    Call<BookSearchResponse> searchBooks(@Query("q") String query);
} 