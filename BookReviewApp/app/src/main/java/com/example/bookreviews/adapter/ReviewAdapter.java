package com.example.bookreviews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RatingBar;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.model.Review;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.commentTextView.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());
        holder.dateTextView.setText(dateFormat.format(new Date(review.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        RatingBar ratingBar;
        TextView dateTextView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
} 