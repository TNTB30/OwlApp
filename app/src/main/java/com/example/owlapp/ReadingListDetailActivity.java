package com.example.owlapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.owlapp.adapter.BookAdapter;
import com.example.owlapp.api.ApiClient;
import com.example.owlapp.api.OpenLibraryService;
import com.example.owlapp.model.Book;
import com.example.owlapp.model.api.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadingListDetailActivity extends AppCompatActivity {

    private ImageView imgBack, imgAdd, imgOptions;
    private TextView tvTitle, tvListName, tvBookCount;
    private Button btnCurrentlyReading, btnArchive, btnReadingLists;
    private RecyclerView rvReadingListBooks;
    private TextView tvNoBooks;
    private ImageView navHome, navSearch, navLibrary, navChat, navCart;

    private int readingListId;
    private String readingListName;
    private List<Book> books;
    private BookAdapter bookAdapter;
    private OpenLibraryService openLibraryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_list_detail);

        // Initialize API service
        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        // Get reading list info from intent
        readingListId = getIntent().getIntExtra("reading_list_id", 0);
        readingListName = getIntent().getStringExtra("reading_list_name");

        initViews();
        setupListeners();
        loadBooksFromApi();
    }

    private void initViews() {
        // Header
        imgBack = findViewById(R.id.imgBack);
        imgAdd = findViewById(R.id.imgAdd);
        imgOptions = findViewById(R.id.imgOptions);
        tvTitle = findViewById(R.id.tvTitle);

        // Tabs
        btnCurrentlyReading = findViewById(R.id.btnCurrentlyReading);
        btnArchive = findViewById(R.id.btnArchive);
        btnReadingLists = findViewById(R.id.btnReadingLists);

        // Reading list info
        tvListName = findViewById(R.id.tvListName);
        tvBookCount = findViewById(R.id.tvBookCount);

        // Books
        rvReadingListBooks = findViewById(R.id.rvReadingListBooks);
        tvNoBooks = findViewById(R.id.tvNoBooks);

        // Navigation
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);

        // Setup views
        tvTitle.setText(readingListName);
        tvListName.setText(readingListName);
        btnReadingLists.setSelected(true);

        // Setup RecyclerView
        rvReadingListBooks.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReadingListDetailActivity.this, "Thêm truyện vào danh sách", Toast.LENGTH_SHORT).show();
            }
        });

        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu();
            }
        });

        // Navigation
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingListDetailActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingListDetailActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        navLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        navChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingListDetailActivity.this, ReadBooksActivity.class);
                startActivity(intent);
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingListDetailActivity.this, RecentlyReadActivity.class);
                startActivity(intent);
            }
        });

        // Tabs
        btnCurrentlyReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingListDetailActivity.this, LibraryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingListDetailActivity.this, LibraryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadBooksFromApi() {
        // Initialize empty list
        books = new ArrayList<>();

        // Fetch books for reading list from API
        // Using "fiction" as search term for demo
        Call<SearchResponse> call = openLibraryService.searchBooks("fiction");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Doc> docs = response.body().getDocs();

                    if (docs != null && !docs.isEmpty()) {
                        // Convert API response to Book objects
                        books.clear();
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
                                        "Fiction", // Default category
                                        null, // Description will be loaded in detail view
                                        null, // Publish date will be loaded in detail view
                                        olid,
                                        isbn
                                );

                                books.add(book);

                                // Limit to 2 books for the reading list
                                if (books.size() >= 2) break;
                            }
                        }

                        // Update book count
                        tvBookCount.setText(books.size() + " stories");

                        // Display books
                        displayBooks();
                    } else {
                        // API returned empty results, use fallback data
                        loadFallbackBooks();
                    }
                } else {
                    // API error, use fallback data
                    loadFallbackBooks();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Network error, use fallback data
                loadFallbackBooks();
            }
        });
    }

    private void loadFallbackBooks() {
        books.clear();
        books.add(new Book(1, "Những Kỳ Vọng Lớn Lao", "Great Expectations", "Charles Dickens", "https://covers.openlibrary.org/b/id/8881464-L.jpg", 1000, 59, "Tiểu thuyết"));
        books.add(new Book(2, "Kiêu Hãnh và Định Kiến", "Pride and Prejudice", "Jane Austen", "https://covers.openlibrary.org/b/id/6738428-L.jpg", 66000, 61, "Tiểu thuyết"));

        // Update book count
        tvBookCount.setText(books.size() + " stories");

        // Display books
        displayBooks();
    }

    private void displayBooks() {
        if (books.isEmpty()) {
            rvReadingListBooks.setVisibility(View.GONE);
            tvNoBooks.setVisibility(View.VISIBLE);
        } else {
            rvReadingListBooks.setVisibility(View.VISIBLE);
            tvNoBooks.setVisibility(View.GONE);

            // Set adapter
            bookAdapter = new BookAdapter(this, books);
            rvReadingListBooks.setAdapter(bookAdapter);
        }
    }

    private void showOptionsMenu() {
        String[] options = {"Thêm Truyện vào danh sách", "Chỉnh sửa Danh sách đọc", "Đổi tên Danh sách đọc"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Thêm Truyện
                        Toast.makeText(ReadingListDetailActivity.this, "Thêm Truyện vào danh sách", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: // Chỉnh sửa
                        Toast.makeText(ReadingListDetailActivity.this, "Chỉnh sửa Danh sách đọc", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // Đổi tên
                        Toast.makeText(ReadingListDetailActivity.this, "Đổi tên Danh sách đọc", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        builder.show();
    }
}
