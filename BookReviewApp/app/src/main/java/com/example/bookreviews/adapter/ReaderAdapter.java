package com.example.bookreviews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.model.Reader;
import java.util.ArrayList;
import java.util.List;

public class ReaderAdapter extends RecyclerView.Adapter<ReaderAdapter.ReaderViewHolder> {
    private List<Reader> readers = new ArrayList<>();

    @NonNull
    @Override
    public ReaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reader, parent, false);
        return new ReaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReaderViewHolder holder, int position) {
        holder.bind(readers.get(position));
    }

    @Override
    public int getItemCount() {
        return readers.size();
    }

    public void setReaders(List<Reader> readers) {
        this.readers = readers;
        notifyDataSetChanged();
    }

    static class ReaderViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameText;
        private TextView bioText;
        private TextView reviewCountText;

        ReaderViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username);
            bioText = itemView.findViewById(R.id.bio);
            reviewCountText = itemView.findViewById(R.id.reviewCount);
        }

        void bind(Reader reader) {
            usernameText.setText(reader.getUsername());
            if (reader.getBio() != null && !reader.getBio().isEmpty()) {
                bioText.setVisibility(View.VISIBLE);
                bioText.setText(reader.getBio());
            } else {
                bioText.setVisibility(View.GONE);
            }
            reviewCountText.setText(itemView.getContext().getString(
                R.string.review_count, reader.getReviewCount()));
        }
    }
} 