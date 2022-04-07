package com.yunianshu.sticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Customize your sticker with text and image background.
 * You can place some text into a given region, however,
 * you can also add a plain text sticker. To support text
 * auto resizing , I take most of the code from AutoResizeTextView.
 * See https://adilatwork.blogspot.com/2014/08/android-textview-which-resizes-its-text.html
 * Notice: It's not efficient to add long text due to too much of
 * StaticLayout object allocation.
 * Created by liutao on 30/11/2016.
 */

public class TextSticker extends Sticker {

  /**
   * Our ellipsis string.
   */
  private static final String mEllipsis = "\u2026";

  private static final Drawable defaultDrawable = TextDrawable.builder().beginConfig().width(100).height(50).endConfig().buildRoundRect("",Color.TRANSPARENT,5);

  private final Context context;
  private Rect realBounds;
  private Rect textRect;
  private TextPaint textPaint;
  private Drawable drawable;
  private StaticLayout staticLayout;
  private Layout.Alignment alignment;
  private String text;

  /**
   * Upper bounds for text size.
   * This acts as a starting point for resizing.
   */
  private float maxTextSizePixels;

  /**
   * Lower bounds for text size.
   */
  private float minTextSizePixels;

  /**
   * Line spacing multiplier.
   */
  private float lineSpacingMultiplier = 1.0f;

  /**
   * Additional line spacing.
   */
  private float lineSpacingExtra = 0.0f;

  public TextSticker(@NonNull Context context) {
    this(context, null);
  }

  public TextSticker(@NonNull Context context, @Nullable Drawable drawable) {
    this.context = context;
    this.drawable = drawable;
    initSticker();
  }

  private void initSticker(){
    if (drawable == null) {
      this.drawable = defaultDrawable;
    }
    textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    realBounds = new Rect(0, 0, getWidth(), getHeight());
    textRect = new Rect(0, 0, getWidth(), getHeight());
    minTextSizePixels = convertSpToPx(1);
    maxTextSizePixels = convertSpToPx(32);
    alignment = Layout.Alignment.ALIGN_NORMAL;
    textPaint.setTextSize(maxTextSizePixels);
  }

  public void reset(){
    drawable = null;
    initSticker();
  }

  @Override public void draw(@NonNull Canvas canvas) {
    Matrix matrix = getMatrix();
    canvas.save();
    canvas.concat(matrix);
    if (drawable != null) {
      drawable.setBounds(realBounds);
      drawable.draw(canvas);
    }
    canvas.restore();

    canvas.save();
    canvas.concat(matrix);
    if (textRect.width() == getWidth()) {
      int dy = getHeight() / 2 - staticLayout.getHeight() / 2;
      // center vertical
      canvas.translate(0, dy);
    } else {
      int dx = textRect.left;
      int dy = textRect.top + textRect.height() / 2 - staticLayout.getHeight() / 2;
      canvas.translate(dx, dy);
    }
    staticLayout.draw(canvas);
    canvas.restore();
  }

  @Override public int getWidth() {
    return drawable.getIntrinsicWidth();
  }

  @Override public int getHeight() {
    return drawable.getIntrinsicHeight();
  }

  @Override public void release() {
    super.release();
    if (drawable != null) {
      drawable = null;
    }
  }



