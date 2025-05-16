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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.owlapp.HomeActivity;
import com.example.owlapp.R;
import com.example.owlapp.ReadBooksActivity;
import com.example.owlapp.RecentlyReadActivity;
import com.example.owlapp.SearchActivity;
import com.example.owlapp.adapter.BookAdapter;
import com.example.owlapp.adapter.ReadingListAdapter;
import com.example.owlapp.api.ApiClient;
import com.example.owlapp.api.OpenLibraryService;
import com.example.owlapp.model.Book;
import com.example.owlapp.model.ReadingList;
import com.example.owlapp.model.api.SearchResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends AppCompatActivity {

    private Button btnCurrentlyReading, btnReadingLists;
    private View currentlyReadingContent, readingListsContent;
    private RecyclerView rvCurrentlyReading, rvReadingLists;
    private TextView tvNoCurrentlyReading, tvNoReadingLists;
    private ImageView navHome, navSearch, navLibrary, navChat, navCart, imgOptions;

    private List<Book> currentlyReadingBooks;
    private List<ReadingList> readingLists;

    private BookAdapter currentlyReadingAdapter;
    private ReadingListAdapter readingListAdapter;

    private OpenLibraryService openLibraryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        openLibraryService = ApiClient.getClient().create(OpenLibraryService.class);

        initViews();
        setupTabListeners();
        setupNavigation();
        loadDataFromApi();
        displayCurrentlyReading();
    }

    private void initViews() {
        btnCurrentlyReading = findViewById(R.id.btnCurrentlyReading);
        btnReadingLists = findViewById(R.id.btnReadingLists);

        currentlyReadingContent = findViewById(R.id.currentlyReadingContent);
        readingListsContent = findViewById(R.id.readingListsContent);

        rvCurrentlyReading = findViewById(R.id.rvCurrentlyReading);
        rvReadingLists = findViewById(R.id.rvReadingLists);

        tvNoCurrentlyReading = findViewById(R.id.tvNoCurrentlyReading);
        tvNoReadingLists = findViewById(R.id.tvNoReadingLists);

        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navLibrary = findViewById(R.id.navLibrary);
        navChat = findViewById(R.id.navChat);
        navCart = findViewById(R.id.navCart);
        imgOptions = findViewById(R.id.imgOptions);

        rvCurrentlyReading.setLayoutManager(new GridLayoutManager(this, 3));
        rvReadingLists.setLayoutManager(new LinearLayoutManager(this));

        imgOptions.setOnClickListener(v -> Toast.makeText(this, "Tùy chọn thư viện", Toast.LENGTH_SHORT).show());
    }

    private void setupTabListeners() {
        btnCurrentlyReading.setOnClickListener(v -> displayCurrentlyReading());
        btnReadingLists.setOnClickListener(v -> displayReadingLists());
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        navLibrary.setOnClickListener(v -> {}); // Already in library
        navChat.setOnClickListener(v -> startActivity(new Intent(this, ReadBooksActivity.class)));
        navCart.setOnClickListener(v -> startActivity(new Intent(this, RecentlyReadActivity.class)));
    }

    private void loadDataFromApi() {
        currentlyReadingBooks = new ArrayList<>();
        readingLists = new ArrayList<>();

        fetchCurrentlyReadingBooks();

        readingLists.add(new ReadingList(1, "Danh sách đọc của Haru", 1));

        currentlyReadingAdapter = new BookAdapter(this, currentlyReadingBooks);
        readingListAdapter = new ReadingListAdapter(this, readingLists);

        rvCurrentlyReading.setAdapter(currentlyReadingAdapter);
        rvReadingLists.setAdapter(readingListAdapter);

        readingListAdapter.setOnItemClickListener(readingList -> {
            Intent intent = new Intent(this, ReadingListDetailActivity.class);
            intent.putExtra("reading_list_id", readingList.getId());
            intent.putExtra("reading_list_name", readingList.getName());
            startActivity(intent);
        });
    }

    private void fetchCurrentlyReadingBooks() {
        Call<SearchResponse> call = openLibraryService.searchBooks("literature");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Doc> docs = response.body().getDocs();
                    currentlyReadingBooks.clear();

                    int id = 1;
                    for (SearchResponse.Doc doc : docs) {
                        if (doc.getTitle() != null && doc.getAuthor() != null) {
                            Book book = new Book(
                                    id++,
                                    doc.getTitle(),
                                    doc.getTitle(),
                                    doc.getAuthor(),
                                    doc.getCoverUrl(),
                                    1000,
                                    10,
                                    "Tiểu thuyết",
                                    null,
                                    null,
                                    doc.getKey() != null ? doc.getKey().replace("/works/", "") : "",
                                    doc.getIsbn()
                            );
                            currentlyReadingBooks.add(book);
                            if (currentlyReadingBooks.size() >= 6) break;
                        }
                    }

                    currentlyReadingAdapter.notifyDataSetChanged();
                    updateCurrentlyReadingUI();
                } else {
                    loadFallbackCurrentlyReadingBooks();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                loadFallbackCurrentlyReadingBooks();
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

    private void updateCurrentlyReadingUI() {
        rvCurrentlyReading.setVisibility(currentlyReadingBooks.isEmpty() ? View.GONE : View.VISIBLE);
        tvNoCurrentlyReading.setVisibility(currentlyReadingBooks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void displayCurrentlyReading() {
        btnCurrentlyReading.setSelected(true);
        btnReadingLists.setSelected(false);

        currentlyReadingContent.setVisibility(View.VISIBLE);
        readingListsContent.setVisibility(View.GONE);

        updateCurrentlyReadingUI();
    }

    private void displayReadingLists() {
        btnCurrentlyReading.setSelected(false);
        btnReadingLists.setSelected(true);

        currentlyReadingContent.setVisibility(View.GONE);
        readingListsContent.setVisibility(View.VISIBLE);

        rvReadingLists.setVisibility(readingLists.isEmpty() ? View.GONE : View.VISIBLE);
        tvNoReadingLists.setVisibility(readingLists.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void addToReadingList(Book book) {
        SharedPreferences prefs = getSharedPreferences("reading_lists", MODE_PRIVATE);
        String readingListJson = prefs.getString("favorites", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Book>>(){}.getType();
        List<Book> favoriteBooks = gson.fromJson(readingListJson, type);

        boolean exists = false;
        for (Book b : favoriteBooks) {
            if (b.getId() == book.getId()) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            favoriteBooks.add(book);
            prefs.edit().putString("favorites", gson.toJson(favoriteBooks)).apply();
            Toast.makeText(this, "Đã thêm vào danh sách đọc", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sách đã có trong danh sách", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCurrentlyReadingBook(int bookId, String title, String englishTitle, String author, String coverUrl) {
        SharedPreferences prefs = getSharedPreferences("currently_reading", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("book_id", bookId);
        editor.putString("title", title);
        editor.putString("english_title", englishTitle);
        editor.putString("author", author);
        editor.putString("cover_url", coverUrl);
        editor.putLong("last_read_time", System.currentTimeMillis());
        editor.apply();
    }
}
