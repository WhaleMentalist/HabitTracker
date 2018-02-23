package us.spencer.habittracker.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/*
 * Allow controlled scrolling for better user experience (i.e less 'jank')
 */
public class CustomRecyclerView extends RecyclerView {

    private Context context;

    public CustomRecyclerView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= 0.4;
        return super.fling(velocityX, velocityY);
    }
}
