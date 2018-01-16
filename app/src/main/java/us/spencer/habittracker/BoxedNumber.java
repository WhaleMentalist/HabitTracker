package us.spencer.habittracker;

import android.content.Context;
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

    private static final float BOX_WIDTH = 40.0f;

    private static final float BOX_HEIGHT = 40.0f;

    private Paint mBoxPaint = new Paint();

    private Paint mNumberPaint = new Paint();

    private RectF mBox = new RectF(0.0f, BOX_HEIGHT, BOX_WIDTH, 0.0f);

    private Rect r  = new Rect();

    private String mText = "DD";

    public BoxedNumber(Context context) {
        super(context);
        init();
    }

    public BoxedNumber(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoxedNumber(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBoxPaint.setColor(Color.parseColor("#4B7B7B7B"));

        mNumberPaint.setColor(Color.WHITE);
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mNumberPaint.setTextSize(25.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(mBox, mBoxPaint);
        drawTextCenter(canvas);
    }

    private void drawTextCenter(Canvas canvas) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        mNumberPaint.setTextAlign(Paint.Align.LEFT);
        mNumberPaint.getTextBounds(mText, 0, mText.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(mText, x, y, mNumberPaint);
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setBoxColor(int color) {
        mBoxPaint.setColor(color);
        invalidate();
    }
}
