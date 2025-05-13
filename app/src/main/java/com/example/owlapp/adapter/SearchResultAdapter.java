package com.example.owlapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.owlapp.R;
import com.example.owlapp.model.Book;
import com.bumptech.glide.Glide;

import java.util.List;

public class SearchResultAdapter extends ArrayAdapter<Book> {
    private Context context;
    private List<Book> books;

    public SearchResultAdapter(Context context, List<Book> books) {
        super(context, R.layout.item_search_result, books);
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_search_result, parent, false);
        }

        Book book = books.get(position);

        TextView tvNumber = view.findViewById(R.id.tvNumber);
        ImageView imgBook = view.findViewById(R.id.imgBook);
        TextView tvBookTitle = view.findViewById(R.id.tvBookTitle);
        TextView tvAuthor = view.findViewById(R.id.tvAuthor);
        TextView tvViews = view.findViewById(R.id.tvViews);
        TextView tvChapters = view.findViewById(R.id.tvChapters);

        tvNumber.setText(String.valueOf(position + 1));

        // Load book cover from URL if available
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(imgBook);
        } else {
            imgBook.setImageResource(R.drawable.book_placeholder);
        }

        tvBookTitle.setText(book.getFullTitle());
        tvAuthor.setText(book.getAuthor());
        tvViews.setText(book.getFormattedViews());
        tvChapters.setText(String.valueOf(book.getChapters()));

        return view;
    }
}
