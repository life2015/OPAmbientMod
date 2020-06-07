/*
 * Copyright (C) 2018 paphonb@xda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.retrox.aodmod.extensions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.*;
import androidx.core.content.res.ResourcesCompat;

import android.view.View;
import com.retrox.aodmod.MainHook;

public class ResourceUtils {

    private static Context mContext;
    private static ResourceUtils mInstance;

    private ResourceUtils(Context context) {
        mInstance = this;
        mContext = createOwnContext(context);
    }

    public static Context createOwnContext(Context context) {
        try {
            return context.createPackageContext(MainHook.PACKAGE_OWN, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Failed to instantiate own package context", e);
        }
    }

    public static ResourceUtils getInstance() {
        return mInstance;
    }

    public static ResourceUtils getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ResourceUtils(context);
        return mInstance;
    }

    public static ResourceUtils getInstance(View view) {
        return getInstance(view.getContext());
    }

    public final XmlResourceParser getLayout(@LayoutRes int resId) {
        return mContext.getResources().getLayout(resId);
    }

    public final float getDimension(@DimenRes int resId) {
        return mContext.getResources().getDimension(resId);
    }

    public final int getDimensionPixelSize(@DimenRes int resId) {
        return mContext.getResources().getDimensionPixelSize(resId);
    }

    @ColorInt
    public final int getColor(@ColorRes int resId) {
        //noinspection deprecation
        return mContext.getResources().getColor(resId);
    }

    @NonNull
    public final Drawable getDrawable(@DrawableRes int resId) {
        //noinspection deprecation
        if(resId == 0) return null;
        return mContext.getDrawable(resId);
    }

    @NonNull
    public final Drawable getDrawable(@DrawableRes int resId, Resources.Theme theme) {
        //noinspection deprecation
        return mContext.getResources().getDrawable(resId, theme);
    }

    @NonNull
    public final Typeface getFont(@FontRes int resId) {
        //noinspection deprecation
        return ResourcesCompat.getFont(mContext, resId);
    }

    @NonNull
    public final String getString(@StringRes int resId) {
        return mContext.getResources().getString(resId);
    }

    @NonNull
    public final String[] getStringArray(@ArrayRes int resId) {
        return mContext.getResources().getStringArray(resId);
    }

    @NonNull
    public final String getString(@StringRes int resId, Object... formatArgs) {
        return mContext.getResources().getString(resId, formatArgs);
    }

    @NonNull
    public CharSequence getText(@StringRes int resId) {
        return mContext.getResources().getText(resId);
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @NonNull
    public Resources getResources() {
        return mContext.getResources();
    }

    public AssetManager getAssets() {
        return mContext.getAssets();
    }

}

