package com.example.bookreviews.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.bookreviews.R;
import com.example.bookreviews.model.Review;
import java.util.UUID;
import androidx.room.Room;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.adapter.ReviewAdapter;
import com.example.bookreviews.database.ReviewDatabase;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.SharedPreferences;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.Book;
import android.util.Log;

import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView descriptionTextView;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private ReviewDatabase database;
    private FloatingActionButton fabFavorite;
    private boolean isFavorite = false;
    private SharedPreferences preferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("BookReviews", MODE_PRIVATE);
        
        // Ініціалізація views
        coverImageView = findViewById(R.id.coverImageView);
        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);

        // Отримання даних про книгу
        String bookId = getIntent().getStringExtra("book_id");
        String title = getIntent().getStringExtra("book_title");
        String author = getIntent().getStringExtra("book_author");
        String description = getIntent().getStringExtra("book_description");
        String thumbnail = getIntent().getStringExtra("book_thumbnail");

        // Додаємо логування
        Log.d("BookDetail", "Saving book: " + bookId + ", " + title);

        // Зберігаємо книгу в базу даних
        Book book = new Book(bookId, title, author, description, thumbnail);
        dbHelper.saveBook(book);

        // Відображення даних
        titleTextView.setText(title);
        authorTextView.setText(author);
        descriptionTextView.setText(description);
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // Замінюємо http на https для безпечного завантаження
            String secureUrl = thumbnail.replace("http://", "https://");
            Glide.with(this)
                .load(secureUrl)
                .placeholder(R.drawable.book_placeholder)
                // .error(R.drawable.book_error)  // Закоментуємо поки що
                .into(coverImageView);
        }

        database = Room.databaseBuilder(getApplicationContext(),
                ReviewDatabase.class, "reviews-db").build();

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        reviewsRecyclerView.setAdapter(reviewAdapter);

        loadReviews(bookId);

        // Оновлюємо обробку відправки рецензії
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = commentEditText.getText().toString().trim();
            
            // Додаємо логування
            Log.d("BookDetail", "Submitting review - Rating: " + rating + ", Book ID: " + bookId);
            
            if (rating == 0) {
                Toast.makeText(this, R.string.error_no_rating, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (comment.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_comment, Toast.LENGTH_SHORT).show();
                return;
            }

            // Отримуємо ID користувача
            int userId = preferences.getInt("user_id", -1);
            Log.d("BookDetail", "User ID: " + userId);
            
            if (userId == -1) {
                Toast.makeText(this, "Будь ласка, увійдіть в систему", Toast.LENGTH_SHORT).show();
                return;
            }

            // Зберігаємо рецензію в обидві бази даних
            Review review = new Review(
                UUID.randomUUID().toString(),
                bookId,
                comment,
                rating
            );

            // Зберігаємо в Room
            saveReview(review);
            
            // Зберігаємо в SQLite
            dbHelper.saveReview(bookId, userId, (int)rating, comment);
            
            // Додаємо логування після збереження
            Log.d("BookDetail", "Review saved successfully");
            
            // Очищаємо поля після збереження
            ratingBar.setRating(0);
            commentEditText.setText("");
            
            // Перезавантажуємо рецензії
            loadReviews(bookId);
        });

        fabFavorite = findViewById(R.id.fab_favorite);
        isFavorite = preferences.getBoolean("favorite_" + bookId, false);
        updateFavoriteIcon();

        fabFavorite.setOnClickListener(v -> toggleFavorite(bookId));
    }

    private void loadReviews(String bookId) {
        AsyncTask.execute(() -> {
            List<Review> reviews = database.reviewDao().getReviewsForBook(bookId);
            runOnUiThread(() -> reviewAdapter.setReviews(reviews));
        });
    }

    private void saveReview(Review review) {
        AsyncTask.execute(() -> {
            try {
                database.reviewDao().insert(review);
                Log.d("BookDetail", "Review saved to Room database");
            } catch (Exception e) {
                Log.e("BookDetail", "Error saving review to Room", e);
            }
        });
    }

    private void toggleFavorite(String bookId) {
        isFavorite = !isFavorite;
        preferences.edit().putBoolean("favorite_" + bookId, isFavorite).apply();
        updateFavoriteIcon();
        
        Toast.makeText(this, 
            isFavorite ? R.string.added_to_favorites : R.string.removed_from_favorites, 
            Toast.LENGTH_SHORT).show();
    }

    private void updateFavoriteIcon() {
        fabFavorite.setImageResource(
            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );
    }
} 