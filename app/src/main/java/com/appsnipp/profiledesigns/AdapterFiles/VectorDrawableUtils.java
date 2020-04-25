package com.appsnipp.profiledesigns.AdapterFiles;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;

class VectorDrawableUtils {
    static Drawable getDrawable(Context context, int drawableResId){
        return VectorDrawableCompat.create(context.getResources(), drawableResId, context.getTheme());
    }

    static Drawable getDrawable(Context context, int drawableResId, int colorFilter){
        Drawable drawable = getDrawable(context, drawableResId);
        drawable.setColorFilter(colorFilter, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
