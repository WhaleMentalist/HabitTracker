package us.spencer.habittracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class BoxedNumber extends View {

    private static final String DAY_OF_MONTH_REGEX = "DD";

    private static final float DEFAULT_TEXT_SIZE = 15.0f;

    private static final boolean DEFAULT_BOLD_VALUE = false;

    private static final int DEFAULT_MARGIN_VALUE = 5;

    private String mText;

    private float mTextSize;

    private boolean mIsBold;

    private int mMargin;

    private Paint mBoxPaint = new Paint();

    private Paint mNumberPaint = new Paint();

    private RectF mBox = new RectF();

    private Rect r  = new Rect();

    public BoxedNumber(Context context) {
        super(context);
        init();
    }

    public BoxedNumber(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        init();
    }

    public BoxedNumber(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(attrs);
        init();
    }

    private void setupAttributes(AttributeSet attrs) {
        TypedArray attr = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.BoxedNumber, 0, 0);

        try {
            mText = attr.getString(R.styleable.BoxedNumber_bn_text);
            mTextSize = attr.getFloat(R.styleable.BoxedNumber_bn_text_size, DEFAULT_TEXT_SIZE);
            mIsBold = attr.getBoolean(R.styleable.BoxedNumber_bn_text_bold, DEFAULT_BOLD_VALUE);
            mMargin = attr.getInteger(R.styleable.BoxedNumber_bn_margin, DEFAULT_MARGIN_VALUE);
        }
        finally {
            attr.recycle();
        }
    }

    private void init() {
        mBoxPaint.setColor(Color.parseColor("#4B7B7B7B"));

        mNumberPaint.setColor(Color.WHITE);
        mNumberPaint.setAntiAlias(true);
        if(mIsBold) {
            mNumberPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            mNumberPaint.setTypeface(Typeface.DEFAULT);
        }
        mNumberPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNumberPaint.getTextBounds(DAY_OF_MONTH_REGEX, 0,
                DAY_OF_MONTH_REGEX.length(), r); /* Displays only days of month */

        int contentWidth = (r.width() + mMargin) * 2;
        int w = resolveSize(contentWidth + getPaddingLeft() + getPaddingRight(),
                widthMeasureSpec);

        int contentHeight = (r.height() + mMargin) * 2;
        int h = resolveSize(contentHeight + getPaddingTop() + getPaddingBottom(),
                heightMeasureSpec);

        int boxSize = h < w ? w : h;
        mBox.top = boxSize - mMargin;
        mBox.right = boxSize - mMargin;

        setMeasuredDimension(boxSize, boxSize);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(mBox, mBoxPaint);
        drawTextCenter(canvas);
    }

    private void drawTextCenter(Canvas canvas) {
        canvas.getClipBounds(r);
        int cHeight = r.height() - mMargin;
        int cWidth = r.width() - mMargin;
        mNumberPaint.setTextAlign(Paint.Align.LEFT);
        mNumberPaint.getTextBounds(mText, 0, mText.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(mText, x, y, mNumberPaint);
    }

    public void setText(String text) {
        mText = text;
    }

    public void setBoxColor(int color) {
        mBoxPaint.setColor(color);
    }
}