  @NonNull @Override public TextSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
    textPaint.setAlpha(alpha);
    return this;
  }

  @NonNull @Override public Drawable getDrawable() {
    return drawable;
  }

  @Override public TextSticker setDrawable(@NonNull Drawable drawable) {
    this.drawable = drawable;
    realBounds.set(0, 0, getWidth(), getHeight());
    textRect.set(0, 0, getWidth(), getHeight());
    return this;
  }

  @NonNull public TextSticker setDrawable(@NonNull Drawable drawable, @Nullable Rect region) {
    this.drawable = drawable;
    realBounds.set(0, 0, getWidth(), getHeight());
    if (region == null) {
      textRect.set(0, 0, getWidth(), getHeight());
    } else {
      textRect.set(region.left, region.top, region.right, region.bottom);
    }
    return this;
  }

  @NonNull public TextSticker setTypeface(@Nullable Typeface typeface) {
    textPaint.setTypeface(typeface);
    return this;
  }
  /**
   * Set the text to be displayed.
   *
   * @param boo 是否设置文字加粗
   * @return This TextSticker.
   */
  @NonNull public TextSticker setFakeBoldText(boolean boo) {
    textPaint.setFakeBoldText(boo);
    return this;
  }

  /**
   * Set the text to be displayed.
   *
   * @param show 是否显示下划线
   * @return This TextSticker.
   */
  @NonNull public TextSticker setTextUnderLine(boolean show) {
    textPaint.setUnderlineText(show);
    return this;
  }

  @NonNull public TextSticker setTextColor(@ColorInt int color) {
    textPaint.setColor(color);
    return this;
  }
  /**
   * 设置文字背景色
   */
  @NonNull public TextSticker setTextBackgroundColor(@ColorInt int color) {
    setTextBackgroundColor(color,5);
    return this;
  }
  /**
   * 设置文字背景色，并设置圆角
   */
  @NonNull public TextSticker setTextBackgroundColor(@ColorInt int color,int radius) {
    drawable = TextDrawable.builder()
            .beginConfig()
            .height(50)
            .width(200)
            .endConfig()
            .buildRoundRect("",color,radius);
    return this;
  }
  /**
   * 设置文字背景色,并设置宽高
   */
  @NonNull public TextSticker setTextBackgroundColor(@ColorInt int color,int width,int height) {
    if(width != 0 && height != 0) {
      setTextBackgroundColor(color,width,height,5);
    }
    return this;
  }
  /**
   * 设置文字背景色,并设置宽高以及圆角
   */
  @NonNull public TextSticker setTextBackgroundColor(@ColorInt int color,int width,int height,int radius) {
    if(width != 0 && height != 0) {
      drawable = TextDrawable.builder()
              .beginConfig()
              .height(height)
              .width(width)
              .endConfig()
              .buildRoundRect("",color,radius);
    }
    return this;
  }



  @NonNull public TextSticker setTextAlign(@NonNull Layout.Alignment alignment) {
    this.alignment = alignment;
    return this;
  }

  @NonNull public TextSticker setMaxTextSize(@Dimension(unit = Dimension.SP) float size) {
    textPaint.setTextSize(convertSpToPx(size));
    maxTextSizePixels = textPaint.getTextSize();
    return this;
  }

  /**
   * Sets the lower text size limit
   *
   * @param minTextSizeScaledPixels the minimum size to use for text in this view,
   * in scaled pixels.
   */
  @NonNull public TextSticker setMinTextSize(float minTextSizeScaledPixels) {
    minTextSizePixels = convertSpToPx(minTextSizeScaledPixels);
    return this;
  }

  @NonNull public TextSticker setLineSpacing(float add, float multiplier) {
    lineSpacingMultiplier = multiplier;
    lineSpacingExtra = add;
    return this;
  }

  @NonNull public TextSticker setText(@Nullable String text) {
    this.text = text;
    return this;
  }

  @Nullable public String getText() {
    return text;
  }

  /**
   * Resize this view's text size with respect to its width and height
   * (minus padding). You should always call this method after the initialization.
   */
  @NonNull public TextSticker resizeText() {
    final int availableHeightPixels = textRect.height();

    final int availableWidthPixels = textRect.width();

    final CharSequence text = getText();

    // Safety check
    // (Do not resize if the view does not have dimensions or if there is no text)
    if (text == null
        || text.length() <= 0
        || availableHeightPixels <= 0
        || availableWidthPixels <= 0
        || maxTextSizePixels <= 0) {
      return this;
    }

    float targetTextSizePixels = maxTextSizePixels;
    int targetTextHeightPixels =
        getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);

    // Until we either fit within our TextView
    // or we have reached our minimum text size,
    // incrementally try smaller sizes
    while (targetTextHeightPixels > availableHeightPixels
        && targetTextSizePixels > minTextSizePixels) {
      targetTextSizePixels = Math.max(targetTextSizePixels - 2, minTextSizePixels);

      targetTextHeightPixels =
          getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);
    }

    // If we have reached our minimum text size and the text still doesn't fit,
    // append an ellipsis
    // (NOTE: Auto-ellipsize doesn't work hence why we have to do it here)
    if (targetTextSizePixels == minTextSizePixels
        && targetTextHeightPixels > availableHeightPixels) {
      // Make a copy of the original TextPaint object for measuring
      TextPaint textPaintCopy = new TextPaint(textPaint);
      textPaintCopy.setTextSize(targetTextSizePixels);

      // Measure using a StaticLayout instance
      StaticLayout staticLayout =
          new StaticLayout(text, textPaintCopy, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
              lineSpacingMultiplier, lineSpacingExtra, false);

      // Check that we have a least one line of rendered text
      if (staticLayout.getLineCount() > 0) {
        // Since the line at the specific vertical position would be cut off,
        // we must trim up to the previous line and add an ellipsis
        int lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1;

        if (lastLine >= 0) {
          int startOffset = staticLayout.getLineStart(lastLine);
          int endOffset = staticLayout.getLineEnd(lastLine);
          float lineWidthPixels = staticLayout.getLineWidth(lastLine);
          float ellipseWidth = textPaintCopy.measureText(mEllipsis);

          // Trim characters off until we have enough room to draw the ellipsis
          while (availableWidthPixels < lineWidthPixels + ellipseWidth) {
            endOffset--;
            lineWidthPixels =
                textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString());
          }

          setText(text.subSequence(0, endOffset) + mEllipsis);
        }
      }
    }
    textPaint.setTextSize(targetTextSizePixels);
    staticLayout =
        new StaticLayout(this.text, textPaint, textRect.width(), alignment, lineSpacingMultiplier,
            lineSpacingExtra, true);
    return this;
  }

  /**
   * @return lower text size limit, in pixels.
   */
  public float getMinTextSizePixels() {
    return minTextSizePixels;
  }

  /**
   * Sets the text size of a clone of the view's {@link TextPaint} object
   * and uses a {@link StaticLayout} instance to measure the height of the text.
   *
   * @return the height of the text when placed in a view
   * with the specified width
   * and when the text has the specified size.
   */
  protected int getTextHeightPixels(@NonNull CharSequence source, int availableWidthPixels,
      float textSizePixels) {
    textPaint.setTextSize(textSizePixels);
    // It's not efficient to create a StaticLayout instance
    // every time when measuring, we can use StaticLayout.Builder
    // since api 23.
    StaticLayout staticLayout =
        new StaticLayout(source, textPaint, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
            lineSpacingMultiplier, lineSpacingExtra, true);
    return staticLayout.getHeight();
  }

  /**
   * @return the number of pixels which scaledPixels corresponds to on the device.
   */
  private float convertSpToPx(float scaledPixels) {
    return scaledPixels * context.getResources().getDisplayMetrics().scaledDensity;
  }
}
