package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import java.util.Locale;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.util.Converter;

public class IconButton extends AppCompatButton {
    protected int drawableWidth;
    protected DrawablePositions drawablePosition;
    protected int iconPadding;

    // Cached to prevent allocation during onLayout
    Rect bounds;

    private enum DrawablePositions {
        NONE,
        LEFT_AND_RIGHT,
        LEFT,
        RIGHT
    }

    public IconButton(Context context) {
        super(context);
        bounds = new Rect();
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        bounds = new Rect();
        applyAttributes(attrs);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSansKR-Light-Hestia.otf");
        setTypeface(typeface);
    }

    public IconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bounds = new Rect();
        applyAttributes(attrs);
    }

    protected void applyAttributes(AttributeSet attrs) {
        // Slight contortion to prevent allocating in onLayout
        if (null == bounds) {
            bounds = new Rect();
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconButton);
        int paddingId = typedArray.getDimensionPixelSize(R.styleable.IconButton_iconPadding, 0);
        setIconPadding(paddingId);
        typedArray.recycle();
    }

    public void setIconPadding(int padding) {
        iconPadding = padding;
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Paint textPaint = getPaint();
        String text = getText().toString();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        int textWidth = bounds.width();
        int factor = (drawablePosition == DrawablePositions.LEFT_AND_RIGHT) ? 2 : 1;
        int contentWidth = drawableWidth + iconPadding * factor + textWidth;
        int horizontalPadding = 0;

        if(Locale.getDefault().getDisplayCountry().equals("ko")) {
            horizontalPadding = (int) ((getWidth() / 2.0) - (contentWidth / 2.0));
        } else {
            horizontalPadding = (int) ((getWidth() / 2.0) - (contentWidth / 1.7));
        }

        setCompoundDrawablePadding(-horizontalPadding + iconPadding);

        switch (drawablePosition) {
            case LEFT:
                setPadding(horizontalPadding, getPaddingTop(), 0, getPaddingBottom());
                break;

            case RIGHT:
                setPadding(0, getPaddingTop(), horizontalPadding, getPaddingBottom());
                break;

            case LEFT_AND_RIGHT:
                setPadding(horizontalPadding, getPaddingTop(), horizontalPadding, getPaddingBottom());
                break;

            default:
                setPadding(0, getPaddingTop(), 0, getPaddingBottom());
        }
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        initCompoundDrawableSize();

        if (left != null && right != null) {
            drawableWidth = left.getIntrinsicWidth() + right.getIntrinsicWidth();
            drawablePosition = DrawablePositions.LEFT_AND_RIGHT;
        } else if (left != null) {
            drawableWidth = left.getIntrinsicWidth();
            drawablePosition = DrawablePositions.LEFT;
        } else if (right != null) {
            drawableWidth = right.getIntrinsicWidth();
            drawablePosition = DrawablePositions.RIGHT;
        } else {
            drawablePosition = DrawablePositions.NONE;
        }

        requestLayout();
    }

    private void initCompoundDrawableSize() {
        Drawable[] drawables = getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable == null) {
                continue;
            }

            Rect realBounds = drawable.getBounds();
            drawable.setBounds(realBounds);
        }

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }
}