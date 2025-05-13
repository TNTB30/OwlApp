package com.example.owlapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.owlapp.adapter.SearchResultAdapter;
import com.example.owlapp.api.ApiClient;
import com.example.owlapp.api.OpenLibraryService;
import com.example.owlapp.model.Book;
import com.example.owlapp.model.api.SearchResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private Button btnCancel;
    private LinearLayout categoryContainer;
    private ListView lvSearchResults;
    private LinearLayout recentSearchLayout;
    private LinearLayout recentSearchContainer;
    private TextView tvNoResults;
    private ImageView navHome, navSearch, navLibrary, navChat, navCart;
    private ProgressBar progressBar;

    private List<String> categories;
    private List<Book> allBooks;
    private SearchResultAdapter searchResultAdapter;
    private SharedPreferences sharedPreferences;
    private Set<String> recentSearches;
    private OpenLibraryService openLibraryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize API service
        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        // Khởi tạo views
        edtSearch = findViewById(R.id.edtSearch);
        btnCancel = findViewById(R.id.btnCancel);
        categoryContainer = findViewById(R.id.categoryContainer);
        lvSearchResults = findViewById(R.id.lvSearchResults);
        recentSearchLayout = findViewById(R.id.recentSearchLayout);
        recentSearchContainer = findViewById(R.id.recentSearchContainer);
        tvNoResults = findViewById(R.id.tvNoResults);
        progressBar = findViewById(R.id.progressBar);

        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);

        // Khởi tạo dữ liệu
        categories = Arrays.asList("Tiểu thuyết", "Truyện ngắn & Văn học", "Kinh dị & Tâm lý", "Kinh doanh - Khởi nghiệp");
        allBooks = createAllSampleBooks(); // Fallback data

        // Khởi tạo adapter
        searchResultAdapter = new SearchResultAdapter(this, new ArrayList<>());
        lvSearchResults.setAdapter(searchResultAdapter);

        // Tải tìm kiếm gần đây
        sharedPreferences = getSharedPreferences("SearchPrefs", MODE_PRIVATE);
        recentSearches = sharedPreferences.getStringSet("recent_searches", new HashSet<>());

        // Thiết lập giao diện
        setupCategories();
        setupSearchBar();
        setupNavigation();
        displayRecentSearches();
    }

    private void setupCategories() {
        LayoutInflater inflater = LayoutInflater.from(this);
        categoryContainer.removeAllViews();

        for (String category : categories) {
            TextView tvCategory = (TextView) inflater.inflate(R.layout.item_category, categoryContainer, false);
            tvCategory.setText(category);

            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBooksByCategory(category);
                }
            });

            categoryContainer.addView(tvCategory);
        }
    }

    private void setupSearchBar() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchBooks(query);
                } else {
                    lvSearchResults.setVisibility(View.GONE);
                    recentSearchLayout.setVisibility(View.VISIBLE);
                    tvNoResults.setVisibility(View.GONE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
                lvSearchResults.setVisibility(View.GONE);
                recentSearchLayout.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);
            }
        });

        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book selectedBook = searchResultAdapter.getItem(position);

                // Save search query
                saveSearchQuery(edtSearch.getText().toString().trim());

                // Open book detail
                Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                intent.putExtra("book_id", selectedBook.getId());
                intent.putExtra("book_title", selectedBook.getTitle());
                intent.putExtra("book_english_title", selectedBook.getEnglishTitle());
                intent.putExtra("book_author", selectedBook.getAuthor());
                intent.putExtra("book_cover_url", selectedBook.getCoverUrl());
                intent.putExtra("book_olid", selectedBook.getOlid());
                intent.putExtra("book_isbn", selectedBook.getIsbn());
                intent.putExtra("show_comments", false);
                startActivity(intent);
            }
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đã ở trang tìm kiếm
            }
        });

        navLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình thư viện (chưa triển khai)
                Intent intent = new Intent(SearchActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        navChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sách đã đọc
                Intent intent = new Intent(SearchActivity.this, ReadBooksActivity.class);
                startActivity(intent);
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sách đọc gần đây
                Intent intent = new Intent(SearchActivity.this, RecentlyReadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchBooks(String query) {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Make API call to search books
        Call<SearchResponse> call = openLibraryService.searchBooks(query);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Doc> docs = response.body().getDocs();

                    if (docs != null && !docs.isEmpty()) {
                        // Convert API response to Book objects
                        List<Book> books = new ArrayList<>();
                        int id = 1;

                        for (SearchResponse.Doc doc : docs) {
                            String title = doc.getTitle();
                            String author = doc.getAuthor();
                            String coverUrl = doc.getCoverUrl();
                            String olid = doc.getKey().replace("/works/", "");
                            String isbn = doc.getIsbn();

                            Book book = new Book(
                                    id++,
                                    title,
                                    title, // Use same title for English title
                                    author,
                                    coverUrl,
                                    1000, // Default views
                                    10,   // Default chapters
                                    "Open Library", // Default category
                                    null, // Description will be loaded in detail view
                                    null, // Publish date will be loaded in detail view
                                    olid,
                                    isbn
                            );

                            books.add(book);
                        }

                        // Update adapter with search results
                        searchResultAdapter.clear();
                        searchResultAdapter.addAll(books);
                        searchResultAdapter.notifyDataSetChanged();

                        // Show results
                        lvSearchResults.setVisibility(View.VISIBLE);
                        recentSearchLayout.setVisibility(View.GONE);
                        tvNoResults.setVisibility(View.GONE);
                    } else {
                        // No results found
                        lvSearchResults.setVisibility(View.GONE);
                        recentSearchLayout.setVisibility(View.GONE);
                        tvNoResults.setVisibility(View.VISIBLE);
                    }
                } else {
                    // API error
                    Toast.makeText(SearchActivity.this, "Không thể tìm kiếm sách. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    lvSearchResults.setVisibility(View.GONE);
                    recentSearchLayout.setVisibility(View.VISIBLE);
                    tvNoResults.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                // Network error
                Toast.makeText(SearchActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                lvSearchResults.setVisibility(View.GONE);
                recentSearchLayout.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);

                // Use fallback data
                searchLocalBooks(edtSearch.getText().toString().trim());
            }
        });
    }

    private void searchBooksByCategory(String category) {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Map Vietnamese categories to English for API search
        String searchTerm;
        switch (category) {
            case "Tiểu thuyết":
                searchTerm = "fiction";
                break;
            case "Truyện ngắn & Văn học":
                searchTerm = "literature";
                break;
            case "Kinh dị & Tâm lý":
                searchTerm = "horror+psychology";
                break;
            case "Kinh doanh - Khởi nghiệp":
                searchTerm = "business+entrepreneurship";
                break;
            default:
                searchTerm = category;
        }

        // Make API call to search books by category
        Call<SearchResponse> call = openLibraryService.searchBooks(searchTerm);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);

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
                                        category, // Use selected category
                                        null, // Description will be loaded in detail view
                                        null, // Publish date will be loaded in detail view
                                        olid,
                                        isbn
                                );

                                books.add(book);
                            }
                        }

                        // Update adapter with search results
                        searchResultAdapter.clear();
                        searchResultAdapter.addAll(books);
                        searchResultAdapter.notifyDataSetChanged();

                        // Show results
                        lvSearchResults.setVisibility(View.VISIBLE);
                        recentSearchLayout.setVisibility(View.GONE);
                        tvNoResults.setVisibility(View.GONE);
                    } else {
                        // If API returns empty results, use local data
                        filterLocalBooksByCategory(category);
                    }
                } else {
                    // API error, use local data
                    filterLocalBooksByCategory(category);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                // Network error, use local data
                filterLocalBooksByCategory(category);
            }
        });
    }

    private void filterLocalBooksByCategory(String category) {
        List<Book> results = new ArrayList<>();

        for (Book book : allBooks) {
            if (book.getCategory().equals(category)) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            // If no books match the category, show all books
            searchResultAdapter.clear();
            searchResultAdapter.addAll(allBooks);
            searchResultAdapter.notifyDataSetChanged();

            lvSearchResults.setVisibility(View.VISIBLE);
            recentSearchLayout.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.GONE);

            Toast.makeText(SearchActivity.this, "Không tìm thấy sách cho danh mục này, hiển thị tất cả sách", Toast.LENGTH_SHORT).show();
        } else {
            searchResultAdapter.clear();
            searchResultAdapter.addAll(results);
            searchResultAdapter.notifyDataSetChanged();

            lvSearchResults.setVisibility(View.VISIBLE);
            recentSearchLayout.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.GONE);
        }
    }

    private void searchLocalBooks(String query) {
        List<Book> results = new ArrayList<>();

        for (Book book : allBooks) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    (book.getEnglishTitle() != null && book.getEnglishTitle().toLowerCase().contains(query.toLowerCase())) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            lvSearchResults.setVisibility(View.GONE);
            recentSearchLayout.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            searchResultAdapter.clear();
            searchResultAdapter.addAll(results);
            searchResultAdapter.notifyDataSetChanged();

            lvSearchResults.setVisibility(View.VISIBLE);
            recentSearchLayout.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.GONE);
        }
    }

    private void saveSearchQuery(String query) {
        if (query.isEmpty()) return;

        Set<String> updatedSearches = new HashSet<>(recentSearches);
        updatedSearches.add(query);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("recent_searches", updatedSearches);
        editor.apply();

        recentSearches = updatedSearches;
        displayRecentSearches();
    }

    private void displayRecentSearches() {
        recentSearchContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String query : recentSearches) {
            View searchItemView = inflater.inflate(R.layout.item_recent_search, recentSearchContainer, false);
            TextView tvSearchQuery = searchItemView.findViewById(R.id.tvSearchQuery);
            tvSearchQuery.setText(query);

            searchItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edtSearch.setText(query);
                    searchBooks(query);
                }
            });

            recentSearchContainer.addView(searchItemView);
        }
    }

    private List<Book> createAllSampleBooks() {
        List<Book> books = new ArrayList<>();

        // Tiểu thuyết
        books.add(new Book(1, "Foundation", "Foundation", "Isaac Asimov", "https://covers.openlibrary.org/b/id/12791261-L.jpg", 1000, 10, "Tiểu thuyết"));
        books.add(new Book(2, "Ficciones", "Ficciones", "Jorge Luis Borges", "https://covers.openlibrary.org/b/id/8430426-L.jpg", 1000, 10, "Tiểu thuyết"));
        books.add(new Book(3, "Dracula", "Dracula", "Bram Stoker", "https://covers.openlibrary.org/b/id/12645114-L.jpg", 1000, 10, "Tiểu thuyết"));
        books.add(new Book(4, "The Last Man", "The Last Man", "Mary Shelley", "https://covers.openlibrary.org/b/id/6738428-L.jpg", 1000, 10, "Tiểu thuyết"));
        books.add(new Book(5, "Trăm Năm Cô Đơn", "One Hundred Years of Solitude", "Gabriel García Márquez", "https://covers.openlibrary.org/b/id/8701247-L.jpg", 1000, 10, "Tiểu thuyết"));

        // Truyện ngắn & Văn học
        books.add(new Book(6, "Dracula", "Dracula", "Bram Stoker", "https://covers.openlibrary.org/b/id/12645114-L.jpg", 1000, 27, "Truyện ngắn & Văn học"));
        books.add(new Book(7, "Frankenstein", "Frankenstein", "Mary Shelley", "https://covers.openlibrary.org/b/id/12890468-L.jpg", 66000, 24, "Truyện ngắn & Văn học"));
        books.add(new Book(8, "Sự Im Lặng Của Bầy Cừu", "The Silence of the Lambs", "Thomas Harris", "https://covers.openlibrary.org/b/id/8575111-L.jpg", 62000, 61, "Truyện ngắn & Văn học"));
        books.add(new Book(9, "Tòa Sáng", "The Shining", "Stephen King", "https://covers.openlibrary.org/b/id/8643691-L.jpg", 1000, 58, "Truyện ngắn & Văn học"));
        books.add(new Book(10, "Bản Năng Gốc", "Psycho", "Robert Bloch", "https://covers.openlibrary.org/b/id/8114155-L.jpg", 1000, 17, "Truyện ngắn & Văn học"));

        // Kinh doanh - Khởi nghiệp
        books.add(new Book(11, "Cha Giàu, Cha Nghèo", "Rich Dad Poor Dad", "Robert T. Kiyosaki", "https://covers.openlibrary.org/b/id/8315302-L.jpg", 1000, 10, "Kinh doanh - Khởi nghiệp"));
        books.add(new Book(12, "Từ Tốt Đến Vĩ Đại", "Good To Great", "Jim Collins", "https://covers.openlibrary.org/b/id/8761543-L.jpg", 66000, 9, "Kinh doanh - Khởi nghiệp"));
        books.add(new Book(13, "Khởi Nghiệp Tinh Gọn", "The Lean Startup", "Eric Ries", "https://covers.openlibrary.org/b/id/6480564-L.jpg", 62000, 13, "Kinh doanh - Khởi nghiệp"));
        books.add(new Book(14, "Dấn Thân", "Lean In", "Sheryl Sandberg", "https://covers.openlibrary.org/b/id/8241313-L.jpg", 1000, 11, "Kinh doanh - Khởi nghiệp"));
        books.add(new Book(15, "Bắt Đầu với Lý do", "Start with Why", "Simon Sinek", "https://covers.openlibrary.org/b/id/8302879-L.jpg", 1000, 6, "Kinh doanh - Khởi nghiệp"));

        // Kinh dị & Tâm lý
        books.add(new Book(16, "Dracula", "Dracula", "Bram Stoker", "https://covers.openlibrary.org/b/id/12645114-L.jpg", 1000, 27, "Kinh dị & Tâm lý"));
        books.add(new Book(17, "Frankenstein", "Frankenstein", "Mary Shelley", "https://covers.openlibrary.org/b/id/12890468-L.jpg", 66000, 24, "Kinh dị & Tâm lý"));
        books.add(new Book(18, "Sự Im Lặng Của Bầy Cừu", "The Silence of the Lambs", "Thomas Harris", "https://covers.openlibrary.org/b/id/8575111-L.jpg", 62000, 61, "Kinh dị & Tâm lý"));
        books.add(new Book(19, "Tòa Sáng", "The Shining", "Stephen King", "https://covers.openlibrary.org/b/id/8643691-L.jpg", 1000, 58, "Kinh dị & Tâm lý"));
        books.add(new Book(20, "Bản Năng Gốc", "Psycho", "Robert Bloch", "https://covers.openlibrary.org/b/id/8114155-L.jpg", 1000, 17, "Kinh dị & Tâm lý"));

        return books;
    }
}
