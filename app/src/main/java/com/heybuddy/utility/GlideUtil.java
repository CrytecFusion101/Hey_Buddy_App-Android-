package com.heybuddy.utility;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.heybuddy.R;


public class GlideUtil {

    private static RequestOptions defaultRequestOption;

    public static RequestOptions getDefaultRequestOption() {
        if (defaultRequestOption == null) {
            return defaultRequestOption = BuildRequestOptionRounded(R.drawable.ic_add_image);
        }
        return defaultRequestOption;
    }

    public static RequestOptions BuildRequestOptionNormal(int image_holder_portrait) {
        return new RequestOptions().placeholder(image_holder_portrait)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(image_holder_portrait);
    }

    public static RequestOptions BuildRequestOptionRounded(int image_holder_portrait) {
        return new RequestOptions().placeholder(image_holder_portrait)
                .error(image_holder_portrait)
                .fallback(image_holder_portrait)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    public static RequestOptions BuildImageLoader(int image_holder_portrait) {
        return new RequestOptions().placeholder(image_holder_portrait)
                .error(image_holder_portrait)
                .fallback(image_holder_portrait)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    public static RequestOptions BuildRequestOptionRounded() {
        return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    public static RequestOptions BuildRequestVideoCenterCrop(int image_holder_portrait) {
        return new RequestOptions().placeholder(image_holder_portrait).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(image_holder_portrait);
    }

    public static void svgLoader(Context context, String url, ImageView imageView) {
//        GlideToVectorYou.justLoadImage((Activity) context, Uri.parse(url), imageView);
    }
}