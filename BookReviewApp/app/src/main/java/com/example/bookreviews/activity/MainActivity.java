package com.example.bookreviews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.google.android.material.navigation.NavigationView;
import com.example.bookreviews.adapter.BookAdapter;
import com.example.bookreviews.api.BookSearchResponse;
import com.example.bookreviews.api.GoogleBooksService;
import com.example.bookreviews.model.Book;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.view.Menu;
import androidx.appcompat.app.AppCompatDelegate;
import android.widget.EditText;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private GoogleBooksService booksService;
    private BookAdapter bookAdapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("BookReviews", MODE_PRIVATE);
        
        // Встановлюємо тему при запуску
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        
        setContentView(R.layout.activity_main);

        // Налаштування Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Ініціалізація views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        // Додаємо цей блок для зміни кольору тексту в полі пошуку на білий у темній темі
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            // Знаходимо EditText всередині SearchView і змінюємо колір тексту
            int id = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
            EditText searchEditText = searchView.findViewById(id);
            if (searchEditText != null) {
                searchEditText.setTextColor(Color.WHITE);
                searchEditText.setHintTextColor(Color.LTGRAY);
            }
        }

        // Налаштування Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Налаштування RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter();
        recyclerView.setAdapter(bookAdapter);

        // Налаштування пошуку
        setupSearchView();

        // Налаштування API
        setupBooksService();
        loadDefaultBooks();

        // Налаштування Navigation View
        setupNavigationView();
    }

    private void setupSearchView() {
        // Встановлюємо слухач для зміни кольору тексту при кожному фокусі
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            setSearchViewTextColor();
        });
        // Також одразу після ініціалізації
        setSearchViewTextColor();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setSearchViewTextColor() {
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            int id = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
            EditText searchEditText = searchView.findViewById(id);
            if (searchEditText != null) {
                searchEditText.setTextColor(Color.WHITE);
                searchEditText.setHintTextColor(Color.LTGRAY);
                // Додаємо ще й колір курсора
                try {
                    searchEditText.setHighlightColor(Color.WHITE);
                    searchEditText.setCursorVisible(true);
                    searchEditText.setTextCursorDrawable(R.drawable.cursor_white);
                } catch (Exception ignored) {}
            }
        }
    }

    private void setupBooksService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        booksService = retrofit.create(GoogleBooksService.class);
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void searchBooks(String query) {
        booksService.searchBooks(query).enqueue(new Callback<BookSearchResponse>() {
            @Override
            public void onResponse(Call<BookSearchResponse> call, Response<BookSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = new ArrayList<>();
                    for (BookSearchResponse.Items item : response.body().getItems()) {
                        BookSearchResponse.Items.VolumeInfo info = item.getVolumeInfo();
                        Book book = new Book(
                                item.getId(),
                                info.getTitle(),
                                info.getAuthors() != null ? String.join(", ", info.getAuthors()) : "Unknown",
                                info.getDescription(),
                                info.getImageLinks() != null ? info.getImageLinks().getThumbnail() : null
                        );
                        books.add(book);
                    }
                    bookAdapter.setBooks(books);
                }
            }

            @Override
            public void onFailure(Call<BookSearchResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.error_loading_books, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFavorites() {
        SharedPreferences preferences = getSharedPreferences("BookReviews", MODE_PRIVATE);
        List<Book> allBooks = bookAdapter.getAllBooks();
        List<Book> favoriteBooks = new ArrayList<>();
        
        for (Book book : allBooks) {
            if (book.getId() != null && preferences.getBoolean("favorite_" + book.getId(), false)) {
                favoriteBooks.add(book);
            }
        }
        
        if (favoriteBooks.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_favorites), Toast.LENGTH_SHORT).show();
            return;
        }
        
        bookAdapter.setBooks(favoriteBooks);
    }

    private void showRecommendations() {
        // Отримуємо всі книги, які користувач оцінив
        List<Book> allBooks = bookAdapter.getAllBooks();
        List<Book> recommendedBooks = new ArrayList<>();
        
        // Тут можна додати логіку рекомендацій на основі жанрів або авторів
        // Наразі просто показуємо випадкові книги
        for (Book book : allBooks) {
            if (Math.random() < 0.3) { // 30% шанс додати книгу до рекомендацій
                recommendedBooks.add(book);
            }
        }
        
        if (recommendedBooks.isEmpty()) {
            Toast.makeText(this, "Почніть оцінювати книги для отримання рекомендацій", Toast.LENGTH_SHORT).show();
        } else {
            bookAdapter.setBooks(recommendedBooks);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        isDarkMode = !isDarkMode;
        preferences.edit().putBoolean("dark_mode", isDarkMode).apply();
        
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        recreate();
    }

    private void loadDefaultBooks() {
        // Наприклад, показати популярні книги або всі книги
        searchBooks("bestseller"); // або searchBooks("") для всіх книг
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_home) {
            // Залишаємось на головній
        } else if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
        } else if (id == R.id.nav_book_clubs) {
            intent = new Intent(this, BookClubsActivity.class);
        } else if (id == R.id.nav_discussions) {
            intent = new Intent(this, DiscussionsActivity.class);
        } else if (id == R.id.nav_recommendations) {
            intent = new Intent(this, RecommendationsActivity.class);
        } else if (id == R.id.nav_find_readers) {
            intent = new Intent(this, FindReadersActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
} 