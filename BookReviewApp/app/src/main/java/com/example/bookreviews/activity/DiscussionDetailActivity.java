package com.example.bookreviews.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.adapter.CommentAdapter;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.Comment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int discussionId;
    private TextView titleText;
    private TextView contentText;
    private TextView authorText;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        discussionId = getIntent().getIntExtra("discussion_id", -1);
        if (discussionId == -1) {
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("BookReviews", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        titleText = findViewById(R.id.discussionTitle);
        contentText = findViewById(R.id.discussionContent);
        authorText = findViewById(R.id.discussionAuthor);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter();
        commentsRecyclerView.setAdapter(commentAdapter);

        FloatingActionButton fab = findViewById(R.id.fabAddComment);
        fab.setOnClickListener(v -> showAddCommentDialog());

        loadDiscussionDetails();
        loadComments();
    }

    private void loadDiscussionDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT d.*, u.username " +
            "FROM discussions d " +
            "JOIN users u ON d.user_id = u.id " +
            "WHERE d.id = ?",
            new String[]{String.valueOf(discussionId)});

        if (cursor.moveToFirst()) {
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            contentText.setText(cursor.getString(cursor.getColumnIndexOrThrow("content")));
            authorText.setText("Автор: " + cursor.getString(cursor.getColumnIndexOrThrow("username")));
            getSupportActionBar().setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        }
        cursor.close();
    }

    private void loadComments() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Comment> comments = new ArrayList<>();

        Cursor cursor = db.rawQuery(
            "SELECT c.*, u.username " +
            "FROM comments c " +
            "JOIN users u ON c.user_id = u.id " +
            "WHERE c.discussion_id = ? " +
            "ORDER BY c.created_date ASC",
            new String[]{String.valueOf(discussionId)});

        while (cursor.moveToNext()) {
            Comment comment = new Comment(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("discussion_id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getString(cursor.getColumnIndexOrThrow("comment_text")),
                cursor.getLong(cursor.getColumnIndexOrThrow("created_date"))
            );
            comments.add(comment);
        }
        cursor.close();

        commentAdapter.setComments(comments);
    }

    private void showAddCommentDialog() {
        EditText commentInput = new EditText(this);
        commentInput.setHint("Ваш коментар");
        commentInput.setMinLines(3);

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Додати коментар")
            .setView(commentInput)
            .setPositiveButton("Додати", (dialog, which) -> {
                String commentText = commentInput.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    addComment(commentText);
                }
            })
            .setNegativeButton("Скасувати", null)
            .show();
    }

    private void addComment(String commentText) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("discussion_id", discussionId);
        values.put("user_id", currentUserId);
        values.put("comment_text", commentText);
        values.put("created_date", System.currentTimeMillis());

        long result = db.insert("comments", null, values);
        if (result != -1) {
            Toast.makeText(this, "Коментар додано", Toast.LENGTH_SHORT).show();
            loadComments();
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
