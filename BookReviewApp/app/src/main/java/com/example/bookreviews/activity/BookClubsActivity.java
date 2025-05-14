package com.example.bookreviews.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.adapter.BookClubAdapter;
import com.example.bookreviews.database.DatabaseHelper;
import com.example.bookreviews.model.BookClub;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class BookClubsActivity extends AppCompatActivity implements BookClubAdapter.OnClubClickListener {
    private DatabaseHelper dbHelper;
    private BookClubAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_clubs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Книжкові клуби");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        adapter = new BookClubAdapter(this);
        recyclerView = findViewById(R.id.clubsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabCreateClub);
        fab.setOnClickListener(v -> startActivity(
            new Intent(this, CreateClubActivity.class)));

        loadClubs();
    }

    private void loadClubs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BookClub> clubs = new ArrayList<>();

        Cursor cursor = db.rawQuery(
            "SELECT c.*, COUNT(m.user_id) as member_count " +
            "FROM book_clubs c " +
            "LEFT JOIN club_members m ON c.id = m.club_id " +
            "GROUP BY c.id", null);

        while (cursor.moveToNext()) {
            BookClub club = new BookClub(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getInt(cursor.getColumnIndexOrThrow("creator_id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("is_private")) == 1
            );
            club.setMemberCount(cursor.getInt(cursor.getColumnIndexOrThrow("member_count")));
            clubs.add(club);
        }
        cursor.close();

        adapter.setClubs(clubs);
    }

    @Override
    public void onClubClick(BookClub club) {
        Intent intent = new Intent(this, ClubDetailActivity.class);
        intent.putExtra("club_id", club.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClubs();
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