package com.retrox.aodmod.util;

import android.graphics.Paint;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextUtils;
import android.text.style.LineHeightSpan;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * Default implementation of the {@link LineHeightSpan}, which changes the line height of the
 * attached paragraph.
 * <p>
 * For example, a paragraph with its line height equal to 100px can be set like this:
 * <pre>
 * SpannableString string = new SpannableString("This is a multiline paragraph. This is a multiline paragraph.");
 * string.setSpan(new LineHeightSpan.Standard(100), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
 * </pre>
 * <img src="{@docRoot}reference/android/images/text/style/lineheightspan.png" />
 * <figcaption>Text with line height set to 100 pixels.</figcaption>
 * <p>
 * Notice that LineHeightSpan will change the line height of the entire paragraph, even though it
 * covers only part of the paragraph.
 * </p>
 */
public class LineHeightSpanStandard implements LineHeightSpan, ParcelableSpan {
    private final @Px
    int mHeight;

    /**
     * Set the line height of the paragraph to <code>height</code> physical pixels.
     */
    public LineHeightSpanStandard(@Px @IntRange(from = 1) int height) {
        mHeight = height;
    }

    /**
     * Constructor called from {@link TextUtils} to restore the span from a parcel
     */
    public LineHeightSpanStandard(@NonNull Parcel src) {
        mHeight = src.readInt();
    }

    /**
     * Returns the line height specified by this span.
     */
    @Px
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    /**
     * @hide
     */
    @Override
    public int getSpanTypeIdInternal() {
        return TextUtils.LINE_HEIGHT_SPAN;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    /**
     * @hide
     */
    @Override
    public void writeToParcelInternal(@NonNull Parcel dest, int flags) {
        dest.writeInt(mHeight);
    }

    @Override
    public void chooseHeight(@NonNull CharSequence text, int start, int end,
                             int spanstartv, int lineHeight,
                             @NonNull Paint.FontMetricsInt fm) {
        final int originHeight = fm.descent - fm.ascent;
        // If original height is not positive, do nothing.
        if (originHeight <= 0) {
            return;
        }
        final float ratio = mHeight * 1.0f / originHeight;
        fm.descent = Math.round(fm.descent * ratio);
        fm.ascent = fm.descent - mHeight;
    }
}