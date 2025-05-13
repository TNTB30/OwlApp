package com.example.owlapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout newBooksContainer;
    private LinearLayout popularBooksContainer;
    private LinearLayout recommendedBooksContainer;
    private ImageView navHome, navSearch, navLibrary, navChat, navCart, imgProfile;
    private TextView tvWelcome;
    private OpenLibraryService openLibraryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize API service
        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        // Khởi tạo views
        newBooksContainer = findViewById(R.id.newBooksContainer);
        popularBooksContainer = findViewById(R.id.popularBooksContainer);
        recommendedBooksContainer = findViewById(R.id.recommendedBooksContainer);
        tvWelcome = findViewById(R.id.tvWelcome);
        imgProfile = findViewById(R.id.imgProfile);

        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);

        // Thiết lập điều hướng
        setupNavigation();

        // Tải dữ liệu sách
        loadBooksFromApi();

        // Xử lý sự kiện khi nhấp vào ảnh hồ sơ
        ImageView imgProfle = findViewById(R.id.imgProfile); // hoặc id khác nếu bạn dùng
        imgProfle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đã ở trang chủ
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        navLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình thư viện (chưa triển khai)
                Intent intent = new Intent(HomeActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        navChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sách đã đọc
                Intent intent = new Intent(HomeActivity.this, ReadBooksActivity.class);
                startActivity(intent);
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sách đọc gần đây
                Intent intent = new Intent(HomeActivity.this, RecentlyReadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logout() {
        // Xóa thông tin đăng nhập nếu có
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Quay lại màn hình đăng nhập
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadBooksFromApi() {
        // Load new books
        fetchBooksForCategory("fiction", newBooksContainer, "new");

        // Load popular books
        fetchBooksForCategory("bestseller", popularBooksContainer, "popular");

        // Load recommended books
        fetchBooksForCategory("recommended", recommendedBooksContainer, "recommended");
    }

    private void fetchBooksForCategory(String searchTerm, final LinearLayout container, final String categoryType) {
        Call<SearchResponse> call = openLibraryService.searchBooks(searchTerm);
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
                                        1000, // Default views
                                        10,   // Default chapters
                                        categoryType, // Use category type
                                        null, // Description will be loaded in detail view
                                        null, // Publish date will be loaded in detail view
                                        olid,
                                        isbn
                                );

                                books.add(book);

                                // Limit to 10 books per category
                                if (books.size() >= 10) break;
                            }
                        }

                        // Display books
                        displayBooks(container, books);
                    } else {
                        // API returned empty results, use fallback data
                        displayBooks(container, createSampleBooks(categoryType));
                    }
                } else {
                    // API error, use fallback data
                    displayBooks(container, createSampleBooks(categoryType));
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Network error, use fallback data
                displayBooks(container, createSampleBooks(categoryType));
            }
        });
    }

    private List<Book> createSampleBooks(String type) {
        List<Book> books = new ArrayList<>();

        if ("new".equals(type)) {
            books.add(new Book(1, "Foundation", "Foundation", "Isaac Asimov", "https://covers.openlibrary.org/b/id/12791261-L.jpg", 1000, 10, "Tiểu thuyết"));
            books.add(new Book(2, "Cha Giàu, Cha Nghèo", "Rich Dad Poor Dad", "Robert T. Kiyosaki", "https://covers.openlibrary.org/b/id/8315302-L.jpg", 1000, 309, "Kinh doanh - Khởi nghiệp"));
            books.add(new Book(3, "Sức Mạnh Tiềm Thức", "The Power of Your Subconscious Mind", "Dr. Joseph Murphy", "https://covers.openlibrary.org/b/id/8231990-L.jpg", 2000, 12, "Tâm lý học"));
            books.add(new Book(4, "Người Giàu Có Nhất Thành Babylon", "The Richest Man in Babylon", "George S. Clason", "https://covers.openlibrary.org/b/id/8237920-L.jpg", 1800, 8, "Tài chính"));
        } else if ("popular".equals(type)) {
            books.add(new Book(5, "Chiến Lược Gia", "The Strategist", "Cynthia Montgomery", "https://covers.openlibrary.org/b/id/7388648-L.jpg", 3000, 14, "Kinh doanh"));
            books.add(new Book(6, "Bí Mật Triệu Phú", "The Millionaire Next Door", "Thomas J. Stanley", "https://covers.openlibrary.org/b/id/8474609-L.jpg", 2500, 11, "Tài chính"));
            books.add(new Book(7, "Ikigai", "Ikigai", "Hector Garcia", "https://covers.openlibrary.org/b/id/8302393-L.jpg", 1700, 9, "Phát triển bản thân"));
            books.add(new Book(8, "Atomic Habits", "Atomic Habits", "James Clear", "https://covers.openlibrary.org/b/id/10389354-L.jpg", 2200, 13, "Phát triển bản thân"));
        } else {
            books.add(new Book(9, "Tư Duy Tối Ưu", "First Things First", "Stephen R. Covey", "https://covers.openlibrary.org/b/id/8426132-L.jpg", 1900, 10, "Phát triển bản thân"));
            books.add(new Book(10, "Tiếp Thị 6.0", "Marketing 6.0", "Philip Kotler", "https://covers.openlibrary.org/b/id/12791261-L.jpg", 2100, 16, "Marketing"));
            books.add(new Book(11, "AI Edge", "The AI Edge", "Karim R. Lakhani", "https://covers.openlibrary.org/b/id/12791261-L.jpg", 1600, 12, "Công nghệ"));
            books.add(new Book(12, "Lập Trình Cuộc Đời", "Logic", "John Doe", "https://covers.openlibrary.org/b/id/12791261-L.jpg", 1400, 8, "Công nghệ"));
        }

        return books;
    }

    private void displayBooks(LinearLayout container, List<Book> books) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Book book : books) {
            View bookView = inflater.inflate(R.layout.item_book, container, false);

            ImageView imgBook = bookView.findViewById(R.id.imgBook);
            TextView tvBookTitle = bookView.findViewById(R.id.tvBookTitle);
            TextView tvAuthor = bookView.findViewById(R.id.tvAuthor);

            // Load book cover from URL
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                Glide.with(this)
                        .load(book.getCoverUrl())
                        .placeholder(R.drawable.book_placeholder)
                        .error(R.drawable.book_placeholder)
                        .into(imgBook);
            } else {
                imgBook.setImageResource(R.drawable.book_placeholder);
            }

            tvBookTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());

            // Xử lý sự kiện khi nhấp vào sách
            final int bookId = book.getId();
            final String title = book.getTitle();
            final String englishTitle = book.getEnglishTitle();
            final String author = book.getAuthor();
            final int views = book.getViews();
            final int chapters = book.getChapters();
            final String coverUrl = book.getCoverUrl();

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lưu thông tin sách đọc gần đây
                    saveRecentlyReadBook(bookId, title, englishTitle, author, views, chapters, coverUrl);

                    // Chuyển đến trang chi tiết sách không hiển thị phần bình luận và đánh giá
                    Intent intent = new Intent(HomeActivity.this, BookDetailActivity.class);
                    intent.putExtra("book_id", bookId);
                    intent.putExtra("book_title", title);
                    intent.putExtra("book_english_title", englishTitle);
                    intent.putExtra("book_author", author);
                    intent.putExtra("book_cover_url", coverUrl);
                    intent.putExtra("show_comments", false); // Không hiển thị phần bình luận và đánh giá
                    startActivity(intent);
                }
            });

            container.addView(bookView);
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
}
