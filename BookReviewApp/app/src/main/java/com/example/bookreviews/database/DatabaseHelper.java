package com.example.bookreviews.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.bookreviews.model.Book;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BookReviews.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        addDefaultUsers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Видаляємо всі таблиці
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS book_clubs");
        db.execSQL("DROP TABLE IF EXISTS club_members");
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS comments");
        db.execSQL("DROP TABLE IF EXISTS discussions");
        
        // Створюємо таблиці заново
        createTables(db);
        addDefaultUsers(db);
    }

    private void createTables(SQLiteDatabase db) {
        // Таблиця користувачів
        db.execSQL(
            "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE NOT NULL, " +
            "email TEXT UNIQUE NOT NULL, " +
            "password TEXT NOT NULL, " +
            "profile_image TEXT, " +
            "bio TEXT)"
        );

        // Таблиця книг
        db.execSQL(
            "CREATE TABLE books (" +
            "id TEXT PRIMARY KEY, " +  // ID з Google Books API
            "title TEXT NOT NULL, " +
            "author TEXT, " +
            "description TEXT, " +
            "genre TEXT, " +
            "image_url TEXT, " +
            "created_date INTEGER)"
        );

        // Таблиця книжкових клубів
        db.execSQL(
            "CREATE TABLE book_clubs (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "description TEXT, " +
            "creator_id INTEGER, " +
            "is_private INTEGER, " +
            "FOREIGN KEY(creator_id) REFERENCES users(id))"
        );

        // Таблиця членства в клубах
        db.execSQL(
            "CREATE TABLE club_members (" +
            "club_id INTEGER, " +
            "user_id INTEGER, " +
            "joined_date TEXT, " +
            "PRIMARY KEY(club_id, user_id), " +
            "FOREIGN KEY(club_id) REFERENCES book_clubs(id), " +
            "FOREIGN KEY(user_id) REFERENCES users(id))"
        );

        // Таблиця відгуків
        db.execSQL(
            "CREATE TABLE reviews (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "book_id TEXT NOT NULL, " +
            "user_id INTEGER, " +
            "rating INTEGER, " +
            "review_text TEXT, " +
            "created_date TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(id), " +
            "FOREIGN KEY(book_id) REFERENCES books(id))"
        );

        // Таблиця коментарів
        db.execSQL(
            "CREATE TABLE comments (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "discussion_id INTEGER, " +
            "review_id INTEGER, " +
            "user_id INTEGER, " +
            "comment_text TEXT, " +
            "created_date TEXT, " +
            "FOREIGN KEY(discussion_id) REFERENCES discussions(id), " +
            "FOREIGN KEY(review_id) REFERENCES reviews(id), " +
            "FOREIGN KEY(user_id) REFERENCES users(id))"
        );

        // Таблиця обговорень
        db.execSQL(
            "CREATE TABLE discussions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, " +
            "content TEXT NOT NULL, " +
            "user_id INTEGER, " +
            "created_date INTEGER, " +
            "FOREIGN KEY(user_id) REFERENCES users(id))"
        );
    }

    public void addDefaultUsers(SQLiteDatabase db) {
        // Масив тестових користувачів
        String[][] defaultUsers = {
            // {username, email, password, bio}
            {"john_reader", "john@example.com", "password123", "Люблю фантастику та наукову літературу"},
            {"anna_books", "anna@example.com", "password123", "Поціновувач класичної літератури"},
            {"mark_fantasy", "mark@example.com", "password123", "Фанат фентезі та пригодницьких романів"},
            {"lisa_poetry", "lisa@example.com", "password123", "Читаю та пишу поезію"},
            {"alex_detective", "alex@example.com", "password123", "Детективи - моя пристрасть"}
        };

        // Додаємо кожного користувача
        for (String[] user : defaultUsers) {
            ContentValues values = new ContentValues();
            values.put("username", user[0]);
            values.put("email", user[1]);
            values.put("password", user[2]); // В реальному додатку пароль має бути захешований
            values.put("bio", user[3]);

            try {
                db.insert("users", null, values);
            } catch (Exception e) {
                // Ігноруємо помилки, якщо користувач вже існує
            }
        }

        // Додаємо деякі відгуки для цих користувачів
        String[][] defaultReviews = {
            // {email, book_id, rating, review_text}
            {"john@example.com", "book1", "5", "Чудова книга! Рекомендую всім."},
            {"anna@example.com", "book1", "4", "Цікавий сюжет, але кінцівка могла бути кращою."},
            {"mark@example.com", "book2", "5", "Неймовірна історія! Не міг відірватися."},
            {"lisa@example.com", "book3", "4", "Гарна книга для вечірнього читання."},
            {"alex@example.com", "book2", "5", "Захоплюючий детектив, несподівана розв'язка!"}
        };

        // Додаємо відгуки
        for (String[] review : defaultReviews) {
            // Спочатку знаходимо ID користувача
            Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "email = ?",
                new String[]{review[0]},
                null, null, null
            );

            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(0);
                ContentValues values = new ContentValues();
                values.put("user_id", userId);
                values.put("book_id", review[1]);
                values.put("rating", Integer.parseInt(review[2]));
                values.put("review_text", review[3]);
                values.put("created_date", System.currentTimeMillis());

                try {
                    db.insert("reviews", null, values);
                } catch (Exception e) {
                    // Ігноруємо помилки
                }
            }
            cursor.close();
        }

        // Додаємо тестові обговорення
        ContentValues discussion1 = new ContentValues();
        discussion1.put("title", "Улюблені книги 2024");
        discussion1.put("content", "Давайте поділимося улюбленими книгами цього року!");
        discussion1.put("user_id", 1);
        discussion1.put("created_date", System.currentTimeMillis());
        db.insert("discussions", null, discussion1);

        ContentValues discussion2 = new ContentValues();
        discussion2.put("title", "Класика чи сучасна література?");
        discussion2.put("content", "Що вам більше до вподоби і чому?");
        discussion2.put("user_id", 2);
        discussion2.put("created_date", System.currentTimeMillis());
        db.insert("discussions", null, discussion2);

        // Додаємо ще дискусій
        ContentValues discussion3 = new ContentValues();
        discussion3.put("title", "Фантастика: що порадите?");
        discussion3.put("content", "Які фантастичні книги справили на вас найбільше враження?");
        discussion3.put("user_id", 3);
        discussion3.put("created_date", System.currentTimeMillis());
        db.insert("discussions", null, discussion3);

        ContentValues discussion4 = new ContentValues();
        discussion4.put("title", "Поезія у сучасному світі");
        discussion4.put("content", "Чи читаєте ви сучасну поезію? Які автори вам подобаються?");
        discussion4.put("user_id", 4);
        discussion4.put("created_date", System.currentTimeMillis());
        db.insert("discussions", null, discussion4);

        ContentValues discussion5 = new ContentValues();
        discussion5.put("title", "Детективи: класика чи новинки?");
        discussion5.put("content", "Які детективи ви радите прочитати цього року?");
        discussion5.put("user_id", 5);
        discussion5.put("created_date", System.currentTimeMillis());
        db.insert("discussions", null, discussion5);

        ContentValues discussion6 = new ContentValues();
        discussion6.put("title", "Книжкові клуби: ваш досвід");
        discussion6.put("content", "Чи брали ви участь у книжкових клубах? Поділіться враженнями!");
        discussion6.put("user_id", 1);
        discussion6.put("created_date", System.currentTimeMillis());
        db.insert("discussions", null, discussion6);

        // Додаємо тестові коментарі
        String[][] defaultComments = {
            // {discussion_id, email, content}
            {"1", "anna@example.com", "Моя улюблена книга цього року - '1984' Оруелла!"},
            {"1", "mark@example.com", "Рекомендую 'Світло, якого ми не бачимо'!"},
            {"2", "lisa@example.com", "Люблю класику, особливо Достоєвського."},
            {"2", "alex@example.com", "Сучасна література більш актуальна."},
            {"3", "john@example.com", "Обов'язково прочитайте 'Марсіянина'!"}
        };

        for (String[] comment : defaultComments) {
            Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "email = ?",
                new String[]{comment[1]},
                null, null, null
            );

            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(0);
                ContentValues values = new ContentValues();
                values.put("discussion_id", Integer.parseInt(comment[0]));
                values.put("user_id", userId);
                values.put("comment_text", comment[2]);
                values.put("created_date", System.currentTimeMillis());

                try {
                    db.insert("comments", null, values);
                } catch (Exception e) {
                    // Ігноруємо помилки
                }
            }
            cursor.close();
        }
    }

    public void saveBook(Book book) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", book.getId());
            values.put("title", book.getTitle());
            values.put("author", book.getAuthor());
            values.put("description", book.getDescription());
            values.put("image_url", book.getThumbnailUrl());
            
            // Перевіряємо, чи книга вже існує
            Cursor cursor = db.query("books", null, "id = ?", 
                new String[]{book.getId()}, null, null, null);
            
            if (cursor.moveToFirst()) {
                // Оновлюємо існуючу книгу
                db.update("books", values, "id = ?", new String[]{book.getId()});
                Log.d("DatabaseHelper", "Book updated: " + book.getId());
            } else {
                // Додаємо нову книгу
                db.insert("books", null, values);
                Log.d("DatabaseHelper", "Book inserted: " + book.getId());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving book", e);
        }
    }

    public void saveReview(String bookId, int userId, int rating, String reviewText) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("book_id", bookId);
            values.put("user_id", userId);
            values.put("rating", rating);
            values.put("review_text", reviewText);
            values.put("created_date", System.currentTimeMillis());
            
            long result = db.insert("reviews", null, values);
            Log.d("DatabaseHelper", "Review saved: " + result);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving review", e);
        }
    }
} 