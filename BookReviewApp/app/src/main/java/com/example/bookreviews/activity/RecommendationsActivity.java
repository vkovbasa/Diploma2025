package com.example.bookreviews.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.adapter.BookAdapter;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class RecommendationsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.recommendations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        
        RecyclerView recyclerView = findViewById(R.id.recommendationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

        loadRecommendations();
    }

    private void loadRecommendations() {
        try {
            Log.d("Recommendations", "Starting loadRecommendations");
            
            SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            
            Log.d("Recommendations", "Current user ID: " + userId);
            if (userId == -1) {
                Log.e("Recommendations", "User ID is -1, user might not be logged in");
                Toast.makeText(this, "Будь ласка, увійдіть в систему", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            List<Book> recommendations = new ArrayList<>();

            // Отримуємо улюблені книги користувача (рейтинг >= 4)
            Cursor favoriteCursor = db.rawQuery(
                "SELECT b.* FROM books b " +
                "JOIN reviews r ON b.id = r.book_id " +
                "WHERE r.user_id = ? AND r.rating >= 4",
                new String[]{String.valueOf(userId)});

            Log.d("Recommendations", "Favorite books count: " + favoriteCursor.getCount());
            
            // Збираємо ID улюблених книг
            List<String> favoriteBookIds = new ArrayList<>();
            while (favoriteCursor.moveToNext()) {
                String bookId = favoriteCursor.getString(favoriteCursor.getColumnIndexOrThrow("id"));
                favoriteBookIds.add(bookId);
                Log.d("Recommendations", "Favorite book ID: " + bookId);
            }
            favoriteCursor.close();

            if (!favoriteBookIds.isEmpty()) {
                // Знаходимо користувачів, які також високо оцінили ваші улюблені книги
                String placeholders = String.join(",", Collections.nCopies(favoriteBookIds.size(), "?"));
                String similarUsersQuery = 
                    "SELECT DISTINCT r1.user_id " +
                    "FROM reviews r1 " +
                    "WHERE r1.book_id IN (" + placeholders + ") " +
                    "AND r1.rating >= 4 " +
                    "AND r1.user_id != ?";
                
                Log.d("Recommendations", "Similar users query: " + similarUsersQuery);
                Log.d("Recommendations", "Parameters: " + String.join(", ", favoriteBookIds) + ", " + userId);
                
                Cursor similarUsersCursor = db.rawQuery(
                    similarUsersQuery,
                    Stream.concat(
                        favoriteBookIds.stream(),
                        Stream.of(String.valueOf(userId))
                    ).toArray(String[]::new));

                List<String> similarUserIds = new ArrayList<>();
                while (similarUsersCursor.moveToNext()) {
                    String similarUserId = similarUsersCursor.getString(0);
                    similarUserIds.add(similarUserId);
                    Log.d("Recommendations", "Found similar user: " + similarUserId);
                }
                similarUsersCursor.close();

                if (!similarUserIds.isEmpty()) {
                    // Знаходимо книги, які сподобалися схожим користувачам
                    String similarBooksQuery = 
                        "SELECT DISTINCT b.*, COUNT(DISTINCT r2.user_id) as similar_users " +
                        "FROM books b " +
                        "JOIN reviews r2 ON r2.book_id = b.id " +
                        "WHERE r2.user_id IN (" + String.join(",", similarUserIds) + ") " +
                        "AND r2.rating >= 4 " +
                        "AND b.id NOT IN (" + placeholders + ") " +
                        "GROUP BY b.id " +
                        "HAVING similar_users >= 1 " +
                        "ORDER BY similar_users DESC " +
                        "LIMIT 20";

                    Log.d("Recommendations", "Similar books query: " + similarBooksQuery);
                    Log.d("Recommendations", "Parameters: " + String.join(", ", favoriteBookIds));

                    Cursor similarBooksCursor = db.rawQuery(
                        similarBooksQuery,
                        favoriteBookIds.toArray(new String[0]));

                    Log.d("Recommendations", "Similar books count: " + similarBooksCursor.getCount());

                    while (similarBooksCursor.moveToNext()) {
                        Book book = createBookFromCursor(similarBooksCursor);
                        recommendations.add(book);
                        Log.d("Recommendations", "Added recommendation: " + book.getTitle());
                    }
                    similarBooksCursor.close();
                } else {
                    Log.d("Recommendations", "No similar users found");
                }
            }

            if (recommendations.isEmpty()) {
                Log.d("Recommendations", "No recommendations from similar users, showing popular books");
                // Показуємо популярні книги з високим рейтингом
                Cursor popularCursor = db.rawQuery(
                    "SELECT b.*, AVG(r.rating) as avg_rating, COUNT(r.id) as review_count " +
                    "FROM books b " +
                    "LEFT JOIN reviews r ON b.id = r.book_id " +
                    "GROUP BY b.id " +
                    "HAVING review_count > 0 AND avg_rating >= 4.0 " +
                    "ORDER BY avg_rating DESC, review_count DESC " +
                    "LIMIT 20",
                    null);

                Log.d("Recommendations", "Popular books count: " + popularCursor.getCount());

                while (popularCursor.moveToNext()) {
                    Book book = createBookFromCursor(popularCursor);
                    recommendations.add(book);
                    Log.d("Recommendations", "Added popular book: " + book.getTitle());
                }
                popularCursor.close();
            }

            if (recommendations.isEmpty()) {
                Log.d("Recommendations", "No recommendations found at all");
                Toast.makeText(this, "Почніть оцінювати книги для отримання рекомендацій", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Recommendations", "Total recommendations: " + recommendations.size());
            }

            adapter.setBooks(recommendations);
        } catch (Exception e) {
            Log.e("Recommendations", "Error loading recommendations", e);
            Toast.makeText(this, "Помилка завантаження рекомендацій", Toast.LENGTH_SHORT).show();
        }
    }

    private Book createBookFromCursor(Cursor cursor) {
        return new Book(
            cursor.getString(cursor.getColumnIndexOrThrow("id")),
            cursor.getString(cursor.getColumnIndexOrThrow("title")),
            cursor.getString(cursor.getColumnIndexOrThrow("author")),
            cursor.getString(cursor.getColumnIndexOrThrow("description")),
            cursor.getString(cursor.getColumnIndexOrThrow("image_url"))
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecommendations();
    }
} 