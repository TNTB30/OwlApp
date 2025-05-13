package com.example.owlapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.owlapp.BookDetailActivity;
import com.example.owlapp.R;
import com.example.owlapp.model.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private Context context;
    private List<Book> books;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);

        holder.tvBookTitle.setText(book.getFullTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvViews.setText(book.getFormattedViews());
        holder.tvChapters.setText(String.valueOf(book.getChapters()));

        // Load book cover from URL
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(holder.imgBook);
        } else if (book.getId() == 2 && book.getTitle().contains("Cha Giàu")) {
            // Set specific image for Cha Giàu, Cha Nghèo book
            holder.imgBook.setImageResource(R.drawable.rich_dad_poor_dad);
        } else {
            holder.imgBook.setImageResource(R.drawable.book_placeholder);
        }

        // Show download icon
        holder.imgDownloaded.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(book);
                } else {
                    // Default behavior - open book detail
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra("book_id", book.getId());
                    intent.putExtra("book_title", book.getTitle());
                    intent.putExtra("book_english_title", book.getEnglishTitle());
                    intent.putExtra("book_author", book.getAuthor());
                    intent.putExtra("book_cover_url", book.getCoverUrl());
                    intent.putExtra("show_comments", false);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook, imgDownloaded;
        TextView tvBookTitle, tvAuthor, tvViews, tvChapters;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            imgDownloaded = itemView.findViewById(R.id.imgDownloaded);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvChapters = itemView.findViewById(R.id.tvChapters);
        }
    }
}
