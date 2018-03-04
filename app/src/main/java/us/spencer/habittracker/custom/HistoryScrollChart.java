package us.spencer.habittracker.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.icu.util.Measure;
import android.util.AttributeSet;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import us.spencer.habittracker.R;
import us.spencer.habittracker.utility.DateUtils;
import us.spencer.habittracker.utility.GraphicsUtilities;

public class HistoryScrollChart extends HorizontalScrollChart {

    /* Seven are dedicated to the days of the week and the last one is the header */
    private static final int ITEM_PER_COLUMN = 8;

    /* TODO: Delete when finished testing */
    private Random random = new Random();

    /* Represents repetition of habit on a specified date */
    private Set<Long> repetitions = new HashSet<>();

    /* Help guide drawing of painters*/
    private RectF mDrawAnchor;

    /* Draws the column square items with no fill */
    private Paint mNonFilledSquarePainter;

    /* Draw the column square item with fill */
    private Paint mFilledSquarePainter;

    /* Draw day of month text on the chart */
    private Paint mDayOfMonthTextPainter;

    /* Draw day of week text */
    private Paint mAxisTextPainter;

    /* Specifies the number columns in view */
    private int mNumberColumns;

    /* Size of the each square item*/
    private float mSquareSize = 40.0f;

    /* The extra space from left over pixels after measuring amount of columns*/
    private float mExtraPadding;

    /* The space between each square item */
    private float mSquareSpacing;

    /* The date that will be starting point for view */
    private MutableDateTime mBaseDate;

    /* Helper data member to prevent redrawing same month on header columns */
    private int mCurrentMonth;

    public HistoryScrollChart(Context context) {
        super(context);
        init(context);
    }

    public HistoryScrollChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void initSquarePainters() {
        mNonFilledSquarePainter = new Paint();
        mNonFilledSquarePainter.setColor(Color.LTGRAY);

        mFilledSquarePainter = new Paint();
        mFilledSquarePainter.setColor(getResources().getColor(R.color.magenta));
    }

    private void initTextPainters() {
        mDayOfMonthTextPainter = new Paint();
        mDayOfMonthTextPainter.setAntiAlias(true);
        mDayOfMonthTextPainter.setColor(Color.WHITE);
        mDayOfMonthTextPainter.setTextSize(mSquareSize * 0.5f);
        mDayOfMonthTextPainter.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mDayOfMonthTextPainter.setTextAlign(Paint.Align.CENTER);

        mAxisTextPainter = new Paint();
        mAxisTextPainter.setAntiAlias(true);
        mAxisTextPainter.setColor(Color.LTGRAY);
        mAxisTextPainter.setTextSize(mSquareSize * 0.45f);
        mAxisTextPainter.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mAxisTextPainter.setTextAlign(Paint.Align.CENTER);
    }

    private void init(Context context) {
        super.mOffsetThreshold = (int) Math.ceil(mSquareSize);
        mDrawAnchor = new RectF(0.0f, 0.0f, mSquareSize, mSquareSize);
        initSquarePainters();
        initTextPainters();
        mSquareSpacing = GraphicsUtilities.dpToPixels(context, 1.0f);
        produceRandomData(); /* TODO: Delete once the view has completed testing phase */
    }

