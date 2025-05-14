package com.example.bookreviews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookreviews.R;
import com.example.bookreviews.model.Discussion;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder> {
    private List<Discussion> discussions = new ArrayList<>();
    private OnDiscussionClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public interface OnDiscussionClickListener {
        void onDiscussionClick(Discussion discussion);
        void onCommentsClick(Discussion discussion);
    }

    public DiscussionAdapter(OnDiscussionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscussionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discussion, parent, false);
        return new DiscussionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionViewHolder holder, int position) {
        holder.bind(discussions.get(position));
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public void setDiscussions(List<Discussion> discussions) {
        this.discussions = discussions;
        notifyDataSetChanged();
    }

    class DiscussionViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView contentText;
        private TextView authorText;
        private TextView dateText;
        private TextView commentCountText;
        private MaterialButton viewCommentsButton;

        DiscussionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.discussionTitle);
            contentText = itemView.findViewById(R.id.discussionContent);
            authorText = itemView.findViewById(R.id.discussionAuthor);
            dateText = itemView.findViewById(R.id.discussionDate);
            commentCountText = itemView.findViewById(R.id.commentCount);
            viewCommentsButton = itemView.findViewById(R.id.viewCommentsButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDiscussionClick(discussions.get(position));
                }
            });

            viewCommentsButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCommentsClick(discussions.get(position));
                }
            });
        }

        void bind(Discussion discussion) {
            titleText.setText(discussion.getTitle());
            contentText.setText(discussion.getContent());
            authorText.setText(discussion.getAuthor());
            dateText.setText(dateFormat.format(new Date(discussion.getCreatedDate())));
            commentCountText.setText(itemView.getContext().getString(
                R.string.comment_count, discussion.getCommentCount()));
        }
    }
} 