package com.example.bookreviews.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.adapter.DiscussionAdapter;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.Discussion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DiscussionsActivity extends AppCompatActivity implements DiscussionAdapter.OnDiscussionClickListener {
    private DatabaseHelper dbHelper;
    private DiscussionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.discussions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        
        RecyclerView recyclerView = findViewById(R.id.discussionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiscussionAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabNewDiscussion);
        fab.setOnClickListener(v -> startActivity(new Intent(this, CreateDiscussionActivity.class)));

        loadDiscussions();
    }

    private void loadDiscussions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Discussion> discussions = new ArrayList<>();

        Cursor cursor = db.rawQuery(
            "SELECT d.*, u.username, " +
            "(SELECT COUNT(*) FROM comments WHERE discussion_id = d.id) as comment_count " +
            "FROM discussions d " +
            "JOIN users u ON d.user_id = u.id " +
            "ORDER BY d.created_date DESC", null);

        while (cursor.moveToNext()) {
            Discussion discussion = new Discussion(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("content")),
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getLong(cursor.getColumnIndexOrThrow("created_date")),
                cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"))
            );
            discussions.add(discussion);
        }
        cursor.close();

        adapter.setDiscussions(discussions);
    }

    @Override
    public void onDiscussionClick(Discussion discussion) {
        Intent intent = new Intent(this, DiscussionDetailActivity.class);
        intent.putExtra("discussion_id", discussion.getId());
        startActivity(intent);
    }

    @Override
    public void onCommentsClick(Discussion discussion) {
        Intent intent = new Intent(this, DiscussionDetailActivity.class);
        intent.putExtra("discussion_id", discussion.getId());
        startActivity(intent);
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
        loadDiscussions();
    }
} 