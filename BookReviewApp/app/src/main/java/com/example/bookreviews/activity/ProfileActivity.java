package com.example.bookreviews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.bookreviews.R;
import com.example.bookreviews.database.DatabaseHelper;
import android.content.ContentValues;
import android.view.MenuItem;

public class ProfileActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText bioInput;
    private ImageView profileImage;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Профіль");

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        usernameInput = findViewById(R.id.usernameInput);
        bioInput = findViewById(R.id.bioInput);
        profileImage = findViewById(R.id.profileImage);
        Button saveButton = findViewById(R.id.saveButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        loadUserData();

        saveButton.setOnClickListener(v -> saveUserData());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            "users",
            new String[]{"username", "bio"},
            "id = ?",
            new String[]{String.valueOf(userId)},
            null, null, null
        );

        if (cursor.moveToFirst()) {
            usernameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            String bio = cursor.getString(cursor.getColumnIndexOrThrow("bio"));
            if (bio != null) {
                bioInput.setText(bio);
            }
        }
        cursor.close();
    }

    private void saveUserData() {
        String username = usernameInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Ім'я користувача не може бути порожнім", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("bio", bio);

        int rowsAffected = db.update(
            "users",
            values,
            "id = ?",
            new String[]{String.valueOf(userId)}
        );

        if (rowsAffected > 0) {
            Toast.makeText(this, "Профіль оновлено", Toast.LENGTH_SHORT).show();
            // Оновлюємо ім'я користувача в SharedPreferences
            getSharedPreferences("BookReviews", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .apply();
        }
    }

    private void logout() {
        // Очищаємо дані користувача
        getSharedPreferences("BookReviews", MODE_PRIVATE)
            .edit()
            .clear()
            .apply();

        // Переходимо на екран входу
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 