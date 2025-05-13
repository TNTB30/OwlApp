package com.example.owlapp.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Comment {
    private int id;
    private String username;
    private String userAvatarUrl;
    private String text;
    private Date date;
    private int likeCount;
    private boolean isLiked;
    private int parentId; // ID của bình luận cha (0 nếu là bình luận gốc)
    private List<Comment> replies; // Danh sách phản hồi

    public Comment(int id, String username, String userAvatarUrl, String text, Date date) {
        this.id = id;
        this.username = username;
        this.userAvatarUrl = userAvatarUrl;
        this.text = text;
        this.date = date;
        this.likeCount = 0;
        this.isLiked = false;
        this.parentId = 0;
        this.replies = new ArrayList<>();
    }

    public Comment(int id, String username, String userAvatarUrl, String text, Date date, int parentId) {
        this(id, username, userAvatarUrl, text, date);
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public String getTimeAgo() {
        long diffInMillis = new Date().getTime() - date.getTime();
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

        if (diffInHours < 1) {
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            return diffInMinutes + " phút trước";
        } else if (diffInHours < 24) {
            return diffInHours + "h trước";
        } else {
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            return diffInDays + " ngày trước";
        }
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void toggleLike() {
        if (isLiked) {
            likeCount--;
        } else {
            likeCount++;
        }
        isLiked = !isLiked;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isReply() {
        return parentId > 0;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }
}