    private int getDimension(int targetSize, int spec) {
        int result;
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(targetSize);

        if(specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        else {
            result = targetSize;
            if(specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) + getPaddingLeft() + getPaddingRight();
        mExtraPadding = (width % (mSquareSize + mSquareSpacing)) / 2; /* Extra space added to padding */
        mNumberColumns = (int) (width / (mSquareSize + mSquareSpacing)); /* Want implicit 'floor' of result */
        int height = (int) (ITEM_PER_COLUMN * (mSquareSize + mSquareSpacing)
                + getPaddingTop() + getPaddingBottom() + 2.0f * mExtraPadding);
        setMeasuredDimension(getDimension(width, widthMeasureSpec),
                getDimension(height, heightMeasureSpec));
    }

    @Override
    public void onDraw(Canvas canvas) {
        mBaseDate = DateUtils.getCurrentDate();
        mBaseDate.addDays(-(super.mOffset * 7));

        /* Draw from right to left and from bottom to top */
        mDrawAnchor.offsetTo(mExtraPadding + ((mNumberColumns - 2) * (mSquareSize + mSquareSpacing)),
                (mExtraPadding + ((mSquareSize + mSquareSpacing) * (ITEM_PER_COLUMN - 1))));

        /* Draw calendar days and header */
        for(int i = 0; i < mNumberColumns - 1; i++) {
            drawColumn(canvas);
            drawColumnHeader(canvas);
            mDrawAnchor.offset(-(mSquareSize + mSquareSpacing),
                    (mSquareSize + mSquareSpacing) * ITEM_PER_COLUMN); /* Top of column and right one column */
        }

        mDrawAnchor.offset(mNumberColumns * (mSquareSize + mSquareSpacing),
                -((mSquareSize + mSquareSpacing) * (ITEM_PER_COLUMN - 2))); /* Weekday axis doesn't need header */
        drawWeekdayAxis(canvas);
    }

    private void drawColumn(Canvas canvas) {
        for(int i = 0; i < ITEM_PER_COLUMN - 1; i++) {
            drawSquareColumnItem(canvas);
            mBaseDate.addDays(-1);
            mDrawAnchor.offset(0.0f, -(mSquareSize + mSquareSpacing));
        }
    }

    private void drawColumnHeader(Canvas canvas) {
        if(mCurrentMonth != mBaseDate.getMonthOfYear() &&
                mBaseDate.getDayOfMonth() >= 1 && mBaseDate.getDayOfMonth() <= 7) {
            drawSquareHeader(canvas, mBaseDate.monthOfYear().getAsShortText());
            mCurrentMonth = mBaseDate.getMonthOfYear();

            if(mDrawAnchor.right < (mNumberColumns - 1) * (mSquareSize + mSquareSpacing)) {
                mDrawAnchor.offset((mSquareSize + mSquareSpacing), 0.0f);
                drawYearHeader(canvas, mBaseDate.year().getAsText());
                mDrawAnchor.offset(-(mSquareSize + mSquareSpacing), 0.0f);
            }
        }
        mDrawAnchor.offset(0.0f, -(mSquareSize + mSquareSpacing));
    }

    private void produceRandomData() {
        int year, month, day;

        for(int i = 0; i < 2000; i++) {
            year = random.nextInt(19) + 2000;
            month = random.nextInt(12) + 1;
            day = random.nextInt(25) + 1;
            repetitions.add(new DateTime(year, month, day, 0, 0).getMillis());
        }
    }

    private void drawWeekdayAxis(Canvas canvas) {
        MutableDateTime today = DateUtils.getCurrentDate();
        today.addDays(-(ITEM_PER_COLUMN - 2)); /* Get first day of the week to draw */
        for(int i = 0; i < ITEM_PER_COLUMN - 1; i++) {
            drawSquareDayOfWeek(canvas, DateUtils.getDayOfWeek(today.getDayOfWeek()));
            today.addDays(1);
            mDrawAnchor.offset(0.0f, (mSquareSize + mSquareSpacing));
        }
    }

    private void drawSquareDayOfWeek(Canvas canvas, String dayOfWeek) {
        canvas.drawText(dayOfWeek, mDrawAnchor.centerX(),
                mDrawAnchor.centerY() - (mAxisTextPainter.ascent() + mAxisTextPainter.descent()) / 2,
                mAxisTextPainter);
    }

    private void drawSquareColumnItem(Canvas canvas) {
        if(repetitions.contains(mBaseDate.getMillis())) {
            canvas.drawRect(mDrawAnchor, mFilledSquarePainter);
            canvas.drawText(String.valueOf(mBaseDate.getDayOfMonth()), mDrawAnchor.centerX(),
                    mDrawAnchor.centerY() - (mDayOfMonthTextPainter.ascent() + mDayOfMonthTextPainter.descent()) / 2,
                    mDayOfMonthTextPainter);
        }
        else {
            canvas.drawRect(mDrawAnchor, mNonFilledSquarePainter);
            canvas.drawText(String.valueOf(mBaseDate.getDayOfMonth()), mDrawAnchor.centerX(),
                    mDrawAnchor.centerY() - (mDayOfMonthTextPainter.ascent() + mDayOfMonthTextPainter.descent()) / 2,
                    mDayOfMonthTextPainter);
        }
    }

    private void drawSquareHeader(Canvas canvas, String monthOfYear) {
        canvas.drawText(monthOfYear, mDrawAnchor.centerX(),
                mDrawAnchor.centerY() - (mAxisTextPainter.ascent() + mAxisTextPainter.descent()) / 2,
                mAxisTextPainter);
    }

    private void drawYearHeader(Canvas canvas, String year) {
        canvas.drawText(year, mDrawAnchor.centerX(),
                mDrawAnchor.centerY() - (mAxisTextPainter.ascent() + mAxisTextPainter.descent()) / 2,
                mAxisTextPainter);
    }
}
