package com.example.bookreviews.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.adapter.ReaderAdapter;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.Reader;
import java.util.ArrayList;
import java.util.List;

public class FindReadersActivity extends AppCompatActivity {
    // Допоміжний клас для роботи з базою даних
    private DatabaseHelper dbHelper;
    // Адаптер для списку читачів
    private ReaderAdapter adapter;
    // Пошуковий віджет
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_readers);

        // Встановлюємо тулбар і заголовок
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.find_readers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        
        // Налаштовуємо пошук читачів
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchReaders(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    searchReaders(newText);
                }
                return true;
            }
        });

        // Встановлюємо адаптер для списку читачів
        RecyclerView recyclerView = findViewById(R.id.readersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReaderAdapter();
        recyclerView.setAdapter(adapter);

        // Завантажуємо схожих читачів при старті
        loadSimilarReaders();
    }

    // Метод для пошуку читачів за ім'ям
    private void searchReaders(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Reader> readers = new ArrayList<>();

        Cursor cursor = db.rawQuery(
            "SELECT u.*, COUNT(r.id) as review_count " +
            "FROM users u " +
            "LEFT JOIN reviews r ON u.id = r.user_id " +
            "WHERE u.username LIKE ? " +
            "GROUP BY u.id " +
            "ORDER BY review_count DESC",
            new String[]{"%" + query + "%"});

        while (cursor.moveToNext()) {
            readers.add(createReaderFromCursor(cursor));
        }
        cursor.close();

        adapter.setReaders(readers);
    }

    private void loadSimilarReaders() {
        SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Reader> readers = new ArrayList<>();

        // Знаходимо читачів з подібними вподобаннями
        Cursor cursor = db.rawQuery(
            "SELECT u.*, COUNT(DISTINCT r2.book_id) as common_books " +
            "FROM users u " +
            "JOIN reviews r1 ON r1.user_id = ? " +
            "JOIN reviews r2 ON r2.book_id = r1.book_id " +
            "WHERE u.id != ? " +
            "GROUP BY u.id " +
            "HAVING common_books >= 3 " +
            "ORDER BY common_books DESC " +
            "LIMIT 20",
            new String[]{String.valueOf(userId), String.valueOf(userId)});

        while (cursor.moveToNext()) {
            readers.add(createReaderFromCursor(cursor));
        }
        cursor.close();

        adapter.setReaders(readers);
    }

    private Reader createReaderFromCursor(Cursor cursor) {
        return new Reader(
            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            cursor.getString(cursor.getColumnIndexOrThrow("username")),
            cursor.getString(cursor.getColumnIndexOrThrow("bio")),
            cursor.getInt(cursor.getColumnIndexOrThrow("review_count"))
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Повертаємось на попередній екран
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 