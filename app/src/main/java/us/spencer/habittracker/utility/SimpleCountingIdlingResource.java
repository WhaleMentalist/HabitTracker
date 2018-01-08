package us.spencer.habittracker.utility;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;

    private AtomicInteger mCounter = new AtomicInteger(0);

    private ResourceCallback mResourceCallback;

    public SimpleCountingIdlingResource(@NonNull String resourceName) {
        mResourceName = checkNotNull(resourceName);
    }

    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;
    }

    @Override
    public boolean isIdleNow() {
        return mCounter.get() == 0;
    }

    public void increment() {
        mCounter.getAndIncrement();
    }

    public void decrement() {
        int counterVal = mCounter.getAndDecrement();

        if(counterVal == 0) {
            if(mResourceCallback != null) {
                mResourceCallback.onTransitionToIdle();
            }
        }

        if(counterVal < 0) {
            throw new IllegalStateException("'mCounter' has become corrupted!");
        }
    }
}
