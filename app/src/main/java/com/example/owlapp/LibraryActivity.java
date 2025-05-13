package com.example.owlapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.owlapp.adapter.BookAdapter;
import com.example.owlapp.adapter.ReadingListAdapter;
import com.example.owlapp.api.ApiClient;
import com.example.owlapp.api.OpenLibraryService;
import com.example.owlapp.model.Book;
import com.example.owlapp.model.ReadingList;
import com.example.owlapp.model.api.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends AppCompatActivity {

    private Button btnCurrentlyReading, btnArchive, btnReadingLists;
    private View currentlyReadingContent, archiveContent, readingListsContent;
    private RecyclerView rvCurrentlyReading, rvArchive, rvReadingLists;
    private TextView tvNoCurrentlyReading, tvNoReadingLists;
    private LinearLayout emptyArchiveLayout;
    private Button btnSelectToArchive;
    private ImageView navHome, navSearch, navLibrary, navChat, navCart, imgOptions;

    private List<Book> currentlyReadingBooks;
    private List<Book> archivedBooks;
    private List<ReadingList> readingLists;

    private BookAdapter currentlyReadingAdapter;
    private BookAdapter archiveAdapter;
    private ReadingListAdapter readingListAdapter;

    private OpenLibraryService openLibraryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // Initialize API service
        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        initViews();
        setupTabListeners();
        setupNavigation();
        loadDataFromApi();
        displayCurrentlyReading();
    }

    private void initViews() {
        // Tabs
        btnCurrentlyReading = findViewById(R.id.btnCurrentlyReading);
        btnArchive = findViewById(R.id.btnArchive);
        btnReadingLists = findViewById(R.id.btnReadingLists);

        // Content containers
        currentlyReadingContent = findViewById(R.id.currentlyReadingContent);
        archiveContent = findViewById(R.id.archiveContent);
        readingListsContent = findViewById(R.id.readingListsContent);

        // RecyclerViews
        rvCurrentlyReading = findViewById(R.id.rvCurrentlyReading);
        rvArchive = findViewById(R.id.rvArchive);
        rvReadingLists = findViewById(R.id.rvReadingLists);

        // Empty state views
        tvNoCurrentlyReading = findViewById(R.id.tvNoCurrentlyReading);
        tvNoReadingLists = findViewById(R.id.tvNoReadingLists);
        emptyArchiveLayout = findViewById(R.id.emptyArchiveLayout);
        btnSelectToArchive = findViewById(R.id.btnSelectToArchive);

        // Navigation
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);
        imgOptions = findViewById(R.id.imgOptions);

        // Setup RecyclerViews
        rvCurrentlyReading.setLayoutManager(new GridLayoutManager(this, 3));
        rvArchive.setLayoutManager(new GridLayoutManager(this, 3));
        rvReadingLists.setLayoutManager(new LinearLayoutManager(this));

        // Setup options button
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LibraryActivity.this, "Tùy chọn thư viện", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup archive button
        btnSelectToArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LibraryActivity.this, "Chọn truyện để lưu trữ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabListeners() {
        btnCurrentlyReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCurrentlyReading();
            }
        });

        btnArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayArchive();
            }
        });

        btnReadingLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayReadingLists();
            }
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        navLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already in library
            }
        });

        navChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sách đã đọc
                Intent intent = new Intent(LibraryActivity.this, ReadBooksActivity.class);
                startActivity(intent);
            }
        });

        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, RecentlyReadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDataFromApi() {
        // Initialize empty lists
        currentlyReadingBooks = new ArrayList<>();
        archivedBooks = new ArrayList<>();
        readingLists = new ArrayList<>();

        // Fetch currently reading books from API
        fetchCurrentlyReadingBooks();

        // Fetch archived books from API
        fetchArchivedBooks();

        // Setup reading lists (using local data for now)
        readingLists.add(new ReadingList(1, "Danh sách đọc của Haru", 1));

        // Setup adapters
        currentlyReadingAdapter = new BookAdapter(this, currentlyReadingBooks);
        archiveAdapter = new BookAdapter(this, archivedBooks);
        readingListAdapter = new ReadingListAdapter(this, readingLists);

        // Set adapters
        rvCurrentlyReading.setAdapter(currentlyReadingAdapter);
        rvArchive.setAdapter(archiveAdapter);
        rvReadingLists.setAdapter(readingListAdapter);

        // Set click listeners for reading lists
        readingListAdapter.setOnItemClickListener(new ReadingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ReadingList readingList) {
                Intent intent = new Intent(LibraryActivity.this, ReadingListDetailActivity.class);
                intent.putExtra("reading_list_id", readingList.getId());
                intent.putExtra("reading_list_name", readingList.getName());
                startActivity(intent);
            }
        });
    }

    private void fetchCurrentlyReadingBooks() {
        // Fetch books with "literature" search term for "Currently Reading" tab
        Call<SearchResponse> call = openLibraryService.searchBooks("literature");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Doc> docs = response.body().getDocs();

                    if (docs != null && !docs.isEmpty()) {
                        // Convert API response to Book objects
                        currentlyReadingBooks.clear();
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
                                        "Tiểu thuyết", // Default category
                                        null, // Description will be loaded in detail view
                                        null, // Publish date will be loaded in detail view
                                        olid,
                                        isbn
                                );

                                currentlyReadingBooks.add(book);

                                // Limit to 6 books
                                if (currentlyReadingBooks.size() >= 6) break;
                            }
                        }

                        // Update adapter
                        currentlyReadingAdapter.notifyDataSetChanged();

                        // Update UI
                        updateCurrentlyReadingUI();
                    } else {
                        // API returned empty results, use fallback data
                        loadFallbackCurrentlyReadingBooks();
                    }
                } else {
                    // API error, use fallback data
                    loadFallbackCurrentlyReadingBooks();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Network error, use fallback data
                loadFallbackCurrentlyReadingBooks();
            }
        });
    }

    private void fetchArchivedBooks() {
        // Fetch books with "classic" search term for "Archive" tab
        Call<SearchResponse> call = openLibraryService.searchBooks("classic");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Doc> docs = response.body().getDocs();

                    if (docs != null && !docs.isEmpty()) {
                        // Convert API response to Book objects
                        archivedBooks.clear();
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
                                        "Lưu trữ", // Default category
                                        null, // Description will be loaded in detail view
                                        null, // Publish date will be loaded in detail view
                                        olid,
                                        isbn
                                );

                                archivedBooks.add(book);

                                // Limit to 6 books
                                if (archivedBooks.size() >= 6) break;
                            }
                        }

                        // Update adapter
                        archiveAdapter.notifyDataSetChanged();

                        // Update UI
                        updateArchiveUI();
                    } else {
                        // API returned empty results, use fallback data
                        loadFallbackArchivedBooks();
                    }
                } else {
                    // API error, use fallback data
                    loadFallbackArchivedBooks();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Network error, use fallback data
                loadFallbackArchivedBooks();
            }
        });
    }

    private void loadFallbackCurrentlyReadingBooks() {
        currentlyReadingBooks.clear();
        currentlyReadingBooks.add(new Book(1, "Haruki Murakami và Âm Nhạc Của Ngôn Từ", "", "Jay Rubin", "https://covers.openlibrary.org/b/id/8426132-L.jpg", 1000, 10, "Tiểu thuyết"));
        currentlyReadingBooks.add(new Book(2, "Kafka Bên Bờ Biển", "", "Haruki Murakami", "https://covers.openlibrary.org/b/id/8117454-L.jpg", 1000, 10, "Tiểu thuyết"));
        currentlyReadingBooks.add(new Book(3, "Cuộc Săn Cừu Hoang", "", "Haruki Murakami", "https://covers.openlibrary.org/b/id/8701247-L.jpg", 1000, 10, "Tiểu thuyết"));

        currentlyReadingAdapter.notifyDataSetChanged();
        updateCurrentlyReadingUI();
    }

    private void loadFallbackArchivedBooks() {
        archivedBooks.clear();
        // Leave empty for now to show empty state

        archiveAdapter.notifyDataSetChanged();
        updateArchiveUI();
    }

    private void updateCurrentlyReadingUI() {
        if (currentlyReadingBooks.isEmpty()) {
            rvCurrentlyReading.setVisibility(View.GONE);
            tvNoCurrentlyReading.setVisibility(View.VISIBLE);
        } else {
            rvCurrentlyReading.setVisibility(View.VISIBLE);
            tvNoCurrentlyReading.setVisibility(View.GONE);
        }
    }

    private void updateArchiveUI() {
        if (archivedBooks.isEmpty()) {
            rvArchive.setVisibility(View.GONE);
            emptyArchiveLayout.setVisibility(View.VISIBLE);
        } else {
            rvArchive.setVisibility(View.VISIBLE);
            emptyArchiveLayout.setVisibility(View.GONE);
        }
    }

    private void displayCurrentlyReading() {
        // Update tab selection
        btnCurrentlyReading.setSelected(true);
        btnArchive.setSelected(false);
        btnReadingLists.setSelected(false);

        // Show/hide content
        currentlyReadingContent.setVisibility(View.VISIBLE);
        archiveContent.setVisibility(View.GONE);
        readingListsContent.setVisibility(View.GONE);

        // Show empty state if needed
        updateCurrentlyReadingUI();
    }

    private void displayArchive() {
        // Update tab selection
        btnCurrentlyReading.setSelected(false);
        btnArchive.setSelected(true);
        btnReadingLists.setSelected(false);

        // Show/hide content
        currentlyReadingContent.setVisibility(View.GONE);
        archiveContent.setVisibility(View.VISIBLE);
        readingListsContent.setVisibility(View.GONE);

        // Show empty state if needed
        updateArchiveUI();
    }

    private void displayReadingLists() {
        // Update tab selection
        btnCurrentlyReading.setSelected(false);
        btnArchive.setSelected(false);
        btnReadingLists.setSelected(true);

        // Show/hide content
        currentlyReadingContent.setVisibility(View.GONE);
        archiveContent.setVisibility(View.GONE);
        readingListsContent.setVisibility(View.VISIBLE);

        // Show empty state if needed
        if (readingLists.isEmpty()) {
            rvReadingLists.setVisibility(View.GONE);
            tvNoReadingLists.setVisibility(View.VISIBLE);
        } else {
            rvReadingLists.setVisibility(View.VISIBLE);
            tvNoReadingLists.setVisibility(View.GONE);
        }
    }
}
