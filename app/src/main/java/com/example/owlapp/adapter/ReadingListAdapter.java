package com.example.owlapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.owlapp.R;
import com.example.owlapp.model.ReadingList;

import java.util.List;

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingListAdapter.ViewHolder> {

    private Context context;
    private List<ReadingList> readingLists;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ReadingList readingList);
    }

    public ReadingListAdapter(Context context, List<ReadingList> readingLists) {
        this.context = context;
        this.readingLists = readingLists;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reading_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadingList readingList = readingLists.get(position);

        holder.tvListName.setText(readingList.getName());
        holder.tvBookCount.setText(readingList.getFormattedBookCount());

        // In a real app, you would load the cover image from a URL or resource
        holder.imgListCover.setImageResource(R.drawable.book_placeholder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(readingList);
                }
            }
        });

        holder.imgListOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle options click
            }
        });
    }

    @Override
    public int getItemCount() {
        return readingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgListCover, imgListOptions;
        TextView tvListName, tvBookCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgListCover = itemView.findViewById(R.id.imgListCover);
            imgListOptions = itemView.findViewById(R.id.imgListOptions);
            tvListName = itemView.findViewById(R.id.tvListName);
            tvBookCount = itemView.findViewById(R.id.tvBookCount);
        }
    }
}
