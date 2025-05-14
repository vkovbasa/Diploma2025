package com.example.bookreviews.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bookreviews.R;
import com.example.bookreviews.model.Book;
import com.example.bookreviews.activity.BookDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> books = new ArrayList<>();
    private List<Book> allBooks = new ArrayList<>();

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        
        if (book.getThumbnailUrl() != null) {
            Glide.with(holder.itemView.getContext())
                .load(book.getThumbnailUrl())
                .into(holder.coverImageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetailActivity.class);
            intent.putExtra("book_id", book.getId());
            intent.putExtra("book_title", book.getTitle());
            intent.putExtra("book_author", book.getAuthor());
            intent.putExtra("book_description", book.getDescription());
            intent.putExtra("book_thumbnail", book.getThumbnailUrl());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(List<Book> newBooks) {
        this.books = new ArrayList<>(newBooks);
        // Додаємо нові книги до allBooks, якщо їх там ще немає
        for (Book newBook : newBooks) {
            boolean exists = false;
            for (Book existingBook : allBooks) {
                if (existingBook.getId().equals(newBook.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                allBooks.add(newBook);
            }
        }
        notifyDataSetChanged();
    }

    public void addBooks(List<Book> newBooks) {
        int startPosition = books.size();
        books.addAll(newBooks);
        // Додаємо нові книги до allBooks, якщо їх там ще немає
        for (Book newBook : newBooks) {
            boolean exists = false;
            for (Book existingBook : allBooks) {
                if (existingBook.getId().equals(newBook.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                allBooks.add(newBook);
            }
        }
        notifyItemRangeInserted(startPosition, newBooks.size());
    }

    public List<Book> getAllBooks() {
        return allBooks;
    }

    public void clearBooks() {
        books.clear();
        books.addAll(allBooks); // Відновлюємо всі книги
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView authorTextView;

        BookViewHolder(View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.coverImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
        }
    }
} 