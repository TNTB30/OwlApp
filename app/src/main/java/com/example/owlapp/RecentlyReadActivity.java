package com.example.owlapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.owlapp.model.Book;

public class RecentlyReadActivity extends AppCompatActivity {

    private ImageView navHome, navSearch, navLibrary, navChat, navCart, imgBack;
    private ImageView imgBook;
    private TextView tvTitle, tvBookTitle, tvAuthor, tvViews, tvChapters, tvLastRead;
    private Button btnContinueReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_read);

        // Khởi tạo views
        imgBack = findViewById(R.id.imgBack);
        tvTitle = findViewById(R.id.tvTitle);
        imgBook = findViewById(R.id.imgBook);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvViews = findViewById(R.id.tvViews);
        tvChapters = findViewById(R.id.tvChapters);
        tvLastRead = findViewById(R.id.tvLastRead);
        btnContinueReading = findViewById(R.id.btnContinueReading);

        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);

        // Thiết lập tiêu đề
        tvTitle.setText("Sách đọc gần đây");

        // Thiết lập điều hướng
        setupNavigation();

        // Hiển thị sách đọc gần đây
        displayRecentlyReadBook();

        // Xử lý sự kiện khi nhấp vào nút quay lại
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Xử lý sự kiện khi nhấp vào nút tiếp tục đọc
        btnContinueReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin sách từ SharedPreferences
                SharedPreferences prefs = getSharedPreferences("recently_read", MODE_PRIVATE);
                int bookId = prefs.getInt("book_id", 1);
                String title = prefs.getString("title", "");
                String englishTitle = prefs.getString("english_title", "");
                String author = prefs.getString("author", "");
                String coverUrl = prefs.getString("cover_url", "");

                // Chuyển đến trang chi tiết sách
                Intent intent = new Intent(RecentlyReadActivity.this, BookDetailActivity.class);
                intent.putExtra("book_id", bookId);
                intent.putExtra("book_title", title);
                intent.putExtra("book_english_title", englishTitle);
                intent.putExtra("book_author", author);
                intent.putExtra("book_cover_url", coverUrl);
                intent.putExtra("show_comments", true);
                startActivity(intent);
            }
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyReadActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyReadActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        navLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyReadActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        navChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyReadActivity.this, ReadBooksActivity.class);
                startActivity(intent);
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đã ở màn hình sách đọc gần đây
            }
        });
    }

    private void displayRecentlyReadBook() {
        // Lấy thông tin sách đọc gần đây từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("recently_read", MODE_PRIVATE);
        int bookId = prefs.getInt("book_id", 1);
        String title = prefs.getString("title", "Những Kỳ Vọng Lớn Lao");
        String englishTitle = prefs.getString("english_title", "Great Expectations");
        String author = prefs.getString("author", "Charles Dickens");
        int views = prefs.getInt("views", 1000);
        int chapters = prefs.getInt("chapters", 10);
        String lastReadTime = prefs.getString("last_read_time", "Hôm nay, 10:30");
        String coverUrl = prefs.getString("cover_url", "");

        // Tạo đối tượng Book
        Book book = new Book(bookId, title, englishTitle, author, coverUrl, views, chapters, "");

        // Hiển thị thông tin sách
        tvBookTitle.setText(book.getFullTitle());
        tvAuthor.setText(book.getAuthor());
        tvViews.setText(book.getFormattedViews() + " lượt xem");
        tvChapters.setText(book.getChapters() + " chương");
        tvLastRead.setText("Đọc lần cuối: " + lastReadTime);

        // Hiển thị ảnh bìa sách từ URL
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(coverUrl)
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(imgBook);
        } else if (bookId == 2) { // Fallback for Cha Giàu, Cha Nghèo
            imgBook.setImageResource(R.drawable.rich_dad_poor_dad);
        } else {
            imgBook.setImageResource(R.drawable.book_placeholder);
        }
    }
}
