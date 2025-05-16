package com.example.owlapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.owlapp.adapter.CommentAdapter;
import com.example.owlapp.api.ApiClient;
import com.example.owlapp.api.OpenLibraryService;
import com.example.owlapp.model.Book;
import com.example.owlapp.model.Comment;
import com.example.owlapp.model.api.WorkResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity implements CommentAdapter.CommentActionListener {

    private ImageView imgBack, imgBookCover;
    private TextView tvBookTitle, tvBookTitleDetail, tvAuthor, tvCategory, tvDescription;
    private LinearLayout ratingStarsContainer;
    private Button btnRead, btnFavorite;
    private LinearLayout commentsContainer;
    private EditText edtComment;
    private ImageView btnSendComment;
    private View commentInputLayout;
    private OpenLibraryService openLibraryService;

    private Book book;
    private List<Comment> comments;
    private float userRating = 0;
    private int selectedStars = 0;
    private boolean showComments = false;
    private Comment replyingToComment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_fixed);

        // Initialize API service
        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        // Lấy thông tin sách từ Intent
        int bookId = getIntent().getIntExtra("book_id", 1);
        String bookTitle = getIntent().getStringExtra("book_title");
        String bookEnglishTitle = getIntent().getStringExtra("book_english_title");
        String bookAuthor = getIntent().getStringExtra("book_author");
        String bookCoverUrl = getIntent().getStringExtra("book_cover_url");
        showComments = getIntent().getBooleanExtra("show_comments", false);

        // Create book object from intent data
        book = new Book(bookId, bookTitle, bookEnglishTitle, bookAuthor, bookCoverUrl, 1000, 10, "");

        // Khởi tạo views
        initViews();

        // Hiển thị thông tin sách
        displayBookDetails();

        // Fetch additional book details from API if we have an OLID
        if (getIntent().hasExtra("book_olid") && getIntent().getStringExtra("book_olid") != null) {
            String olid = getIntent().getStringExtra("book_olid");
            fetchBookDetails(olid);
        }

        // Lưu thông tin sách đọc gần đây
        saveRecentlyReadBook(book.getId(), book.getTitle(), book.getEnglishTitle(), book.getAuthor(), book.getViews(), book.getChapters(), book.getCoverUrl());

        // Tải bình luận nếu cần
        if (showComments) {
            loadComments();
            // Hiển thị phần bình luận
            if (commentsContainer != null) commentsContainer.setVisibility(View.VISIBLE);
            if (commentInputLayout != null) commentInputLayout.setVisibility(View.VISIBLE);
        } else {
            // Ẩn phần bình luận
            if (commentsContainer != null) commentsContainer.setVisibility(View.GONE);
            if (commentInputLayout != null) commentInputLayout.setVisibility(View.GONE);
        }

        // Thiết lập sự kiện
        setupListeners();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgBookCover = findViewById(R.id.imgBookCover);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvBookTitleDetail = findViewById(R.id.tvBookTitleDetail);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        ratingStarsContainer = findViewById(R.id.ratingStarsContainer);
        btnRead = findViewById(R.id.btnRead);
        btnFavorite = findViewById(R.id.btnFavorite);
        commentsContainer = findViewById(R.id.commentsContainer);
        edtComment = findViewById(R.id.edtComment);
        btnSendComment = findViewById(R.id.btnSendComment);

        // Tìm phần bình luận để ẩn/hiện
        commentInputLayout = findViewById(R.id.commentInputLayout);
    }

    private void fetchBookDetails(String olid) {
        Call<WorkResponse> call = openLibraryService.getWorkDetails(olid);
        call.enqueue(new Callback<WorkResponse>() {
            @Override
            public void onResponse(Call<WorkResponse> call, Response<WorkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkResponse workResponse = response.body();

                    // Update description if available
                    if (workResponse.getDescription() != null) {
                        tvDescription.setText(workResponse.getDescription());
                    }

                    // Update category if available
                    if (workResponse.getSubjects() != null && !workResponse.getSubjects().isEmpty()) {
                        tvCategory.setText(workResponse.getSubjects().get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<WorkResponse> call, Throwable t) {
                // Just use the existing data
            }
        });
    }

    private void displayBookDetails() {
        if (book != null) {
            tvBookTitle.setText(book.getTitle());
            tvBookTitleDetail.setText(book.getFullTitle());
            tvAuthor.setText(book.getAuthor());
            tvCategory.setText(book.getCategory());

            // Load book cover from URL
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                Glide.with(this)
                        .load(book.getCoverUrl())
                        .placeholder(R.drawable.book_placeholder)
                        .error(R.drawable.book_placeholder)
                        .into(imgBookCover);
            } else if (book.getId() == 2) { // Fallback for Cha Giàu, Cha Nghèo
                imgBookCover.setImageResource(R.drawable.rich_dad_poor_dad);
            } else {
                imgBookCover.setImageResource(R.drawable.book_placeholder);
            }

            // Hiển thị đánh giá sao
            displayRatingStars(getBookRating());

            // Mô tả sách
            if (book.getId() == 2) { // Cha Giàu, Cha Nghèo
                tvDescription.setText("\"Cha Giàu, Cha Nghèo\" (Rich Dad Poor Dad) là một cuốn sách tài chính cá nhân nổi tiếng của Robert T. Kiyosaki, kể về hai người cha với hai tư duy tài chính hoàn toàn khác nhau và cách chúng ảnh hưởng đến quan điểm về tiền bạc của tác giả.");
            } else {
                tvDescription.setText("Đây là mô tả chi tiết về cuốn sách. Trong ứng dụng thực tế, nội dung này sẽ được lấy từ cơ sở dữ liệu hoặc API.");
            }
        }
    }

    private void displayRatingStars(float rating) {
        ratingStarsContainer.removeAllViews();

        // Tạo 5 sao đánh giá
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 8, 0);
            star.setLayoutParams(params);

            // Thiết lập hình ảnh sao (đầy hoặc rỗng)
            if (i <= Math.round(rating)) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else {
                star.setImageResource(R.drawable.ic_star_outline);
            }

            ratingStarsContainer.addView(star);
        }
    }

    private void setupListeners() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Add this method to BookDetailActivity.java to launch the ReadBookActivity
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReadingBook();
            }
        });

        // Sửa sự kiện nút yêu thích
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thêm vào danh sách đọc
                LibraryActivity libraryActivity = new LibraryActivity();
                libraryActivity.addToReadingList(book);
            }
        });

        ratingStarsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showComments) {
                    showRatingDialog();
                } else {
                    Toast.makeText(BookDetailActivity.this, "Chỉ có thể đánh giá sách đã đọc", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = edtComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    if (replyingToComment != null) {
                        addReply(commentText, replyingToComment);
                        replyingToComment = null;
                        edtComment.setHint("Viết bình luận...");
                    } else {
                        addComment(commentText);
                    }
                    edtComment.setText("");
                }
            }
        });
    }

    private void loadComments() {
        comments = getCommentsForBook(book.getId());
        displayComments();
    }

    private void displayComments() {
        commentsContainer.removeAllViews();

        if (comments != null && !comments.isEmpty()) {
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);

                // Chỉ hiển thị bình luận gốc (không phải phản hồi)
                if (comment.getParentId() == 0) {
                    View commentView = LayoutInflater.from(this).inflate(R.layout.item_comment, commentsContainer, false);

                    de.hdodenhof.circleimageview.CircleImageView imgUserAvatar = commentView.findViewById(R.id.imgUserAvatar);
                    TextView tvUsername = commentView.findViewById(R.id.tvUsername);
                    TextView tvCommentText = commentView.findViewById(R.id.tvCommentText);
                    TextView tvCommentDate = commentView.findViewById(R.id.tvCommentDate);
                    TextView tvTimeAgo = commentView.findViewById(R.id.tvTimeAgo);
                    ImageView btnLike = commentView.findViewById(R.id.btnLike);
                    TextView tvLikeCount = commentView.findViewById(R.id.tvLikeCount);
                    TextView tvReply = commentView.findViewById(R.id.tvReply);

                    tvUsername.setText(comment.getUsername());
                    tvCommentText.setText(comment.getText());
                    tvCommentDate.setText(comment.getFormattedDate());
                    tvTimeAgo.setText(comment.getTimeAgo());

                    if (comment.getLikeCount() > 0) {
                        tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
                    } else {
                        tvLikeCount.setText("");
                    }

                    if (comment.isLiked()) {
                        btnLike.setColorFilter(getResources().getColor(R.color.teal));
                    } else {
                        btnLike.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                    }

                    final int position = i;
                    btnLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onLikeClicked(comment, position);
                            if (comment.isLiked()) {
                                btnLike.setColorFilter(getResources().getColor(R.color.teal));
                                tvLikeCount.setText(comment.getLikeCount() > 0 ? String.valueOf(comment.getLikeCount()) : "");
                            } else {
                                btnLike.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                                tvLikeCount.setText(comment.getLikeCount() > 0 ? String.valueOf(comment.getLikeCount()) : "");
                            }
                        }
                    });

                    tvReply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onReplyClicked(comment, position);
                        }
                    });

                    commentsContainer.addView(commentView);

                    // Hiển thị các phản hồi cho bình luận này
                    displayReplies(comment);
                }
            }
        }
    }

    private void displayReplies(Comment parentComment) {
        for (Comment reply : parentComment.getReplies()) {
            View replyView = LayoutInflater.from(this).inflate(R.layout.item_reply, commentsContainer, false);

            de.hdodenhof.circleimageview.CircleImageView imgUserAvatar = replyView.findViewById(R.id.imgUserAvatar);
            TextView tvUsername = replyView.findViewById(R.id.tvUsername);
            TextView tvReplyingTo = replyView.findViewById(R.id.tvReplyingTo);
            TextView tvCommentText = replyView.findViewById(R.id.tvCommentText);
            TextView tvCommentDate = replyView.findViewById(R.id.tvCommentDate);
            TextView tvTimeAgo = replyView.findViewById(R.id.tvTimeAgo);
            ImageView btnLike = replyView.findViewById(R.id.btnLike);
            TextView tvLikeCount = replyView.findViewById(R.id.tvLikeCount);

            tvUsername.setText(reply.getUsername());
            tvReplyingTo.setText("Trả lời @" + parentComment.getUsername());
            tvCommentText.setText(reply.getText());
            tvCommentDate.setText(reply.getFormattedDate());
            tvTimeAgo.setText(reply.getTimeAgo());

            if (reply.getLikeCount() > 0) {
                tvLikeCount.setText(String.valueOf(reply.getLikeCount()));
            } else {
                tvLikeCount.setText("");
            }

            if (reply.isLiked()) {
                btnLike.setColorFilter(getResources().getColor(R.color.teal));
            } else {
                btnLike.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            }

            final Comment finalReply = reply;
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalReply.toggleLike();
                    if (finalReply.isLiked()) {
                        btnLike.setColorFilter(getResources().getColor(R.color.teal));
                        tvLikeCount.setText(finalReply.getLikeCount() > 0 ? String.valueOf(finalReply.getLikeCount()) : "");
                    } else {
                        btnLike.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                        tvLikeCount.setText(finalReply.getLikeCount() > 0 ? String.valueOf(finalReply.getLikeCount()) : "");
                    }
                }
            });

            commentsContainer.addView(replyView);
        }
    }

    private void showRatingDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rating);

        // Đảm bảo dialog hiển thị đúng kích thước
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        TextView tvBookTitleRating = dialog.findViewById(R.id.tvBookTitleRating);
        ImageView star1 = dialog.findViewById(R.id.star1);
        ImageView star2 = dialog.findViewById(R.id.star2);
        ImageView star3 = dialog.findViewById(R.id.star3);
        ImageView star4 = dialog.findViewById(R.id.star4);
        ImageView star5 = dialog.findViewById(R.id.star5);
        Button btnSubmitRating = dialog.findViewById(R.id.btnSubmitRating);

        tvBookTitleRating.setText(book.getFullTitle());

        View.OnClickListener starClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.star1) {
                    selectedStars = 1;
                } else if (v.getId() == R.id.star2) {
                    selectedStars = 2;
                } else if (v.getId() == R.id.star3) {
                    selectedStars = 3;
                } else if (v.getId() == R.id.star4) {
                    selectedStars = 4;
                } else if (v.getId() == R.id.star5) {
                    selectedStars = 5;
                }

                updateStars(star1, star2, star3, star4, star5, selectedStars);
            }
        };

        star1.setOnClickListener(starClickListener);
        star2.setOnClickListener(starClickListener);
        star3.setOnClickListener(starClickListener);
        star4.setOnClickListener(starClickListener);
        star5.setOnClickListener(starClickListener);

        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedStars > 0) {
                    userRating = selectedStars;
                    submitRating();
                    dialog.dismiss();
                } else {
                    Toast.makeText(BookDetailActivity.this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void updateStars(ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5, int selectedStars) {
        star1.setImageResource(selectedStars >= 1 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        star2.setImageResource(selectedStars >= 2 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        star3.setImageResource(selectedStars >= 3 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        star4.setImageResource(selectedStars >= 4 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        star5.setImageResource(selectedStars >= 5 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
    }

    private void submitRating() {
        // Trong ứng dụng thực tế, bạn sẽ gửi đánh giá đến server
        Toast.makeText(this, "Đã đánh giá " + userRating + " sao", Toast.LENGTH_SHORT).show();

        // Cập nhật hiển thị đánh giá
        displayRatingStars(userRating);
    }

    private void addComment(String commentText) {
        // Trong ứng dụng thực tế, bạn sẽ gửi bình luận đến server
        Comment newComment = new Comment(
                comments.size() + 1,
                "shizuka", // Giả định tên người dùng hiện tại
                "",
                commentText,
                new Date()
        );

        comments.add(0, newComment);
        displayComments();

        Toast.makeText(this, "Đã thêm bình luận", Toast.LENGTH_SHORT).show();
    }

    private void addReply(String replyText, Comment parentComment) {
        // Trong ứng dụng thực tế, bạn sẽ gửi phản hồi đến server
        Comment reply = new Comment(
                comments.size() + 1,
                "shizuka", // Giả định tên người dùng hiện tại
                "",
                replyText,
                new Date(),
                parentComment.getId()
        );

        // Thêm phản hồi vào bình luận cha
        parentComment.addReply(reply);

        // Thêm phản hồi vào danh sách bình luận
        comments.add(reply);

        // Cập nhật hiển thị
        displayComments();

        Toast.makeText(this, "Đã thêm phản hồi", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLikeClicked(Comment comment, int position) {
        comment.toggleLike();
        // Trong ứng dụng thực tế, bạn sẽ cập nhật trạng thái like lên server
    }

    @Override
    public void onReplyClicked(Comment comment, int position) {
        // Thiết lập trạng thái đang trả lời
        replyingToComment = comment;
        edtComment.setHint("Trả lời @" + comment.getUsername() + "...");
        edtComment.requestFocus();
    }

    // Phương thức giả định để lấy bình luận
    private List<Comment> getCommentsForBook(int bookId) {
        // Trong ứng dụng thực tế, bạn sẽ lấy bình luận từ cơ sở dữ liệu hoặc API
        List<Comment> commentList = new ArrayList<>();

        // Tạo dữ liệu mẫu
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            Comment comment = new Comment(
                    i + 1,
                    "shizuka",
                    "",
                    "good",
                    new Date(currentTime - (i * 3600000)) // Mỗi bình luận cách nhau 1 giờ
            );

            if (i == 0) {
                comment.setLikeCount(1);
                comment.setLiked(true);

                // Thêm phản hồi cho bình luận đầu tiên
                Comment reply1 = new Comment(
                        100,
                        "user123",
                        "",
                        "Tôi cũng nghĩ vậy!",
                        new Date(currentTime - 1800000),
                        comment.getId()
                );

                Comment reply2 = new Comment(
                        101,
                        "bookfan",
                        "",
                        "Sách này rất hay, tôi đã đọc 3 lần",
                        new Date(currentTime - 900000),
                        comment.getId()
                );

                comment.addReply(reply1);
                comment.addReply(reply2);

                // Thêm phản hồi vào danh sách bình luận
                commentList.add(reply1);
                commentList.add(reply2);
            }

            commentList.add(comment);
        }

        return commentList;
    }

    // Phương thức giả định để lấy đánh giá sách
    private float getBookRating() {
        // Trong ứng dụng thực tế, bạn sẽ tính trung bình đánh giá từ cơ sở dữ liệu
        if (userRating > 0) {
            return userRating;
        }

        switch (book.getId()) {
            case 1: return 4.2f;
            case 2: return 4.0f;
            case 3: return 4.9f;
            case 4: return 4.3f;
            case 5: return 4.5f;
            default: return 4.0f;
        }
    }

    private void saveRecentlyReadBook(int bookId, String title, String englishTitle, String author, int views, int chapters, String coverUrl) {
        // Lấy thời gian hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = "Hôm nay, " + sdf.format(new Date());

        // Lưu thông tin sách đọc gần đây vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("recently_read", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("book_id", bookId);
        editor.putString("title", title);
        editor.putString("english_title", englishTitle);
        editor.putString("author", author);
        editor.putInt("views", views);
        editor.putInt("chapters", chapters);
        editor.putString("last_read_time", currentTime);
        editor.putString("cover_url", coverUrl);
        editor.apply();
    }

    // Add this method to BookDetailActivity.java to launch the ReadBookActivity
    private void startReadingBook() {
        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("book_id", String.valueOf(book.getId()));
        intent.putExtra("book_title", book.getTitle());
        intent.putExtra("book_author", book.getAuthor());
        intent.putExtra("book_cover_url", book.getCoverUrl());
        intent.putExtra("book_olid", book.getOlid());
        startActivity(intent);
    }
}
