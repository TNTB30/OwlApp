package com.example.owlapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.owlapp.api.ApiClient;
import com.example.owlapp.api.OpenLibraryService;
import com.example.owlapp.model.Book;
import com.example.owlapp.model.api.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadBooksActivity extends AppCompatActivity {

    private LinearLayout booksContainer;
    private ImageView navHome, navSearch, navLibrary, navChat, navCart, imgBack;
    private TextView tvTitle;
    private OpenLibraryService openLibraryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_books);

        // Initialize API service
        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        // Khởi tạo views
        booksContainer = findViewById(R.id.booksContainer);
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);
        imgBack = findViewById(R.id.imgBack);
        tvTitle = findViewById(R.id.tvTitle);

        // Thiết lập tiêu đề
        tvTitle.setText("Sách đã đọc");

        // Thiết lập điều hướng
        setupNavigation();

        // Tải danh sách sách đã đọc
        loadReadBooksFromApi();

        // Xử lý sự kiện khi nhấp vào nút quay lại
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadBooksActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadBooksActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        navLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình thư viện (chưa triển khai)
                Intent intent = new Intent(ReadBooksActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        navChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đã ở màn hình sách đã đọc
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sách đọc gần đây
                Intent intent = new Intent(ReadBooksActivity.this, RecentlyReadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadReadBooksFromApi() {
        // Fetch popular books as "read books"
        Call<SearchResponse> call = openLibraryService.searchBooks("bestseller");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Doc> docs = response.body().getDocs();

                    if (docs != null && !docs.isEmpty()) {
                        // Convert API response to Book objects
                        List<Book> books = new ArrayList<>();
                        int id = 1;

                        for (SearchResponse.Doc doc : docs) {
                            if (doc.getTitle() != null && doc.getAuthor() != null) {
                                String title = doc.getTitle();
                                String author = doc.getAuthor();
                                String coverUrl = doc.getCoverUrl();
                                String olid = doc.getKey() != null ? doc.getKey().replace("/works/", "") : "";
                                String isbn = doc.getIsbn();

                                Book book = new Book(
                                        id++,
                                        title,
                                        title, // Use same title for English title
                                        author,
                                        coverUrl,
                                        1000 + (int)(Math.random() * 10000), // Random views
                                        10 + (int)(Math.random() * 300),   // Random chapters
                                        "Bestseller", // Category
                                        null, // Description will be loaded in detail view
                                        null, // Publish date will be loaded in detail view
                                        olid,
                                        isbn
                                );

                                books.add(book);

                                // Limit to 10 books
                                if (books.size() >= 10) break;
                            }
                        }

                        // Display books
                        displayBooks(books);
                    } else {
                        // API returned empty results, use fallback data
                        displayBooks(getReadBooks());
                    }
                } else {
                    // API error, use fallback data
                    displayBooks(getReadBooks());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Network error, use fallback data
                displayBooks(getReadBooks());
            }
        });
    }

    private List<Book> getReadBooks() {
        // Trong ứng dụng thực tế, bạn sẽ lấy danh sách từ cơ sở dữ liệu hoặc API
        List<Book> books = new ArrayList<>();

        books.add(new Book(1, "Những Kỳ Vọng Lớn Lao", "Great Expectations", "Charles Dickens", "https://covers.openlibrary.org/b/id/8881464-L.jpg", 1000, 309, "Tiểu thuyết"));
        books.add(new Book(2, "Cha Giàu, Cha Nghèo", "Rich Dad Poor Dad", "Robert T. Kiyosaki", "https://covers.openlibrary.org/b/id/8315302-L.jpg", 1000, 309, "Kinh doanh - Khởi nghiệp"));
        books.add(new Book(3, "Bố Già", "The Godfather", "Mario Puzo", "https://covers.openlibrary.org/b/id/8117454-L.jpg", 62000, 300, "Tiểu thuyết"));
        books.add(new Book(4, "Cuốn Theo Chiều Gió", "Gone with the Wind", "Margaret Mitchell", "https://covers.openlibrary.org/b/id/6678249-L.jpg", 1000, 20, "Tiểu thuyết"));
        books.add(new Book(5, "Trăm Năm Cô Đơn", "One Hundred Years of Solitude", "Gabriel García Márquez", "https://covers.openlibrary.org/b/id/8701247-L.jpg", 1000, 20, "Tiểu thuyết"));

        return books;
    }

    private void displayBooks(List<Book> books) {
        booksContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            View bookView = inflater.inflate(R.layout.item_read_book, booksContainer, false);

            TextView tvNumber = bookView.findViewById(R.id.tvNumber);
            ImageView imgBook = bookView.findViewById(R.id.imgBook);
            TextView tvBookTitle = bookView.findViewById(R.id.tvBookTitle);
            TextView tvAuthor = bookView.findViewById(R.id.tvAuthor);
            TextView tvViews = bookView.findViewById(R.id.tvViews);
            TextView tvComments = bookView.findViewById(R.id.tvComments);
            TextView tvRating = bookView.findViewById(R.id.tvRating);

            // Thiết lập dữ liệu
            tvNumber.setText(String.valueOf(i + 1));

            // Load book cover from URL
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                Glide.with(this)
                        .load(book.getCoverUrl())
                        .placeholder(R.drawable.book_placeholder)
                        .error(R.drawable.book_placeholder)
                        .into(imgBook);
            } else if (book.getId() == 2) { // Fallback for Cha Giàu, Cha Nghèo
                imgBook.setImageResource(R.drawable.rich_dad_poor_dad);
            } else {
                imgBook.setImageResource(R.drawable.book_placeholder);
            }

            tvBookTitle.setText(book.getFullTitle());
            tvAuthor.setText(book.getAuthor());
            tvViews.setText(book.getFormattedViews());
            tvComments.setText(String.valueOf(book.getChapters()));

            // Đánh giá giả lập
            float rating = 0;
            switch (book.getId()) {
                case 1: rating = 4.2f; break;
                case 2: rating = 4.0f; break;
                case 3: rating = 4.9f; break;
                case 4: rating = 4.3f; break;
                case 5: rating = 4.5f; break;
                default: rating = 4.0f + (float)(Math.random() * 0.9);
            }

            tvRating.setText(String.format("%.1f", rating));

            // Xử lý sự kiện khi nhấp vào sách
            final int bookId = book.getId();
            final String title = book.getTitle();
            final String englishTitle = book.getEnglishTitle();
            final String author = book.getAuthor();
            final String coverUrl = book.getCoverUrl();

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Chuyển đến trang chi tiết sách với chức năng bình luận và đánh giá
                    Intent intent = new Intent(ReadBooksActivity.this, BookDetailActivity.class);
                    intent.putExtra("book_id", bookId);
                    intent.putExtra("book_title", title);
                    intent.putExtra("book_english_title", englishTitle);
                    intent.putExtra("book_author", author);
                    intent.putExtra("book_cover_url", coverUrl);
                    intent.putExtra("show_comments", true); // Hiển thị phần bình luận và đánh giá
                    startActivity(intent);
                }
            });

            booksContainer.addView(bookView);

            // Thêm đường kẻ ngang giữa các mục, trừ mục cuối cùng
            if (i < books.size() - 1) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                ));
                divider.setBackgroundColor(getResources().getColor(R.color.teal));
                booksContainer.addView(divider);
            }
        }
    }
}
