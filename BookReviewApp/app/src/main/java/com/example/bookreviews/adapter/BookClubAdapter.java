package com.example.bookreviews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.model.BookClub;
import java.util.ArrayList;
import java.util.List;

public class BookClubAdapter extends RecyclerView.Adapter<BookClubAdapter.BookClubViewHolder> {
    private List<BookClub> clubs = new ArrayList<>();
    private OnClubClickListener listener;

    public interface OnClubClickListener {
        void onClubClick(BookClub club);
    }

    public BookClubAdapter(OnClubClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_club, parent, false);
        return new BookClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookClubViewHolder holder, int position) {
        holder.bind(clubs.get(position));
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public void setClubs(List<BookClub> clubs) {
        this.clubs = clubs;
        notifyDataSetChanged();
    }

    class BookClubViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;
        private TextView memberCountText;

        BookClubViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.clubName);
            descriptionText = itemView.findViewById(R.id.clubDescription);
            memberCountText = itemView.findViewById(R.id.memberCount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClubClick(clubs.get(position));
                }
            });
        }

        void bind(BookClub club) {
            nameText.setText(club.getName());
            descriptionText.setText(club.getDescription());
            memberCountText.setText(itemView.getContext().getString(
                R.string.member_count, club.getMemberCount()));
        }
    }
} 