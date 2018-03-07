package us.spencer.habittracker.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Scroller;

public abstract class HorizontalScrollChart extends View implements GestureDetector.OnGestureListener,
                                                            ValueAnimator.AnimatorUpdateListener {

    /*
     * Allows query of scrolling, in order to update item positions
     */
    private Scroller mScroller;

    /*
     * Need this to detect gestures as a delimiter for querying 'scroller'
     */
    private GestureDetector mGestureDetector;

    /*
     * Introduces values for scrolling animation. Important for smoother scrolling.
     */
    private ValueAnimator mScrollAnimator;

    /*
     * Affects the scrolling direction of the chart
     */
    private short mOrientation = 1;

    /*
     * Holds the current offset position of the chart
     */
    protected int mOffset;

    /*
     * Governs the sensitivity of scrolling. NOTE: This
     * will greatly impact how many 'postInvalidate' calls are made.
     * The lower the threshold the more times 'postInvalidate' calls
     * are made, thus the more drawing that needs to occur.
     */
    protected int mOffsetThreshold = 1;

    /*
     * Prevents chart from going beyond a specified boundary
     */
    private int mMaxOffset = 10000;

    public HorizontalScrollChart(Context context) {
        super(context);
        init(context);
    }

    public HorizontalScrollChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalScrollChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, null, true); /* Scroller to get position values for drawer */
        mGestureDetector = new GestureDetector(context, this); /* Listen for gestures on custom scrollable chart */
        mScrollAnimator = ValueAnimator.ofFloat(0.0f, 1.0f); /* Generates values in range of 0 to 1 */
        mScrollAnimator.addUpdateListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) { }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX, float distY) {

        if(Math.abs(distX) > Math.abs(distY)) {
            ViewParent parent = getParent();

            if(parent != null) {
                parent.requestDisallowInterceptTouchEvent(true); /* Prevent scrolling from creating other events */
            }
        }

        distX = -mOrientation * distX; /* Scroll based on orientation of chart */
        distX = Math.min(distX, mMaxOffset - mScroller.getCurrX());
        mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), (int) distX, 0, 0);
        mScroller.computeScrollOffset();
        updateDataOffset();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.fling(mScroller.getCurrX(), mScroller.getCurrY(),
                mOrientation * ((int) velocityX) / 2, 0,
                0, mMaxOffset, 0, 0);
        invalidate();
        mScrollAnimator.setDuration(mScroller.getDuration());
        mScrollAnimator.start();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if(!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            updateDataOffset();
        }
        else {
            mScrollAnimator.cancel();
        }
    }

    public void setOrientation(short orientation) {
        if(orientation == 1 || orientation == -1)
            throw new IllegalArgumentException();
        mOrientation = orientation;
    }

    private void updateDataOffset() {
        int newOffset = mScroller.getCurrX() / mOffsetThreshold;
        newOffset = Math.max(0, newOffset);
        newOffset = Math.min(newOffset, mMaxOffset);

        if(newOffset != mOffset) {
            mOffset = newOffset;
            postInvalidate();
        }
    }
}
