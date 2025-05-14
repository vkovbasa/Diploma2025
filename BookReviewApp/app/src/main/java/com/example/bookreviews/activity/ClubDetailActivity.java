package com.example.bookreviews.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.adapter.ClubDiscussionAdapter;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.BookClub;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClubDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private BookClub club;
    private int currentUserId;
    private Button joinButton;
    private RecyclerView discussionRecyclerView;
    private ClubDiscussionAdapter discussionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        int clubId = getIntent().getIntExtra("club_id", -1);
        if (clubId == -1) {
            finish();
            return;
        }

        loadClubDetails(clubId);
        setupViews();
        loadDiscussions();
    }

    private void loadClubDetails(int clubId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            "book_clubs",
            null,
            "id = ?",
            new String[]{String.valueOf(clubId)},
            null, null, null
        );

        if (cursor.moveToFirst()) {
            club = new BookClub(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getInt(cursor.getColumnIndexOrThrow("creator_id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("is_private")) == 1
            );
            getSupportActionBar().setTitle(club.getName());
        }
        cursor.close();
    }

    private void setupViews() {
        TextView descriptionText = findViewById(R.id.clubDescription);
        descriptionText.setText(club.getDescription());

        joinButton = findViewById(R.id.joinButton);
        updateJoinButtonState();

        FloatingActionButton fab = findViewById(R.id.fabNewDiscussion);
        if (isMember()) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> startNewDiscussion());
        } else {
            fab.setVisibility(View.GONE);
        }

        discussionRecyclerView = findViewById(R.id.discussionsRecyclerView);
        discussionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        discussionAdapter = new ClubDiscussionAdapter();
        discussionRecyclerView.setAdapter(discussionAdapter);
    }

    private boolean isMember() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            "club_members",
            null,
            "club_id = ? AND user_id = ?",
            new String[]{String.valueOf(club.getId()), String.valueOf(currentUserId)},
            null, null, null
        );
        boolean isMember = cursor.getCount() > 0;
        cursor.close();
        return isMember;
    }

    private void updateJoinButtonState() {
        boolean member = isMember();
        joinButton.setText(member ? R.string.leave_club : R.string.join_club);
        joinButton.setOnClickListener(v -> {
            if (member) {
                leaveClub();
            } else {
                joinClub();
            }
        });
    }

    private void joinClub() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("club_id", club.getId());
        values.put("user_id", currentUserId);
        values.put("joined_date", System.currentTimeMillis());

        long result = db.insert("club_members", null, values);
        if (result != -1) {
            Toast.makeText(this, R.string.joined_club, Toast.LENGTH_SHORT).show();
            updateJoinButtonState();
        }
    }

    private void leaveClub() {
        if (club.getCreatorId() == currentUserId) {
            Toast.makeText(this, R.string.creator_cant_leave, Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(
            "club_members",
            "club_id = ? AND user_id = ?",
            new String[]{String.valueOf(club.getId()), String.valueOf(currentUserId)}
        );

        if (result > 0) {
            Toast.makeText(this, R.string.left_club, Toast.LENGTH_SHORT).show();
            updateJoinButtonState();
        }
    }

    private void startNewDiscussion() {
        // TODO: Запустити активність для створення нової дискусії
    }

    private void loadDiscussions() {
        // TODO: Завантажити дискусії з бази даних
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