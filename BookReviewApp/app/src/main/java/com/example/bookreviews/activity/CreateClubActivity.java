package com.example.bookreviews.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.bookreviews.R;
import com.example.bookreviews.database.DatabaseHelper;

public class CreateClubActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText descriptionInput;
    private CheckBox privateCheckbox;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_club);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        
        nameInput = findViewById(R.id.clubNameInput);
        descriptionInput = findViewById(R.id.clubDescriptionInput);
        privateCheckbox = findViewById(R.id.privateCheckbox);
        Button createButton = findViewById(R.id.createButton);

        createButton.setOnClickListener(v -> createClub());
    }

    private void createClub() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        boolean isPrivate = privateCheckbox.isChecked();

        if (name.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_club_name, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
        int creatorId = prefs.getInt("user_id", -1);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("creator_id", creatorId);
        values.put("is_private", isPrivate ? 1 : 0);

        long clubId = db.insert("book_clubs", null, values);

        if (clubId != -1) {
            // Додаємо творця як першого учасника клубу
            ContentValues memberValues = new ContentValues();
            memberValues.put("club_id", clubId);
            memberValues.put("user_id", creatorId);
            memberValues.put("joined_date", System.currentTimeMillis());
            db.insert("club_members", null, memberValues);

            Toast.makeText(this, R.string.club_created, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error_creating_club, Toast.LENGTH_SHORT).show();
        }
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