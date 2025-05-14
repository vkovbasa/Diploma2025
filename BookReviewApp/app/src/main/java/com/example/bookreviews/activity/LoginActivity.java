package com.example.bookreviews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bookreviews.R;
import com.example.bookreviews.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);

        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Перевірка, чи користувач вже увійшов
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            startMainActivity();
        }
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            "users",
            new String[]{"id", "username"},
            "email = ? AND password = ?",
            new String[]{email, password},
            null, null, null
        );

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            
            // Зберігаємо дані користувача
            SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
            prefs.edit()
                .putInt("user_id", userId)
                .putString("username", username)
                .apply();

            startMainActivity();
        } else {
            Toast.makeText(this, "Невірний email або пароль", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 