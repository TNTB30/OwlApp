package com.example.owlapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.owlapp.R;
import com.example.owlapp.model.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;
    private List<Comment> comments;
    private CommentActionListener listener;

    public interface CommentActionListener {
        void onLikeClicked(Comment comment, int position);
        void onReplyClicked(Comment comment, int position);
    }

    public CommentAdapter(Context context, List<Comment> comments, CommentActionListener listener) {
        super(context, R.layout.item_comment, comments);
        this.context = context;
        this.comments = comments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_comment, parent, false);
        }

        final Comment comment = comments.get(position);

        CircleImageView imgUserAvatar = view.findViewById(R.id.imgUserAvatar);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvCommentText = view.findViewById(R.id.tvCommentText);
        TextView tvCommentDate = view.findViewById(R.id.tvCommentDate);
        TextView tvTimeAgo = view.findViewById(R.id.tvTimeAgo);
        ImageButton btnLike = view.findViewById(R.id.btnLike);
        TextView tvLikeCount = view.findViewById(R.id.tvLikeCount);
        TextView tvReply = view.findViewById(R.id.tvReply);

        // Set data
        tvUsername.setText(comment.getUsername());
        tvCommentText.setText(comment.getText());
        tvCommentDate.setText(comment.getFormattedDate());
        tvTimeAgo.setText(comment.getTimeAgo());

        // Set like count
        if (comment.getLikeCount() > 0) {
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
        } else {
            tvLikeCount.setText("");
        }

        // Set like button state
        if (comment.isLiked()) {
            btnLike.setColorFilter(context.getResources().getColor(R.color.teal));
        } else {
            btnLike.setColorFilter(context.getResources().getColor(android.R.color.darker_gray));
        }

        // Set click listeners
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLikeClicked(comment, position);
                }
            }
        });

        tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReplyClicked(comment, position);
                }
            }
        });

        return view;
    }
}
