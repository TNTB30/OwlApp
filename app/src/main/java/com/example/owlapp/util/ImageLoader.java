package com.example.owlapp.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.owlapp.R;

public class ImageLoader {

    public static void loadBookCover(Context context, String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.book_placeholder)
                            .error(R.drawable.book_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.book_placeholder);
        }
    }

    public static void loadAuthorPhoto(Context context, String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.user_placeholder)
                            .error(R.drawable.user_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop())
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.user_placeholder);
        }
    }
}
